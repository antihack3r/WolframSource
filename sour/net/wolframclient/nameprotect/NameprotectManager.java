/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.nameprotect;

import java.util.regex.Pattern;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.wolframclient.Wolfram;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.DisplayComponentEvent;

public class NameprotectManager
implements Listener {
    public NameprotectManager() {
        registry.registerListener(this);
    }

    public void setName(String name, String alias) {
        if (!Wolfram.getWolfram().storageManager.nameProtect.containsKey(name)) {
            Wolfram.getWolfram().addChatMessage("New alias sucessfully set! (" + name + " -> " + alias + ")");
        } else {
            Wolfram.getWolfram().addChatMessage("Alias sucessfully modified! (" + name + " -> " + alias + ")");
        }
        Wolfram.getWolfram().storageManager.nameProtect.set(name, alias);
    }

    public void removeName(String name) {
        if (Wolfram.getWolfram().storageManager.nameProtect.containsKey(name)) {
            Wolfram.getWolfram().storageManager.nameProtect.remove(name);
            Wolfram.getWolfram().addChatMessage("Alias '" + name + "' successfully removed!");
        } else {
            Wolfram.getWolfram().addChatMessage("Alias '" + name + "' not found!");
        }
    }

    @EventTarget
    public void onMessageDisplay(DisplayComponentEvent event) {
        TextComponentBase component = (TextComponentBase)event.getComponent();
        Style style = component.getStyle();
        String message = component.getUnformattedComponentText();
        for (String name : Wolfram.getWolfram().storageManager.nameProtect.keySet()) {
            message = message.replaceAll("(?i)" + Pattern.quote(name), (String)Wolfram.getWolfram().storageManager.nameProtect.get(name));
        }
        TextComponentString newComponent = new TextComponentString(message);
        newComponent.setStyle(style.createShallowCopy());
        event.setComponent(newComponent);
    }
}

