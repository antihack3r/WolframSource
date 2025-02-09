/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.module;

import net.wolframclient.Wolfram;
import net.wolframclient.module.Module;
import net.wolframclient.module.modules.auto.AutoEat;
import net.wolframclient.module.modules.auto.AutoFish;
import net.wolframclient.module.modules.auto.AutoMine;
import net.wolframclient.module.modules.auto.AutoSwim;
import net.wolframclient.module.modules.auto.AutoTool;
import net.wolframclient.module.modules.auto.AutoWalk;
import net.wolframclient.module.modules.combat.AimAssist;
import net.wolframclient.module.modules.combat.Aimbot;
import net.wolframclient.module.modules.combat.AutoArmor;
import net.wolframclient.module.modules.combat.AutoAttack;
import net.wolframclient.module.modules.combat.AutoShoot;
import net.wolframclient.module.modules.combat.AutoSoup;
import net.wolframclient.module.modules.combat.AutoSword;
import net.wolframclient.module.modules.combat.BowAimbot;
import net.wolframclient.module.modules.combat.ClickAimbot;
import net.wolframclient.module.modules.combat.ClickAura;
import net.wolframclient.module.modules.combat.Criticals;
import net.wolframclient.module.modules.combat.FastBow;
import net.wolframclient.module.modules.combat.Fightbot;
import net.wolframclient.module.modules.combat.ForceField;
import net.wolframclient.module.modules.combat.Killaura;
import net.wolframclient.module.modules.combat.Reach;
import net.wolframclient.module.modules.fun.Derp;
import net.wolframclient.module.modules.fun.Headless;
import net.wolframclient.module.modules.fun.Twerk;
import net.wolframclient.module.modules.movement.AirMove;
import net.wolframclient.module.modules.movement.AirWalk;
import net.wolframclient.module.modules.movement.Blink;
import net.wolframclient.module.modules.movement.BunnyHop;
import net.wolframclient.module.modules.movement.CreativeFly;
import net.wolframclient.module.modules.movement.Dolphin;
import net.wolframclient.module.modules.movement.FastFall;
import net.wolframclient.module.modules.movement.FastLadder;
import net.wolframclient.module.modules.movement.Flight;
import net.wolframclient.module.modules.movement.Flip;
import net.wolframclient.module.modules.movement.FlyNCP;
import net.wolframclient.module.modules.movement.FlyVanilla;
import net.wolframclient.module.modules.movement.Glide;
import net.wolframclient.module.modules.movement.GlideFly;
import net.wolframclient.module.modules.movement.Jesus;
import net.wolframclient.module.modules.movement.NoFall;
import net.wolframclient.module.modules.movement.ParkourJump;
import net.wolframclient.module.modules.movement.Phase;
import net.wolframclient.module.modules.movement.SilentSneak;
import net.wolframclient.module.modules.movement.Sneak;
import net.wolframclient.module.modules.movement.Speed;
import net.wolframclient.module.modules.movement.Spider;
import net.wolframclient.module.modules.movement.Sprint;
import net.wolframclient.module.modules.movement.SprintLegit;
import net.wolframclient.module.modules.movement.Step;
import net.wolframclient.module.modules.movement.StepLegit;
import net.wolframclient.module.modules.others.AntiAFK;
import net.wolframclient.module.modules.others.FakeHackers;
import net.wolframclient.module.modules.others.MiddleClickFriends;
import net.wolframclient.module.modules.others.Panic;
import net.wolframclient.module.modules.player.Enhance;
import net.wolframclient.module.modules.player.FastBreak;
import net.wolframclient.module.modules.player.FastPlace;
import net.wolframclient.module.modules.player.Freecam;
import net.wolframclient.module.modules.player.Ghost;
import net.wolframclient.module.modules.player.MoreInventory;
import net.wolframclient.module.modules.player.NoBreakDelay;
import net.wolframclient.module.modules.player.Regen;
import net.wolframclient.module.modules.player.Zoot;
import net.wolframclient.module.modules.render.ArmorESP;
import net.wolframclient.module.modules.render.ArrowTrajectories;
import net.wolframclient.module.modules.render.Breadcrumbs;
import net.wolframclient.module.modules.render.ChestESP;
import net.wolframclient.module.modules.render.CompassTracer;
import net.wolframclient.module.modules.render.FarmhuntESP;
import net.wolframclient.module.modules.render.Fullbright;
import net.wolframclient.module.modules.render.ItemESP;
import net.wolframclient.module.modules.render.ItemLabels;
import net.wolframclient.module.modules.render.MobESP;
import net.wolframclient.module.modules.render.MobSpawnESP;
import net.wolframclient.module.modules.render.Nametags;
import net.wolframclient.module.modules.render.PlayerESP;
import net.wolframclient.module.modules.render.PotionEffects;
import net.wolframclient.module.modules.render.Projectiles;
import net.wolframclient.module.modules.render.ProphuntESP;
import net.wolframclient.module.modules.render.Tracers;
import net.wolframclient.module.modules.render.Xray;
import net.wolframclient.module.modules.world.ClickNuker;
import net.wolframclient.module.modules.world.Nuker;
import net.wolframclient.module.modules.world.Pathfinder;
import net.wolframclient.module.modules.world.Timer;
import net.wolframclient.utils.Manager;

