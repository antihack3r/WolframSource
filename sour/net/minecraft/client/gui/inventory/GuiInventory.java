/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.wolframclient.Wolfram;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiInventory
extends InventoryEffectRenderer
implements IRecipeShownListener {
    private float oldMouseX;
    private float oldMouseY;
    private GuiButtonImage field_192048_z;
    private final GuiRecipeBook field_192045_A = new GuiRecipeBook();
    private boolean field_192046_B;
    private boolean field_194031_B;

    public GuiInventory(EntityPlayer player) {
        super(player.inventoryContainer);
        this.allowUserInput = true;
    }

    @Override
    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }
        if (!Wolfram.getWolfram().moduleManager.isEnabled("invwalk") && Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    @Override
    public void updateScreen() {
        if (this.mc.playerController.isInCreativeMode()) {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
        }
        this.field_192045_A.func_193957_d();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        if (this.mc.playerController.isInCreativeMode()) {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
        } else {
            super.initGui();
        }
        this.field_192046_B = this.width < 379;
        this.field_192045_A.func_191856_a(this.width, this.height, this.mc, this.field_192046_B, this.inventorySlots, ((ContainerPlayer)this.inventorySlots).craftMatrix);
        this.guiLeft = this.field_192045_A.func_193011_a(this.field_192046_B, this.width, this.xSize);
        this.field_192048_z = new GuiButtonImage(10, this.guiLeft + 104, this.height / 2 - 22, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
        this.buttonList.add(this.field_192048_z);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 97, 8, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        boolean bl = this.hasActivePotionEffects = !this.field_192045_A.func_191878_b();
        if (this.field_192045_A.func_191878_b() && this.field_192046_B) {
            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            this.field_192045_A.func_191861_a(mouseX, mouseY, partialTicks);
        } else {
            this.field_192045_A.func_191861_a(mouseX, mouseY, partialTicks);
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.field_192045_A.func_191864_a(this.guiLeft, this.guiTop, false, partialTicks);
        }
        this.func_191948_b(mouseX, mouseY);
        this.field_192045_A.func_191876_c(this.guiLeft, this.guiTop, mouseX, mouseY);
        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.mc.player);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 50.0f);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        ent.renderYawOffset = (float)Math.atan(mouseX / 40.0f) * 20.0f;
        ent.rotationYaw = (float)Math.atan(mouseX / 40.0f) * 40.0f;
        ent.rotationPitch = -((float)Math.atan(mouseY / 40.0f)) * 20.0f;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return (!this.field_192046_B || !this.field_192045_A.func_191878_b()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!(this.field_192045_A.func_191862_a(mouseX, mouseY, mouseButton) || this.field_192046_B && this.field_192045_A.func_191878_b())) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.field_194031_B) {
            this.field_194031_B = false;
        } else {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected boolean func_193983_c(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_) {
        boolean flag;
        boolean bl = flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + this.xSize || p_193983_2_ >= p_193983_4_ + this.ySize;
        return this.field_192045_A.func_193955_c(p_193983_1_, p_193983_2_, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 10) {
            this.field_192045_A.func_193014_a(this.field_192046_B, ((ContainerPlayer)this.inventorySlots).craftMatrix);
            this.field_192045_A.func_191866_a();
            this.guiLeft = this.field_192045_A.func_193011_a(this.field_192046_B, this.width, this.xSize);
            this.field_192048_z.func_191746_c(this.guiLeft + 104, this.height / 2 - 22);
            this.field_194031_B = true;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.field_192045_A.func_191859_a(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.field_192045_A.func_191874_a(slotIn);
    }

    @Override
    public void func_192043_J_() {
        this.field_192045_A.func_193948_e();
    }

    @Override
    public void onGuiClosed() {
        this.field_192045_A.func_191871_c();
        super.onGuiClosed();
    }
}

