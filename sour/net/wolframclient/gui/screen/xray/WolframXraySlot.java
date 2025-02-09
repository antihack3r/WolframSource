/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.gui.screen.xray;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.wolframclient.Wolfram;
import net.wolframclient.gui.screen.xray.WolframXrayScreen;
import net.wolframclient.module.modules.render.Xray;
import net.wolframclient.utils.AnimationTimer;
import net.wolframclient.utils.RenderUtils;

public class WolframXraySlot
extends GuiSlot {
    private final WolframXrayScreen owner;
    public int currentSlot = -1;

    public WolframXraySlot(WolframXrayScreen owner) {
        this(owner, Minecraft.getMinecraft(), owner.width, owner.height, 32, owner.height - 61, 25);
    }

    public WolframXraySlot(WolframXrayScreen owner, Minecraft mcIn, int width, int height, int top, int bottom, int slotHeight) {
        super(mcIn, width, height, top, bottom, slotHeight);
        this.owner = owner;
    }

    public boolean currentSlotInBounds() {
        return this.currentSlot != -1 && this.currentSlot < this.owner.blockList.size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        int i = slotIndex;
        int j = 0;
        for (Object[] blockData : this.owner.blockList) {
            String s = (String)blockData[1];
            if (s.toLowerCase().contains(this.owner.search.getText().toLowerCase())) {
                ++j;
            } else {
                ++i;
            }
            if (j == slotIndex + 1) break;
        }
        this.currentSlot = i;
        if (isDoubleClick) {
            Block block = Block.getBlockById((Integer)this.owner.blockList.get(this.currentSlot)[0]);
            if (Xray.getInstance().isXrayBlock(block)) {
                Wolfram.getWolfram().storageManager.xrayBlocks.remove(new Integer(Block.getIdFromBlock(block)).toString());
            } else {
                Wolfram.getWolfram().storageManager.xrayBlocks.add(new Integer(Block.getIdFromBlock(block)).toString());
            }
            Wolfram.getWolfram().storageManager.xrayBlocks.save();
        }
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.currentSlot;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int slotIndex, int left, int top, int height, int mouseX, int mouseY, float partialTicks) {
        String blockName = (String)this.owner.blockList.get(slotIndex)[1];
        this.renderBlock(Block.getBlockById((Integer)this.owner.blockList.get(slotIndex)[0]), left + 1, top + 2);
        this.owner.drawString(this.mc.fontRendererObj, blockName, left + 50, top + 1, -263693982);
        this.owner.drawString(this.mc.fontRendererObj, Xray.getInstance().isXrayBlock(Block.getBlockById((Integer)this.owner.blockList.get(slotIndex)[0])) ? "Enabled" : "Disabled", left + 50, top + 11, Xray.getInstance().isXrayBlock(Block.getBlockById((Integer)this.owner.blockList.get(slotIndex)[0])) ? 65280 : 0xFF0000);
    }

    private void renderBlock(Block block, int x, int y) {
        GlStateManager.enableBlend();
        TextureAtlasSprite var26 = this.getBlockTexture(block);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.drawTexturedQuad(x, y, var26, 16, 16);
        GlStateManager.disableBlend();
    }

    private TextureAtlasSprite getBlockTexture(Block p_175371_1_) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(p_175371_1_.getDefaultState());
    }

    private void drawTexturedQuad(int x, int y, TextureAtlasSprite textureSprite, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x + 0, y + height, 0.0).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        vertexbuffer.pos(x + width, y + height, 0.0).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        vertexbuffer.pos(x + width, y + 0, 0.0).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        vertexbuffer.pos(x + 0, y + 0, 0.0).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    @Override
    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int p_148120_3_, int p_148120_4_, float partialTicks) {
        int var5 = this.getSize();
        int drawedIndex = 0;
        int var8 = 0;
        while (var8 < var5) {
            String s = (String)this.owner.blockList.get(var8)[1];
            if (s.toLowerCase().contains(this.owner.search.getText().toLowerCase())) {
                int var9 = p_148120_2_ + drawedIndex * this.slotHeight + this.headerPadding;
                int var10 = this.slotHeight - 4;
                if (var9 > this.bottom || var9 + var10 < this.top) {
                    this.updateItemPos(var8, p_148120_1_, var9, partialTicks);
                }
                if (this.showSelectionBox) {
                    int var11 = this.left + this.width / 2 - this.getListWidth() / 2;
                    int var12 = this.left + this.width / 2 + this.getListWidth() / 2;
                    AnimationTimer anim = (AnimationTimer)this.timers.get(var8);
                    if (anim == null) {
                        anim = new AnimationTimer(20);
                        this.timers.put(var8, anim);
                    }
                    anim.update(this.isSelected(var8));
                    if (anim.getValue() > 0.0) {
                        RenderUtils.drawLine2D(var11, var9 - 2, (double)var11 + (double)(var12 - var11) * anim.getValue(), var9 - 2, 1.0f, -263693982);
                        RenderUtils.drawLine2D((double)var12 - (double)(var12 - var11) * anim.getValue(), var9 - 2 + var10 + 4, var12, var9 - 2 + var10 + 4, 1.0f, -263693982);
                    }
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                }
                this.drawSlot(var8, p_148120_1_, var9, var10, p_148120_3_, p_148120_4_, partialTicks);
                ++drawedIndex;
            }
            ++var8;
        }
    }

    @Override
    protected int getContentHeight() {
        int size = 0;
        for (Object[] blockData : this.owner.blockList) {
            String s = (String)blockData[1];
            if (!s.toLowerCase().contains(this.owner.search.getText().toLowerCase())) continue;
            ++size;
        }
        return size * this.slotHeight + this.headerPadding;
    }

    @Override
    protected int getSize() {
        return this.owner.blockList.size();
    }
}

