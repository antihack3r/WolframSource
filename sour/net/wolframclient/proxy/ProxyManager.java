/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 */
package net.wolframclient.proxy;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.wolframclient.proxy.WolframProxy;
import net.wolframclient.utils.Manager;

public final class ProxyManager
extends Manager<WolframProxy> {
    public void test() {
        this.addProxy("221.182.134.20", 80, "", "");
    }

    public void addProxy(String address, int port, String username, String password) {
        this.add(new WolframProxy(Proxy.Type.SOCKS, new InetSocketAddress(address, port), username, password));
    }

    public void useProxy(final WolframProxy proxy) {
        Minecraft.getMinecraft().sessionService = new YggdrasilAuthenticationService((Proxy)proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        Minecraft.getMinecraft().proxy = proxy;
        Authenticator.setDefault(new Authenticator(){

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(proxy.getUsername(), proxy.getPassword().toCharArray());
            }
        });
    }
}

