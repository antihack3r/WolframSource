/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.compatibility.WPlayerController;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.LeftClickEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.EntityHelper;

public final class ClickAimbot
extends ModuleListener {
    public ClickAimbot() {
        super("ClickAimbot", Module.Category.COMBAT, "Face your enemy after clicking");
    }

    @EventTarget
    public void onClick(LeftClickEvent event) {
        EntityLivingBase entity = EntityHelper.getClosestEntity();
        if (WMinecraft.getPlayer().getDistanceToEntity(entity) > this.getSettings().getFloat("reach")) {
            return;
        }
        EntityHelper.faceEntity(entity, 180.0f);
        WPlayerController.attackAndSwing(entity);
    }
}

