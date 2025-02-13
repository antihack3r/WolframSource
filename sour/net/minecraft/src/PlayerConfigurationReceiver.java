/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 */
package net.minecraft.src;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.src.Config;
import net.minecraft.src.IFileDownloadListener;
import net.minecraft.src.PlayerConfiguration;
import net.minecraft.src.PlayerConfigurationParser;
import net.minecraft.src.PlayerConfigurations;

public class PlayerConfigurationReceiver
implements IFileDownloadListener {
    private String player = null;

    public PlayerConfigurationReceiver(String p_i68_1_) {
        this.player = p_i68_1_;
    }

    @Override
    public void fileDownloadFinished(String p_fileDownloadFinished_1_, byte[] p_fileDownloadFinished_2_, Throwable p_fileDownloadFinished_3_) {
        if (p_fileDownloadFinished_2_ != null) {
            try {
                String s = new String(p_fileDownloadFinished_2_, "ASCII");
                JsonParser jsonparser = new JsonParser();
                JsonElement jsonelement = jsonparser.parse(s);
                PlayerConfigurationParser playerconfigurationparser = new PlayerConfigurationParser(this.player);
                PlayerConfiguration playerconfiguration = playerconfigurationparser.parsePlayerConfiguration(jsonelement);
                if (playerconfiguration != null) {
                    playerconfiguration.setInitialized(true);
                    PlayerConfigurations.setPlayerConfiguration(this.player, playerconfiguration);
                }
            }
            catch (Exception exception) {
                Config.dbg("Error parsing configuration: " + p_fileDownloadFinished_1_ + ", " + exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }
}

