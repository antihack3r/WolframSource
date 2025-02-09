/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatLayerInfo;

public class FlatGeneratorInfo {
    private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
    private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
    private int biomeToUse;

    public int getBiome() {
        return this.biomeToUse;
    }

    public void setBiome(int biome) {
        this.biomeToUse = biome;
    }

    public Map<String, Map<String, String>> getWorldFeatures() {
        return this.worldFeatures;
    }

    public List<FlatLayerInfo> getFlatLayers() {
        return this.flatLayers;
    }

    public void updateLayers() {
        int i = 0;
        for (FlatLayerInfo flatlayerinfo : this.flatLayers) {
            flatlayerinfo.setMinY(i);
            i += flatlayerinfo.getLayerCount();
        }
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(3);
        stringbuilder.append(";");
        int i = 0;
        while (i < this.flatLayers.size()) {
            if (i > 0) {
                stringbuilder.append(",");
            }
            stringbuilder.append(this.flatLayers.get(i));
            ++i;
        }
        stringbuilder.append(";");
        stringbuilder.append(this.biomeToUse);
        if (this.worldFeatures.isEmpty()) {
            stringbuilder.append(";");
        } else {
            stringbuilder.append(";");
            int k = 0;
            for (Map.Entry<String, Map<String, String>> entry : this.worldFeatures.entrySet()) {
                if (k++ > 0) {
                    stringbuilder.append(",");
                }
                stringbuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
                Map<String, String> map = entry.getValue();
                if (map.isEmpty()) continue;
                stringbuilder.append("(");
                int j = 0;
                for (Map.Entry<String, String> entry1 : map.entrySet()) {
                    if (j++ > 0) {
                        stringbuilder.append(" ");
                    }
                    stringbuilder.append(entry1.getKey());
                    stringbuilder.append("=");
                    stringbuilder.append(entry1.getValue());
                }
                stringbuilder.append(")");
            }
        }
        return stringbuilder.toString();
    }

    private static FlatLayerInfo getLayerFromString(int p_180715_0_, String p_180715_1_, int p_180715_2_) {
        Block block;
        int j;
        int i;
        block15: {
            String[] astring = p_180715_0_ >= 3 ? p_180715_1_.split("\\*", 2) : p_180715_1_.split("x", 2);
            i = 1;
            j = 0;
            if (astring.length == 2) {
                try {
                    i = Integer.parseInt(astring[0]);
                    if (p_180715_2_ + i >= 256) {
                        i = 256 - p_180715_2_;
                    }
                    if (i < 0) {
                        i = 0;
                    }
                }
                catch (Throwable var8) {
                    return null;
                }
            }
            try {
                String s = astring[astring.length - 1];
                if (p_180715_0_ < 3) {
                    astring = s.split(":", 2);
                    if (astring.length > 1) {
                        j = Integer.parseInt(astring[1]);
                    }
                    block = Block.getBlockById(Integer.parseInt(astring[0]));
                    break block15;
                }
                astring = s.split(":", 3);
                Block block2 = block = astring.length > 1 ? Block.getBlockFromName(String.valueOf(astring[0]) + ":" + astring[1]) : null;
                if (block != null) {
                    j = astring.length > 2 ? Integer.parseInt(astring[2]) : 0;
                } else {
                    block = Block.getBlockFromName(astring[0]);
                    if (block != null) {
                        int n = j = astring.length > 1 ? Integer.parseInt(astring[1]) : 0;
                    }
                }
                if (block != null) break block15;
                return null;
            }
            catch (Throwable var9) {
                return null;
            }
        }
        if (block == Blocks.AIR) {
            j = 0;
        }
        if (j < 0 || j > 15) {
            j = 0;
        }
        FlatLayerInfo flatlayerinfo = new FlatLayerInfo(p_180715_0_, i, block, j);
        flatlayerinfo.setMinY(p_180715_2_);
        return flatlayerinfo;
    }

    private static List<FlatLayerInfo> getLayersFromString(int p_180716_0_, String p_180716_1_) {
        if (p_180716_1_ != null && p_180716_1_.length() >= 1) {
            ArrayList list = Lists.newArrayList();
            String[] astring = p_180716_1_.split(",");
            int i = 0;
            String[] stringArray = astring;
            int n = astring.length;
            int n2 = 0;
            while (n2 < n) {
                String s = stringArray[n2];
                FlatLayerInfo flatlayerinfo = FlatGeneratorInfo.getLayerFromString(p_180716_0_, s, i);
                if (flatlayerinfo == null) {
                    return null;
                }
                list.add(flatlayerinfo);
                i += flatlayerinfo.getLayerCount();
                ++n2;
            }
            return list;
        }
        return null;
    }

    public static FlatGeneratorInfo createFlatGeneratorFromString(String flatGeneratorSettings) {
        int i;
        if (flatGeneratorSettings == null) {
            return FlatGeneratorInfo.getDefaultFlatGenerator();
        }
        String[] astring = flatGeneratorSettings.split(";", -1);
        int n = i = astring.length == 1 ? 0 : MathHelper.getInt(astring[0], 0);
        if (i >= 0 && i <= 3) {
            List<FlatLayerInfo> list;
            FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
            int j = astring.length == 1 ? 0 : 1;
            if ((list = FlatGeneratorInfo.getLayersFromString(i, astring[j++])) != null && !list.isEmpty()) {
                flatgeneratorinfo.getFlatLayers().addAll(list);
                flatgeneratorinfo.updateLayers();
                int k = Biome.getIdForBiome(Biomes.PLAINS);
                if (i > 0 && astring.length > j) {
                    k = MathHelper.getInt(astring[j++], k);
                }
                flatgeneratorinfo.setBiome(k);
                if (i > 0 && astring.length > j) {
                    String[] astring1;
                    String[] stringArray = astring1 = astring[j++].toLowerCase(Locale.ROOT).split(",");
                    int n2 = astring1.length;
                    int n3 = 0;
                    while (n3 < n2) {
                        String s = stringArray[n3];
                        String[] astring2 = s.split("\\(", 2);
                        HashMap map = Maps.newHashMap();
                        if (!astring2[0].isEmpty()) {
                            flatgeneratorinfo.getWorldFeatures().put(astring2[0], map);
                            if (astring2.length > 1 && astring2[1].endsWith(")") && astring2[1].length() > 1) {
                                String[] astring3;
                                String[] stringArray2 = astring3 = astring2[1].substring(0, astring2[1].length() - 1).split(" ");
                                int n4 = astring3.length;
                                int n5 = 0;
                                while (n5 < n4) {
                                    String s1 = stringArray2[n5];
                                    String[] astring4 = s1.split("=", 2);
                                    if (astring4.length == 2) {
                                        map.put(astring4[0], astring4[1]);
                                    }
                                    ++n5;
                                }
                            }
                        }
                        ++n3;
                    }
                } else {
                    flatgeneratorinfo.getWorldFeatures().put("village", Maps.newHashMap());
                }
                return flatgeneratorinfo;
            }
            return FlatGeneratorInfo.getDefaultFlatGenerator();
        }
        return FlatGeneratorInfo.getDefaultFlatGenerator();
    }

    public static FlatGeneratorInfo getDefaultFlatGenerator() {
        FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
        flatgeneratorinfo.setBiome(Biome.getIdForBiome(Biomes.PLAINS));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
        flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS));
        flatgeneratorinfo.updateLayers();
        flatgeneratorinfo.getWorldFeatures().put("village", Maps.newHashMap());
        return flatgeneratorinfo;
    }
}

