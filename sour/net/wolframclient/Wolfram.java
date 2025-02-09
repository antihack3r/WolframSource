/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.core.LoggerContext
 *  org.apache.logging.log4j.core.lookup.Interpolator
 */
package net.wolframclient;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.constant.Constable;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.wolframclient.account_manager.AccountManager;
import net.wolframclient.analytics.Analytics;
import net.wolframclient.command.CommandManager;
import net.wolframclient.compatibility.WChat;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.keybind.KeybindManager;
import net.wolframclient.module.ModuleManager;
import net.wolframclient.module.modules.combat.Killaura;
import net.wolframclient.nameprotect.NameprotectManager;
import net.wolframclient.proxy.ProxyManager;
import net.wolframclient.relations.RelationManager;
import net.wolframclient.storage.MapStorage;
import net.wolframclient.storage.SettingData;
import net.wolframclient.storage.StorageManager;
import net.wolframclient.update.UpdateManager;
import net.wolframclient.utils.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.lookup.Interpolator;

public final class Wolfram {
    public static final String CLIENT_VERSION = "9.8.1";
    public static final String[] CREDITS = new String[]{"Development: Alexander01998", "Marketing: WiZARDHAX", "Original Creator: ShadowSpl0it", "Original Designer: Ben Roberts"};
    private static final Wolfram theWolfram = new Wolfram();
    private static final Minecraft mc = Minecraft.getMinecraft();
    public final Logger logger = new Logger();
    public final StorageManager storageManager = new StorageManager();
    public final GuiManager guiManager = new GuiManager();
    public final ModuleManager moduleManager = new ModuleManager();
    public final CommandManager commandManager = new CommandManager();
    public final KeybindManager keybindManager = new KeybindManager();
    public final RelationManager relationManager = new RelationManager();
    public final AccountManager accountManager = new AccountManager();
    public final ProxyManager proxyManager = new ProxyManager();
    public final NameprotectManager nameprotectManager = new NameprotectManager();
    public final UpdateManager updateManager = new UpdateManager();
    public Analytics analytics;
    public ServerData lastServer = null;
    public final List<SettingData> preferencesData = new ArrayList<SettingData>();
    public final List<SettingData> settingsData = new ArrayList<SettingData>();
    public final List<SettingData> allData = new ArrayList<SettingData>();

