/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.render;

import net.minecraft.entity.item.EntityItem;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.WorldRenderEvent;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;

public class ItemLabels
extends Module
implements Listener {
    public ItemLabels() {
        super("ItemLabels", Module.Category.RENDER, "Draws a nametag over dropped items");
    }

    @Override
    public void onEnable() {
        registry.registerListener(this);
    }

    @Override
    public void onDisable() {
        registry.unregisterListener(this);
    }

    @EventTarget(priority=2)
    public void onRenderWorld(WorldRenderEvent event) {
        for (Object object : WMinecraft.getWorld().loadedEntityList) {
            if (!(object instanceof EntityItem)) continue;
            EntityItem item = (EntityItem)object;
            String name = String.valueOf(item.getEntityItem().stackSize) + "x " + item.getEntityItem().getDisplayName();
            RenderUtils.renderNameTag(name, item, 1, 3.0, 0xFFFFFF);
        }
    }
}

