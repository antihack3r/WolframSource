/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import java.security.SecureRandom;
import java.util.ArrayList;
import net.wolframclient.clickgui.Window;

public final class WindowPreset
extends ArrayList<Window> {
    private static final SecureRandom random = new SecureRandom();
    private String title;
    private int id;

    public WindowPreset(String title) {
        this.title = title;
        this.id = random.nextInt() & Integer.MAX_VALUE;
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
}

