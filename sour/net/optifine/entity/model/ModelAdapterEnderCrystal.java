/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterEnderCrystal
extends ModelAdapter {
    public ModelAdapterEnderCrystal() {
        this("end_crystal");
    }

    protected ModelAdapterEnderCrystal(String name) {
        super(EntityEnderCrystal.class, name, 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelEnderCrystal(0.0f, true);
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelEnderCrystal)) {
            return null;
        }
        ModelEnderCrystal modelendercrystal = (ModelEnderCrystal)model;
        if (modelPart.equals("cube")) {
            return (ModelRenderer)Reflector.getFieldValue(modelendercrystal, Reflector.ModelEnderCrystal_ModelRenderers, 0);
        }
        if (modelPart.equals("glass")) {
            return (ModelRenderer)Reflector.getFieldValue(modelendercrystal, Reflector.ModelEnderCrystal_ModelRenderers, 1);
        }
        return modelPart.equals("base") ? (ModelRenderer)Reflector.getFieldValue(modelendercrystal, Reflector.ModelEnderCrystal_ModelRenderers, 2) : null;
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        Render render = rendermanager.getEntityRenderMap().get(EntityEnderCrystal.class);
        if (!(render instanceof RenderEnderCrystal)) {
            Config.warn("Not an instance of RenderEnderCrystal: " + render);
            return null;
        }
        RenderEnderCrystal renderendercrystal = (RenderEnderCrystal)render;
        if (!Reflector.RenderEnderCrystal_modelEnderCrystal.exists()) {
            Config.warn("Field not found: RenderEnderCrystal.modelEnderCrystal");
            return null;
        }
        Reflector.setFieldValue(renderendercrystal, Reflector.RenderEnderCrystal_modelEnderCrystal, modelBase);
        renderendercrystal.shadowSize = shadowSize;
        return renderendercrystal;
    }
}

