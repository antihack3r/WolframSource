/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.wolframclient.module.modules.render;

import com.google.common.collect.Lists;
import java.util.List;

public class ChatTab {
    public List lines = Lists.newArrayList();
    public String name;

    public ChatTab(String name) {
        this.name = name;
    }
}

