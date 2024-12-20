/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.wolframclient.Wolfram;

public class GuiChest
extends GuiContainer {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final IInventory upperChestInventory;
    private final IInventory lowerChestInventory;
    private final int inventoryRows;
    private Thread stealThread;
    private Thread storeThread;
    private boolean stealing;
    private boolean storing;

    public GuiChest(IInventory upperInv, IInventory lowerInv) {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().player));
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(-69, this.width / 2 + this.xSize / 2 - 58, (this.height - this.ySize) / 2 + 5, 50, 10, "Steal"));
        this.buttonList.add(new GuiButton(-70, this.width / 2 + this.xSize / 2 - 110, (this.height - this.ySize) / 2 + 5, 50, 10, "Store"));
        super.initGui();
        if (Wolfram.getWolfram().moduleManager.isEnabled("autosteal")) {
            try {
                this.actionPerformed((GuiButton)this.buttonList.get(0));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        this.stealing = false;
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        block7: {
            block6: {
                super.actionPerformed(button);
                if (button.id != -69) break block6;
                final GuiChest this_ = this;
                if (!this.stealing) {
                    this.stealThread = new Thread(){

                        @Override
                        public void run() {
                            int size = GuiChest.this.lowerChestInventory.getSizeInventory();
                            long time = System.currentTimeMillis();
                            long newTime = 0L;
                            int speed = 20;
                            int i = 0;
                            boolean second = false;
                            while (i < size) {
                                if (((GuiChest)GuiChest.this).mc.currentScreen != this_ && ((GuiChest)GuiChest.this).mc.currentScreen != null || !GuiChest.this.stealing) break;
                                if (GuiChest.this.lowerChestInventory.getStackInSlot(i) == null) {
                                    if (++i < size || second) continue;
                                    second = true;
                                    speed = 30;
                                    i = 0;
                                    continue;
                                }
                                newTime = System.currentTimeMillis();
                                if (newTime - time < (long)speed) continue;
                                time = newTime;
                                Minecraft.getMinecraft().playerController.windowClick(GuiChest.this.inventorySlots.windowId, i, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().player);
                            }
                            Minecraft.getMinecraft().displayGuiScreen(null);
                            GuiChest.this.stealing = false;
                        }
                    };
                    this.stealThread.start();
                    this.stealing = true;
                } else {
                    this.stealing = false;
                    int size = this.lowerChestInventory.getSizeInventory();
                    int i = 0;
                    while (i < size) {
                        Minecraft.getMinecraft().playerController.windowClick(this.inventorySlots.windowId, i, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().player);
                        ++i;
                    }
                }
                break block7;
            }
            if (button.id != -70) break block7;
            if (!this.storing) {
                this.storeThread = new Thread(){

                    @Override
                    public void run() {
                        int size = GuiChest.this.lowerChestInventory.getSizeInventory();
                        long time = System.currentTimeMillis();
                        long newTime = 0L;
                        int i = 0;
                        while (i < 36) {
                            if (!GuiChest.this.storing) break;
                            if (GuiChest.this.inventorySlots.getSlot(i + size).getStack() == null) {
                                ++i;
                                continue;
                            }
                            newTime = System.currentTimeMillis();
                            if (newTime - time < 20L) continue;
                            time = newTime;
                            Minecraft.getMinecraft().playerController.windowClick(GuiChest.this.inventorySlots.windowId, i + size, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().player);
                        }
                        GuiChest.this.storing = false;
                    }
                };
                this.storeThread.start();
                this.storing = true;
            } else {
                this.storing = false;
                int size = this.lowerChestInventory.getSizeInventory();
                int i = 0;
                while (i < 36) {
                    Minecraft.getMinecraft().playerController.windowClick(this.inventorySlots.windowId, i + size, 0, ClickType.QUICK_MOVE, Minecraft.getMinecraft().player);
                    ++i;
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.func_191948_b(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 0x404040);
        this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}

