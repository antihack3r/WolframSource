/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.src.HttpPipeline;
import net.minecraft.src.HttpRequest;
import net.minecraft.src.HttpResponse;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData
extends SimpleTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);
    @Nullable
    private final File cacheFile;
    private final String imageUrl;
    @Nullable
    private final IImageBuffer imageBuffer;
    @Nullable
    private BufferedImage bufferedImage;
    @Nullable
    private Thread imageThread;
    private boolean textureUploaded;
    public Boolean imageFound = null;
    public boolean pipeline = false;

    public ThreadDownloadImageData(@Nullable File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, @Nullable IImageBuffer imageBufferIn) {
        super(textureResourceLocation);
        this.cacheFile = cacheFileIn;
        this.imageUrl = imageUrlIn;
        this.imageBuffer = imageBufferIn;
    }

    private void checkTextureUploaded() {
        if (!this.textureUploaded && this.bufferedImage != null) {
            this.textureUploaded = true;
            if (this.textureLocation != null) {
                this.deleteGlTexture();
            }
            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
        }
    }

    @Override
    public int getGlTextureId() {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void setBufferedImage(BufferedImage bufferedImageIn) {
        this.bufferedImage = bufferedImageIn;
        if (this.imageBuffer != null) {
            this.imageBuffer.skinAvailable();
        }
        this.imageFound = this.bufferedImage != null;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null) {
            super.loadTexture(resourceManager);
        }
        if (this.imageThread == null) {
            if (this.cacheFile != null && this.cacheFile.isFile()) {
                LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
                try {
                    this.bufferedImage = ImageIO.read(this.cacheFile);
                    if (this.imageBuffer != null) {
                        this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
                    }
                    this.imageFound = this.bufferedImage != null;
                }
                catch (IOException ioexception) {
                    LOGGER.error("Couldn't load skin {}", (Object)this.cacheFile, (Object)ioexception);
                    this.loadTextureFromServer();
                }
            } else {
                this.loadTextureFromServer();
            }
        }
    }

    protected void loadTextureFromServer() {
        this.imageThread = new Thread("Texture Downloader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet()){

            @Override
            public void run() {
                HttpURLConnection httpurlconnection = null;
                LOGGER.debug("Downloading http texture from {} to {}", (Object)ThreadDownloadImageData.this.imageUrl, (Object)ThreadDownloadImageData.this.cacheFile);
                if (ThreadDownloadImageData.this.shouldPipeline()) {
                    ThreadDownloadImageData.this.loadPipelined();
                } else {
                    block14: {
                        block15: {
                            httpurlconnection = (HttpURLConnection)new URL(ThreadDownloadImageData.this.imageUrl).openConnection(Minecraft.getMinecraft().getProxy());
                            httpurlconnection.setDoInput(true);
                            httpurlconnection.setDoOutput(false);
                            httpurlconnection.connect();
                            if (httpurlconnection.getResponseCode() / 100 == 2) break block14;
                            if (httpurlconnection.getErrorStream() != null) {
                                Config.readAll(httpurlconnection.getErrorStream());
                            }
                            if (httpurlconnection == null) break block15;
                            httpurlconnection.disconnect();
                        }
                        ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                        return;
                    }
                    try {
                        try {
                            BufferedImage bufferedimage;
                            if (ThreadDownloadImageData.this.cacheFile != null) {
                                FileUtils.copyInputStreamToFile((InputStream)httpurlconnection.getInputStream(), (File)ThreadDownloadImageData.this.cacheFile);
                                bufferedimage = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
                            } else {
                                bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());
                            }
                            if (ThreadDownloadImageData.this.imageBuffer != null) {
                                bufferedimage = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(bufferedimage);
                            }
                            ThreadDownloadImageData.this.setBufferedImage(bufferedimage);
                        }
                        catch (Exception exception1) {
                            LOGGER.error("Couldn't download http texture: " + exception1.getMessage());
                            if (httpurlconnection != null) {
                                httpurlconnection.disconnect();
                            }
                            ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                            return;
                        }
                    }
                    catch (Throwable throwable) {
                        if (httpurlconnection != null) {
                            httpurlconnection.disconnect();
                        }
                        ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                        throw throwable;
                    }
                    if (httpurlconnection != null) {
                        httpurlconnection.disconnect();
                    }
                    ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.this.bufferedImage != null;
                }
            }
        };
        this.imageThread.setDaemon(true);
        this.imageThread.start();
    }

    private boolean shouldPipeline() {
        if (!this.pipeline) {
            return false;
        }
        Proxy proxy = Minecraft.getMinecraft().getProxy();
        if (proxy.type() != Proxy.Type.DIRECT && proxy.type() != Proxy.Type.SOCKS) {
            return false;
        }
        return this.imageUrl.startsWith("http://");
    }

    private void loadPipelined() {
        HttpResponse httpresponse;
        block8: {
            HttpRequest httprequest = HttpPipeline.makeRequest(this.imageUrl, Minecraft.getMinecraft().getProxy());
            httpresponse = HttpPipeline.executeRequest(httprequest);
            if (httpresponse.getStatus() / 100 == 2) break block8;
            this.imageFound = this.bufferedImage != null;
            return;
        }
        try {
            try {
                BufferedImage bufferedimage;
                byte[] abyte = httpresponse.getBody();
                ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
                if (this.cacheFile != null) {
                    FileUtils.copyInputStreamToFile((InputStream)bytearrayinputstream, (File)this.cacheFile);
                    bufferedimage = ImageIO.read(this.cacheFile);
                } else {
                    bufferedimage = TextureUtil.readBufferedImage(bytearrayinputstream);
                }
                if (this.imageBuffer != null) {
                    bufferedimage = this.imageBuffer.parseUserSkin(bufferedimage);
                }
                this.setBufferedImage(bufferedimage);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
                this.imageFound = this.bufferedImage != null;
                return;
            }
        }
        catch (Throwable throwable) {
            this.imageFound = this.bufferedImage != null;
            throw throwable;
        }
        this.imageFound = this.bufferedImage != null;
    }
}

