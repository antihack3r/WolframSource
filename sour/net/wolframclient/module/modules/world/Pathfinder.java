/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.module.modules.world;

import java.math.BigDecimal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.MillisTimer;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public class Pathfinder
extends ModuleListener {
    WalkNodeProcessor walkNodeProcessor = new WalkNodeProcessor();
    PathFinder pathFinder = new PathFinder(this.walkNodeProcessor);
    Path pathEntity = null;
    boolean pathSet = false;
    double destX;
    double destY;
    double destZ;
    MillisTimer timer = new MillisTimer();

    public Pathfinder() {
        super("Pathfinder", Module.Category.WORLD, "Finds a path to a certain point");
    }

    public static Pathfinder getInstance() {
        return (Pathfinder)Wolfram.getWolfram().moduleManager.getModule("pathfinder");
    }

    @Override
    public void onEnable2() {
        this.refresh();
    }

    @EventTarget
    public void onRender(WorldRenderEvent event) {
        if (this.pathEntity != null) {
            RenderUtils.enableRender3D(!this.getSettings().getBoolean("pathfinder_depth"));
            RenderUtils.setColor(GuiManager.getHexMainColor());
            try {
                double lastX = -1.0;
                double lastY = -1.0;
                double lastZ = -1.0;
                GL11.glBegin((int)3);
                int i = this.pathEntity.getCurrentPathLength() - 1;
                while (i >= 0) {
                    PathPoint point = this.pathEntity.getPathPointFromIndex(i);
                    double x = (double)point.xCoord - RenderManager.renderPosX + 0.5;
                    double y = (double)point.yCoord - RenderManager.renderPosY + 0.05;
                    double z = (double)point.zCoord - RenderManager.renderPosZ + 0.5;
                    if (lastY != -1.0 && lastY > y && this.getSettings().getBoolean("pathfinder_depth")) {
                        GL11.glVertex3d((double)x, (double)lastY, (double)z);
                    }
                    if (lastY != -1.0 && lastY < y && this.getSettings().getBoolean("pathfinder_depth")) {
                        GL11.glVertex3d((double)lastX, (double)y, (double)lastZ);
                    }
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                    GL11.glVertex3d((double)x, (double)y, (double)z);
                    --i;
                }
                GL11.glEnd();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            RenderUtils.disableRender3D(!this.getSettings().getBoolean("pathfinder_depth"));
        }
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        int delay = this.getSettings().getInt("pathfinder_delay");
        if (delay != 0 && this.timer.check(delay * 1000)) {
            this.refresh();
            this.timer.reset();
        }
    }

    public void setDestination(double posX, double posY, double posZ) {
        this.pathSet = true;
        this.destX = posX;
        this.destY = posY;
        this.destZ = posZ;
        Wolfram.getWolfram().addChatMessage("Path destination set to " + (int)posX + "|" + (int)posY + "|" + (int)posZ);
    }

    public void clear() {
        this.pathEntity = null;
        this.pathSet = false;
    }

    public void refresh() {
        if (!this.pathSet) {
            Wolfram.getWolfram().addChatMessage("No path set, use the 'help path' command to find out more");
            return;
        }
        Wolfram.getWolfram().addChatMessage("Started searching path...");
        new Thread(){
            long startTime;

            @Override
            public void run() {
                this.startTime = System.currentTimeMillis();
                Pathfinder.this.pathEntity = Pathfinder.this.pathFinder.findPath(WMinecraft.getWorld(), WMinecraft.getPlayer(), Pathfinder.this.destX, Pathfinder.this.destY, Pathfinder.this.destZ, 1000.0f);
                Wolfram.getWolfram().addChatMessage("Found path in " + Pathfinder.this.round((float)(System.currentTimeMillis() - this.startTime) / 1000.0f, 3) + " seconds");
            }
        }.start();
    }

    public BigDecimal round(float f, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        bd = bd.setScale(decimalPlace, 4);
        return bd;
    }
}

