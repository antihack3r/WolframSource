/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.tileentity.TileEntityChest;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterChestLarge
extends ModelAdapter {
    public ModelAdapterChestLarge() {
        super(TileEntityChest.class, "chest_large", 0.0f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelLargeChest();
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
        TileEntityChestRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityChest.class);
        if (!(tileentityspecialrenderer instanceof TileEntityChestRenderer)) {
            return null;
        }
        if (tileentityspecialrenderer.getEntityClass() == null) {
            tileentityspecialrenderer = new TileEntityChestRenderer();
            tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
        }
        if (!Reflector.TileEntityChestRenderer_largeChest.exists()) {
            Config.warn("Field not found: TileEntityChestRenderer.largeChest");
            return null;
        }
        Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityChestRenderer_largeChest, modelBase);
        return tileentityspecialrenderer;
    }
}

