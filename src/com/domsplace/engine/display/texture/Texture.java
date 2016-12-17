/*
 * Copyright 2016 Dominic Masters <dominic@domsplace.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.domsplace.engine.display.texture;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.disposable.IDisposable;
import com.domsplace.engine.utilities.TimeUtilities;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Texture implements Runnable, IDisposable {
    private static Texture BOUND_TEXTURE = null;
    
    private static final List<Texture> TEXTURES_TO_UPLOAD = new ArrayList<Texture>();
    public static final List<Texture> getTexturesToUpload() {return new ArrayList<Texture>(TEXTURES_TO_UPLOAD);}
    
    public static final int generateTextureHandle() {
        return glGenTextures();
    }
    
    public static void unbind() {
        BOUND_TEXTURE = null;
        glBindTexture(GL_TEXTURE_2D,0);
    }

    //Instance
    protected boolean loaded = false;
    protected boolean loading = false;
    
    private InputStream is;
    private int handle = -1;
    protected int tWidth;
    protected int tHeight;
    protected ByteBuffer buff;
    private boolean disposed = false;
    
    protected int textureType = GL_RGBA;
    protected int textureFilter = GL_LINEAR_MIPMAP_LINEAR ;
    
    public boolean smooth = true;
    
    //Threading rules
    private boolean reqLoadLater = false;
    private boolean reqUploadLater = false;

    public Texture(InputStream is) {
        this.is = is;
    }
    
    protected Texture() {
        
    }
    
    public int getWidth() {return this.tWidth;}
    public int getHeight() {return this.tHeight;}
    public int getHandle() {return this.handle;}
    
    public boolean isUploaded() {return this.handle != -1;}
    @Override public boolean isDisposed() { return disposed; }
    public boolean isBound() {return this.equals(BOUND_TEXTURE);}

    public void genHandle() {
        this.handle = Texture.generateTextureHandle();
    }
    
    public void load() throws IOException {
        if(this.loaded || this.loading) return;
        this.loading = true;
        PNGDecoder decoder = new PNGDecoder(is);

        tWidth = decoder.getWidth();
        tHeight = decoder.getHeight();
        buff = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        decoder.decode(buff, decoder.getWidth() * 4, Format.RGBA);
        buff.flip();

        this.is.close();
        this.loaded = true;
        this.loading = false;
    }
    
    public Thread uploadLater() {
        return genThread(false,true);
    }
    
    public Thread loadLater() {
        return genThread(true,false);
    }
    
    public Thread loadAndUploadLater() {
        return genThread(true,true);
    }
    
    private Thread genThread(boolean loadLater, boolean uploadLater) {
        this.reqLoadLater = loadLater;
        this.reqUploadLater = uploadLater;
        Thread thread = new Thread(this);
        thread.start();
        return thread;
    }
    
    @Override
    public void run() {
        try {
            if(reqLoadLater) this.load();
            if(reqUploadLater) this.upload();
        } catch (Exception ex) {
            DisplayManager.getInstance().getLogger().log(Level.SEVERE, "Faield to load Texture!", ex);
        }
    }
    
    private void uploadMainThread() throws IOException {
        Texture.TEXTURES_TO_UPLOAD.remove(this);
        this.genHandle();
        //Put Texture on GFX card
        this.bind();
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, textureType, getWidth(), getHeight(), 0, textureType, GL_UNSIGNED_BYTE, buff);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void upload() throws IOException {
        if(Thread.currentThread().equals(DisplayManager.getInstance().getKnownMainThread())) {
            this.uploadMainThread();
        } else {
            if(Texture.TEXTURES_TO_UPLOAD.contains(this)) return;
            if(this.handle != -1) return;
            Texture.TEXTURES_TO_UPLOAD.add(this);
            while(!this.isUploaded()) {TimeUtilities.sleepThread(1);}
        }
    }

    public void bind() {
        if(this.equals(BOUND_TEXTURE)) return;
        if(this.handle == -1) return;
        
        float f = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);//Texture filtering
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, textureFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, textureFilter);
        if(f > 0) glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, f);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.handle);
        BOUND_TEXTURE = this;
    }

    @Override
    public void dispose() {
        disposed=true;
        TEXTURES_TO_UPLOAD.remove(this);
        
        if(this.isUploaded()) {
            if(this.isBound()) {
                Texture.unbind();
            }
            
            //Delete the texture
            glDeleteTextures(this.handle);
            this.handle = -1;
        }
        
        try {this.buff.clear(); }catch(Exception e) {}
        try { this.is.close(); } catch(Exception e) { }
    }
}
