/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityShulkerBoxRenderer;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterShulkerBox
extends ModelAdapter {
    public ModelAdapterShulkerBox() {
        super(TileEntityShulkerBox.class, "shulker_box", 0.0f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelShulker();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelShulker)) {
            return null;
        }
        ModelShulker modelshulker = (ModelShulker)model;
        if (modelPart.equals("head")) {
            return modelshulker.head;
        }
        if (modelPart.equals("base")) {
            return modelshulker.base;
        }
        return modelPart.equals("lid") ? modelshulker.lid : null;
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntityShulkerBoxRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityShulkerBox.class);
        if (!(tileentityspecialrenderer instanceof TileEntityShulkerBoxRenderer)) {
            return null;
        }
        if (tileentityspecialrenderer.getEntityClass() == null) {
            tileentityspecialrenderer = new TileEntityShulkerBoxRenderer((ModelShulker)modelBase);
            tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
        }
        if (!Reflector.TileEntityShulkerBoxRenderer_model.exists()) {
            Config.warn("Field not found: TileEntityShulkerBoxRenderer.model");
            return null;
        }
        Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityShulkerBoxRenderer_model, modelBase);
        return tileentityspecialrenderer;
    }
}

