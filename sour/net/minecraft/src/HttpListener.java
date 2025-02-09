/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import net.minecraft.src.HttpRequest;
import net.minecraft.src.HttpResponse;

public interface HttpListener {
    public void finished(HttpRequest var1, HttpResponse var2);

    public void failed(HttpRequest var1, Exception var2);
}

