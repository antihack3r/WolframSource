/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.wolframclient.gui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

public final class WolframFont {
    private int IMAGE_WIDTH = 1024;
    private int IMAGE_HEIGHT = 1024;
    private int texID;
    private final CharTexture[] chars = new CharTexture[2048];
    private final Font font;
    private boolean antiAlias;
    private int fontHeight = -1;
    private int charOffset = 8;

    public WolframFont(Font font, boolean antiAlias) {
        this(font, antiAlias, 8);
    }

    public WolframFont(Font font, boolean antiAlias, int charOffset) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.charOffset = charOffset;
        this.setupTexture();
    }

    private void setupTexture() {
        if (this.font.getSize() <= 15) {
            this.IMAGE_WIDTH = 256;
            this.IMAGE_HEIGHT = 256;
        }
        if (this.font.getSize() <= 43) {
            this.IMAGE_WIDTH = 512;
            this.IMAGE_HEIGHT = 512;
        } else if (this.font.getSize() <= 91) {
            this.IMAGE_WIDTH = 1024;
            this.IMAGE_HEIGHT = 1024;
        } else {
            this.IMAGE_WIDTH = 2048;
            this.IMAGE_HEIGHT = 2048;
        }
        BufferedImage img = new BufferedImage(this.IMAGE_WIDTH, this.IMAGE_HEIGHT, 2);
        Graphics2D g = img.createGraphics();
        g.setFont(this.font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, this.IMAGE_WIDTH, this.IMAGE_HEIGHT);
        g.setColor(Color.white);
        int rowHeight = 0;
        int positionX = 0;
        int positionY = 0;
        int i = 0;
        while (i < 2048) {
            char ch = (char)i;
            BufferedImage fontImage = this.getFontImage(ch, this.antiAlias);
            CharTexture charTexture = new CharTexture();
            charTexture.width = fontImage.getWidth();
            charTexture.height = fontImage.getHeight();
            if (positionX + charTexture.width >= this.IMAGE_WIDTH) {
                positionX = 0;
                positionY += rowHeight;
                rowHeight = 0;
            }
            charTexture.posX = positionX;
            charTexture.posY = positionY;
            if (charTexture.height > this.fontHeight) {
                this.fontHeight = charTexture.height;
            }
            if (charTexture.height > rowHeight) {
                rowHeight = charTexture.height;
            }
            this.chars[i] = charTexture;
            g.drawImage((Image)fontImage, positionX, positionY, null);
            positionX += charTexture.width;
            ++i;
        }
        try {
            this.texID = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), img, true, true);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getFontImage(char ch, boolean antiAlias) {
        int charheight;
        Graphics2D tempG = new BufferedImage(1, 1, 2).createGraphics();
        tempG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        tempG.setFont(this.font);
        FontMetrics fontMetrics = tempG.getFontMetrics();
        int charwidth = fontMetrics.charWidth(ch) + 8;
        if (charwidth <= 0) {
            charwidth = 7;
        }
        if ((charheight = fontMetrics.getHeight() + 3) <= 0) {
            charheight = this.font.getSize();
        }
        BufferedImage fontImage = new BufferedImage(charwidth, charheight, 2);
        Graphics2D g = fontImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setFont(this.font);
        g.setColor(Color.WHITE);
        int charx = 3;
        int chary = 1;
        g.drawString(String.valueOf(ch), charx, chary + fontMetrics.getAscent());
        return fontImage;
    }

    public void drawChar(char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        try {
            this.drawQuad(x, y, this.chars[c].width, this.chars[c].height, this.chars[c].posX, this.chars[c].posY, this.chars[c].width, this.chars[c].height);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
        float renderSRCX = srcX / (float)this.IMAGE_WIDTH;
        float renderSRCY = srcY / (float)this.IMAGE_HEIGHT;
        float renderSRCWidth = srcWidth / (float)this.IMAGE_WIDTH;
        float renderSRCHeight = srcHeight / (float)this.IMAGE_HEIGHT;
        GL11.glBegin((int)4);
        GL11.glTexCoord2f((float)(renderSRCX + renderSRCWidth), (float)renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glTexCoord2f((float)renderSRCX, (float)renderSRCY);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glTexCoord2f((float)renderSRCX, (float)(renderSRCY + renderSRCHeight));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f((float)renderSRCX, (float)(renderSRCY + renderSRCHeight));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f((float)(renderSRCX + renderSRCWidth), (float)(renderSRCY + renderSRCHeight));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glTexCoord2f((float)(renderSRCX + renderSRCWidth), (float)renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glEnd();
    }

    public void drawString(String text, double x, double y, Color color, boolean shadow) {
        x *= 2.0;
        y = y * 2.0 - 6.0;
        GL11.glPushMatrix();
        GL11.glScaled((double)0.5, (double)0.5, (double)0.5);
        TextureUtil.bindTexture(this.texID);
        this.glColor(shadow ? new Color(0.05f, 0.05f, 0.05f, (float)color.getAlpha() / 255.0f) : color);
        int size = text.length();
        int indexInString = 0;
        while (indexInString < size) {
            char character = text.charAt(indexInString);
            if (character < this.chars.length && character >= '\u0000') {
                this.drawChar(character, (float)x, (float)y);
                x += (double)(this.chars[character].width - this.charOffset);
            }
            ++indexInString;
        }
        GL11.glPopMatrix();
    }

    public void glColor(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public int getStringHeight(String text) {
        return (this.fontHeight - this.charOffset) / 2;
    }

    public int getHeight() {
        return (this.fontHeight - this.charOffset) / 2;
    }

    public int getStringWidth(String text) {
        int width = 0;
        char[] cArray = text.toCharArray();
        int n = cArray.length;
        int n2 = 0;
        while (n2 < n) {
            char c = cArray[n2];
            if (c < this.chars.length && c >= '\u0000') {
                width += this.chars[c].width - this.charOffset;
            }
            ++n2;
        }
        return width / 2;
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        if (this.antiAlias == antiAlias) {
            return;
        }
        this.antiAlias = antiAlias;
        this.setupTexture();
    }

    public Font getFont() {
        return this.font;
    }

    private static class CharTexture {
        private int width;
        private int height;
        private int posX;
        private int posY;

        private CharTexture() {
        }
    }
}

