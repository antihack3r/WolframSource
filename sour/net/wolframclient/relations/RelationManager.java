/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package net.wolframclient.relations;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.wolframclient.Wolfram;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.AttackEntityEvent;
import net.wolframclient.event.events.NametagRenderEvent;

public final class RelationManager
implements Listener {
    public RelationManager() {
        registry.registerListener(this);
    }

    public boolean isFriend(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return this.isFriend((EntityPlayer)entity);
        }
        return false;
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getGameProfile().getName());
    }

    public boolean isFriend(String name) {
        return Wolfram.getWolfram().storageManager.friends.contains(name.toLowerCase());
    }

    public void addFriend(String name) {
        if (this.isFriend(name)) {
            return;
        }
        Wolfram.getWolfram().storageManager.friends.add(name.toLowerCase());
        Wolfram.getWolfram().addChatMessage("Added friend " + name);
    }

    public void removeFriend(String name) {
        if (!this.isFriend(name)) {
            Wolfram.getWolfram().addChatMessage("Failed to remove " + name);
            return;
        }
        Wolfram.getWolfram().storageManager.friends.remove(name);
        Wolfram.getWolfram().addChatMessage("Removed friend " + name);
    }

    public void listFriends() {
        Wolfram.getWolfram().addChatMessage("Friends: " + String.join((CharSequence)", ", Wolfram.getWolfram().storageManager.friends));
    }

    public boolean isEnemy(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return this.isEnemy((EntityPlayer)entity);
        }
        return false;
    }

    public boolean isEnemy(EntityPlayer player) {
        return this.isEnemy(player.getGameProfile().getName());
    }

    public boolean isEnemy(String name) {
        return Wolfram.getWolfram().storageManager.enemies.contains(name.toLowerCase());
    }

    public void addEnemy(String name) {
        Wolfram.getWolfram().storageManager.enemies.add(name);
        Wolfram.getWolfram().addChatMessage("Added enemy " + name);
    }

    public void removeEnemy(String name) {
        if (!this.isEnemy(name)) {
            Wolfram.getWolfram().addChatMessage("Failed to remove enemy " + name);
            return;
        }
        Wolfram.getWolfram().storageManager.enemies.remove(name);
        Wolfram.getWolfram().addChatMessage("Removed enemy " + name);
    }

    public void listEnemies() {
        Wolfram.getWolfram().addChatMessage("Enemies: " + String.join((CharSequence)", ", Wolfram.getWolfram().storageManager.enemies));
    }

    @EventTarget
    public void onAttack(AttackEntityEvent event) {
        if (!(event.getTarget() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.getTarget();
        if (this.isFriend(player)) {
            event.setCancelled(true);
        }
    }

    @EventTarget(priority=1)
    public void onNametag(NametagRenderEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }
        if (this.isFriend((EntityPlayer)event.getEntity())) {
            event.setName(ChatFormatting.GREEN + event.getName());
        }
    }

    public void clearFriends() {
        Wolfram.getWolfram().storageManager.friends.clear();
        Wolfram.getWolfram().addChatMessage("Cleared friend list");
    }

    public void clearEnemies() {
        Wolfram.getWolfram().storageManager.enemies.clear();
        Wolfram.getWolfram().addChatMessage("Cleared enemy list");
    }
}

