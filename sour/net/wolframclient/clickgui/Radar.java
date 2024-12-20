/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.relations.RelationManager;
import net.wolframclient.utils.EntityFakePlayer;
import net.wolframclient.utils.RenderUtils;

public final class Radar
extends Component {
    public Radar(Window window, int id, int offX, int offY, String title, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.width = 100;
        this.height = 100;
        this.type = "Radar";
        this.editable = false;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        double middleX = (double)this.x + (double)this.width / 2.0;
        double middleY = (double)this.y + (double)this.height / 2.0;
        RenderUtils.drawRect(this.x, this.y, this.width, this.height, WolframGui.getBackgroundColor());
        RenderUtils.drawPoint(middleX, middleY, 4.0f, GuiManager.getHexMainColor());
        for (Entity entity : WMinecraft.getWorld().loadedEntityList) {
            int color;
            if (!(entity instanceof EntityLivingBase) || entity instanceof EntityFakePlayer) continue;
            EntityPlayerSP player = WMinecraft.getPlayer();
            double diffX = entity.posX - player.posX;
            double diffZ = entity.posZ - player.posZ;
            double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
            double neededRotation = Math.toDegrees(Math.atan2(diffZ, diffX));
            double angle = Math.toRadians((double)player.rotationYaw - neededRotation - 90.0);
            double renderX = Math.sin(angle) * distance;
            double renderY = Math.cos(angle) * distance;
            if (Math.abs(renderX) > (double)this.width / 2.0 || Math.abs(renderY) > (double)this.height / 2.0) continue;
            if (entity instanceof EntityLiving) {
                color = entity instanceof EntityMob ? 0xFF7070 : (entity instanceof EntityAnimal ? 0x70FF70 : (entity instanceof EntityAmbientCreature ? 0x7070FF : 0xFF70FF));
            } else {
                EntityPlayer otherPlayer;
                if (!(entity instanceof EntityPlayer) || (otherPlayer = (EntityPlayer)entity) == player) continue;
                RelationManager rm = Wolfram.getWolfram().relationManager;
                color = rm.isFriend(otherPlayer) ? 65280 : (rm.isEnemy(otherPlayer) ? 0xFF0000 : 28927);
            }
            RenderUtils.drawPoint(middleX + renderX, middleY + renderY, 2.0f, color);
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }
}

