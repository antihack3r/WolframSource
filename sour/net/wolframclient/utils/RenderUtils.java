/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.wolframclient.Wolfram;
import net.wolframclient.compatibility.WMath;
import net.wolframclient.compatibility.WMinecraft;
import net.wolframclient.gui.font.FontRenderers;
import net.wolframclient.utils.EntityHelper;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class RenderUtils {
    public static void beginCrop(float x, float y, float width, float height) {
        int scaleFactor = RenderUtils.getScaleFactor();
        GL11.glEnable((int)3089);
        GL11.glScissor((int)((int)(x * (float)scaleFactor)), (int)((int)((float)Display.getHeight() - y * (float)scaleFactor)), (int)((int)(width * (float)scaleFactor)), (int)((int)(height * (float)scaleFactor)));
    }

    public static void endCrop() {
        GL11.glDisable((int)3089);
    }

    public static void drawLine3D(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        RenderUtils.drawLine3D(x1, y1, z1, x2, y2, z2, color, true);
    }

    public static void drawLine3D(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean disableDepth) {
        RenderUtils.enableRender3D(disableDepth);
        RenderUtils.setColor(color);
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)x1, (double)y1, (double)z1);
        GL11.glVertex3d((double)x2, (double)y2, (double)z2);
        GL11.glEnd();
        RenderUtils.disableRender3D(disableDepth);
    }

    public static void drawLine2D(double x1, double y1, double x2, double y2, float width, int color) {
        RenderUtils.enableRender2D();
        RenderUtils.setColor(color);
        GL11.glLineWidth((float)width);
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        RenderUtils.disableRender2D();
    }

    public static void drawPoint(double x, double y, float size, int color) {
        RenderUtils.enableRender2D();
        RenderUtils.setColor(color);
        GL11.glPointSize((float)size);
        GL11.glEnable((int)2832);
        GL11.glBegin((int)0);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
        GL11.glDisable((int)2832);
        RenderUtils.disableRender2D();
    }

    public static void drawTexturedRect(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
        float f = 1.0f / (float)textureWidth;
        float f1 = 1.0f / (float)textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(x, y + height, 0.0).tex((float)textureX * f, ((float)textureY + (float)height) * f1).endVertex();
        vertexbuffer.pos(x + width, y + height, 0.0).tex(((float)textureX + (float)width) * f, ((float)textureY + (float)height) * f1).endVertex();
        vertexbuffer.pos(x + width, y, 0.0).tex(((float)textureX + (float)width) * f, (float)textureY * f1).endVertex();
        vertexbuffer.pos(x, y, 0.0).tex((float)textureX * f, (float)textureY * f1).endVertex();
        tessellator.draw();
    }

    public static int getDisplayWidth() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int displayWidth = scaledResolution.getScaledWidth();
        return displayWidth;
    }

    public static int getDisplayHeight() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int displayHeight = scaledResolution.getScaledHeight();
        return displayHeight;
    }

    public static int getScaleFactor() {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = scaledResolution.getScaleFactor();
        return scaleFactor;
    }

    public static void drawOutlinedBox(AxisAlignedBB boundingBox, int color) {
        RenderUtils.drawOutlinedBox(boundingBox, color, true);
    }

    public static void drawOutlinedBox(AxisAlignedBB boundingBox, int color, boolean disableDepth) {
        if (boundingBox == null) {
            return;
        }
        RenderUtils.enableRender3D(disableDepth);
        RenderUtils.setColor(color);
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)3);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        RenderUtils.disableRender3D(disableDepth);
    }

    public static void drawBox(AxisAlignedBB boundingBox, int color) {
        RenderUtils.drawBox(boundingBox, color, true);
    }

    public static void drawBox(AxisAlignedBB boundingBox, int color, boolean disableDepth) {
        if (boundingBox == null) {
            return;
        }
        RenderUtils.enableRender3D(disableDepth);
        RenderUtils.setColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.maxY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.minZ);
        GL11.glVertex3d((double)boundingBox.minX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glVertex3d((double)boundingBox.maxX, (double)boundingBox.minY, (double)boundingBox.maxZ);
        GL11.glEnd();
        RenderUtils.disableRender3D(disableDepth);
    }

    public static void renderNameTag(String name, Entity entity, int borderWidth, double scale, int color) {
        RenderManager renderManager = Minecraft.getMinecraft().renderManager;
        FontRenderer fontRenderer = FontRenderers.getFontRenderer();
        int stringWidth = fontRenderer.getStringWidth(name);
        double distance = WMinecraft.getPlayer().getDistanceToEntity(entity);
        GL11.glPushMatrix();
        RenderUtils.enableRender3D(true);
        double[] coords = EntityHelper.interpolate(entity);
        double x = coords[0] - RenderManager.renderPosX;
        double y = coords[1] - RenderManager.renderPosY + (double)entity.height + 0.5;
        double z = coords[2] - RenderManager.renderPosZ;
        GL11.glTranslated((double)x, (double)y, (double)z);
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glScaled((double)-0.027, (double)-0.027, (double)0.027);
        double scaleSize = 10.0 - scale;
        if (distance > scaleSize) {
            GL11.glScaled((double)(distance / scaleSize), (double)(distance / scaleSize), (double)(distance / scaleSize));
        }
        if (Wolfram.getWolfram().storageManager.moduleSettings.getBoolean("nametags_resize")) {
            float diffPitch;
            double x1 = entity.posX;
            double y1 = entity.posY;
            double z1 = entity.posZ;
            double diffX = x1 - WMinecraft.getPlayer().posX;
            double diffY = y1 - WMinecraft.getPlayer().posY;
            double diffZ = z1 - WMinecraft.getPlayer().posZ;
            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
            float pitch = (float)(-(Math.atan2(diffY, distance) * 180.0 / Math.PI));
            float diffYaw = Math.abs(WMath.wrapDegrees(yaw - WMinecraft.getPlayer().rotationYaw));
            float factor = (float)((75.0 - Math.sqrt(diffYaw * diffYaw + (diffPitch = Math.abs(WMath.wrapDegrees(pitch - WMinecraft.getPlayer().rotationPitch))) * diffPitch)) / 50.0);
            if (factor > 1.0f) {
                factor = 1.0f;
            }
            if (factor < 0.0f) {
                factor = 0.0f;
            }
            GL11.glScaled((double)factor, (double)factor, (double)factor);
        }
        GL11.glRotatef((float)renderManager.playerViewY, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-renderManager.playerViewX), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)0.3f);
        GL11.glBegin((int)7);
        GL11.glVertex3d((double)(-stringWidth / 2 - 2), (double)-2.0, (double)0.0);
        GL11.glVertex3d((double)(-stringWidth / 2 - 2), (double)9.0, (double)0.0);
        GL11.glVertex3d((double)(stringWidth / 2 + 2), (double)9.0, (double)0.0);
        GL11.glVertex3d((double)(stringWidth / 2 + 2), (double)-2.0, (double)0.0);
        GL11.glEnd();
        GL11.glColor3f((float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glEnable((int)3553);
        fontRenderer.drawString(name, -stringWidth / 2, 0, color);
        GL11.glDisable((int)3553);
        RenderUtils.disableRender3D(true);
        GL11.glPopMatrix();
    }

    public static void enableRender3D(boolean disableDepth) {
        if (disableDepth) {
            GL11.glDepthMask((boolean)false);
            GL11.glDisable((int)2929);
        }
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)1.0f);
    }

    public static void disableRender3D(boolean enableDepth) {
        if (enableDepth) {
            GL11.glDepthMask((boolean)true);
            GL11.glEnable((int)2929);
        }
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDisable((int)2848);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void enableRender2D() {
        GL11.glEnable((int)3042);
        GL11.glDisable((int)2884);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)1.0f);
    }

    public static void disableRender2D() {
        GL11.glDisable((int)3042);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2848);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void setColor(int colorHex) {
        float alpha = (float)(colorHex >> 24 & 0xFF) / 255.0f;
        float red = (float)(colorHex >> 16 & 0xFF) / 255.0f;
        float green = (float)(colorHex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(colorHex & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)(alpha == 0.0f ? 1.0f : alpha));
    }

    public static void drawBorderedRect(float x, float y, float width, float height, float borderWidth, int rectColor, int borderColor) {
        RenderUtils.drawRect(x + borderWidth, y + borderWidth, width - borderWidth * 2.0f, height - borderWidth * 2.0f, rectColor);
        RenderUtils.drawRect(x, y, width, borderWidth, borderColor);
        RenderUtils.drawRect(x, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        RenderUtils.drawRect(x + width - borderWidth, y + borderWidth, borderWidth, height - borderWidth, borderColor);
        RenderUtils.drawRect(x + borderWidth, y + height - borderWidth, width - borderWidth * 2.0f, borderWidth, borderColor);
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        RenderUtils.enableRender2D();
        RenderUtils.setColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glEnd();
        RenderUtils.disableRender2D();
    }

    public static void drawLogo(float x, float y, float width, int color) {
        float x1 = x + width * 0.0f;
        float x2 = x + width * 0.216f;
        float x3 = x + width * 0.33f;
        float x4 = x + width * 0.5f;
        float x5 = x + width * 0.67f;
        float x6 = x + width * 0.784f;
        float x7 = x + width * 1.0f;
        float y1 = y + width * 0.0f;
        float y2 = y + width * 0.205f;
        float y3 = y + width * 0.525f;
        float y4 = y + width * 1.139f;
        RenderUtils.enableRender2D();
        GL11.glEnable((int)2881);
        RenderUtils.setColor(color);
        GL11.glBegin((int)6);
        GL11.glVertex2d((double)x3, (double)y3);
        GL11.glVertex2d((double)x4, (double)y4);
        GL11.glVertex2d((double)x5, (double)y3);
        GL11.glVertex2d((double)x4, (double)y1);
        GL11.glEnd();
        GL11.glBegin((int)6);
        GL11.glVertex2d((double)x1, (double)y3);
        GL11.glVertex2d((double)x4, (double)y4);
        GL11.glVertex2d((double)x2, (double)y3);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glBegin((int)6);
        GL11.glVertex2d((double)x7, (double)y3);
        GL11.glVertex2d((double)x4, (double)y4);
        GL11.glVertex2d((double)x6, (double)y3);
        GL11.glVertex2d((double)x6, (double)y2);
        GL11.glEnd();
        GL11.glDisable((int)2881);
        RenderUtils.disableRender2D();
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float edgeRadius, int color, float borderWidth, int borderColor) {
        double angleRadians;
        if (edgeRadius < 0.0f) {
            edgeRadius = 0.0f;
        }
        if (edgeRadius > width / 2.0f) {
            edgeRadius = width / 2.0f;
        }
        if (edgeRadius > height / 2.0f) {
            edgeRadius = height / 2.0f;
        }
        RenderUtils.drawRect(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0f, height - edgeRadius * 2.0f, color);
        RenderUtils.drawRect(x + edgeRadius, y, width - edgeRadius * 2.0f, edgeRadius, color);
        RenderUtils.drawRect(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0f, edgeRadius, color);
        RenderUtils.drawRect(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        RenderUtils.drawRect(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0f, color);
        RenderUtils.enableRender2D();
        RenderUtils.setColor(color);
        GL11.glBegin((int)6);
        float centerX = x + edgeRadius;
        float centerY = y + edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        int vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        int i = 0;
        while (i < vertices + 1) {
            angleRadians = Math.PI * 2 * (double)(i + 180) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            ++i;
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        i = 0;
        while (i < vertices + 1) {
            angleRadians = Math.PI * 2 * (double)(i + 90) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            ++i;
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        i = 0;
        while (i < vertices + 1) {
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            ++i;
        }
        GL11.glEnd();
        GL11.glBegin((int)6);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d((double)centerX, (double)centerY);
        vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        i = 0;
        while (i < vertices + 1) {
            angleRadians = Math.PI * 2 * (double)i / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            ++i;
        }
        GL11.glEnd();
        RenderUtils.setColor(borderColor);
        GL11.glLineWidth((float)borderWidth);
        GL11.glBegin((int)3);
        centerX = x + edgeRadius;
        centerY = y + edgeRadius;
        i = vertices = (int)Math.min(Math.max(edgeRadius, 10.0f), 90.0f);
        while (i >= 0) {
            angleRadians = Math.PI * 2 * (double)(i + 180) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            --i;
        }
        GL11.glVertex2d((double)(x + edgeRadius), (double)y);
        GL11.glVertex2d((double)(x + width - edgeRadius), (double)y);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        i = vertices;
        while (i >= 0) {
            angleRadians = Math.PI * 2 * (double)(i + 90) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            --i;
        }
        GL11.glVertex2d((double)(x + width), (double)(y + edgeRadius));
        GL11.glVertex2d((double)(x + width), (double)(y + height - edgeRadius));
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        i = vertices;
        while (i >= 0) {
            angleRadians = Math.PI * 2 * (double)i / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            --i;
        }
        GL11.glVertex2d((double)(x + width - edgeRadius), (double)(y + height));
        GL11.glVertex2d((double)(x + edgeRadius), (double)(y + height));
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        i = vertices;
        while (i >= 0) {
            angleRadians = Math.PI * 2 * (double)(i + 270) / (double)(vertices * 4);
            GL11.glVertex2d((double)((double)centerX + Math.sin(angleRadians) * (double)edgeRadius), (double)((double)centerY + Math.cos(angleRadians) * (double)edgeRadius));
            --i;
        }
        GL11.glVertex2d((double)x, (double)(y + height - edgeRadius));
        GL11.glVertex2d((double)x, (double)(y + edgeRadius));
        GL11.glEnd();
        RenderUtils.disableRender2D();
    }

    public static void drawCircle(float x, float y, float radius, float lineWidth, int color) {
        RenderUtils.enableRender2D();
        RenderUtils.setColor(color);
        GL11.glLineWidth((float)lineWidth);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)2);
        int i = 0;
        while (i < vertices) {
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
        GL11.glEnd();
        RenderUtils.disableRender2D();
    }

    public static void drawFilledCircle(float x, float y, float radius, int color) {
        RenderUtils.enableRender2D();
        RenderUtils.setColor(color);
        int vertices = (int)Math.min(Math.max(radius, 45.0f), 360.0f);
        GL11.glBegin((int)9);
        int i = 0;
        while (i < vertices) {
            double angleRadians = Math.PI * 2 * (double)i / (double)vertices;
            GL11.glVertex2d((double)((double)x + Math.sin(angleRadians) * (double)radius), (double)((double)y + Math.cos(angleRadians) * (double)radius));
            ++i;
        }
        GL11.glEnd();
        RenderUtils.disableRender2D();
        RenderUtils.drawCircle(x, y, radius, 1.5f, 0xFFFFFF);
    }

    public static int darker(int hexColor, int factor) {
        float alpha = hexColor >> 24 & 0xFF;
        float red = Math.max((float)(hexColor >> 16 & 0xFF) - (float)(hexColor >> 16 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float green = Math.max((float)(hexColor >> 8 & 0xFF) - (float)(hexColor >> 8 & 0xFF) / (100.0f / (float)factor), 0.0f);
        float blue = Math.max((float)(hexColor & 0xFF) - (float)(hexColor & 0xFF) / (100.0f / (float)factor), 0.0f);
        return (int)((float)(((int)alpha << 24) + ((int)red << 16) + ((int)green << 8)) + blue);
    }
}

