/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterEnderChest
extends ModelAdapter {
    public ModelAdapterEnderChest() {
        super(TileEntityEnderChest.class, "ender_chest", 0.0f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelChest();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelChest)) {
            return null;
        }
        ModelChest modelchest = (ModelChest)model;
        if (modelPart.equals("lid")) {
            return modelchest.chestLid;
        }
        if (modelPart.equals("base")) {
            return modelchest.chestBelow;
        }
        return modelPart.equals("knob") ? modelchest.chestKnob : null;
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityEnderChestRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityEnderChest.class);
        if (!(tileentityspecialrenderer instanceof TileEntityEnderChestRenderer)) {
            return null;
        }
        if (tileentityspecialrenderer.getEntityClass() == null) {
            tileentityspecialrenderer = new TileEntityEnderChestRenderer();
            tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
        }
        if (!Reflector.TileEntityEnderChestRenderer_modelChest.exists()) {
            Config.warn("Field not found: TileEntityEnderChestRenderer.modelChest");
            return null;
        }
        Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityEnderChestRenderer_modelChest, modelBase);
        return tileentityspecialrenderer;
    }
}

