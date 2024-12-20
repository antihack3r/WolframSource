/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.PlayerDamageBlockEvent;
import net.wolframclient.module.Module;

public class FastBreak
extends Module
implements Listener {
    public FastBreak() {
        super("FastBreak", Module.Category.PLAYER, "Break faster");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget
    public void onBlockBreaking(PlayerDamageBlockEvent event) {
        IBlockState state = WMinecraft.getWorld().getBlockState(event.getPos());
        if (state.getMaterial() != Material.AIR) {
            event.setProgress(event.getProgress() + state.getPlayerRelativeBlockHardness(WMinecraft.getPlayer(), WMinecraft.getWorld(), event.getPos()) * this.getSettings().getFloat("fastbreak_speed"));
        }
    }
}

