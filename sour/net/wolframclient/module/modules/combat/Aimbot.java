/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.RenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.EntityHelper;

public final class Aimbot
extends ModuleListener {
    public Aimbot() {
        super("Aimbot", Module.Category.COMBAT, "Automatically aims at the closest entity");
    }

    @EventTarget
    public void onUpdate(RenderEvent event) {
        EntityLivingBase entity = EntityHelper.getClosestEntity(false);
        if (entity == null) {
            return;
        }
        double distance = WMinecraft.getPlayer().getDistanceToEntity(entity);
        if (distance > (double)this.getSettings().getFloat("aimbot_range")) {
            return;
        }
        EntityHelper.faceEntity(entity);
    }
}

