/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.account_manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.wolframclient.Wolfram;
import net.wolframclient.account_manager.AccountManagerScreen;
import net.wolframclient.account_manager.LoginUtils;
import net.wolframclient.gui.screen.wolfram.WolframLoading;

public final class AccountListGui
extends GuiSlot {
    private final AccountManagerScreen screen;
    public int currentSlot = -1;

    public AccountListGui(AccountManagerScreen screen) {
        this(screen, Minecraft.getMinecraft(), screen.width, screen.height, 32, screen.height - 61, 25);
    }

    public AccountListGui(AccountManagerScreen screen, Minecraft mc, int width, int height, int top, int bottom, int slotHeight) {
        super(mc, width, height, top, bottom, slotHeight);
        this.screen = screen;
    }

    @Override
    protected int getSize() {
        return Wolfram.getWolfram().storageManager.altAccounts.size();
    }

    public boolean currentSlotInBounds() {
        return this.currentSlot > -1 && this.currentSlot < this.getSize();
    }

    public void login() {
        if (!this.currentSlotInBounds()) {
            return;
        }
        String username = (String)Wolfram.getWolfram().storageManager.altAccounts.keySet().toArray()[this.currentSlot];
        Wolfram.getWolfram().accountManager.login(username);
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        this.currentSlot = slotIndex;
        if (isDoubleClick) {
            this.login();
        }
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.currentSlot;
    }

    public void removeSelected() {
        if (!this.currentSlotInBounds()) {
            return;
        }
        Wolfram.getWolfram().storageManager.altAccounts.remove(Wolfram.getWolfram().storageManager.altAccounts.keySet().toArray()[this.currentSlot]);
        this.currentSlot = -1;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int slotIndex, int left, int top, int height, int mouseX, int mouseY, float partialTicks) {
        String username = (String)Wolfram.getWolfram().storageManager.altAccounts.keySet().toArray()[slotIndex];
        String password = (String)Wolfram.getWolfram().storageManager.altAccounts.get(username);
        this.screen.drawCenteredString(this.mc.fontRendererObj, username, this.screen.width / 2, top + 1, -263693982);
        if (!password.isEmpty()) {
            this.screen.drawCenteredString(this.mc.fontRendererObj, "Premium", this.screen.width / 2, top + 13, -1337435806);
        } else {
            this.screen.drawCenteredString(this.mc.fontRendererObj, "Non-Premium", this.screen.width / 2, top + 13, -1337435806);
        }
    }

    public void autoRemove() {
        this.currentSlot = -1;
        WolframLoading loading = new WolframLoading(this.screen, true);
        this.mc.displayGuiScreen(loading);
        loading.infoMsg = "Processing accounts... This may take a while.";
        new Thread(() -> {
            wolframLoading.statusMsg = "Removed 0 accounts.";
            int count = 0;
            boolean done = false;
            block0: while (!done) {
                done = true;
                String[] stringArray = Wolfram.getWolfram().storageManager.altAccounts.keySet().toArray(new String[Wolfram.getWolfram().storageManager.altAccounts.size()]);
                int n = stringArray.length;
                int n2 = 0;
                while (n2 < n) {
                    String username = stringArray[n2];
                    String password = (String)Wolfram.getWolfram().storageManager.altAccounts.get(username);
                    int status = LoginUtils.check(username, password);
                    if (status == 0) {
                        Wolfram.getWolfram().storageManager.altAccounts.remove(username);
                        wolframLoading.statusMsg = "Removed " + ++count + " accounts.";
                    }
                    if (status == 2) {
                        done = false;
                    }
                    if (wolframLoading.endRequested) {
                        done = true;
                        continue block0;
                    }
                    ++n2;
                }
            }
            Wolfram.getWolfram().accountManager.lastLoginFailed = false;
            Wolfram.getWolfram().accountManager.lastLoginStatus = "Removed " + count + " accounts.";
            this.mc.displayGuiScreen(this.screen);
        }).start();
    }
}

