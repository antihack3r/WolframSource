/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.Display
 */
package net.wolframclient.clickgui;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Checkbox;
import net.wolframclient.clickgui.CommandButton;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Info;
import net.wolframclient.clickgui.ModuleButton;
import net.wolframclient.clickgui.ModuleList;
import net.wolframclient.clickgui.Radar;
import net.wolframclient.clickgui.ScreenButton;
import net.wolframclient.clickgui.Slider;
import net.wolframclient.clickgui.TabGui;
import net.wolframclient.clickgui.TextRadar;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WindowButton;
import net.wolframclient.clickgui.WindowPreset;
import net.wolframclient.clickgui_editor.GuiEditorScreen;
import net.wolframclient.clickgui_editor.ManagePresetsScreen;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.Module;
import net.wolframclient.module.modules.combat.Killaura;
import net.wolframclient.storage.MapStorage;
import net.wolframclient.storage.SettingData;
import net.wolframclient.utils.MathUtils;
import net.wolframclient.utils.RenderUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public final class WolframGui
extends GuiScreen {
    public static final int defaultWidth = 80;
    public static final int settingsWidth = 100;
    public static final int defaultHeight = 18;
    public static final int buttonHeight = 15;
    public static final int scrollbarWidth = 2;
    public static final int scrollbarHeight = 50;
    public static final int maxWindowHeight = 180;
    private static int mainColor;
    private static int backgroundColor;
    private final ArrayList<Window> windows = new ArrayList();
    private final ArrayList<Window> staticWindows = new ArrayList();
    private final ArrayList<WindowPreset> presets = new ArrayList();
    private final WindowPreset defaultPreset = new WindowPreset("Default");
    private final File windowFile = Wolfram.getSaveFile("windowData");
    private final File presetsFile = Wolfram.getSaveFile("windowPresets");
    private boolean open;
    private String tooltip;
    private int mouseX;
    private int mouseY;

    public WolframGui() {
        this.loadStaticWindows();
        this.allowUserInput = true;
        this.setupDefaultPreset();
        this.loadPresets();
        if (this.windows.size() <= 0) {
            this.loadPreset(this.defaultPreset);
        }
        this.loadWindows();
    }

    private void loadStaticWindows() {
        this.staticWindows.clear();
        Window hubWindow = new Window("Hub", 5, 5);
        int i = 0;
        while (i < this.windows.size()) {
            Window window = this.windows.get(i);
            WindowButton windowButton = new WindowButton(hubWindow, i, 0, 0, window.getTitle(), "", window.getId());
            hubWindow.getChildren().add(windowButton);
            ++i;
        }
        hubWindow.repositionComponents();
        hubWindow.setEnabled(true);
        hubWindow.setId(-1);
        this.staticWindows.add(hubWindow);
        Window guiEditor = new Window("Gui Options", 5, 25);
        guiEditor.getChildren().add(new ScreenButton(guiEditor, 1, 0, 18, "Edit GUI", "Customize the window layout", new GuiEditorScreen()));
        guiEditor.getChildren().add(new ScreenButton(guiEditor, 2, 0, 36, "Presets", "Save/Load window layout presets", new ManagePresetsScreen()));
        guiEditor.setEnabled(true);
        guiEditor.setId(-2);
        guiEditor.repositionComponents();
        this.staticWindows.add(guiEditor);
    }

    public void reloadHubWindow() {
        Window hubWindow = this.getWindowByID(-1);
        hubWindow.getChildren().clear();
        int i = 0;
        while (i < this.windows.size()) {
            Window window = this.windows.get(i);
            WindowButton windowButton = new WindowButton(hubWindow, i, 0, 0, window.getTitle(), "", window.getId());
            hubWindow.getChildren().add(windowButton);
            ++i;
        }
        hubWindow.repositionComponents();
    }

    @Override
    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }
    }

    public void update() {
        this.tooltip = null;
        this.mouseX = (int)MathUtils.map(Mouse.getX(), 0.0f, Display.getWidth(), 0.0f, RenderUtils.getDisplayWidth());
        this.mouseY = (int)MathUtils.map(Display.getHeight() - Mouse.getY(), 0.0f, Display.getHeight(), 0.0f, RenderUtils.getDisplayHeight());
        this.open = Minecraft.getMinecraft().currentScreen instanceof WolframGui;
        backgroundColor = this.open ? -803200992 : -1340071904;
        mainColor = this.open ? GuiManager.getHexMainColor() : GuiManager.getHexMainColor() + -1342177280;
        Window frontWindow = null;
        for (Window window : this.staticWindows) {
            window.update(this.mouseX, this.mouseY);
            if (this.open) {
                if (!window.handleMouseUpdates(this.mouseX, this.mouseY, Mouse.isButtonDown((int)0))) continue;
                frontWindow = window;
                continue;
            }
            window.noMouseUpdates();
        }
        for (Window window : this.windows) {
            window.update(this.mouseX, this.mouseY);
            if (this.open) {
                if (!window.handleMouseUpdates(this.mouseX, this.mouseY, Mouse.isButtonDown((int)0))) continue;
                frontWindow = window;
                continue;
            }
            window.noMouseUpdates();
        }
        if (frontWindow != null) {
            this.bringToFront(frontWindow);
        }
    }

    public void bringToFront(Window window) {
        if (this.staticWindows.remove(window)) {
            this.staticWindows.add(0, window);
        } else if (this.windows.remove(window)) {
            this.windows.add(0, window);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (this.open && Mouse.getEventDWheel() != 0) {
            for (Window window : this.windows) {
                window.handleWheelUpdates(this.mouseX, this.mouseY, Mouse.isButtonDown((int)0));
            }
            for (Window window : this.staticWindows) {
                window.handleWheelUpdates(this.mouseX, this.mouseY, Mouse.isButtonDown((int)0));
            }
        }
        super.handleMouseInput();
    }

    public void render() {
        Window window;
        int i = this.windows.size() - 1;
        while (i >= 0) {
            window = this.windows.get(i);
            if (window.isEnabled() && (this.open || window.isPinnable() && window.isPinned())) {
                window.render(this.mouseX, this.mouseY);
            }
            --i;
        }
        i = this.staticWindows.size() - 1;
        while (i >= 0) {
            window = this.staticWindows.get(i);
            if (window.isEnabled() && (this.open || window.isPinnable() && window.isPinned())) {
                window.render(this.mouseX, this.mouseY);
            }
            --i;
        }
        if (this.open && this.tooltip != null) {
            int tooltipX = this.mouseX + 8;
            int tooltipY = this.mouseY - 16;
            int tooltipWidth = 10 + FontRenderers.DEFAULT.getStringWidth(this.tooltip);
            int tooltipHeight = 10 + FontRenderers.DEFAULT.FONT_HEIGHT;
            if (tooltipX + tooltipWidth > this.width) {
                tooltipX -= tooltipWidth + 16;
            }
            RenderUtils.drawRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, Integer.MIN_VALUE);
            FontRenderers.DEFAULT.drawString(this.tooltip, tooltipX + 5, tooltipY + 5, 0xFFFFFF);
        }
    }

    public void loadWindows() {
        this.windows.clear();
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonReader reader = new JsonReader((Reader)new FileReader(this.windowFile));){
                reader.beginObject();
                reader.nextName();
                reader.beginArray();
                while (reader.hasNext()) {
                    try {
                        reader.beginObject();
                        Window w = new Window("", 0, 0);
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            if (name.equals("ID")) {
                                w.setId(reader.nextInt());
                            }
                            if (name.equals("Title")) {
                                w.setTitle(reader.nextString());
                            }
                            if (name.equals("X")) {
                                w.setX(reader.nextInt());
                            }
                            if (name.equals("Y")) {
                                w.setY(reader.nextInt());
                            }
                            if (name.equals("Enabled")) {
                                w.setEnabled(reader.nextBoolean());
                            }
                            if (name.equals("Opened")) {
                                w.setOpen(reader.nextBoolean());
                            }
                            if (name.equals("Pinned")) {
                                w.setPinned(reader.nextBoolean());
                            }
                            if (name.equals("Pinnable")) {
                                w.setPinnable(reader.nextBoolean());
                            }
                            if (!name.equals("Components")) continue;
                            reader.beginArray();
                            while (reader.hasNext()) {
                                Component c = null;
                                HashMap<String, String> cd = new HashMap<String, String>();
                                reader.beginObject();
                                while (reader.hasNext()) {
                                    cd.put(reader.nextName(), reader.nextString());
                                }
                                reader.endObject();
                                if (((String)cd.get("Type")).equals("ModuleButton")) {
                                    c = new ModuleButton(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"), Wolfram.getWolfram().moduleManager.getModule((String)cd.get("Module")));
                                } else if (((String)cd.get("Type")).equals("CommandButton")) {
                                    c = new CommandButton(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"), (String)cd.get("Command"));
                                } else if (((String)cd.get("Type")).equals("Checkbox")) {
                                    c = new Checkbox((MapStorage)Wolfram.getWolfram().storageManager.getByName((String)cd.get("Storage")), w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Setting"), (String)cd.get("Tooltip"));
                                } else if (((String)cd.get("Type")).equals("Slider")) {
                                    c = new Slider((MapStorage)Wolfram.getWolfram().storageManager.getByName((String)cd.get("Storage")), w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Setting"), Float.parseFloat((String)cd.get("Minimum")), Float.parseFloat((String)cd.get("Maximum")), Float.parseFloat((String)cd.get("Increment")), (String)cd.get("Tooltip"));
                                } else if (((String)cd.get("Type")).equals("WindowButton")) {
                                    c = new WindowButton(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"), Integer.parseInt((String)cd.get("Window ID")));
                                } else if (((String)cd.get("Type")).equals("TextRadar")) {
                                    c = new TextRadar(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                } else if (((String)cd.get("Type")).equals("TabGui")) {
                                    c = new TabGui(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                } else if (((String)cd.get("Type")).equals("Radar")) {
                                    c = new Radar(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                } else if (((String)cd.get("Type")).equals("ModuleList")) {
                                    c = new ModuleList(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                } else if (((String)cd.get("Type")).equals("Info")) {
                                    c = new Info(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                }
                                w.getChildren().add(c);
                            }
                            reader.endArray();
                        }
                        this.windows.add(w);
                        reader.endObject();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        reader.skipValue();
                    }
                }
                reader.endArray();
                try {
                    reader.nextName();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        int id = 0;
                        int x = 0;
                        int y = 0;
                        boolean opened = false;
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            if (name.equals("ID")) {
                                id = reader.nextInt();
                                continue;
                            }
                            if (name.equals("X")) {
                                x = reader.nextInt();
                                continue;
                            }
                            if (name.equals("Y")) {
                                y = reader.nextInt();
                                continue;
                            }
                            if (!name.equals("Opened")) continue;
                            opened = reader.nextBoolean();
                        }
                        reader.endObject();
                        if (id == 0) continue;
                        Window w = null;
                        for (Window win : this.staticWindows) {
                            if (win.getId() != id) continue;
                            w = win;
                            break;
                        }
                        if (w == null) continue;
                        if (x != 0) {
                            w.setX(x);
                        }
                        if (y != 0) {
                            w.setY(y);
                        }
                        w.setOpen(opened);
                    }
                    reader.endArray();
                    reader.endObject();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                for (Window w : this.windows) {
                    w.repositionComponents();
                }
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
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.windows.size() <= 0) {
            this.loadPreset(this.defaultPreset);
        }
        this.reloadHubWindow();
    }

    public void saveWindows() {
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonWriter writer = new JsonWriter((Writer)new FileWriter(this.windowFile));){
                writer.setIndent("    ");
                writer.beginObject();
                writer.name("Windows");
                writer.beginArray();
                for (Window w : this.windows) {
                    writer.beginObject();
                    writer.name("ID");
                    writer.value((long)w.getId());
                    writer.name("Title");
                    writer.value(w.getTitle());
                    writer.name("X");
                    writer.value((long)w.getX());
                    writer.name("Y");
                    writer.value((long)w.getY());
                    writer.name("Enabled");
                    writer.value(w.isEnabled());
                    writer.name("Opened");
                    writer.value(w.isOpen());
                    writer.name("Pinned");
                    writer.value(w.isPinned());
                    writer.name("Pinnable");
                    writer.value(w.isPinnable());
                    writer.name("Components");
                    writer.beginArray();
                    for (Component c : w.getChildren()) {
                        writer.beginObject();
                        writer.name("ID");
                        writer.value((long)c.id);
                        writer.name("Title");
                        writer.value(c.title);
                        writer.name("Offset X");
                        writer.value((long)c.offX);
                        writer.name("Offset Y");
                        writer.value((long)c.offY);
                        writer.name("Tooltip");
                        writer.value(c.tooltip);
                        writer.name("Type");
                        writer.value(c.type);
                        if (c instanceof ModuleButton) {
                            writer.name("Module");
                            writer.value(((ModuleButton)c).getModule().getName());
                        } else if (c instanceof CommandButton) {
                            writer.name("Command");
                            writer.value(((CommandButton)c).command);
                        } else if (c instanceof Checkbox) {
                            writer.name("Storage");
                            writer.value(((Checkbox)c).storage.getTitle());
                            writer.name("Setting");
                            writer.value(((Checkbox)c).setting);
                        } else if (c instanceof Slider) {
                            Slider slider = (Slider)c;
                            writer.name("Storage");
                            writer.value(slider.getStorage().getTitle());
                            writer.name("Setting");
                            writer.value(slider.getSetting());
                            writer.name("Minimum");
                            writer.value((double)slider.min);
                            writer.name("Maximum");
                            writer.value((double)slider.max);
                            writer.name("Increment");
                            writer.value((double)slider.increment);
                        } else if (c instanceof WindowButton) {
                            writer.name("Window ID");
                            writer.value((long)((WindowButton)c).getWindowId());
                        }
                        writer.endObject();
                    }
                    writer.endArray();
                    writer.endObject();
                }
                writer.endArray();
                writer.name("Static Windows");
                writer.beginArray();
                for (Window w : this.staticWindows) {
                    writer.beginObject();
                    writer.name("ID");
                    writer.value((long)w.getId());
                    writer.name("X");
                    writer.value((long)w.getX());
                    writer.name("Y");
                    writer.value((long)w.getY());
                    writer.name("Opened");
                    writer.value(w.isOpen());
                    writer.endObject();
                }
                writer.endArray();
                writer.endObject();
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPresets() {
        this.presets.clear();
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonReader reader = new JsonReader((Reader)new FileReader(this.presetsFile));){
                reader.beginArray();
                while (reader.hasNext()) {
                    try {
                        reader.beginObject();
                        WindowPreset wp = new WindowPreset("");
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            if (name.equals("Title")) {
                                wp.setTitle(reader.nextString());
                            }
                            if (name.equals("ID")) {
                                wp.setId(reader.nextInt());
                            }
                            if (!name.equals("Windows")) continue;
                            reader.beginArray();
                            while (reader.hasNext()) {
                                try {
                                    reader.beginObject();
                                    Window w = new Window("", 0, 0);
                                    while (reader.hasNext()) {
                                        String name1 = reader.nextName();
                                        if (name1.equals("ID")) {
                                            w.setId(reader.nextInt());
                                        }
                                        if (name1.equals("Title")) {
                                            w.setTitle(reader.nextString());
                                        }
                                        if (name1.equals("X")) {
                                            w.setX(reader.nextInt());
                                        }
                                        if (name1.equals("Y")) {
                                            w.setY(reader.nextInt());
                                        }
                                        if (name1.equals("Enabled")) {
                                            w.setEnabled(reader.nextBoolean());
                                        }
                                        if (name1.equals("Opened")) {
                                            w.setOpen(reader.nextBoolean());
                                        }
                                        if (name1.equals("Pinned")) {
                                            w.setPinned(reader.nextBoolean());
                                        }
                                        if (name1.equals("Pinnable")) {
                                            w.setPinnable(reader.nextBoolean());
                                        }
                                        if (!name1.equals("Components")) continue;
                                        reader.beginArray();
                                        while (reader.hasNext()) {
                                            Component c = null;
                                            HashMap<String, String> cd = new HashMap<String, String>();
                                            reader.beginObject();
                                            while (reader.hasNext()) {
                                                cd.put(reader.nextName(), reader.nextString());
                                            }
                                            reader.endObject();
                                            if (((String)cd.get("Type")).equals("ModuleButton")) {
                                                c = new ModuleButton(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"), Wolfram.getWolfram().moduleManager.getModule((String)cd.get("Module")));
                                            } else if (((String)cd.get("Type")).equals("CommandButton")) {
                                                c = new CommandButton(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"), (String)cd.get("Command"));
                                            } else if (((String)cd.get("Type")).equals("Checkbox")) {
                                                c = new Checkbox((MapStorage)Wolfram.getWolfram().storageManager.getByName((String)cd.get("Storage")), w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Setting"), (String)cd.get("Tooltip"));
                                            } else if (((String)cd.get("Type")).equals("Slider")) {
                                                c = new Slider((MapStorage)Wolfram.getWolfram().storageManager.getByName((String)cd.get("Storage")), w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Setting"), Float.parseFloat((String)cd.get("Minimum")), Float.parseFloat((String)cd.get("Maximum")), Float.parseFloat((String)cd.get("Increment")), (String)cd.get("Tooltip"));
                                            } else if (((String)cd.get("Type")).equals("WindowButton")) {
                                                c = new WindowButton(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"), Integer.parseInt((String)cd.get("Window ID")));
                                            } else if (((String)cd.get("Type")).equals("TextRadar")) {
                                                c = new TextRadar(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                            } else if (((String)cd.get("Type")).equals("TabGui")) {
                                                c = new TabGui(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                            } else if (((String)cd.get("Type")).equals("Radar")) {
                                                c = new Radar(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                            } else if (((String)cd.get("Type")).equals("ModuleList")) {
                                                c = new ModuleList(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                            } else if (((String)cd.get("Type")).equals("Info")) {
                                                c = new Info(w, Integer.parseInt((String)cd.get("ID")), Integer.parseInt((String)cd.get("Offset X")), Integer.parseInt((String)cd.get("Offset Y")), (String)cd.get("Title"), (String)cd.get("Tooltip"));
                                            }
                                            w.getChildren().add(c);
                                        }
                                        reader.endArray();
                                    }
                                    wp.add(w);
                                    reader.endObject();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                    reader.skipValue();
                                }
                            }
                            reader.endArray();
                        }
                        for (Window w : wp) {
                            w.repositionComponents();
                        }
                        this.presets.add(wp);
                        reader.endObject();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        reader.skipValue();
                    }
                }
                reader.endArray();
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePresets() {
        try {
            Throwable throwable = null;
            Object var2_4 = null;
            try (JsonWriter writer = new JsonWriter((Writer)new FileWriter(this.presetsFile));){
                writer.setIndent("    ");
                writer.beginArray();
                for (WindowPreset preset : this.presets) {
                    writer.beginObject();
                    writer.name("Title");
                    writer.value(preset.getTitle());
                    writer.name("ID");
                    writer.value((long)preset.getId());
                    writer.name("Windows");
                    writer.beginArray();
                    for (Window window : preset) {
                        writer.beginObject();
                        writer.name("ID");
                        writer.value((long)window.getId());
                        writer.name("Title");
                        writer.value(window.getTitle());
                        writer.name("X");
                        writer.value((long)window.getX());
                        writer.name("Y");
                        writer.value((long)window.getY());
                        writer.name("Enabled");
                        writer.value(window.isEnabled());
                        writer.name("Opened");
                        writer.value(window.isOpen());
                        writer.name("Pinned");
                        writer.value(window.isPinned());
                        writer.name("Pinnable");
                        writer.value(window.isPinnable());
                        writer.name("Components");
                        writer.beginArray();
                        for (Component component : window.getChildren()) {
                            writer.beginObject();
                            writer.name("ID");
                            writer.value((long)component.id);
                            writer.name("Title");
                            writer.value(component.title);
                            writer.name("Offset X");
                            writer.value((long)component.offX);
                            writer.name("Offset Y");
                            writer.value((long)component.offY);
                            writer.name("Tooltip");
                            writer.value(component.tooltip);
                            writer.name("Type");
                            writer.value(component.type);
                            if (component instanceof ModuleButton) {
                                writer.name("Module");
                                writer.value(((ModuleButton)component).getModule().getName());
                            } else if (component instanceof CommandButton) {
                                writer.name("Command");
                                writer.value(((CommandButton)component).command);
                            } else if (component instanceof Checkbox) {
                                Checkbox checkbox = (Checkbox)component;
                                writer.name("Storage");
                                writer.value(checkbox.storage.getTitle());
                                writer.name("Setting");
                                writer.value(checkbox.setting);
                            } else if (component instanceof Slider) {
                                Slider slider = (Slider)component;
                                writer.name("Storage");
                                writer.value(slider.getStorage().getTitle());
                                writer.name("Setting");
                                writer.value(slider.getSetting());
                                writer.name("Minimum");
                                writer.value((double)slider.min);
                                writer.name("Maximum");
                                writer.value((double)slider.max);
                                writer.name("Increment");
                                writer.value((double)slider.increment);
                            } else if (component instanceof WindowButton) {
                                writer.name("Window ID");
                                writer.value((long)((WindowButton)component).getWindowId());
                            }
                            writer.endObject();
                        }
                        writer.endArray();
                        writer.endObject();
                    }
                    writer.endArray();
                    writer.endObject();
                }
                writer.endArray();
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPreset(WindowPreset preset) {
        this.windows.clear();
        for (Window window : preset) {
            this.windows.add(window);
        }
        this.reloadHubWindow();
    }

    public void setupDefaultPreset() {
        this.defaultPreset.clear();
        int x = 110;
        int y = 5;
        int id = 0;
        Module.Category[] categoryArray = Module.Category.values();
        int n = categoryArray.length;
        int n2 = 0;
        while (n2 < n) {
            Module.Category category = categoryArray[n2];
            Window window = new Window(category.categoryName, x, y);
            for (Module module : Wolfram.getWolfram().moduleManager.getList()) {
                if (module.getCategory() != category) continue;
                window.getChildren().add(new ModuleButton(window, id, 0, 0, module.getDisplayName(), module.getDescription(), module));
                ++id;
            }
            window.repositionComponents();
            this.defaultPreset.add(window);
            y += 20;
            ++n2;
        }
        y = 5;
        Window radar = new Window("Radar", x += 105, y);
        radar.getChildren().add(new Radar(radar, 0, 0, 18, "Radar", ""));
        radar.repositionComponents();
        radar.setPinnable(true);
        this.defaultPreset.add(radar);
        Window textRadar = new Window("Text Radar", x, y + 20);
        textRadar.getChildren().add(new TextRadar(textRadar, 0, 0, 18, "Text Radar", ""));
        textRadar.repositionComponents();
        textRadar.setPinnable(true);
        this.defaultPreset.add(textRadar);
        Window arrayList = new Window("Array List", x, y + 40);
        arrayList.getChildren().add(new ModuleList(arrayList, 0, 0, 18, "Array List", ""));
        arrayList.repositionComponents();
        arrayList.setPinnable(true);
        this.defaultPreset.add(arrayList);
        Window info = new Window("Info", x, y + 60);
        info.getChildren().add(new Info(info, 0, 0, 18, "Info", ""));
        info.repositionComponents();
        info.setPinnable(true);
        this.defaultPreset.add(info);
        Window settings = new Window("Settings", x, y + 80);
        id = 0;
        for (SettingData s : Wolfram.getWolfram().settingsData) {
            if (s.isFloat) {
                settings.getChildren().add(new Slider(s.storage, settings, id, 0, 18 * (id + 1), s.name, s.setting, s.min, s.max, s.increment, s.description));
            } else {
                settings.getChildren().add(new Checkbox(s.storage, settings, id, 0, 18 * (id + 1), s.name, s.setting, s.description));
            }
            ++id;
        }
        settings.repositionComponents();
        this.defaultPreset.add(settings);
        Window preferences = new Window("Preferences", x, y + 100);
        id = 0;
        for (SettingData s : Wolfram.getWolfram().preferencesData) {
            if (s.isFloat) {
                preferences.getChildren().add(new Slider(s.storage, preferences, id, 0, 18 * (id + 1), s.name, s.setting, s.min, s.max, s.increment, s.description));
            } else {
                preferences.getChildren().add(new Checkbox(s.storage, preferences, id, 0, 18 * (id + 1), s.name, s.setting, s.description));
            }
            ++id;
        }
        preferences.repositionComponents();
        this.defaultPreset.add(preferences);
        Window killaura = new Window("Killaura", x, y + 120);
        id = 0;
        for (SettingData s : Killaura.settingsData) {
            if (s.isFloat) {
                killaura.getChildren().add(new Slider(s.storage, killaura, id, 0, 18 * (id + 1), s.name, s.setting, s.min, s.max, s.increment, s.description));
            } else {
                killaura.getChildren().add(new Checkbox(s.storage, killaura, id, 0, 18 * (id + 1), s.name, s.setting, s.description));
            }
            ++id;
        }
        killaura.repositionComponents();
        this.defaultPreset.add(killaura);
        Window tabGui = new Window("Tab GUI", 5, 25);
        tabGui.getChildren().add(new TabGui(tabGui, 0, 0, 18, "Tab GUI", ""));
        tabGui.repositionComponents();
        tabGui.setPinnable(true);
        this.defaultPreset.add(tabGui);
        this.defaultPreset.setId(-1);
    }

    public Window getWindowByID(int id) {
        for (Window w : this.windows) {
            if (w.getId() != id) continue;
            return w;
        }
        for (Window w : this.staticWindows) {
            if (w.getId() != id) continue;
            return w;
        }
        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void save() {
        this.saveWindows();
        this.savePresets();
    }

    public static int getMainColor() {
        return mainColor;
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public ArrayList<Window> getWindows() {
        return this.windows;
    }

    public ArrayList<Window> getStaticWindows() {
        return this.staticWindows;
    }

    public ArrayList<WindowPreset> getPresets() {
        return this.presets;
    }

    public WindowPreset getDefaultPreset() {
        return this.defaultPreset;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}

