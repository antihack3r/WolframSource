/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedParser;
import net.minecraft.src.MatchBlock;
import net.minecraft.src.PropertiesOrdered;
import net.minecraft.src.StrUtils;
import shadersmod.client.BlockAlias;
import shadersmod.client.IShaderPack;

public class BlockAliases {
    private static BlockAlias[][] blockAliases = null;

    public static int getMappedBlockId(int blockId, int metadata) {
        if (blockAliases == null) {
            return blockId;
        }
        if (blockId >= 0 && blockId < blockAliases.length) {
            BlockAlias[] ablockalias = blockAliases[blockId];
            if (ablockalias == null) {
                return blockId;
            }
            int i = 0;
            while (i < ablockalias.length) {
                BlockAlias blockalias = ablockalias[i];
                if (blockalias.matches(blockId, metadata)) {
                    return blockalias.getBlockId();
                }
                ++i;
            }
            return blockId;
        }
        return blockId;
    }

    public static void update(IShaderPack shaderPack) {
        BlockAliases.reset();
        String s = "/shaders/block.properties";
        try {
            InputStream inputstream = shaderPack.getResourceAsStream(s);
            if (inputstream == null) {
                return;
            }
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            Config.dbg("[Shaders] Parsing block mappings: " + s);
            ArrayList<List<BlockAlias>> list = new ArrayList<List<BlockAlias>>();
            ConnectedParser connectedparser = new ConnectedParser("Shaders");
            for (Object s10 : ((Properties)properties).keySet()) {
                String s1 = (String)s10;
                String s2 = properties.getProperty(s1);
                String s3 = "block.";
                if (!s1.startsWith(s3)) {
                    Config.warn("[Shaders] Invalid block ID: " + s1);
                    continue;
                }
                String s4 = StrUtils.removePrefix(s1, s3);
                int i = Config.parseInt(s4, -1);
                if (i < 0) {
                    Config.warn("[Shaders] Invalid block ID: " + s1);
                    continue;
                }
                MatchBlock[] amatchblock = connectedparser.parseMatchBlocks(s2);
                if (amatchblock != null && amatchblock.length >= 1) {
                    BlockAlias blockalias = new BlockAlias(i, amatchblock);
                    BlockAliases.addToList(list, blockalias);
                    continue;
                }
                Config.warn("[Shaders] Invalid block ID mapping: " + s1 + "=" + s2);
            }
            if (list.size() <= 0) {
                return;
            }
            blockAliases = BlockAliases.toArrays(list);
        }
        catch (IOException var15) {
            Config.warn("[Shaders] Error reading: " + s);
        }
    }

    private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba) {
        int[] aint = ba.getMatchBlockIds();
        int i = 0;
        while (i < aint.length) {
            int j = aint[i];
            while (j >= blocksAliases.size()) {
                blocksAliases.add(null);
            }
            List<BlockAlias> list = blocksAliases.get(j);
            if (list == null) {
                list = new ArrayList<BlockAlias>();
                blocksAliases.set(j, list);
            }
            list.add(ba);
            ++i;
        }
    }

    private static BlockAlias[][] toArrays(List<List<BlockAlias>> listBlocksAliases) {
        BlockAlias[][] ablockalias = new BlockAlias[listBlocksAliases.size()][];
        int i = 0;
        while (i < ablockalias.length) {
            List<BlockAlias> list = listBlocksAliases.get(i);
            if (list != null) {
                ablockalias[i] = list.toArray(new BlockAlias[list.size()]);
            }
            ++i;
        }
        return ablockalias;
    }

    public static void reset() {
        blockAliases = null;
    }
}

