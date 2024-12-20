/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.combat;

import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.AttackEntityEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;
import net.wolframclient.utils.InventoryUtils;

public final class AutoSword
extends ModuleListener {
    public AutoSword() {
        super("AutoSword", Module.Category.COMBAT, "Always use the best sword on attacks");
    }

    @EventTarget
    public void onAttack(AttackEntityEvent event) {
        int bestSwordIndex = InventoryUtils.getBestHotbarSword();
        if (bestSwordIndex == -1) {
            return;
        }
        WMinecraft.getPlayer().inventory.currentItem = bestSwordIndex;
    }
}

