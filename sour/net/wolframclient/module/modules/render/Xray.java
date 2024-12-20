/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.render;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WItem;
import net.wolframclient.module.Module;

public final class Xray
extends Module {
    public final ArrayList<Object[]> blockList = new ArrayList();

    public Xray() {
        super("Xray", Module.Category.RENDER, "Shows only selected blocks");
        for (Block block : Block.REGISTRY) {
            String name = this.getBlockName(block);
            if (name == null) continue;
            Object[] blockData = new Object[]{Block.getIdFromBlock(block), name};
            this.blockList.add(blockData);
        }
    }

    @Override
    public void onEnable() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    public String getBlockName(Block block) {
        String name;
        if (block instanceof BlockAir) {
            return null;
        }
        Item item = Item.getItemFromBlock(block);
        ItemStack stack = WItem.isNullOrEmpty(item) ? null : new ItemStack(Item.getByNameOrId(block.unlocalizedName), 1, 0);
        String string = name = stack == null ? block.getLocalizedName() : item.getItemStackDisplayName(stack);
        if (name.length() > 5 && name.startsWith("tile.")) {
            return block.unlocalizedName;
        }
        return name;
    }

    public boolean isXrayBlock(Block block) {
        return Wolfram.getWolfram().storageManager.xrayBlocks.contains(Integer.toString(Block.getIdFromBlock(block)));
    }

    public static Xray getInstance() {
        return (Xray)Wolfram.getWolfram().moduleManager.getModule("xray");
    }
}

