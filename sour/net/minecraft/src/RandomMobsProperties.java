/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedParser;
import net.minecraft.src.RandomMobsRule;
import net.minecraft.src.RangeInt;
import net.minecraft.src.RangeListInt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class RandomMobsProperties {
    public String name = null;
    public String basePath = null;
    public ResourceLocation[] resourceLocations = null;
    public RandomMobsRule[] rules = null;

    public RandomMobsProperties(String p_i74_1_, ResourceLocation[] p_i74_2_) {
        ConnectedParser connectedparser = new ConnectedParser("RandomMobs");
        this.name = connectedparser.parseName(p_i74_1_);
        this.basePath = connectedparser.parseBasePath(p_i74_1_);
        this.resourceLocations = p_i74_2_;
    }

    public RandomMobsProperties(Properties p_i75_1_, String p_i75_2_, ResourceLocation p_i75_3_) {
        ConnectedParser connectedparser = new ConnectedParser("RandomMobs");
        this.name = connectedparser.parseName(p_i75_2_);
        this.basePath = connectedparser.parseBasePath(p_i75_2_);
        this.rules = this.parseRules(p_i75_1_, p_i75_3_, connectedparser);
    }

    public ResourceLocation getTextureLocation(ResourceLocation p_getTextureLocation_1_, EntityLiving p_getTextureLocation_2_) {
        if (this.rules != null) {
            int i = 0;
            while (i < this.rules.length) {
                RandomMobsRule randommobsrule = this.rules[i];
                if (randommobsrule.matches(p_getTextureLocation_2_)) {
                    return randommobsrule.getTextureLocation(p_getTextureLocation_1_, p_getTextureLocation_2_.randomMobsId);
                }
                ++i;
            }
        }
        if (this.resourceLocations != null) {
            int j = p_getTextureLocation_2_.randomMobsId;
            int k = j % this.resourceLocations.length;
            return this.resourceLocations[k];
        }
        return p_getTextureLocation_1_;
    }

    private RandomMobsRule[] parseRules(Properties p_parseRules_1_, ResourceLocation p_parseRules_2_, ConnectedParser p_parseRules_3_) {
        ArrayList<RandomMobsRule> list = new ArrayList<RandomMobsRule>();
        int i = p_parseRules_1_.size();
        int j = 0;
        while (j < i) {
            int k = j + 1;
            String s = p_parseRules_1_.getProperty("skins." + k);
            if (s != null) {
                int[] aint = p_parseRules_3_.parseIntList(s);
                int[] aint1 = p_parseRules_3_.parseIntList(p_parseRules_1_.getProperty("weights." + k));
                Biome[] abiome = p_parseRules_3_.parseBiomes(p_parseRules_1_.getProperty("biomes." + k));
                RangeListInt rangelistint = p_parseRules_3_.parseRangeListInt(p_parseRules_1_.getProperty("heights." + k));
                if (rangelistint == null) {
                    rangelistint = this.parseMinMaxHeight(p_parseRules_1_, k);
                }
                RandomMobsRule randommobsrule = new RandomMobsRule(p_parseRules_2_, aint, aint1, abiome, rangelistint);
                list.add(randommobsrule);
            }
            ++j;
        }
        RandomMobsRule[] arandommobsrule = list.toArray(new RandomMobsRule[list.size()]);
        return arandommobsrule;
    }

    private RangeListInt parseMinMaxHeight(Properties p_parseMinMaxHeight_1_, int p_parseMinMaxHeight_2_) {
        String s = p_parseMinMaxHeight_1_.getProperty("minHeight." + p_parseMinMaxHeight_2_);
        String s1 = p_parseMinMaxHeight_1_.getProperty("maxHeight." + p_parseMinMaxHeight_2_);
        if (s == null && s1 == null) {
            return null;
        }
        int i = 0;
        if (s != null && (i = Config.parseInt(s, -1)) < 0) {
            Config.warn("Invalid minHeight: " + s);
            return null;
        }
        int j = 256;
        if (s1 != null && (j = Config.parseInt(s1, -1)) < 0) {
            Config.warn("Invalid maxHeight: " + s1);
            return null;
        }
        if (j < 0) {
            Config.warn("Invalid minHeight, maxHeight: " + s + ", " + s1);
            return null;
        }
        RangeListInt rangelistint = new RangeListInt();
        rangelistint.addRange(new RangeInt(i, j));
        return rangelistint;
    }

    public boolean isValid(String p_isValid_1_) {
        if (this.resourceLocations == null && this.rules == null) {
            Config.warn("No skins specified: " + p_isValid_1_);
            return false;
        }
        if (this.rules != null) {
            int i = 0;
            while (i < this.rules.length) {
                RandomMobsRule randommobsrule = this.rules[i];
                if (!randommobsrule.isValid(p_isValid_1_)) {
                    return false;
                }
                ++i;
            }
        }
        if (this.resourceLocations != null) {
            int j = 0;
            while (j < this.resourceLocations.length) {
                ResourceLocation resourcelocation = this.resourceLocations[j];
                if (!Config.hasResource(resourcelocation)) {
                    Config.warn("Texture not found: " + resourcelocation.getResourcePath());
                    return false;
                }
                ++j;
            }
        }
        return true;
    }
}

