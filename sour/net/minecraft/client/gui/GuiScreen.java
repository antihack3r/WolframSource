/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.wolframclient.utils.RenderUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiScreen
extends Gui
implements GuiYesNoCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet((Object[])new String[]{"http", "https"});
    private static final Splitter NEWLINE_SPLITTER = Splitter.on((char)'\n');
    protected Minecraft mc;
    protected RenderItem itemRender;
    public int width;
    public int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;
    protected FontRenderer fontRendererObj;
    protected GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private URI clickedLinkURI;
    private boolean field_193977_u;
    private static final ResourceLocation bg = new ResourceLocation("wolfram/bg.jpg");
    private static final ResourceLocation bgBlurred = new ResourceLocation("wolfram/bg-blur.jpg");

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int i = 0;
        while (i < this.buttonList.size()) {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY, partialTicks);
            ++i;
        }
        int j = 0;
        while (j < this.labelList.size()) {
            this.labelList.get(j).drawLabel(this.mc, mouseX, mouseY);
            ++j;
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    protected <T extends GuiButton> T addButton(T p_189646_1_) {
        this.buttonList.add(p_189646_1_);
        return p_189646_1_;
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "";
    }

    public static void setClipboardString(String copyText) {
        if (!StringUtils.isEmpty((CharSequence)copyText)) {
            try {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y) {
        this.drawHoveringText(this.func_191927_a(stack), x, y);
    }

    public List<String> func_191927_a(ItemStack p_191927_1_) {
        List<String> list = p_191927_1_.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        int i = 0;
        while (i < list.size()) {
            if (i == 0) {
                list.set(i, (Object)((Object)p_191927_1_.getRarity().rarityColor) + list.get(i));
            } else {
                list.set(i, (Object)((Object)TextFormatting.GRAY) + list.get(i));
            }
            ++i;
        }
        return list;
    }

    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
    }

    public void func_193975_a(boolean p_193975_1_) {
        this.field_193977_u = p_193975_1_;
    }

    public boolean func_193976_p() {
        return this.field_193977_u;
    }

    public void drawHoveringText(List<String> textLines, int x, int y) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;
            for (String s : textLines) {
                int j = this.fontRendererObj.getStringWidth(s);
                if (j <= i) continue;
                i = j;
            }
            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;
            if (textLines.size() > 1) {
                k += 2 + (textLines.size() - 1) * 10;
            }
            if (l1 + i > this.width) {
                l1 -= 28 + i;
            }
            if (i2 + k + 6 > this.height) {
                i2 = this.height - k - 6;
            }
            this.zLevel = 300.0f;
            this.itemRender.zLevel = 300.0f;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            int i1 = 0x505000FF;
            int j1 = 1344798847;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 0x505000FF, 1344798847);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 0x505000FF, 1344798847);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 0x505000FF, 0x505000FF);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
            int k1 = 0;
            while (k1 < textLines.size()) {
                String s1 = textLines.get(k1);
                this.fontRendererObj.drawStringWithShadow(s1, l1, i2, -1);
                if (k1 == 0) {
                    i2 += 2;
                }
                i2 += 10;
                ++k1;
            }
            this.zLevel = 0.0f;
            this.itemRender.zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(ITextComponent component, int x, int y) {
        if (component != null && component.getStyle().getHoverEvent() != null) {
            HoverEvent hoverevent = component.getStyle().getHoverEvent();
            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                ItemStack itemstack = ItemStack.EMPTY;
                try {
                    NBTTagCompound nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                    if (nbtbase instanceof NBTTagCompound) {
                        itemstack = new ItemStack(nbtbase);
                    }
                }
                catch (NBTException nbtbase) {
                    // empty catch block
                }
                if (itemstack.func_190926_b()) {
                    this.drawCreativeTabHoveringText((Object)((Object)TextFormatting.RED) + "Invalid Item!", x, y);
                } else {
                    this.renderToolTip(itemstack, x, y);
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                if (this.mc.gameSettings.advancedItemTooltips) {
                    try {
                        NBTTagCompound nbttagcompound = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                        ArrayList list = Lists.newArrayList();
                        list.add(nbttagcompound.getString("name"));
                        if (nbttagcompound.hasKey("type", 8)) {
                            String s = nbttagcompound.getString("type");
                            list.add("Type: " + s);
                        }
                        list.add(nbttagcompound.getString("id"));
                        this.drawHoveringText(list, x, y);
                    }
                    catch (NBTException var8) {
                        this.drawCreativeTabHoveringText((Object)((Object)TextFormatting.RED) + "Invalid Entity!", x, y);
                    }
                }
            } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                this.drawHoveringText(this.mc.fontRendererObj.listFormattedStringToWidth(hoverevent.getValue().getFormattedText(), Math.max(this.width / 2, 200)), x, y);
            }
            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite) {
    }

    public boolean handleComponentClick(ITextComponent component) {
        if (component == null) {
            return false;
        }
        ClickEvent clickevent = component.getStyle().getClickEvent();
        if (GuiScreen.isShiftKeyDown()) {
            if (component.getStyle().getInsertion() != null) {
                this.setText(component.getStyle().getInsertion(), false);
            }
        } else if (clickevent != null) {
            block19: {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }
                    try {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();
                        if (s == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }
                        if (!PROTOCOLS.contains(s.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase(Locale.ROOT));
                        }
                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink((GuiYesNoCallback)this, clickevent.getValue(), 31102009, false));
                            break block19;
                        }
                        this.openWebLink(uri);
                    }
                    catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for {}", (Object)clickevent, (Object)urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = new File(clickevent.getValue()).toURI();
                    this.openWebLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickevent.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickevent);
                }
            }
            return true;
        }
        return false;
    }

    public void sendChatMessage(String msg) {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat) {
        if (addToChat) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }
        this.mc.player.sendChatMessage(msg);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int i = 0;
            while (i < this.buttonList.size()) {
                GuiButton guibutton = this.buttonList.get(i);
                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
                ++i;
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    protected void actionPerformed(GuiButton button) throws IOException {
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    public void setGuiSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void initGui() {
    }

    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }
        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }
            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        } else if (k != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }
            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    public void handleKeyboardInput() throws IOException {
        char c0 = Keyboard.getEventCharacter();
        if (Keyboard.getEventKey() == 0 && c0 >= ' ' || Keyboard.getEventKeyState()) {
            this.keyTyped(c0, Keyboard.getEventKey());
        }
        this.mc.dispatchKeypresses();
    }

    public void updateScreen() {
    }

    public void onGuiClosed() {
    }

    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint) {
        if (this.mc.world != null) {
            RenderUtils.drawRect(0.0f, 0.0f, this.width, this.height, -1593835521);
        } else {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint) {
        ResourceLocation bg = this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiMainMenu ? GuiScreen.bg : bgBlurred;
        this.mc.getTextureManager().bindTexture(bg);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0.0f, 0.0f, 3840, 2160, this.width, this.height, 3840.0f, 2160.0f);
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (id == 31102009) {
            if (result) {
                this.openWebLink(this.clickedLinkURI);
            }
            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url) {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            oclass.getMethod("browse", URI.class).invoke(object, url);
        }
        catch (Throwable throwable1) {
            Throwable throwable = throwable1.getCause();
            LOGGER.error("Couldn't open link: {}", (Object)(throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
        }
    }

    public static boolean isCtrlKeyDown() {
        if (Minecraft.IS_RUNNING_ON_MAC) {
            return Keyboard.isKeyDown((int)219) || Keyboard.isKeyDown((int)220);
        }
        return Keyboard.isKeyDown((int)29) || Keyboard.isKeyDown((int)157);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown((int)42) || Keyboard.isKeyDown((int)54);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown((int)56) || Keyboard.isKeyDown((int)184);
    }

    public static boolean isKeyComboCtrlX(int keyID) {
        return keyID == 45 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID) {
        return keyID == 47 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID) {
        return keyID == 46 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID) {
        return keyID == 30 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int w, int h) {
        this.setWorldAndResolution(mcIn, w, h);
    }
}

