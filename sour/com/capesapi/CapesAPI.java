/*
 * Decompiled with CFR 0.152.
 */
package com.capesapi;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class CapesAPI {
    private static final String BASE_URL = "http://capesapi.com/api/v1/%s/getCape";
    private static final ArrayList<UUID> pendingRequests = new ArrayList();
    private static final Map<UUID, ResourceLocation> capes = new HashMap<UUID, ResourceLocation>();

    public static void loadCape(final UUID uuid) {
        if (CapesAPI.hasPendingRequests(uuid)) {
            return;
        }
        CapesAPI.setCape(uuid, null);
        String url = String.format(BASE_URL, uuid);
        final ResourceLocation resourceLocation = new ResourceLocation(String.format("capesapi/capes/%s.png", new Date().getTime()));
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, url, null, new IImageBuffer(){

            @Override
            public BufferedImage parseUserSkin(BufferedImage image) {
                return image;
            }

            @Override
            public void skinAvailable() {
                CapesAPI.setCape(uuid, resourceLocation);
                pendingRequests.remove(uuid);
            }
        });
        textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        pendingRequests.add(uuid);
    }

    public static void setCape(UUID uuid, ResourceLocation resourceLocation) {
        capes.put(uuid, resourceLocation);
    }

    public static void deleteCape(UUID uuid) {
        capes.remove(uuid);
    }

    public static ResourceLocation getCape(UUID uuid) {
        return capes.getOrDefault(uuid, null);
    }

    public static boolean hasCape(UUID uuid) {
        boolean hasCape = capes.containsKey(uuid);
        ResourceLocation resourceLocation = capes.get(uuid);
        if (hasCape && resourceLocation == null && !CapesAPI.hasPendingRequests(uuid)) {
            CapesAPI.loadCape(uuid);
            return false;
        }
        return hasCape;
    }

    public static void resetCapes() {
        for (UUID userId : capes.keySet()) {
            capes.put(userId, null);
        }
    }

    private static boolean hasPendingRequests(UUID uuid) {
        return pendingRequests.contains(uuid);
    }
}

