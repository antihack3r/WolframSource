/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.storage;

import net.wolframclient.storage.MapStorage;

public final class SettingData {
    public final boolean isFloat;
    public final MapStorage storage;
    public final String setting;
    public final float value;
    public final float min;
    public final float max;
    public final float increment;
    public final String name;
    public final String description;

    public SettingData(MapStorage storage, String setting, float value, float min, float max, float increment, String name, String description) {
        this.storage = storage;
        this.isFloat = true;
        this.setting = setting;
        this.value = value;
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.name = name;
        this.description = description;
    }

    public SettingData(MapStorage storage, String setting, boolean value, String name, String description) {
        this.storage = storage;
        this.isFloat = false;
        this.setting = setting;
        this.value = value ? 1 : 0;
        this.min = 0.0f;
        this.max = 0.0f;
        this.increment = 0.0f;
        this.name = name;
        this.description = description;
    }
}

