/*
 * Decompiled with CFR 0.152.
 */
package net.wolframclient.compatibility;

import net.minecraft.block.BlockChest;
import net.minecraft.tileentity.TileEntityChest;

public final class WTileEntity {
    public static boolean isTrappedChest(TileEntityChest chest) {
        return chest.getChestType() == BlockChest.Type.TRAP;
    }
}

