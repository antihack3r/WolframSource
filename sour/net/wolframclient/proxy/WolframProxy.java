/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.proxy;

import java.net.Proxy;
import java.net.SocketAddress;

public final class WolframProxy
extends Proxy {
    private final String username;
    private final String password;

    public WolframProxy(Proxy.Type type, SocketAddress sa, String username, String password) {
        super(type, sa);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}

