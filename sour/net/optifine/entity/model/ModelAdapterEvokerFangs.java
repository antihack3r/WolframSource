/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEvokerFangs;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderEvokerFangs;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterEvokerFangs
extends ModelAdapter {
    public ModelAdapterEvokerFangs() {
        super(EntityEvokerFangs.class, "evocation_fangs", 0.0f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelEvokerFangs();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelEvokerFangs)) {
            return null;
        }
        ModelEvokerFangs modelevokerfangs = (ModelEvokerFangs)model;
        if (modelPart.equals("base")) {
            return (ModelRenderer)Reflector.getFieldValue(modelevokerfangs, Reflector.ModelEvokerFangs_ModelRenderers, 0);
        }
        if (modelPart.equals("upper_jaw")) {
            return (ModelRenderer)Reflector.getFieldValue(modelevokerfangs, Reflector.ModelEvokerFangs_ModelRenderers, 1);
        }
        return modelPart.equals("lower_jaw") ? (ModelRenderer)Reflector.getFieldValue(modelevokerfangs, Reflector.ModelEvokerFangs_ModelRenderers, 2) : null;
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderEvokerFangs renderevokerfangs = new RenderEvokerFangs(rendermanager);
        if (!Reflector.RenderEvokerFangs_model.exists()) {
            Config.warn("Field not found: RenderEvokerFangs.model");
            return null;
        }
        Reflector.setFieldValue(renderevokerfangs, Reflector.RenderEvokerFangs_model, modelBase);
        renderevokerfangs.shadowSize = shadowSize;
        return renderevokerfangs;
    }
}

