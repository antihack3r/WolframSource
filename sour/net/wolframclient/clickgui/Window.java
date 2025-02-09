/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package net.wolframclient.clickgui;

import java.util.ArrayList;
import java.util.Random;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.TabGui;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.AnimationTimer;
import net.wolframclient.utils.MathUtils;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.input.Mouse;

public final class Window {
    private static final Random random = new Random();
    private String title;
    private int id;
    private final ArrayList<Component> children = new ArrayList();
    private boolean pinnable;
    private boolean pinned;
    private boolean open;
    private boolean enabled;
    private int x;
    private int y;
    private int width;
    private int height;
    private int grabX;
    private int grabY;
    private boolean dragging;
    private int scrollY;
    private float scrollAmount;
    private boolean scrollbarEnabled;
    private int componentsHeight;
    private boolean isScrolling;
    private int toScrollY;
    private final AnimationTimer closeAnim = new AnimationTimer(20);
    private final AnimationTimer pinAnim = new AnimationTimer(20);
    private boolean pinHover;
    private boolean openHover;
    private boolean wasMousePressed;

    public Window(String title, int x, int y) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.id = random.nextInt();
        this.width = 80;
        this.height = 18;
    }

    public void repositionComponents() {
        int maxWidth = 0;
        int currentY = 18;
        for (Component c : this.children) {
            c.offY = currentY;
            currentY += c.height;
            maxWidth = Math.max(maxWidth, c.width);
        }
        this.width = Math.max(80, maxWidth);
        for (Component c : this.children) {
            c.width = this.width;
        }
        this.height = Math.min(180, currentY);
        this.componentsHeight = currentY - 18;
    }

    public void update(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = mouseX - this.grabX;
            this.y = mouseY - this.grabY;
        }
        this.scrollbarEnabled = this.componentsHeight + 18 > 180;
        for (Component c : this.children) {
            c.update(mouseX, mouseY);
        }
        this.closeAnim.update(this.open);
        this.pinAnim.update(this.pinned);
        if (this.scrollbarEnabled && this.isScrolling) {
            this.scrollAmount = MathUtils.map(mouseY - this.y, 46.0f, 152.0f, 0.0f, 1.0f);
            if (this.scrollAmount > 1.0f) {
                this.scrollAmount = 1.0f;
            }
            if (this.scrollAmount < 0.0f) {
                this.scrollAmount = 0.0f;
            }
            this.toScrollY = this.scrollY = (int)(this.scrollAmount * (float)(this.componentsHeight - 162));
        }
        this.scrollY = Math.abs(this.toScrollY - this.scrollY) < 4 ? this.toScrollY : (this.scrollY > this.toScrollY ? (this.scrollY -= 4) : (this.scrollY += 4));
        this.scrollAmount = (float)this.scrollY / (float)(this.componentsHeight - 162);
    }

    public void render(int mouseX, int mouseY) {
        int height2 = 18;
        RenderUtils.drawRect(this.x, this.y, this.width, height2, WolframGui.getMainColor());
        if (Wolfram.getWolfram().guiManager.gui.isOpen()) {
            float yFactor = (float)this.closeAnim.getValue() * 2.0f - 1.0f;
            float pointLeftY = (float)(this.y + height2 / 2) + yFactor * 2.0f;
            float pointCenterY = (float)(this.y + height2 / 2) - yFactor * 2.0f;
            float pointRightY = (float)(this.y + height2 / 2) + yFactor * 2.0f;
            RenderUtils.drawLine2D(this.x + this.width - height2 + 5, pointLeftY, (float)(this.x + this.width) - (float)height2 / 2.0f, pointCenterY, 1.5f, 0xFFFFFF);
            RenderUtils.drawLine2D((float)(this.x + this.width) - (float)height2 / 2.0f, pointCenterY, this.x + this.width - 5, pointRightY, 1.5f, 0xFFFFFF);
        }
        if (this.openHover) {
            RenderUtils.drawRect(this.x + this.width - height2, this.y, height2, height2, 0x20FFFFFF);
        }
        if (this.pinnable && Wolfram.getWolfram().guiManager.gui.isOpen()) {
            RenderUtils.drawCircle(this.x + height2 / 2, this.y + height2 / 2, 3.0f, 1.5f, 0xFFFFFF);
            if (this.pinAnim.getValue() > 0.0) {
                RenderUtils.drawFilledCircle(this.x + height2 / 2, this.y + height2 / 2, (float)this.pinAnim.getValue() * 1.5f, 0xFFFFFF);
            }
            if (this.pinHover) {
                RenderUtils.drawRect(this.x, this.y, height2, height2, 0x20FFFFFF);
            }
        }
        FontRenderers.SMALL.drawCenteredStringXY(this.title, this.x + this.width / 2, this.y + height2 / 2, 0xFFFFFF, false);
        if (this.closeAnim.getValue() > 0.0) {
            int contentY = this.y + 18;
            float contentHeight = (float)(this.height - 18) * (float)this.closeAnim.getValue();
            RenderUtils.beginCrop(this.x, (float)contentY + contentHeight, this.width, contentHeight);
            ArrayList<TabGui> tabGuis = new ArrayList<TabGui>();
            for (Component component : this.children) {
                if (!component.isEnabled) continue;
                if (component instanceof TabGui) {
                    tabGuis.add((TabGui)component);
                    continue;
                }
                component.render(mouseX, mouseY);
            }
            if (this.scrollbarEnabled) {
                int scrollbarX = this.x + this.width - 2;
                int visibleContentHeight = 162;
                int scrollbarY = contentY + (int)MathUtils.map(this.scrollAmount, 0.0f, 1.0f, 3.0f, visibleContentHeight - 50 - 3);
                RenderUtils.drawRect(scrollbarX, contentY, 2.0f, visibleContentHeight, -803727336);
                RenderUtils.drawRect(scrollbarX, scrollbarY, 2.0f, 50.0f, GuiManager.getHexMainColor());
            }
            RenderUtils.endCrop();
            for (TabGui tabGui : tabGuis) {
                tabGui.render(mouseX, mouseY);
            }
        }
    }

    private boolean overScrollbar(int mouseX, int mouseY) {
        return this.scrollbarEnabled && this.mouseOver(mouseX, mouseY) && MathUtils.contains(mouseX, mouseY, this.x + this.width - 2, this.y + 18, this.x + this.width, this.y + this.height);
    }

    public boolean handleMouseUpdates(int mouseX, int mouseY, boolean pressed) {
        if (pressed && !this.wasMousePressed && this.overScrollbar(mouseX, mouseY)) {
            this.isScrolling = true;
        }
        if (!pressed) {
            this.isScrolling = false;
        }
        boolean bringToFront = false;
        if (this.mouseOver(mouseX, mouseY)) {
            int barHeight = 18;
            this.pinHover = this.pinnable && MathUtils.contains(mouseX, mouseY, this.x, this.y, this.x + barHeight, this.y + barHeight);
            this.openHover = MathUtils.contains(mouseX, mouseY, this.x + this.width - barHeight, this.y, this.x + this.width, this.y + barHeight);
            if (!this.wasMousePressed && pressed) {
                bringToFront = true;
            }
            if (this.pinHover && !this.wasMousePressed && pressed) {
                boolean bl = this.pinned = !this.pinned;
            }
            if (this.openHover && !this.wasMousePressed && pressed) {
                this.open = !this.open;
            }
            boolean overBar = MathUtils.contains(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + barHeight);
            if (!this.pinHover && !this.openHover && overBar && !this.wasMousePressed && pressed) {
                this.dragging = true;
                this.grabX = mouseX - this.x;
                this.grabY = mouseY - this.y;
            } else if (!pressed) {
                this.dragging = false;
            }
            if (this.open) {
                if (!overBar) {
                    for (Component c : this.children) {
                        if (!c.isEnabled) continue;
                        c.mouseUpdates(mouseX, mouseY, pressed);
                    }
                } else {
                    for (Component c : this.children) {
                        if (!c.isEnabled) continue;
                        c.noMouseUpdates();
                    }
                }
            }
        } else if (this.dragging) {
            if (!pressed) {
                this.dragging = false;
            }
        } else {
            this.noMouseUpdates();
        }
        this.wasMousePressed = pressed;
        return bringToFront;
    }

    public void handleWheelUpdates(int mouseX, int mouseY, boolean b) {
        int wheelEvent;
        if (this.mouseOver(mouseX, mouseY) && (wheelEvent = Mouse.getEventDWheel()) != 0) {
            if (wheelEvent > 0) {
                wheelEvent = -1;
            } else if (wheelEvent < 0) {
                wheelEvent = 1;
            }
            this.toScrollY += wheelEvent * 18;
            if (this.toScrollY > this.componentsHeight - 162) {
                this.toScrollY = this.componentsHeight - 162;
            }
            if (this.toScrollY < 0) {
                this.toScrollY = 0;
            }
        }
    }

    public boolean contains(int mouseX, int mouseY) {
        return MathUtils.contains(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + (this.open ? this.height : 18));
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        for (Window window : Wolfram.getWolfram().guiManager.gui.getStaticWindows()) {
            if (!window.enabled || !window.contains(mouseX, mouseY)) continue;
            return window == this;
        }
        for (Window window : Wolfram.getWolfram().guiManager.gui.getWindows()) {
            if (!window.enabled || !window.contains(mouseX, mouseY)) continue;
            return window == this;
        }
        return false;
    }

    protected void keepInBounds() {
        this.x = Math.max(this.x, 0);
        this.y = Math.max(this.y, 0);
        this.x = Math.min(this.x, RenderUtils.getDisplayWidth() - 80);
        this.y = Math.min(this.y, RenderUtils.getDisplayHeight() - 18);
    }

    public void noMouseUpdates() {
        this.pinHover = false;
        this.openHover = false;
        this.dragging = false;
        if (this.open) {
            for (Component c : this.children) {
                if (!c.isEnabled) continue;
                c.noMouseUpdates();
            }
        }
        if (!Mouse.isButtonDown((int)0)) {
            this.isScrolling = false;
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Component> getChildren() {
        return this.children;
    }

    public boolean isPinnable() {
        return this.pinnable;
    }

    public void setPinnable(boolean pinnable) {
        this.pinnable = pinnable;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getScrollY() {
        return this.scrollY;
    }

    public boolean isScrollbarEnabled() {
        return this.scrollbarEnabled;
    }
}

