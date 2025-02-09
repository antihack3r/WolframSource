/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.clickgui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import net.wolframclient.Wolfram;
import net.wolframclient.clickgui.Component;
import net.wolframclient.clickgui.Window;
import net.wolframclient.clickgui.WolframGui;
import net.wolframclient.event.EventTarget;
import net.wolframclient.event.Listener;
import net.wolframclient.event.events.KeyPressEvent;
import net.wolframclient.gui.GuiManager;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.module.Module;
import net.wolframclient.utils.RenderUtils;

public final class TabGui
extends Component
implements Listener {
    private static final HashSet<TabGui> registeredTabGuis = new HashSet();
    private final List<Tab> tabs = new ArrayList<Tab>();
    private int tabIndex = 0;
    private boolean heightInitialized;
    private boolean tabOpened;

    public TabGui(Window window, int id, int offX, int offY, String title, String tooltip) {
        super(window, id, offX, offY, title, tooltip);
        this.width = Math.max(80, window.getWidth());
        this.height = 80;
        this.type = "TabGui";
        this.editable = false;
        this.initTabs();
    }

    private void initTabs() {
        LinkedHashMap<Module.Category, Tab> tabMap = new LinkedHashMap<Module.Category, Tab>();
        Module.Category[] categoryArray = Module.Category.values();
        int n = categoryArray.length;
        int n2 = 0;
        while (n2 < n) {
            Module.Category category = categoryArray[n2];
            tabMap.put(category, new Tab(category.categoryName));
            ++n2;
        }
        for (Module module : Wolfram.getWolfram().moduleManager.getList()) {
            ((Tab)tabMap.get((Object)module.getCategory())).modules.add(module);
        }
        this.tabs.addAll(tabMap.values());
    }

    @Override
    public void render(int mouseX, int mouseY) {
        RenderUtils.drawRect(this.x, this.y, this.width, 1.0f, WolframGui.getBackgroundColor());
        Tab openTab = null;
        int openTabY = 0;
        int tabHeight = 12;
        int currentY = this.y + 1;
        int i = 0;
        while (i < this.tabs.size()) {
            Tab tab = this.tabs.get(i);
            RenderUtils.drawRect(this.x, currentY, this.width, tabHeight, WolframGui.getBackgroundColor());
            float fontY = (float)currentY + (float)(tabHeight - FontRenderers.SMALL.FONT_HEIGHT) / 2.0f;
            if (i == this.tabIndex && !Wolfram.getWolfram().guiManager.gui.isOpen()) {
                RenderUtils.drawRect(this.x, currentY, this.width, tabHeight, 0x40000000);
                String arrow = this.tabOpened ? "<" : ">";
                FontRenderers.SMALL.drawString(arrow, (float)(this.x + this.width - FontRenderers.SMALL.getStringWidth(arrow) - 2), fontY, GuiManager.getHexMainColor());
                if (this.tabOpened) {
                    openTabY = currentY;
                    openTab = tab;
                }
            }
            FontRenderers.SMALL.drawString(tab.name, (float)(this.x + 2), fontY, 0xFFFFFF);
            currentY += tabHeight;
            ++i;
        }
        RenderUtils.drawRect(this.x, currentY, this.width, 1.0f, WolframGui.getBackgroundColor());
        this.height = currentY + 1 - this.y;
        if (openTab != null) {
            openTab.render(this.width + this.x + 2, openTabY);
        }
        if (!this.heightInitialized) {
            this.window.repositionComponents();
            this.heightInitialized = true;
        }
        if (!registeredTabGuis.contains(this)) {
            for (TabGui tabGui : registeredTabGuis) {
                registry.unregisterListener(tabGui);
            }
            registry.registerListener(this);
            registeredTabGuis.add(this);
        }
    }

    @EventTarget
    public void onKeyPress(KeyPressEvent event) {
        if (!this.window.isOpen() || Wolfram.getWolfram().guiManager.gui.isOpen()) {
            return;
        }
        int keyCode = event.getKeyCode();
        if (this.tabOpened) {
            switch (keyCode) {
                case 203: {
                    this.tabOpened = false;
                    break;
                }
                default: {
                    this.tabs.get(this.tabIndex).onKeyPress(keyCode);
                    break;
                }
            }
        } else {
            switch (keyCode) {
                case 200: {
                    if (this.tabIndex > 0) {
                        --this.tabIndex;
                        break;
                    }
                    this.tabIndex = this.tabs.size() - 1;
                    break;
                }
                case 208: {
                    if (this.tabIndex < this.tabs.size() - 1) {
                        ++this.tabIndex;
                        break;
                    }
                    this.tabIndex = 0;
                    break;
                }
                case 205: {
                    this.tabOpened = true;
                }
            }
        }
    }

    @Override
    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
    }

    private static final class Tab {
        private final String name;
        private final List<Module> modules = new ArrayList<Module>();
        private int selectedIndex = 0;

        public Tab(String name) {
            this.name = name;
        }

        public void render(int x, int currentY) {
            int width = this.getWidth();
            int height = 10;
            RenderUtils.drawRect(x, currentY - 1, width, 1.0f, WolframGui.getBackgroundColor());
            int i = 0;
            while (i < this.modules.size()) {
                Module module = this.modules.get(i);
                RenderUtils.drawRect(x, currentY, width, height, WolframGui.getBackgroundColor());
                float fontX = x + 2;
                float fontY = (float)(currentY + height / 2) - (float)FontRenderers.SMALL.FONT_HEIGHT / 2.0f;
                if (i == this.selectedIndex) {
                    RenderUtils.drawRect(x, currentY, width, height, 0x40000000);
                    String arrow = "> ";
                    FontRenderers.SMALL.drawString(arrow, fontX, fontY, GuiManager.getHexMainColor());
                    fontX += (float)FontRenderers.SMALL.getStringWidth(arrow);
                }
                FontRenderers.SMALL.drawString(module.getName(), fontX, fontY, module.isEnabled() ? GuiManager.getHexMainColor() : 0xFFFFFF);
                currentY += height;
                ++i;
            }
            RenderUtils.drawRect(x, currentY, width, 1.0f, WolframGui.getBackgroundColor());
        }

        private int getWidth() {
            int maxWidth = 0;
            for (Module module : this.modules) {
                int width = FontRenderers.DEFAULT.getStringWidth(module.getName());
                if (width <= maxWidth) continue;
                maxWidth = width;
            }
            return maxWidth + FontRenderers.DEFAULT.getStringWidth("> ") + 4;
        }

        public void onKeyPress(int keyCode) {
            switch (keyCode) {
                case 200: {
                    if (this.selectedIndex > 0) {
                        --this.selectedIndex;
                        break;
                    }
                    this.selectedIndex = this.modules.size() - 1;
                    break;
                }
                case 208: {
                    if (this.selectedIndex < this.modules.size() - 1) {
                        ++this.selectedIndex;
                        break;
                    }
                    this.selectedIndex = 0;
                    break;
                }
                case 28: {
                    this.modules.get(this.selectedIndex).toggleModule();
                }
            }
        }
    }
}

