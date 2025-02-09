/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.account_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import javax.swing.JFileChooser;
import net.wolframclient.Wolfram;
import net.wolframclient.account_manager.LoginUtils;
import net.wolframclient.storage.MapStorage;

public final class AccountManager {
    public String lastLoginStatus;
    public boolean lastLoginFailed;
    private final Random random = new Random();

    public MapStorage getAccounts() {
        return Wolfram.getWolfram().storageManager.altAccounts;
    }

    public void importAccounts() {
        new Thread(() -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) != 0) {
                return;
            }
            try {
                Throwable throwable = null;
                Object var3_5 = null;
                try (BufferedReader reader = Files.newBufferedReader(fileChooser.getSelectedFile().toPath());){
                    while (true) {
                        String line;
                        if ((line = reader.readLine()) == null) {
                            this.getAccounts().save();
                            return;
                        }
                        try {
                            String[] data = line.split(":", 2);
                            if (data.length < 2) continue;
                            this.getAccounts().set(data[0], data[1]);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Throwable throwable2) {
                    if (throwable == null) {
                        throwable = throwable2;
                        throw throwable;
                    }
                    if (throwable == throwable2) throw throwable;
                    throwable.addSuppressed(throwable2);
                    throw throwable;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void login(String username) {
        new Thread(() -> {
            String status;
            String password = (String)this.getAccounts().get(username);
            this.lastLoginStatus = status = LoginUtils.login(username, password);
            this.lastLoginFailed = !status.equals("Logged in as non-premium.") && !status.equals("Logged in as premium.");
        }).start();
    }

    public boolean randomLogin() {
        MapStorage accounts = this.getAccounts();
        if (accounts.isEmpty()) {
            return false;
        }
        String[] usernames = accounts.keySet().toArray(new String[accounts.size()]);
        int i = 0;
        while (i < Math.min(accounts.size(), 20)) {
            String status;
            String username = usernames[this.random.nextInt(accounts.size())];
            String password = (String)accounts.get(username);
            this.lastLoginStatus = status = LoginUtils.login(username, password);
            boolean bl = this.lastLoginFailed = !status.equals("Logged in as non-premium.") && !status.equals("Logged in as premium.");
            if (!this.lastLoginFailed) {
                return true;
            }
            ++i;
        }
        return false;
    }
}

