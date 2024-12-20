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

public final class ClickAura
extends ModuleListener {
    public ClickAura() {
        super("ClickAura", Module.Category.COMBAT, "Always hit your enemy");
    }

    @EventTarget
    public void onLeftClick(LeftClickEvent event) {
        EntityLivingBase entity = EntityHelper.getClosestEntity();
        if (WMinecraft.getPlayer().getDistanceToEntity(entity) > this.getSettings().getFloat("reach")) {
            return;
        }
        WPlayerController.attackAndSwing(entity);
    }
}

