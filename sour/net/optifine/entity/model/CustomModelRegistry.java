/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.src.Config;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.entity.model.ModelAdapterArmorStand;
import net.optifine.entity.model.ModelAdapterBanner;
import net.optifine.entity.model.ModelAdapterBat;
import net.optifine.entity.model.ModelAdapterBlaze;
import net.optifine.entity.model.ModelAdapterBoat;
import net.optifine.entity.model.ModelAdapterBook;
import net.optifine.entity.model.ModelAdapterCaveSpider;
import net.optifine.entity.model.ModelAdapterChest;
import net.optifine.entity.model.ModelAdapterChestLarge;
import net.optifine.entity.model.ModelAdapterChicken;
import net.optifine.entity.model.ModelAdapterCow;
import net.optifine.entity.model.ModelAdapterCreeper;
import net.optifine.entity.model.ModelAdapterDonkey;
import net.optifine.entity.model.ModelAdapterDragon;
import net.optifine.entity.model.ModelAdapterEnderChest;
import net.optifine.entity.model.ModelAdapterEnderCrystal;
import net.optifine.entity.model.ModelAdapterEnderCrystalNoBase;
import net.optifine.entity.model.ModelAdapterEnderman;
import net.optifine.entity.model.ModelAdapterEndermite;
import net.optifine.entity.model.ModelAdapterEvoker;
import net.optifine.entity.model.ModelAdapterEvokerFangs;
import net.optifine.entity.model.ModelAdapterGhast;
import net.optifine.entity.model.ModelAdapterGuardian;
import net.optifine.entity.model.ModelAdapterHeadDragon;
import net.optifine.entity.model.ModelAdapterHeadHumanoid;
import net.optifine.entity.model.ModelAdapterHeadSkeleton;
import net.optifine.entity.model.ModelAdapterHorse;
import net.optifine.entity.model.ModelAdapterHusk;
import net.optifine.entity.model.ModelAdapterIronGolem;
import net.optifine.entity.model.ModelAdapterLeadKnot;
import net.optifine.entity.model.ModelAdapterLlama;
import net.optifine.entity.model.ModelAdapterMagmaCube;
import net.optifine.entity.model.ModelAdapterMinecart;
import net.optifine.entity.model.ModelAdapterMinecartMobSpawner;
import net.optifine.entity.model.ModelAdapterMinecartTnt;
import net.optifine.entity.model.ModelAdapterMooshroom;
import net.optifine.entity.model.ModelAdapterMule;
import net.optifine.entity.model.ModelAdapterOcelot;
import net.optifine.entity.model.ModelAdapterPig;
import net.optifine.entity.model.ModelAdapterPigZombie;
import net.optifine.entity.model.ModelAdapterPolarBear;
import net.optifine.entity.model.ModelAdapterRabbit;
import net.optifine.entity.model.ModelAdapterSheep;
import net.optifine.entity.model.ModelAdapterSheepWool;
import net.optifine.entity.model.ModelAdapterShulker;
import net.optifine.entity.model.ModelAdapterShulkerBox;
import net.optifine.entity.model.ModelAdapterShulkerBullet;
import net.optifine.entity.model.ModelAdapterSign;
import net.optifine.entity.model.ModelAdapterSilverfish;
import net.optifine.entity.model.ModelAdapterSkeleton;
import net.optifine.entity.model.ModelAdapterSkeletonHorse;
import net.optifine.entity.model.ModelAdapterSlime;
import net.optifine.entity.model.ModelAdapterSnowman;
import net.optifine.entity.model.ModelAdapterSpider;
import net.optifine.entity.model.ModelAdapterSquid;
import net.optifine.entity.model.ModelAdapterStray;
import net.optifine.entity.model.ModelAdapterVex;
import net.optifine.entity.model.ModelAdapterVillager;
import net.optifine.entity.model.ModelAdapterVindicator;
import net.optifine.entity.model.ModelAdapterWitch;
import net.optifine.entity.model.ModelAdapterWither;
import net.optifine.entity.model.ModelAdapterWitherSkeleton;
import net.optifine.entity.model.ModelAdapterWitherSkull;
import net.optifine.entity.model.ModelAdapterWolf;
import net.optifine.entity.model.ModelAdapterZombie;
import net.optifine.entity.model.ModelAdapterZombieHorse;
import net.optifine.entity.model.ModelAdapterZombieVillager;

public class CustomModelRegistry {
    private static Map<String, ModelAdapter> mapModelAdapters = CustomModelRegistry.makeMapModelAdapters();

    private static Map<String, ModelAdapter> makeMapModelAdapters() {
        LinkedHashMap<String, ModelAdapter> map = new LinkedHashMap<String, ModelAdapter>();
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterArmorStand());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBat());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBlaze());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBoat());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCaveSpider());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterChicken());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCow());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCreeper());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterDragon());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterDonkey());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderCrystal());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderCrystalNoBase());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderman());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEndermite());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEvoker());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEvokerFangs());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterGhast());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterGuardian());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHorse());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHusk());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterIronGolem());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterLeadKnot());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterLlama());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMagmaCube());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecart());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartTnt());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartMobSpawner());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMooshroom());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMule());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterOcelot());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPig());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPigZombie());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPolarBear());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterRabbit());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSheep());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterShulker());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterShulkerBullet());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSilverfish());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSkeletonHorse());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSlime());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSnowman());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSpider());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSquid());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterStray());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterVex());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterVillager());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterVindicator());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWitch());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWither());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWitherSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWitherSkull());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWolf());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombie());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombieHorse());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombieVillager());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSheepWool());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBanner());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBook());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterChest());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterChestLarge());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderChest());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadDragon());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadHumanoid());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterShulkerBox());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSign());
        return map;
    }

    private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter) {
        if (map.containsKey(modelAdapter.getName())) {
            Config.warn("Model adapter already registered for id: " + modelAdapter.getName() + ", class: " + modelAdapter.getEntityClass().getName());
        }
        map.put(modelAdapter.getName(), modelAdapter);
    }

    public static ModelAdapter getModelAdapter(String name) {
        return mapModelAdapters.get(name);
    }

    public static String[] getModelNames() {
        Set<String> set = mapModelAdapters.keySet();
        String[] astring = set.toArray(new String[set.size()]);
        return astring;
    }
}

