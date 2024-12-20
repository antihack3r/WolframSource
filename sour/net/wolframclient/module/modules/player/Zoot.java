/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.player;

import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wolframclient.compatibility.WConnection;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.events.NetworkManagerPacketSendEvent;
import net.wolframclient.event.events.PlayerUpdateEvent;
import net.wolframclient.module.Module;
import net.wolframclient.module.ModuleListener;

public class Zoot
extends ModuleListener {
    public static boolean stopped;

    public Zoot() {
        super("Zoot", Module.Category.PLAYER, "Gets rid of bad potion effects and keeps the good ones");
    }

    @EventTarget
    public void onUpdate(PlayerUpdateEvent event) {
        if (this.shouldSpam() && (this.debuffsActive() || !this.buffsActive() && this.regenActive() && !this.isPlayerAtFullHealth())) {
            this.spamPackets();
        }
        stopped = this.shouldStop() && (this.buffsActive() || this.regenActive() && this.isPlayerAtFullHealth());
    }

    @EventTarget
    public void onPacketSend(NetworkManagerPacketSendEvent event) {
        if (this.shouldStop() && this.shouldKeepPacket(event.getPacket()) && (this.buffsActive() || this.regenActive() && this.isPlayerAtFullHealth())) {
            event.setCancelled(true);
        }
    }

    private boolean debuffsActive() {
        boolean result = false;
        if (WMinecraft.getPlayer().isBurning()) {
            result = true;
        }
        if (WMinecraft.getPlayer().isPotionActive(MobEffects.BLINDNESS) || WMinecraft.getPlayer().isPotionActive(MobEffects.NAUSEA) || WMinecraft.getPlayer().isPotionActive(MobEffects.MINING_FATIGUE) || WMinecraft.getPlayer().isPotionActive(MobEffects.HUNGER) || WMinecraft.getPlayer().isPotionActive(MobEffects.SLOWNESS) || WMinecraft.getPlayer().isPotionActive(MobEffects.WEAKNESS) || WMinecraft.getPlayer().isPotionActive(MobEffects.WITHER) || WMinecraft.getPlayer().isPotionActive(MobEffects.POISON)) {
            result = true;
        }
        return result;
    }

    private boolean shouldSpam() {
        return WMinecraft.getPlayer().isOnLadder() || WMinecraft.getPlayer().isInWater() || WMinecraft.getPlayer().onGround;
    }

    private boolean buffsActive() {
        boolean result = false;
        if (WMinecraft.getPlayer().isPotionActive(MobEffects.ABSORPTION) || WMinecraft.getPlayer().isPotionActive(MobEffects.STRENGTH) || WMinecraft.getPlayer().isPotionActive(MobEffects.HASTE) || WMinecraft.getPlayer().isPotionActive(MobEffects.FIRE_RESISTANCE) || WMinecraft.getPlayer().isPotionActive(MobEffects.HEALTH_BOOST) || WMinecraft.getPlayer().isPotionActive(MobEffects.INVISIBILITY) || WMinecraft.getPlayer().isPotionActive(MobEffects.JUMP_BOOST) || WMinecraft.getPlayer().isPotionActive(MobEffects.NIGHT_VISION) || WMinecraft.getPlayer().isPotionActive(MobEffects.RESISTANCE) || WMinecraft.getPlayer().isPotionActive(MobEffects.SATURATION) || WMinecraft.getPlayer().isPotionActive(MobEffects.WATER_BREATHING) || WMinecraft.getPlayer().isPotionActive(MobEffects.SPEED)) {
            result = true;
        }
        return result;
    }

    private boolean regenActive() {
        return WMinecraft.getPlayer().isPotionActive(MobEffects.REGENERATION);
    }

    private boolean shouldStop() {
        return !WMinecraft.getPlayer().isOnLadder() && !WMinecraft.getPlayer().isInWater() && WMinecraft.getPlayer().onGround && WMinecraft.getPlayer().motionX == 0.0 && WMinecraft.getPlayer().motionZ == 0.0;
    }

    private boolean isPlayerAtFullHealth() {
        return WMinecraft.getPlayer().getHealth() == 20.0f;
    }

    private void spamPackets() {
        new Thread(){

            @Override
            public void run() {
                int i = 0;
                while (i < 50) {
                    WConnection.sendPacket(new CPacketPlayer());
                    i = (byte)(i + 1);
                }
            }
        }.start();
    }

    private boolean shouldKeepPacket(Packet packet) {
        return packet instanceof CPacketPlayer;
    }
}

