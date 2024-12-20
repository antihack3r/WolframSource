/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;

public final class WSoundEvents {
    public static boolean isBobberSplash(SPacketSoundEffect soundEffect) {
        return SoundEvents.ENTITY_BOBBER_SPLASH.equals(soundEffect.getSound());
    }
}