public class ModuleManager
extends Manager<Module> {
    public void loadModules() {
        this.add(new AimAssist());
        this.add(new Aimbot());
        this.add(new AirMove());
        this.add(new AirWalk());
        this.add(new ArrowTrajectories());
        this.add(new AntiAFK());
        this.add(new Module("AntiHurtcam", Module.Category.RENDER, "Prevents the screen from shaking when hurt"));
        this.add(new Module("AntiSpam", Module.Category.OTHER, "Prevents people from spamming your chat"));
        this.add(new ArmorESP());
        this.add(new AutoArmor());
        this.add(new AutoAttack());
        this.add(new Module("AutoSteal", Module.Category.PLAYER, "Automatically steal all from chests"));
        this.add(new AutoEat());
        this.add(new AutoFish());
        this.add(new AutoMine());
        this.add(new AutoShoot());
        this.add(new Module("AutoRespawn", Module.Category.AUTO, "Automatically respawns after dying"));
        this.add(new AutoSoup());
        this.add(new AutoSwim());
        this.add(new AutoSword());
        this.add(new AutoTool());
        this.add(new AutoWalk());
        this.add(new Blink());
        this.add(new Breadcrumbs());
        this.add(new BunnyHop());
        this.add(new BowAimbot());
        this.add(new Module("CaveFinder", Module.Category.RENDER, "Shows all caves"));
        this.add(new Module("CameraNoClip", Module.Category.RENDER, "Let the camera in 3rd person go into walls"));
        this.add(new ChestESP());
        this.add(new ClickAimbot());
        this.add(new ClickAura());
        this.add(new ClickNuker());
        this.add(new CompassTracer());
        this.add(new CreativeFly());
        this.add(new Criticals());
        this.add(new Derp());
        this.add(new Dolphin());
        this.add(new Enhance());
        this.add(new FarmhuntESP());
        this.add(new FakeHackers());
        this.add(new FastBow());
        this.add(new FastBreak());
        this.add(new FastFall());
        this.add(new FastLadder());
        this.add(new FastPlace());
        this.add(new Fightbot());
        this.add(new Flight());
        this.add(new Flip());
        this.add(new FlyNCP());
        this.add(new FlyVanilla());
        this.add(new ForceField());
        this.add(new Freecam());
        this.add(new Fullbright());
        this.add(new Ghost());
        this.add(new Glide());
        this.add(new GlideFly());
        this.add(new Headless());
        this.add(new Module("HighJump", Module.Category.MOVEMENT, "Jumps high"));
        this.add(new Module("InvWalk", Module.Category.PLAYER, "Allows movement while inventory opened"));
        this.add(new ItemESP());
        this.add(new ItemLabels());
        this.add(new Jesus());
        this.add(new Killaura());
        this.add(new MiddleClickFriends());
        this.add(new MobESP());
        this.add(new MobSpawnESP());
        this.add(new MoreInventory());
        this.add(new Nametags());
        this.add(new Module("NoBlind", Module.Category.PLAYER, "Negates the Blindness effect"));
        this.add(new NoBreakDelay());
        this.add(new NoFall());
        this.add(new Module("NoFireworks", Module.Category.RENDER, "Disables firework particles"));
        this.add(new Module("NoOverlay", Module.Category.RENDER, "Disables water and fire overlays"));
        this.add(new Module("NoSlowdown", Module.Category.MOVEMENT, "Prevents the player from slowing down"));
        this.add(new Nuker());
        this.add(new Panic());
        this.add(new ParkourJump());
        this.add(new Pathfinder());
        this.add(new Phase());
        this.add(new PlayerESP());
        this.add(new PotionEffects());
        this.add(new Projectiles());
        this.add(new ProphuntESP());
        this.add(new Reach());
        this.add(new Regen());
        this.add(new Module("SafeWalk", Module.Category.MOVEMENT, "Prevents from falling of blocks"));
        this.add(new SilentSneak());
        this.add(new Sneak());
        this.add(new Spider());
        this.add(new Speed());
        this.add(new Sprint());
        this.add(new SprintLegit());
        this.add(new Step());
        this.add(new StepLegit());
        this.add(new Timer());
        this.add(new Tracers());
        this.add(new Twerk());
        this.add(new Module("TrueSight", Module.Category.RENDER, "Shows invisible enemies"));
        this.add(new Module("Velocity", Module.Category.COMBAT, "Handles the knockback force"));
        this.add(new Module("Wallhack", Module.Category.RENDER, "See entities through walls"));
        this.add(new Xray());
        this.add(new Zoot());
    }

    public void toggleModules() {
        for (Module module : this.getList()) {
            if (!Wolfram.getWolfram().storageManager.moduleStates.getBoolean(module.getName())) continue;
            module.setEnabled(true, false);
        }
    }

    public void disableModules() {
        for (Module m : this.getList()) {
            if (!m.isEnabled()) continue;
            m.toggleModule();
        }
    }

    public boolean isEnabled(String moduleName) {
        if (this.getModule(moduleName) == null) {
            return false;
        }
        return this.getModule(moduleName).isEnabled();
    }

    public Module getModule(String moduleName) {
        for (Module module : this.getList()) {
            if (!module.getName().equalsIgnoreCase(moduleName)) continue;
            return module;
        }
        return null;
    }

    public void clean() {
        for (Module module : this.getList()) {
            module.onShutdown();
        }
    }
}

