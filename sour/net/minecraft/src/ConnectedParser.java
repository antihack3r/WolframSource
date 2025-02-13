/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.src;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedProperties;
import net.minecraft.src.MatchBlock;
import net.minecraft.src.RangeInt;
import net.minecraft.src.RangeListInt;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class ConnectedParser {
    private String context = null;

    public ConnectedParser(String p_i26_1_) {
        this.context = p_i26_1_;
    }

    public String parseName(String p_parseName_1_) {
        int j;
        String s = p_parseName_1_;
        int i = p_parseName_1_.lastIndexOf(47);
        if (i >= 0) {
            s = p_parseName_1_.substring(i + 1);
        }
        if ((j = s.lastIndexOf(46)) >= 0) {
            s = s.substring(0, j);
        }
        return s;
    }

    public String parseBasePath(String p_parseBasePath_1_) {
        int i = p_parseBasePath_1_.lastIndexOf(47);
        return i < 0 ? "" : p_parseBasePath_1_.substring(0, i);
    }

    public MatchBlock[] parseMatchBlocks(String p_parseMatchBlocks_1_) {
        if (p_parseMatchBlocks_1_ == null) {
            return null;
        }
        ArrayList<MatchBlock> list = new ArrayList<MatchBlock>();
        String[] astring = Config.tokenize(p_parseMatchBlocks_1_, " ");
        int i = 0;
        while (i < astring.length) {
            String s = astring[i];
            MatchBlock[] amatchblock = this.parseMatchBlock(s);
            if (amatchblock != null) {
                list.addAll(Arrays.asList(amatchblock));
            }
            ++i;
        }
        MatchBlock[] amatchblock1 = list.toArray(new MatchBlock[list.size()]);
        return amatchblock1;
    }

    public IBlockState parseBlockState(String p_parseBlockState_1_, IBlockState p_parseBlockState_2_) {
        MatchBlock[] amatchblock = this.parseMatchBlock(p_parseBlockState_1_);
        if (amatchblock == null) {
            return p_parseBlockState_2_;
        }
        if (amatchblock.length != 1) {
            return p_parseBlockState_2_;
        }
        MatchBlock matchblock = amatchblock[0];
        int i = matchblock.getBlockId();
        Block block = Block.getBlockById(i);
        return block.getDefaultState();
    }

    public MatchBlock[] parseMatchBlock(String p_parseMatchBlock_1_) {
        if (p_parseMatchBlock_1_ == null) {
            return null;
        }
        if ((p_parseMatchBlock_1_ = p_parseMatchBlock_1_.trim()).length() <= 0) {
            return null;
        }
        String[] astring = Config.tokenize(p_parseMatchBlock_1_, ":");
        String s = "minecraft";
        int i = 0;
        if (astring.length > 1 && this.isFullBlockName(astring)) {
            s = astring[0];
            i = 1;
        } else {
            s = "minecraft";
            i = 0;
        }
        String s1 = astring[i];
        String[] astring1 = Arrays.copyOfRange(astring, i + 1, astring.length);
        Block[] ablock = this.parseBlockPart(s, s1);
        if (ablock == null) {
            return null;
        }
        MatchBlock[] amatchblock = new MatchBlock[ablock.length];
        int j = 0;
        while (j < ablock.length) {
            MatchBlock matchblock;
            Block block = ablock[j];
            int k = Block.getIdFromBlock(block);
            int[] aint = null;
            if (astring1.length > 0 && (aint = this.parseBlockMetadatas(block, astring1)) == null) {
                return null;
            }
            amatchblock[j] = matchblock = new MatchBlock(k, aint);
            ++j;
        }
        return amatchblock;
    }

    public boolean isFullBlockName(String[] p_isFullBlockName_1_) {
        if (p_isFullBlockName_1_.length < 2) {
            return false;
        }
        String s = p_isFullBlockName_1_[1];
        if (s.length() < 1) {
            return false;
        }
        if (this.startsWithDigit(s)) {
            return false;
        }
        return !s.contains("=");
    }

    public boolean startsWithDigit(String p_startsWithDigit_1_) {
        if (p_startsWithDigit_1_ == null) {
            return false;
        }
        if (p_startsWithDigit_1_.length() < 1) {
            return false;
        }
        char c0 = p_startsWithDigit_1_.charAt(0);
        return Character.isDigit(c0);
    }

    public Block[] parseBlockPart(String p_parseBlockPart_1_, String p_parseBlockPart_2_) {
        if (this.startsWithDigit(p_parseBlockPart_2_)) {
            int[] aint = this.parseIntList(p_parseBlockPart_2_);
            if (aint == null) {
                return null;
            }
            Block[] ablock1 = new Block[aint.length];
            int j = 0;
            while (j < aint.length) {
                int i = aint[j];
                Block block1 = Block.getBlockById(i);
                if (block1 == null) {
                    this.warn("Block not found for id: " + i);
                    return null;
                }
                ablock1[j] = block1;
                ++j;
            }
            return ablock1;
        }
        String s = String.valueOf(p_parseBlockPart_1_) + ":" + p_parseBlockPart_2_;
        Block block = Block.getBlockFromName(s);
        if (block == null) {
            this.warn("Block not found for name: " + s);
            return null;
        }
        Block[] ablock = new Block[]{block};
        return ablock;
    }

    public int[] parseBlockMetadatas(Block p_parseBlockMetadatas_1_, String[] p_parseBlockMetadatas_2_) {
        if (p_parseBlockMetadatas_2_.length <= 0) {
            return null;
        }
        String s = p_parseBlockMetadatas_2_[0];
        if (this.startsWithDigit(s)) {
            int[] aint = this.parseIntList(s);
            return aint;
        }
        IBlockState iblockstate = p_parseBlockMetadatas_1_.getDefaultState();
        Collection<IProperty> collection = iblockstate.getPropertyNames();
        HashMap<IProperty, List<Comparable>> map = new HashMap<IProperty, List<Comparable>>();
        int i = 0;
        while (i < p_parseBlockMetadatas_2_.length) {
            String s1 = p_parseBlockMetadatas_2_[i];
            if (s1.length() > 0) {
                String[] astring = Config.tokenize(s1, "=");
                if (astring.length != 2) {
                    this.warn("Invalid block property: " + s1);
                    return null;
                }
                String s2 = astring[0];
                String s3 = astring[1];
                IProperty iproperty = ConnectedProperties.getProperty(s2, collection);
                if (iproperty == null) {
                    this.warn("Property not found: " + s2 + ", block: " + p_parseBlockMetadatas_1_);
                    return null;
                }
                ArrayList<Comparable> list = (ArrayList<Comparable>)map.get(s2);
                if (list == null) {
                    list = new ArrayList<Comparable>();
                    map.put(iproperty, list);
                }
                String[] astring1 = Config.tokenize(s3, ",");
                int j = 0;
                while (j < astring1.length) {
                    String s4 = astring1[j];
                    Comparable comparable = ConnectedParser.parsePropertyValue(iproperty, s4);
                    if (comparable == null) {
                        this.warn("Property value not found: " + s4 + ", property: " + s2 + ", block: " + p_parseBlockMetadatas_1_);
                        return null;
                    }
                    list.add(comparable);
                    ++j;
                }
            }
            ++i;
        }
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        int k = 0;
        while (k < 16) {
            int l = k;
            try {
                IBlockState iblockstate1 = this.getStateFromMeta(p_parseBlockMetadatas_1_, l);
                if (this.matchState(iblockstate1, map)) {
                    list1.add(l);
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            ++k;
        }
        if (list1.size() == 16) {
            return null;
        }
        int[] aint1 = new int[list1.size()];
        int i1 = 0;
        while (i1 < aint1.length) {
            aint1[i1] = (Integer)list1.get(i1);
            ++i1;
        }
        return aint1;
    }

    private IBlockState getStateFromMeta(Block p_getStateFromMeta_1_, int p_getStateFromMeta_2_) {
        try {
            IBlockState iblockstate = p_getStateFromMeta_1_.getStateFromMeta(p_getStateFromMeta_2_);
            if (p_getStateFromMeta_1_ == Blocks.DOUBLE_PLANT && p_getStateFromMeta_2_ > 7) {
                IBlockState iblockstate1 = p_getStateFromMeta_1_.getStateFromMeta(p_getStateFromMeta_2_ & 7);
                iblockstate = iblockstate.withProperty(BlockDoublePlant.VARIANT, iblockstate1.getValue(BlockDoublePlant.VARIANT));
            }
            if (p_getStateFromMeta_1_ == Blocks.field_190976_dk && (p_getStateFromMeta_2_ & 8) != 0) {
                iblockstate = iblockstate.withProperty(BlockObserver.field_190963_a, true);
            }
            return iblockstate;
        }
        catch (IllegalArgumentException var5) {
            return p_getStateFromMeta_1_.getDefaultState();
        }
    }

    public static Comparable parsePropertyValue(IProperty p_parsePropertyValue_0_, String p_parsePropertyValue_1_) {
        Class oclass = p_parsePropertyValue_0_.getValueClass();
        Comparable comparable = ConnectedParser.parseValue(p_parsePropertyValue_1_, oclass);
        if (comparable == null) {
            Collection<Comparable> collection = p_parsePropertyValue_0_.getAllowedValues();
            comparable = ConnectedParser.getPropertyValue(p_parsePropertyValue_1_, collection);
        }
        return comparable;
    }

    public static Comparable getPropertyValue(String p_getPropertyValue_0_, Collection<Comparable> p_getPropertyValue_1_) {
        for (Comparable comparable : p_getPropertyValue_1_) {
            if (!ConnectedParser.getValueName(comparable).equals(p_getPropertyValue_0_)) continue;
            return comparable;
        }
        return null;
    }

    private static Object getValueName(Comparable p_getValueName_0_) {
        if (p_getValueName_0_ instanceof IStringSerializable) {
            IStringSerializable istringserializable = (IStringSerializable)((Object)p_getValueName_0_);
            return istringserializable.getName();
        }
        return p_getValueName_0_.toString();
    }

    public static Comparable parseValue(String p_parseValue_0_, Class p_parseValue_1_) {
        if (p_parseValue_1_ == String.class) {
            return p_parseValue_0_;
        }
        if (p_parseValue_1_ == Boolean.class) {
            return Boolean.valueOf(p_parseValue_0_);
        }
        if (p_parseValue_1_ == Float.class) {
            return Float.valueOf(p_parseValue_0_);
        }
        if (p_parseValue_1_ == Double.class) {
            return Double.valueOf(p_parseValue_0_);
        }
        if (p_parseValue_1_ == Integer.class) {
            return Integer.valueOf(p_parseValue_0_);
        }
        return p_parseValue_1_ == Long.class ? Long.valueOf(p_parseValue_0_) : null;
    }

    public boolean matchState(IBlockState p_matchState_1_, Map<IProperty, List<Comparable>> p_matchState_2_) {
        for (IProperty iproperty : p_matchState_2_.keySet()) {
            List<Comparable> list = p_matchState_2_.get(iproperty);
            Object comparable = p_matchState_1_.getValue(iproperty);
            if (comparable == null) {
                return false;
            }
            if (list.contains(comparable)) continue;
            return false;
        }
        return true;
    }

    public Biome[] parseBiomes(String p_parseBiomes_1_) {
        if (p_parseBiomes_1_ == null) {
            return null;
        }
        boolean flag = false;
        if (p_parseBiomes_1_.startsWith("!")) {
            flag = true;
            p_parseBiomes_1_ = p_parseBiomes_1_.substring(1);
        }
        String[] astring = Config.tokenize(p_parseBiomes_1_, " ");
        ArrayList list = new ArrayList();
        int i = 0;
        while (i < astring.length) {
            String s = astring[i];
            Biome biome = this.findBiome(s);
            if (biome == null) {
                this.warn("Biome not found: " + s);
            } else {
                list.add(biome);
            }
            ++i;
        }
        if (flag) {
            ArrayList list1 = Lists.newArrayList(Biome.REGISTRY.iterator());
            list1.removeAll(list);
            list = list1;
        }
        Biome[] abiome = list.toArray(new Biome[list.size()]);
        return abiome;
    }

    public Biome findBiome(String p_findBiome_1_) {
        if ((p_findBiome_1_ = p_findBiome_1_.toLowerCase()).equals("nether")) {
            return Biomes.HELL;
        }
        for (ResourceLocation resourcelocation : Biome.REGISTRY.getKeys()) {
            String s;
            Biome biome = Biome.REGISTRY.getObject(resourcelocation);
            if (biome == null || !(s = biome.getBiomeName().replace(" ", "").toLowerCase()).equals(p_findBiome_1_)) continue;
            return biome;
        }
        return null;
    }

    public int parseInt(String p_parseInt_1_) {
        if (p_parseInt_1_ == null) {
            return -1;
        }
        int i = Config.parseInt(p_parseInt_1_, -1);
        if (i < 0) {
            this.warn("Invalid number: " + p_parseInt_1_);
        }
        return i;
    }

    public int parseInt(String p_parseInt_1_, int p_parseInt_2_) {
        if (p_parseInt_1_ == null) {
            return p_parseInt_2_;
        }
        int i = Config.parseInt(p_parseInt_1_, -1);
        if (i < 0) {
            this.warn("Invalid number: " + p_parseInt_1_);
            return p_parseInt_2_;
        }
        return i;
    }

    public int[] parseIntList(String p_parseIntList_1_) {
        if (p_parseIntList_1_ == null) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        String[] astring = Config.tokenize(p_parseIntList_1_, " ,");
        int i = 0;
        while (i < astring.length) {
            String s = astring[i];
            if (s.contains("-")) {
                String[] astring1 = Config.tokenize(s, "-");
                if (astring1.length != 2) {
                    this.warn("Invalid interval: " + s + ", when parsing: " + p_parseIntList_1_);
                } else {
                    int k = Config.parseInt(astring1[0], -1);
                    int l = Config.parseInt(astring1[1], -1);
                    if (k >= 0 && l >= 0 && k <= l) {
                        int i1 = k;
                        while (i1 <= l) {
                            list.add(i1);
                            ++i1;
                        }
                    } else {
                        this.warn("Invalid interval: " + s + ", when parsing: " + p_parseIntList_1_);
                    }
                }
            } else {
                int j = Config.parseInt(s, -1);
                if (j < 0) {
                    this.warn("Invalid number: " + s + ", when parsing: " + p_parseIntList_1_);
                } else {
                    list.add(j);
                }
            }
            ++i;
        }
        int[] aint = new int[list.size()];
        int j1 = 0;
        while (j1 < aint.length) {
            aint[j1] = (Integer)list.get(j1);
            ++j1;
        }
        return aint;
    }

    public boolean[] parseFaces(String p_parseFaces_1_, boolean[] p_parseFaces_2_) {
        if (p_parseFaces_1_ == null) {
            return p_parseFaces_2_;
        }
        EnumSet<EnumFacing> enumset = EnumSet.allOf(EnumFacing.class);
        String[] astring = Config.tokenize(p_parseFaces_1_, " ,");
        int i = 0;
        while (i < astring.length) {
            String s = astring[i];
            if (s.equals("sides")) {
                enumset.add(EnumFacing.NORTH);
                enumset.add(EnumFacing.SOUTH);
                enumset.add(EnumFacing.WEST);
                enumset.add(EnumFacing.EAST);
            } else if (s.equals("all")) {
                enumset.addAll(Arrays.asList(EnumFacing.VALUES));
            } else {
                EnumFacing enumfacing = this.parseFace(s);
                if (enumfacing != null) {
                    enumset.add(enumfacing);
                }
            }
            ++i;
        }
        boolean[] aboolean = new boolean[EnumFacing.VALUES.length];
        int j = 0;
        while (j < aboolean.length) {
            aboolean[j] = enumset.contains(EnumFacing.VALUES[j]);
            ++j;
        }
        return aboolean;
    }

    public EnumFacing parseFace(String p_parseFace_1_) {
        if (!(p_parseFace_1_ = p_parseFace_1_.toLowerCase()).equals("bottom") && !p_parseFace_1_.equals("down")) {
            if (!p_parseFace_1_.equals("top") && !p_parseFace_1_.equals("up")) {
                if (p_parseFace_1_.equals("north")) {
                    return EnumFacing.NORTH;
                }
                if (p_parseFace_1_.equals("south")) {
                    return EnumFacing.SOUTH;
                }
                if (p_parseFace_1_.equals("east")) {
                    return EnumFacing.EAST;
                }
                if (p_parseFace_1_.equals("west")) {
                    return EnumFacing.WEST;
                }
                Config.warn("Unknown face: " + p_parseFace_1_);
                return null;
            }
            return EnumFacing.UP;
        }
        return EnumFacing.DOWN;
    }

    public void dbg(String p_dbg_1_) {
        Config.dbg(this.context + ": " + p_dbg_1_);
    }

    public void warn(String p_warn_1_) {
        Config.warn(this.context + ": " + p_warn_1_);
    }

    public RangeListInt parseRangeListInt(String p_parseRangeListInt_1_) {
        if (p_parseRangeListInt_1_ == null) {
            return null;
        }
        RangeListInt rangelistint = new RangeListInt();
        String[] astring = Config.tokenize(p_parseRangeListInt_1_, " ,");
        int i = 0;
        while (i < astring.length) {
            String s = astring[i];
            RangeInt rangeint = this.parseRangeInt(s);
            if (rangeint == null) {
                return null;
            }
            rangelistint.addRange(rangeint);
            ++i;
        }
        return rangelistint;
    }

    private RangeInt parseRangeInt(String p_parseRangeInt_1_) {
        if (p_parseRangeInt_1_ == null) {
            return null;
        }
        if (p_parseRangeInt_1_.indexOf(45) >= 0) {
            String[] astring = Config.tokenize(p_parseRangeInt_1_, "-");
            if (astring.length != 2) {
                this.warn("Invalid range: " + p_parseRangeInt_1_);
                return null;
            }
            int j = Config.parseInt(astring[0], -1);
            int k = Config.parseInt(astring[1], -1);
            if (j >= 0 && k >= 0) {
                return new RangeInt(j, k);
            }
            this.warn("Invalid range: " + p_parseRangeInt_1_);
            return null;
        }
        int i = Config.parseInt(p_parseRangeInt_1_, -1);
        if (i < 0) {
            this.warn("Invalid integer: " + p_parseRangeInt_1_);
            return null;
        }
        return new RangeInt(i, i);
    }

    public static boolean parseBoolean(String p_parseBoolean_0_) {
        return p_parseBoolean_0_ == null ? false : p_parseBoolean_0_.toLowerCase().equals("true");
    }

    public static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        }
        p_parseColor_0_ = p_parseColor_0_.trim();
        try {
            int i = Integer.parseInt(p_parseColor_0_, 16) & 0xFFFFFF;
            return i;
        }
        catch (NumberFormatException var3) {
            return p_parseColor_1_;
        }
    }

    public static int parseColor4(String p_parseColor4_0_, int p_parseColor4_1_) {
        if (p_parseColor4_0_ == null) {
            return p_parseColor4_1_;
        }
        p_parseColor4_0_ = p_parseColor4_0_.trim();
        try {
            int i = (int)(Long.parseLong(p_parseColor4_0_, 16) & 0xFFFFFFFFFFFFFFFFL);
            return i;
        }
        catch (NumberFormatException var3) {
            return p_parseColor4_1_;
        }
    }

    public BlockRenderLayer parseBlockRenderLayer(String p_parseBlockRenderLayer_1_, BlockRenderLayer p_parseBlockRenderLayer_2_) {
        if (p_parseBlockRenderLayer_1_ == null) {
            return p_parseBlockRenderLayer_2_;
        }
        p_parseBlockRenderLayer_1_ = p_parseBlockRenderLayer_1_.toLowerCase().trim();
        BlockRenderLayer[] ablockrenderlayer = BlockRenderLayer.values();
        int i = 0;
        while (i < ablockrenderlayer.length) {
            BlockRenderLayer blockrenderlayer = ablockrenderlayer[i];
            if (p_parseBlockRenderLayer_1_.equals(blockrenderlayer.name().toLowerCase())) {
                return blockrenderlayer;
            }
            ++i;
        }
        return p_parseBlockRenderLayer_2_;
    }
}

