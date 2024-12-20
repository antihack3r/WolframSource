/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.others;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.wolframclient.Wolfram;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.MiddleClickEvent;
import net.wolframclient.module.Module;

public class MiddleClickFriends
extends Module
implements Listener {
    public MiddleClickFriends() {
        super("MiddleClickFriends", Module.Category.OTHER, "Add friends by middle-clicking them");
    }

    @Override
    public void onEnable() {
        if (Wolfram.getWolfram().moduleManager.isEnabled("middleclickenemies")) {
            Wolfram.getWolfram().moduleManager.getModule("middleclickenemies").toggleModule();
        }
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget
    public void onMouseClick(MiddleClickEvent event) {
        if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().objectMouseOver.entityHit;
            if (Wolfram.getWolfram().relationManager.isFriend(player)) {
                Wolfram.getWolfram().relationManager.removeFriend(player.getGameProfile().getName().toLowerCase());
            } else {
                Wolfram.getWolfram().relationManager.addFriend(player.getGameProfile().getName().toLowerCase());
            }
        }
    }
}