    public void onStartup() {
        ((LoggerContext)LogManager.getContext((boolean)false)).addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("config")) {
                    Wolfram.applyLog4ShellFix();
                }
            }
        });
        Wolfram.applyLog4ShellFix();
        this.logger.info("Starting client...");
        Wolfram.getSaveDirectory().mkdirs();
        this.moduleManager.loadModules();
        this.logger.info("Loaded modules!");
        this.commandManager.loadCommands();
        this.logger.info("Loaded commands!");
        this.setDefaults(false);
        this.storageManager.loadAll();
        this.logger.info("Loaded settings!");
        this.analytics = new Analytics("UA-52838431-12", "client.wolframclient.net");
        this.guiManager.loadGui();
        this.logger.info("Loaded GUI!");
        this.keybindManager.loadKeybinds();
        this.logger.info("Loaded Keybinds!");
        this.moduleManager.toggleModules();
        this.logger.info("Toggled modules!");
        Wolfram.mc.fontRendererObj = FontRenderers.getFontRenderer();
        this.logger.info("Loaded font!");
        new Thread(() -> this.createWarningFile()).start();
        this.proxyManager.test();
        this.logger.info("Created generic files!");
        this.updateManager.checkForUpdates();
        this.logger.info("Checked for updates!");
        this.analytics.trackPageView("/mc1.12/v9.8.1", "Wolfram 9.8.1 MC1.12");
    }

    private static void applyLog4ShellFix() {
        try {
            Interpolator interpolator = (Interpolator)((LoggerContext)LogManager.getContext((boolean)false)).getConfiguration().getStrSubstitutor().getVariableResolver();
            if (interpolator == null) {
                return;
            }
            boolean removed = false;
            Field[] fieldArray = Interpolator.class.getDeclaredFields();
            int n = fieldArray.length;
            int n2 = 0;
            while (n2 < n) {
                Field field = fieldArray[n2];
                if (Map.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    boolean bl = removed = ((Map)field.get(interpolator)).remove("jndi") != null;
                    if (removed) break;
                }
                ++n2;
            }
            if (!removed) {
                throw new RuntimeException("couldn't find jndi lookup entry");
            }
            System.out.println("Removed JNDI lookup");
        }
        catch (Throwable t) {
            t.printStackTrace();
            Runtime.getRuntime().halt(1);
            throw new RuntimeException("application failed");
        }
    }

    public void postStartup() {
    }

    public void onDisconnect() {
    }

    public void onWorldLoad() {
    }

    public void onEnd() {
        this.logger.info("Stopping client...");
        this.guiManager.gui.saveWindows();
        this.moduleManager.clean();
        this.storageManager.saveAll();
    }

    public MapStorage getClientSettings() {
        return this.storageManager.clientSettings;
    }

    private void createWarningFile() {
        Path warningFile = Wolfram.getSaveDirectory().toPath().resolve("WARNING.txt");
        if (Files.exists(warningFile, new LinkOption[0])) {
            return;
        }
        try {
            Throwable throwable = null;
            Object var3_5 = null;
            try (BufferedWriter writer = Files.newBufferedWriter(warningFile, new OpenOption[0]);){
                writer.write("DO NOT MODIFY THE CONTENT OF SETTINGS FILES!\n");
                writer.write("IF A FILE GETS CORRUPTED, SETTINGS CONTAINED IN IT WILL BE RESET\n");
                writer.write("ALL SETTINGS CAN BE CHANGED IN GAME.\n");
            }
            catch (Throwable throwable2) {
                if (throwable == null) {
                    throwable = throwable2;
                } else if (throwable != throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (IOException e) {
            this.logger.error("Could not create warning file.");
            e.printStackTrace();
        }
    }

    public void setDefaults(boolean overwrite) {
        MapStorage clientSettings = this.getClientSettings();
        MapStorage moduleSettings = this.storageManager.moduleSettings;
        this.preferencesData.add(new SettingData(clientSettings, "color_hue", 0.0f, 0.0f, 1.0f, 0.001f, "Color Hue", "Color hue"));
        this.preferencesData.add(new SettingData(clientSettings, "color_saturation", 0.6f, 0.0f, 1.0f, 0.001f, "Color Saturation", "Color intensity"));
        this.preferencesData.add(new SettingData(clientSettings, "color_luminosity", 0.6f, 0.0f, 1.0f, 0.001f, "Color Luminosity", "Color luminosity"));
        this.preferencesData.add(new SettingData(clientSettings, "rainbow_speed", 0.0f, 0.0f, 1.0f, 0.001f, "Rainbow Speed", "Rainbow color transition speed"));
        this.preferencesData.add(new SettingData(clientSettings, "rainbow", false, "Rainbow Mode", "Rainbow colors"));
        this.preferencesData.add(new SettingData(clientSettings, "use_default_font", false, "Minecraft Font", "Use the default Minecraft font"));
        this.preferencesData.add(new SettingData(clientSettings, "block_overlay", true, "Block Overlay", "Show the Wolfram block overlay"));
        this.preferencesData.add(new SettingData(clientSettings, "short_arraylist", true, "Short Array List", "Doesn't show render mods in the array list"));
        this.preferencesData.add(new SettingData(clientSettings, "chat_autoclose", false, "Chat Auto-Close", "Automatically close chat after message sent"));
        this.preferencesData.add(new SettingData(clientSettings, "chat_autoswitch", false, "Chat Auto-Switch", "Automatically switch tabs, when new message received"));
        this.preferencesData.add(new SettingData(clientSettings, "wolfram_hud", false, "Wolfram HUD", "Custom Wolfram HUD"));
        this.preferencesData.add(new SettingData(clientSettings, "watermark", true, "Wolfram Watermark", "Show the client name in the upper left corner"));
        this.preferencesData.add(new SettingData(clientSettings, "toggle_msg", true, "Toggle Messages", "Add a chat message when toggling a mod"));
        for (SettingData s : this.preferencesData) {
            clientSettings.set(s.setting, s.isFloat ? (Constable)Float.valueOf(s.value) : (Constable)Boolean.valueOf(s.value == 1.0f), overwrite);
        }
        clientSettings.set("clickgui_key", 29, overwrite);
        clientSettings.set("analytics_id", new SecureRandom().nextInt() & Integer.MAX_VALUE, overwrite);
        clientSettings.set("analytics_first_launch", System.currentTimeMillis() / 1000L, overwrite);
        clientSettings.set("analytics_last_launch", System.currentTimeMillis() / 1000L, overwrite);
        clientSettings.set("analytics_launch_count", 0, overwrite);
        clientSettings.set("wolfram_chat", true, true);
        if ("1.12".equals("1.12")) {
            clientSettings.set("mc112x_compatibility", 2);
        }
        this.settingsData.add(new SettingData(moduleSettings, "horizontal_velocity", 0.0f, -3.0f, 3.0f, 0.1f, "Velocity Horizontal", "Horizontal multiplier of received knockback"));
        this.settingsData.add(new SettingData(moduleSettings, "vertical_velocity", 0.0f, -3.0f, 3.0f, 0.1f, "Velocity Vertical", "Vertical multiplier of received knockback"));
        this.settingsData.add(new SettingData(moduleSettings, "aimbot_range", 4.5f, 1.0f, 30.0f, 0.001f, "Aimbot Range", "Maximum range at which Aimbot aims at enemies"));
        this.settingsData.add(new SettingData(moduleSettings, "nametags_size", 5.0f, 1.0f, 8.0f, 0.001f, "Nametags Size", "Size of nametags"));
        this.settingsData.add(new SettingData(moduleSettings, "timer_speed", 2.0f, 1.0f, 20.0f, 0.001f, "Timer Speed", "Multiplier of the game speed"));
        this.settingsData.add(new SettingData(moduleSettings, "fastbreak_speed", 1.0f, 1.0f, 5.0f, 0.001f, "Fastbreak Speed", "Speed of block breaking with FastBreak"));
        this.settingsData.add(new SettingData(moduleSettings, "nuker_range", 4.0f, 4.0f, 6.0f, 0.001f, "Nuker Range", "Maximum range at which nuker still destroys blocks"));
        this.settingsData.add(new SettingData(moduleSettings, "step_height", 1.0f, 1.0f, 10.0f, 0.5f, "Step Height", "The maximum height the player can step up with step"));
        this.settingsData.add(new SettingData(moduleSettings, "flight_speed", 1.0f, 0.1f, 10.0f, 0.001f, "Flight Speed", "The speed of Flight and CreativeFly hacks"));
        this.settingsData.add(new SettingData(moduleSettings, "regen_speed", 20.0f, 1.0f, 100.0f, 1.0f, "Regen Speed", "Multiplier of the Regen speed"));
        this.settingsData.add(new SettingData(moduleSettings, "glide_speed", 0.01f, 0.0f, 0.1f, 0.001f, "Glide Speed", "Speed of Glide falling"));
        this.settingsData.add(new SettingData(moduleSettings, "killaura_players", true, "Aim Players", "Target players with combat mods (KillAura not affected)"));
        this.settingsData.add(new SettingData(moduleSettings, "killaura_mobs", true, "Aim Mobs", "Target mobs with combat mods (KillAura not affected)"));
        this.settingsData.add(new SettingData(moduleSettings, "killaura_animals", true, "Aim Animals", "Target animals with combat mods (KillAura not affected)"));
        this.settingsData.add(new SettingData(moduleSettings, "nametags_health", true, "Nametags Health", "Shows players health besides the name when Nametags enabled"));
        this.settingsData.add(new SettingData(moduleSettings, "nametags_resize", true, "Nametags Resizing", "Resize nametags based on their distance from the cursor"));
        this.settingsData.add(new SettingData(moduleSettings, "wallhack_overlay", true, "Wallhack Overlay", "Shows a red overlay over entities that cannot be seen"));
        this.settingsData.add(new SettingData(moduleSettings, "breadcrumbs_depth", true, "Breadcrumbs Depth", "Don't show lines behind walls"));
        this.settingsData.add(new SettingData(moduleSettings, "pathfinder_depth", true, "Pathfinder Depth", "Don't show lines behind walls"));
        for (SettingData s : this.settingsData) {
            moduleSettings.set(s.setting, s.isFloat ? (Constable)Float.valueOf(s.value) : (Constable)Boolean.valueOf(s.value == 1.0f), overwrite);
        }
        moduleSettings.set("reach", Float.valueOf(3.8f), overwrite);
        this.allData.addAll(this.preferencesData);
        this.allData.addAll(this.settingsData);
        this.allData.addAll(Killaura.settingsData);
    }

    public void addChatMessage(String message) {
        WChat.setChatLineForTab(this.formatMessage(message), WMinecraft.getChatGui().commandChat);
    }

    private String formatMessage(String message) {
        message = message.replace("[", ChatFormatting.GRAY + "[" + ChatFormatting.GOLD);
        message = message.replace("]", ChatFormatting.GRAY + "]" + ChatFormatting.RESET);
        message = message.replace("<", ChatFormatting.GREEN + "<" + ChatFormatting.AQUA);
        message = message.replace(">", ChatFormatting.GREEN + ">" + ChatFormatting.RESET);
        message = message.replace("$", ChatFormatting.RED + "$" + ChatFormatting.RESET);
        return message;
    }

    public static final Wolfram getWolfram() {
        return theWolfram;
    }

    public static String getClientName() {
        return "Wolfram 9.8.1 MC1.12";
    }

    public static File getSaveFile(String name) {
        return new File(Wolfram.getSaveDirectory(), String.valueOf(name) + ".wolfram");
    }

    public static File getSaveDirectory() {
        return new File(Minecraft.getMinecraft().mcDataDir, "Wolfram");
    }
}

