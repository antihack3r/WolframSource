/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.utils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Manager<Type> {
    private final CopyOnWriteArrayList<Type> list = new CopyOnWriteArrayList();

    public void add(Type object) {
        this.list.add(object);
    }

    public void clear() {
        this.list.clear();
    }

    public List<Type> getList() {
        return Collections.unmodifiableList(this.list);
    }

    public void remove(Type object) {
        this.list.remove(object);
    }
}

