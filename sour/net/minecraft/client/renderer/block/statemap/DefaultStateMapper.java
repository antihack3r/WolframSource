/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.block.statemap;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class DefaultStateMapper
extends StateMapperBase {
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), this.getPropertyString((Map<IProperty<?>, Comparable<?>>)state.getProperties()));
    }
}

