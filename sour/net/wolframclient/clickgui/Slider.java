/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import net.wolframclient.clickgui.BasicSlider;
import net.wolframclient.clickgui.Window;
import net.wolframclient.storage.MapStorage;

public final class Slider
extends BasicSlider {
    @Deprecated
    public MapStorage storage;
    @Deprecated
    public String setting;

    public Slider(MapStorage storage, Window window, int id, int offX, int offY, String title, String setting, float min, float max, float increment, String tooltip) {
        super(window, id, offX, offY, title, min, max, increment, tooltip);
        this.storage = storage;
        this.setting = setting;
        if (storage != null) {
            this.value = storage.getFloat(setting);
        }
        this.type = "Slider";
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        if (this.storage == null) {
            return;
        }
        if (this.isDragging) {
            this.storage.set(this.setting, Float.valueOf(this.value));
        } else {
            this.value = this.storage.getFloat(this.setting);
        }
    }

    public MapStorage getStorage() {
        return this.storage;
    }

    public void setStorage(MapStorage storage) {
        this.storage = storage;
    }

    public String getSetting() {
        return this.setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}

