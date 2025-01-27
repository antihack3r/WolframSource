/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderMagmaCube;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;

public class ModelAdapterMagmaCube
extends ModelAdapter {
    public ModelAdapterMagmaCube() {
        super(EntityMagmaCube.class, "magma_cube", 0.5f);
    }

    @Override
    public ModelBase makeModel() {
        return new ModelMagmaCube();
    }

    @Override
    public ModelRenderer getModelRenderer(ModelBase model, String modelPart) {
        if (!(model instanceof ModelMagmaCube)) {
            return null;
        }
        ModelMagmaCube modelmagmacube = (ModelMagmaCube)model;
        if (modelPart.equals("core")) {
            return (ModelRenderer)Reflector.getFieldValue(modelmagmacube, Reflector.ModelMagmaCube_core);
        }
        String s = "segment";
        if (modelPart.startsWith(s)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(modelmagmacube, Reflector.ModelMagmaCube_segments);
            if (amodelrenderer == null) {
                return null;
            }
            String s1 = modelPart.substring(s.length());
            int i = Config.parseInt(s1, -1);
            return --i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
        }
        return null;
    }

    @Override
    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize) {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderMagmaCube rendermagmacube = new RenderMagmaCube(rendermanager);
        rendermagmacube.mainModel = modelBase;
        rendermagmacube.shadowSize = shadowSize;
        return rendermagmacube;
    }
}

