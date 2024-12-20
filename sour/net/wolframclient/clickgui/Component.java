/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package net.wolframclient.clickgui;

import net.wolframclient.clickgui.Window;
import net.wolframclient.utils.MathUtils;
import org.lwjgl.input.Mouse;

public abstract class Component {
    public int x;
    public int y;
    public int width;
    public int height;
    public int offX;
    public int offY;
    public int id;
    public String title;
    public String tooltip;
    protected boolean hovering;
    public boolean isEnabled = true;
    protected boolean wasMousePressed;
    public Window window;
    public String type = "Component";
    public boolean editable = true;

    public Component(Window window, int id, int offX, int offY, String title, String tooltip) {
        this.window = window;
        this.offX = offX;
        this.offY = offY;
        this.title = title;
        this.id = id;
        this.tooltip = tooltip;
    }

    protected void reposition() {
        this.x = this.window.getX() + this.offX;
        this.y = this.window.getY() + this.offY - this.window.getScrollY();
    }

    public boolean contains(int mouseX, int mouseY) {
        return MathUtils.contains(mouseX, mouseY, this.x, this.y, this.x + this.width - (this.window.isScrollbarEnabled() ? 2 : 0), this.y + this.height);
    }

    public void noMouseUpdates() {
        this.hovering = false;
        this.wasMousePressed = Mouse.isButtonDown((int)0);
    }

    public void update(int mouseX, int mouseY) {
        this.reposition();
    }

    public abstract void render(int var1, int var2);

    public abstract void mouseUpdates(int var1, int var2, boolean var3);
}

