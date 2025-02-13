/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.src;

import java.util.Map;
import java.util.Set;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;
import net.minecraft.src.PlayerConfigurations;

public class PlayerItemsLayer
implements LayerRenderer {
    private RenderPlayer renderPlayer = null;

    public PlayerItemsLayer(RenderPlayer p_i72_1_) {
        this.renderPlayer = p_i72_1_;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.renderEquippedItems(entitylivingbaseIn, scale, partialTicks);
    }

    protected void renderEquippedItems(EntityLivingBase p_renderEquippedItems_1_, float p_renderEquippedItems_2_, float p_renderEquippedItems_3_) {
        if (Config.isShowCapes() && p_renderEquippedItems_1_ instanceof AbstractClientPlayer) {
            AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)p_renderEquippedItems_1_;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableRescaleNormal();
            GlStateManager.enableCull();
            ModelPlayer modelbiped = this.renderPlayer.getMainModel();
            PlayerConfigurations.renderPlayerItems(modelbiped, abstractclientplayer, p_renderEquippedItems_2_, p_renderEquippedItems_3_);
            GlStateManager.disableCull();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    public static void register(Map p_register_0_) {
        Set set = p_register_0_.keySet();
        boolean flag = false;
        for (Object object : set) {
            Object object1 = p_register_0_.get(object);
            if (!(object1 instanceof RenderPlayer)) continue;
            RenderPlayer renderplayer = (RenderPlayer)object1;
            renderplayer.addLayer(new PlayerItemsLayer(renderplayer));
            flag = true;
        }
        if (!flag) {
            Config.warn("PlayerItemsLayer not registered");
        }
    }
}

