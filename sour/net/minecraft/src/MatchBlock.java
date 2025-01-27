/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import net.minecraft.block.state.BlockStateBase;
import net.minecraft.src.Config;
import net.minecraft.src.Matches;

public class MatchBlock {
    private int blockId = -1;
    private int[] metadatas = null;

    public MatchBlock(int p_i60_1_) {
        this.blockId = p_i60_1_;
    }

    public MatchBlock(int p_i61_1_, int p_i61_2_) {
        this.blockId = p_i61_1_;
        if (p_i61_2_ >= 0 && p_i61_2_ <= 15) {
            this.metadatas = new int[]{p_i61_2_};
        }
    }

    public MatchBlock(int p_i62_1_, int[] p_i62_2_) {
        this.blockId = p_i62_1_;
        this.metadatas = p_i62_2_;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public int[] getMetadatas() {
        return this.metadatas;
    }

    public boolean matches(BlockStateBase p_matches_1_) {
        if (p_matches_1_.getBlockId() != this.blockId) {
            return false;
        }
        return Matches.metadata(p_matches_1_.getMetadata(), this.metadatas);
    }

    public boolean matches(int p_matches_1_, int p_matches_2_) {
        if (p_matches_1_ != this.blockId) {
            return false;
        }
        return Matches.metadata(p_matches_2_, this.metadatas);
    }

    public void addMetadata(int p_addMetadata_1_) {
        if (this.metadatas != null && p_addMetadata_1_ >= 0 && p_addMetadata_1_ <= 15) {
            int i = 0;
            while (i < this.metadatas.length) {
                if (this.metadatas[i] == p_addMetadata_1_) {
                    return;
                }
                ++i;
            }
            this.metadatas = Config.addIntToArray(this.metadatas, p_addMetadata_1_);
        }
    }

    public String toString() {
        return this.blockId + ":" + Config.arrayToString(this.metadatas);
    }
}

