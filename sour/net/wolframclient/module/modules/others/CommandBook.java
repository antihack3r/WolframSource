/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module.modules.others;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.wolframclient.Wolfram;
import net.wolframclient.module.Module;

public class CommandBook
extends Module {
    public CommandBook() {
        super("CommandBook", Module.Category.OTHER, "Apply a command on a book when signing");
    }

    public void applyCommand(ITextComponent c) {
        if (this.isEnabled()) {
            if (this.getSettings().get("commandbook") != null) {
                c.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (String)this.getSettings().get("commandbook")));
            } else {
                Wolfram.getWolfram().addChatMessage("No command was applied, set it with the command \"cbook [command]\"!");
            }
        }
    }
}

