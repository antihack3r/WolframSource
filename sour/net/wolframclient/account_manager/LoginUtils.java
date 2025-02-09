/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
 */
package net.wolframclient.account_manager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public final class LoginUtils {
    public static final int FAIL = 0;
    public static final int SUCCESS = 1;
    public static final int REJECTED = 2;
    public static final int OTHER = 3;

    public static synchronized String login(String email, String password) {
        if (password.isEmpty()) {
            Minecraft.getMinecraft().session = new Session(email, "0", "0", "legacy");
            return "Logged in as non-premium.";
        }
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);
        try {
            auth.logIn();
            Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "MOJANG");
        }
        catch (AuthenticationException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            return "Error!!";
        }
        return "Logged in as premium.";
    }

    public static synchronized int check(String email, String password) {
        if (password.isEmpty()) {
            return 1;
        }
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);
        try {
            auth.logIn();
        }
        catch (AuthenticationException e) {
            if (e.getMessage().equals("Invalid credentials. Invalid username or password.") || e.getMessage().equals("Invalid credentials. Account migrated, use e-mail as username.")) {
                return 0;
            }
            if (e.getMessage().equals("Invalid credentials.")) {
                return 2;
            }
        }
        catch (Exception e) {
            return 3;
        }
        return 1;
    }
}

