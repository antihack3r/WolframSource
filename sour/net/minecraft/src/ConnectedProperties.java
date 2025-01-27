/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedParser;
import net.minecraft.src.MatchBlock;
import net.minecraft.src.Matches;
import net.minecraft.src.MathUtils;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class ConnectedProperties {
    public String name = null;
    public String basePath = null;
    public MatchBlock[] matchBlocks = null;
    public int[] metadatas = null;
    public String[] matchTiles = null;
    public int method = 0;
    public String[] tiles = null;
    public int connect = 0;
    public int faces = 63;
    public Biome[] biomes = null;
    public int minHeight = 0;
    public int maxHeight = 1024;
    public int renderPass = 0;
    public boolean innerSeams = false;
    public int[] ctmTileIndexes = null;
    public int width = 0;
    public int height = 0;
    public int[] weights = null;
    public int symmetry = 1;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    public TextureAtlasSprite[] matchTileIcons = null;
    public TextureAtlasSprite[] tileIcons = null;
    public MatchBlock[] connectBlocks = null;
    public String[] connectTiles = null;
    public TextureAtlasSprite[] connectTileIcons = null;
    public int tintIndex = -1;
    public IBlockState tintBlockState = Blocks.AIR.getDefaultState();
    public BlockRenderLayer layer = null;
    public static final int METHOD_NONE = 0;
    public static final int METHOD_CTM = 1;
    public static final int METHOD_HORIZONTAL = 2;
    public static final int METHOD_TOP = 3;
    public static final int METHOD_RANDOM = 4;
    public static final int METHOD_REPEAT = 5;
    public static final int METHOD_VERTICAL = 6;
    public static final int METHOD_FIXED = 7;
    public static final int METHOD_HORIZONTAL_VERTICAL = 8;
    public static final int METHOD_VERTICAL_HORIZONTAL = 9;
    public static final int METHOD_CTM_COMPACT = 10;
    public static final int METHOD_OVERLAY = 11;
    public static final int CONNECT_NONE = 0;
    public static final int CONNECT_BLOCK = 1;
    public static final int CONNECT_TILE = 2;
    public static final int CONNECT_MATERIAL = 3;
    public static final int CONNECT_UNKNOWN = 128;
    public static final int FACE_BOTTOM = 1;
    public static final int FACE_TOP = 2;
    public static final int FACE_NORTH = 4;
    public static final int FACE_SOUTH = 8;
    public static final int FACE_WEST = 16;
    public static final int FACE_EAST = 32;
    public static final int FACE_SIDES = 60;
    public static final int FACE_ALL = 63;
    public static final int FACE_UNKNOWN = 128;
    public static final int SYMMETRY_NONE = 1;
    public static final int SYMMETRY_OPPOSITE = 2;
    public static final int SYMMETRY_ALL = 6;
    public static final int SYMMETRY_UNKNOWN = 128;

    public ConnectedProperties(Properties p_i27_1_, String p_i27_2_) {
        ConnectedParser connectedparser = new ConnectedParser("ConnectedTextures");
        this.name = connectedparser.parseName(p_i27_2_);
        this.basePath = connectedparser.parseBasePath(p_i27_2_);
        this.matchBlocks = connectedparser.parseMatchBlocks(p_i27_1_.getProperty("matchBlocks"));
        this.metadatas = connectedparser.parseIntList(p_i27_1_.getProperty("metadata"));
        this.matchTiles = this.parseMatchTiles(p_i27_1_.getProperty("matchTiles"));
        this.method = ConnectedProperties.parseMethod(p_i27_1_.getProperty("method"));
        this.tiles = this.parseTileNames(p_i27_1_.getProperty("tiles"));
        this.connect = ConnectedProperties.parseConnect(p_i27_1_.getProperty("connect"));
        this.faces = ConnectedProperties.parseFaces(p_i27_1_.getProperty("faces"));
        this.biomes = connectedparser.parseBiomes(p_i27_1_.getProperty("biomes"));
        this.minHeight = connectedparser.parseInt(p_i27_1_.getProperty("minHeight"), -1);
        this.maxHeight = connectedparser.parseInt(p_i27_1_.getProperty("maxHeight"), 1024);
        this.renderPass = connectedparser.parseInt(p_i27_1_.getProperty("renderPass"));
        this.innerSeams = ConnectedParser.parseBoolean(p_i27_1_.getProperty("innerSeams"));
        this.ctmTileIndexes = this.parseCtmTileIndexes(p_i27_1_);
        this.width = connectedparser.parseInt(p_i27_1_.getProperty("width"));
        this.height = connectedparser.parseInt(p_i27_1_.getProperty("height"));
        this.weights = connectedparser.parseIntList(p_i27_1_.getProperty("weights"));
        this.symmetry = ConnectedProperties.parseSymmetry(p_i27_1_.getProperty("symmetry"));
        this.connectBlocks = connectedparser.parseMatchBlocks(p_i27_1_.getProperty("connectBlocks"));
        this.connectTiles = this.parseMatchTiles(p_i27_1_.getProperty("connectTiles"));
        this.tintIndex = connectedparser.parseInt(p_i27_1_.getProperty("tintIndex"));
        this.tintBlockState = connectedparser.parseBlockState(p_i27_1_.getProperty("tintBlock"), Blocks.AIR.getDefaultState());
        this.layer = connectedparser.parseBlockRenderLayer(p_i27_1_.getProperty("layer"), BlockRenderLayer.CUTOUT_MIPPED);
    }

    private int[] parseCtmTileIndexes(Properties p_parseCtmTileIndexes_1_) {
        if (this.tiles == null) {
            return null;
        }
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Object object : p_parseCtmTileIndexes_1_.keySet()) {
            String s1;
            String s;
            if (!(object instanceof String) || !(s = (String)object).startsWith(s1 = "ctm.")) continue;
            String s2 = s.substring(s1.length());
            String s3 = p_parseCtmTileIndexes_1_.getProperty(s);
            if (s3 == null) continue;
            int i = Config.parseInt(s2, -1);
            if (i >= 0 && i <= 46) {
                int j = Config.parseInt(s3, -1);
                if (j >= 0 && j < this.tiles.length) {
                    map.put(i, j);
                    continue;
                }
                Config.warn("Invalid CTM tile index: " + s3);
                continue;
            }
            Config.warn("Invalid CTM index: " + s2);
        }
        if (map.isEmpty()) {
            return null;
        }
        int[] aint = new int[47];
        int k = 0;
        while (k < aint.length) {
            aint[k] = -1;
            if (map.containsKey(k)) {
                aint[k] = (Integer)map.get(k);
            }
            ++k;
        }
        return aint;
    }

    private String[] parseMatchTiles(String p_parseMatchTiles_1_) {
        if (p_parseMatchTiles_1_ == null) {
            return null;
        }
        String[] astring = Config.tokenize(p_parseMatchTiles_1_, " ");
        int i = 0;
        while (i < astring.length) {
            String s = astring[i];
            if (s.endsWith(".png")) {
                s = s.substring(0, s.length() - 4);
            }
            astring[i] = s = TextureUtils.fixResourcePath(s, this.basePath);
            ++i;
        }
        return astring;
    }

    private static String parseName(String p_parseName_0_) {
        int j;
        String s = p_parseName_0_;
        int i = p_parseName_0_.lastIndexOf(47);
        if (i >= 0) {
            s = p_parseName_0_.substring(i + 1);
        }
        if ((j = s.lastIndexOf(46)) >= 0) {
            s = s.substring(0, j);
        }
        return s;
    }

    private static String parseBasePath(String p_parseBasePath_0_) {
        int i = p_parseBasePath_0_.lastIndexOf(47);
        return i < 0 ? "" : p_parseBasePath_0_.substring(0, i);
    }

    /*
     * Enabled aggressive block sorting
     */
    private String[] parseTileNames(String p_parseTileNames_1_) {
        if (p_parseTileNames_1_ == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();
        String[] astring = Config.tokenize(p_parseTileNames_1_, " ,");
        int i = 0;
        while (i < astring.length) {
            block10: {
                int k;
                block9: {
                    String s;
                    block8: {
                        String[] astring1;
                        s = astring[i];
                        if (!s.contains("-") || (astring1 = Config.tokenize(s, "-")).length != 2) break block8;
                        int j = Config.parseInt(astring1[0], -1);
                        k = Config.parseInt(astring1[1], -1);
                        if (j < 0 || k < 0) break block8;
                        if (j <= k) break block9;
                        Config.warn("Invalid interval: " + s + ", when parsing: " + p_parseTileNames_1_);
                        break block10;
                    }
                    list.add(s);
                    break block10;
                }
                for (int l = j; l <= k; ++l) {
                    list.add(String.valueOf(l));
                }
            }
            ++i;
        }
        String[] astring2 = list.toArray(new String[list.size()]);
        int i1 = 0;
        while (i1 < astring2.length) {
            String s2;
            String s1 = astring2[i1];
            if (!((s1 = TextureUtils.fixResourcePath(s1, this.basePath)).startsWith(this.basePath) || s1.startsWith("textures/") || s1.startsWith("mcpatcher/"))) {
                s1 = String.valueOf(this.basePath) + "/" + s1;
            }
            if (s1.endsWith(".png")) {
                s1 = s1.substring(0, s1.length() - 4);
            }
            if (s1.startsWith(s2 = "textures/blocks/")) {
                s1 = s1.substring(s2.length());
            }
            if (s1.startsWith("/")) {
                s1 = s1.substring(1);
            }
            astring2[i1] = s1;
            ++i1;
        }
        return astring2;
    }

    private static int parseSymmetry(String p_parseSymmetry_0_) {
        if (p_parseSymmetry_0_ == null) {
            return 1;
        }
        if (p_parseSymmetry_0_.equals("opposite")) {
            return 2;
        }
        if (p_parseSymmetry_0_.equals("all")) {
            return 6;
        }
        Config.warn("Unknown symmetry: " + p_parseSymmetry_0_);
        return 1;
    }

    private static int parseFaces(String p_parseFaces_0_) {
        if (p_parseFaces_0_ == null) {
            return 63;
        }
        String[] astring = Config.tokenize(p_parseFaces_0_, " ,");
        int i = 0;
        int j = 0;
        while (j < astring.length) {
            String s = astring[j];
            int k = ConnectedProperties.parseFace(s);
            i |= k;
            ++j;
        }
        return i;
    }

    private static int parseFace(String p_parseFace_0_) {
        if (!(p_parseFace_0_ = p_parseFace_0_.toLowerCase()).equals("bottom") && !p_parseFace_0_.equals("down")) {
            if (!p_parseFace_0_.equals("top") && !p_parseFace_0_.equals("up")) {
                if (p_parseFace_0_.equals("north")) {
                    return 4;
                }
                if (p_parseFace_0_.equals("south")) {
                    return 8;
                }
                if (p_parseFace_0_.equals("east")) {
                    return 32;
                }
                if (p_parseFace_0_.equals("west")) {
                    return 16;
                }
                if (p_parseFace_0_.equals("sides")) {
                    return 60;
                }
                if (p_parseFace_0_.equals("all")) {
                    return 63;
                }
                Config.warn("Unknown face: " + p_parseFace_0_);
                return 128;
            }
            return 2;
        }
        return 1;
    }

    private static int parseConnect(String p_parseConnect_0_) {
        if (p_parseConnect_0_ == null) {
            return 0;
        }
        if (p_parseConnect_0_.equals("block")) {
            return 1;
        }
        if (p_parseConnect_0_.equals("tile")) {
            return 2;
        }
        if (p_parseConnect_0_.equals("material")) {
            return 3;
        }
        Config.warn("Unknown connect: " + p_parseConnect_0_);
        return 128;
    }

    public static IProperty getProperty(String p_getProperty_0_, Collection<IProperty> p_getProperty_1_) {
        for (IProperty iproperty : p_getProperty_1_) {
            if (!p_getProperty_0_.equals(iproperty.getName())) continue;
            return iproperty;
        }
        return null;
    }

    private static int parseMethod(String p_parseMethod_0_) {
        if (p_parseMethod_0_ == null) {
            return 1;
        }
        if (!p_parseMethod_0_.equals("ctm") && !p_parseMethod_0_.equals("glass")) {
            if (p_parseMethod_0_.equals("ctm_compact")) {
                return 10;
            }
            if (!p_parseMethod_0_.equals("horizontal") && !p_parseMethod_0_.equals("bookshelf")) {
                if (p_parseMethod_0_.equals("vertical")) {
                    return 6;
                }
                if (p_parseMethod_0_.equals("top")) {
                    return 3;
                }
                if (p_parseMethod_0_.equals("random")) {
                    return 4;
                }
                if (p_parseMethod_0_.equals("repeat")) {
                    return 5;
                }
                if (p_parseMethod_0_.equals("fixed")) {
                    return 7;
                }
                if (!p_parseMethod_0_.equals("horizontal+vertical") && !p_parseMethod_0_.equals("h+v")) {
                    if (!p_parseMethod_0_.equals("vertical+horizontal") && !p_parseMethod_0_.equals("v+h")) {
                        if (p_parseMethod_0_.equals("overlay")) {
                            return 11;
                        }
                        Config.warn("Unknown method: " + p_parseMethod_0_);
                        return 0;
                    }
                    return 9;
                }
                return 8;
            }
            return 2;
        }
        return 1;
    }

    public boolean isValid(String p_isValid_1_) {
        if (this.name != null && this.name.length() > 0) {
            if (this.basePath == null) {
                Config.warn("No base path found: " + p_isValid_1_);
                return false;
            }
            if (this.matchBlocks == null) {
                this.matchBlocks = this.detectMatchBlocks();
            }
            if (this.matchTiles == null && this.matchBlocks == null) {
                this.matchTiles = this.detectMatchTiles();
            }
            if (this.matchBlocks == null && this.matchTiles == null) {
                Config.warn("No matchBlocks or matchTiles specified: " + p_isValid_1_);
                return false;
            }
            if (this.method == 0) {
                Config.warn("No method: " + p_isValid_1_);
                return false;
            }
            if (this.tiles != null && this.tiles.length > 0) {
                if (this.connect == 0) {
                    this.connect = this.detectConnect();
                }
                if (this.connect == 128) {
                    Config.warn("Invalid connect in: " + p_isValid_1_);
                    return false;
                }
                if (this.renderPass > 0) {
                    Config.warn("Render pass not supported: " + this.renderPass);
                    return false;
                }
                if ((this.faces & 0x80) != 0) {
                    Config.warn("Invalid faces in: " + p_isValid_1_);
                    return false;
                }
                if ((this.symmetry & 0x80) != 0) {
                    Config.warn("Invalid symmetry in: " + p_isValid_1_);
                    return false;
                }
                switch (this.method) {
                    case 1: {
                        return this.isValidCtm(p_isValid_1_);
                    }
                    case 2: {
                        return this.isValidHorizontal(p_isValid_1_);
                    }
                    case 3: {
                        return this.isValidTop(p_isValid_1_);
                    }
                    case 4: {
                        return this.isValidRandom(p_isValid_1_);
                    }
                    case 5: {
                        return this.isValidRepeat(p_isValid_1_);
                    }
                    case 6: {
                        return this.isValidVertical(p_isValid_1_);
                    }
                    case 7: {
                        return this.isValidFixed(p_isValid_1_);
                    }
                    case 8: {
                        return this.isValidHorizontalVertical(p_isValid_1_);
                    }
                    case 9: {
                        return this.isValidVerticalHorizontal(p_isValid_1_);
                    }
                    case 10: {
                        return this.isValidCtmCompact(p_isValid_1_);
                    }
                    case 11: {
                        return this.isValidOverlay(p_isValid_1_);
                    }
                }
                Config.warn("Unknown method: " + p_isValid_1_);
                return false;
            }
            Config.warn("No tiles specified: " + p_isValid_1_);
            return false;
        }
        Config.warn("No name found: " + p_isValid_1_);
        return false;
    }

    private int detectConnect() {
        if (this.matchBlocks != null) {
            return 1;
        }
        return this.matchTiles != null ? 2 : 128;
    }

    private MatchBlock[] detectMatchBlocks() {
        int[] aint = this.detectMatchBlockIds();
        if (aint == null) {
            return null;
        }
        MatchBlock[] amatchblock = new MatchBlock[aint.length];
        int i = 0;
        while (i < amatchblock.length) {
            amatchblock[i] = new MatchBlock(aint[i]);
            ++i;
        }
        return amatchblock;
    }

    private int[] detectMatchBlockIds() {
        int[] nArray;
        int i;
        if (!this.name.startsWith("block")) {
            return null;
        }
        int j = i = "block".length();
        while (j < this.name.length()) {
            char c0 = this.name.charAt(j);
            if (c0 < '0' || c0 > '9') break;
            ++j;
        }
        if (j == i) {
            return null;
        }
        String s = this.name.substring(i, j);
        int k = Config.parseInt(s, -1);
        if (k < 0) {
            nArray = null;
        } else {
            int[] nArray2 = new int[1];
            nArray = nArray2;
            nArray2[0] = k;
        }
        return nArray;
    }

    private String[] detectMatchTiles() {
        String[] stringArray;
        TextureAtlasSprite textureatlassprite = ConnectedProperties.getIcon(this.name);
        if (textureatlassprite == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = this.name;
        }
        return stringArray;
    }

    private static TextureAtlasSprite getIcon(String p_getIcon_0_) {
        TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe(p_getIcon_0_);
        if (textureatlassprite != null) {
            return textureatlassprite;
        }
        textureatlassprite = texturemap.getSpriteSafe("blocks/" + p_getIcon_0_);
        return textureatlassprite;
    }

    private boolean isValidCtm(String p_isValidCtm_1_) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
        }
        if (this.tiles.length < 47) {
            Config.warn("Invalid tiles, must be at least 47: " + p_isValidCtm_1_);
            return false;
        }
        return true;
    }

    private boolean isValidCtmCompact(String p_isValidCtmCompact_1_) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-4");
        }
        if (this.tiles.length < 5) {
            Config.warn("Invalid tiles, must be at least 5: " + p_isValidCtmCompact_1_);
            return false;
        }
        return true;
    }

    private boolean isValidOverlay(String p_isValidOverlay_1_) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("0-16");
        }
        if (this.tiles.length < 17) {
            Config.warn("Invalid tiles, must be at least 17: " + p_isValidOverlay_1_);
            return false;
        }
        if (this.layer != null && this.layer != BlockRenderLayer.SOLID) {
            return true;
        }
        Config.warn("Invalid overlay layer: " + (Object)((Object)this.layer));
        return false;
    }

    private boolean isValidHorizontal(String p_isValidHorizontal_1_) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("12-15");
        }
        if (this.tiles.length != 4) {
            Config.warn("Invalid tiles, must be exactly 4: " + p_isValidHorizontal_1_);
            return false;
        }
        return true;
    }

    private boolean isValidVertical(String p_isValidVertical_1_) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for vertical: " + p_isValidVertical_1_);
            return false;
        }
        if (this.tiles.length != 4) {
            Config.warn("Invalid tiles, must be exactly 4: " + p_isValidVertical_1_);
            return false;
        }
        return true;
    }

    private boolean isValidHorizontalVertical(String p_isValidHorizontalVertical_1_) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for horizontal+vertical: " + p_isValidHorizontalVertical_1_);
            return false;
        }
        if (this.tiles.length != 7) {
            Config.warn("Invalid tiles, must be exactly 7: " + p_isValidHorizontalVertical_1_);
            return false;
        }
        return true;
    }

    private boolean isValidVerticalHorizontal(String p_isValidVerticalHorizontal_1_) {
        if (this.tiles == null) {
            Config.warn("No tiles defined for vertical+horizontal: " + p_isValidVerticalHorizontal_1_);
            return false;
        }
        if (this.tiles.length != 7) {
            Config.warn("Invalid tiles, must be exactly 7: " + p_isValidVerticalHorizontal_1_);
            return false;
        }
        return true;
    }

    private boolean isValidRandom(String p_isValidRandom_1_) {
        if (this.tiles != null && this.tiles.length > 0) {
            if (this.weights != null) {
                if (this.weights.length > this.tiles.length) {
                    Config.warn("More weights defined than tiles, trimming weights: " + p_isValidRandom_1_);
                    int[] aint = new int[this.tiles.length];
                    System.arraycopy(this.weights, 0, aint, 0, aint.length);
                    this.weights = aint;
                }
                if (this.weights.length < this.tiles.length) {
                    Config.warn("Less weights defined than tiles, expanding weights: " + p_isValidRandom_1_);
                    int[] aint1 = new int[this.tiles.length];
                    System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                    int i = MathUtils.getAverage(this.weights);
                    int j = this.weights.length;
                    while (j < aint1.length) {
                        aint1[j] = i;
                        ++j;
                    }
                    this.weights = aint1;
                }
                this.sumWeights = new int[this.weights.length];
                int k = 0;
                int l = 0;
                while (l < this.weights.length) {
                    this.sumWeights[l] = k += this.weights[l];
                    ++l;
                }
                this.sumAllWeights = k;
                if (this.sumAllWeights <= 0) {
                    Config.warn("Invalid sum of all weights: " + k);
                    this.sumAllWeights = 1;
                }
            }
            return true;
        }
        Config.warn("Tiles not defined: " + p_isValidRandom_1_);
        return false;
    }

    private boolean isValidRepeat(String p_isValidRepeat_1_) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + p_isValidRepeat_1_);
            return false;
        }
        if (this.width > 0 && this.width <= 16) {
            if (this.height > 0 && this.height <= 16) {
                if (this.tiles.length != this.width * this.height) {
                    Config.warn("Number of tiles does not equal width x height: " + p_isValidRepeat_1_);
                    return false;
                }
                return true;
            }
            Config.warn("Invalid height: " + p_isValidRepeat_1_);
            return false;
        }
        Config.warn("Invalid width: " + p_isValidRepeat_1_);
        return false;
    }

    private boolean isValidFixed(String p_isValidFixed_1_) {
        if (this.tiles == null) {
            Config.warn("Tiles not defined: " + p_isValidFixed_1_);
            return false;
        }
        if (this.tiles.length != 1) {
            Config.warn("Number of tiles should be 1 for method: fixed.");
            return false;
        }
        return true;
    }

    private boolean isValidTop(String p_isValidTop_1_) {
        if (this.tiles == null) {
            this.tiles = this.parseTileNames("66");
        }
        if (this.tiles.length != 1) {
            Config.warn("Invalid tiles, must be exactly 1: " + p_isValidTop_1_);
            return false;
        }
        return true;
    }

    public void updateIcons(TextureMap p_updateIcons_1_) {
        if (this.matchTiles != null) {
            this.matchTileIcons = ConnectedProperties.registerIcons(this.matchTiles, p_updateIcons_1_);
        }
        if (this.connectTiles != null) {
            this.connectTileIcons = ConnectedProperties.registerIcons(this.connectTiles, p_updateIcons_1_);
        }
        if (this.tiles != null) {
            this.tileIcons = ConnectedProperties.registerIcons(this.tiles, p_updateIcons_1_);
        }
    }

    private static TextureAtlasSprite[] registerIcons(String[] p_registerIcons_0_, TextureMap p_registerIcons_1_) {
        if (p_registerIcons_0_ == null) {
            return null;
        }
        ArrayList<TextureAtlasSprite> list = new ArrayList<TextureAtlasSprite>();
        int i = 0;
        while (i < p_registerIcons_0_.length) {
            String s3;
            ResourceLocation resourcelocation1;
            boolean flag;
            String s = p_registerIcons_0_[i];
            ResourceLocation resourcelocation = new ResourceLocation(s);
            String s1 = resourcelocation.getResourceDomain();
            String s2 = resourcelocation.getResourcePath();
            if (!s2.contains("/")) {
                s2 = "textures/blocks/" + s2;
            }
            if (!(flag = Config.hasResource(resourcelocation1 = new ResourceLocation(s1, s3 = String.valueOf(s2) + ".png")))) {
                Config.warn("File not found: " + s3);
            }
            String s4 = "textures/";
            String s5 = s2;
            if (s2.startsWith(s4)) {
                s5 = s2.substring(s4.length());
            }
            ResourceLocation resourcelocation2 = new ResourceLocation(s1, s5);
            TextureAtlasSprite textureatlassprite = p_registerIcons_1_.registerSprite(resourcelocation2);
            list.add(textureatlassprite);
            ++i;
        }
        TextureAtlasSprite[] atextureatlassprite = list.toArray(new TextureAtlasSprite[list.size()]);
        return atextureatlassprite;
    }

    public boolean matchesBlockId(int p_matchesBlockId_1_) {
        return Matches.blockId(p_matchesBlockId_1_, this.matchBlocks);
    }

    public boolean matchesBlock(int p_matchesBlock_1_, int p_matchesBlock_2_) {
        if (!Matches.block(p_matchesBlock_1_, p_matchesBlock_2_, this.matchBlocks)) {
            return false;
        }
        return Matches.metadata(p_matchesBlock_2_, this.metadatas);
    }

    public boolean matchesIcon(TextureAtlasSprite p_matchesIcon_1_) {
        return Matches.sprite(p_matchesIcon_1_, this.matchTileIcons);
    }

    public String toString() {
        return "CTM name: " + this.name + ", basePath: " + this.basePath + ", matchBlocks: " + Config.arrayToString(this.matchBlocks) + ", matchTiles: " + Config.arrayToString(this.matchTiles);
    }

    public boolean matchesBiome(Biome p_matchesBiome_1_) {
        return Matches.biome(p_matchesBiome_1_, this.biomes);
    }

    public int getMetadataMax() {
        int i = -1;
        i = this.getMax(this.metadatas, i);
        if (this.matchBlocks != null) {
            int j = 0;
            while (j < this.matchBlocks.length) {
                MatchBlock matchblock = this.matchBlocks[j];
                i = this.getMax(matchblock.getMetadatas(), i);
                ++j;
            }
        }
        return i;
    }

    private int getMax(int[] p_getMax_1_, int p_getMax_2_) {
        if (p_getMax_1_ == null) {
            return p_getMax_2_;
        }
        int i = 0;
        while (i < p_getMax_1_.length) {
            int j = p_getMax_1_[i];
            if (j > p_getMax_2_) {
                p_getMax_2_ = j;
            }
            ++i;
        }
        return p_getMax_2_;
    }
}

