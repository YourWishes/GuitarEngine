/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.display.texture;

import static com.domsplace.engine.utilities.FileUtilities.ioResourceToByteBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import org.lwjgl.stb.STBTTBakedChar;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class FontTexture extends Texture {
    private static final List<FontTexture> LOADED_FONTS = new ArrayList<FontTexture>();
    public static final List<FontTexture> getLoadedFonts() {return new ArrayList<FontTexture>(LOADED_FONTS);}
    
    public static final FontTexture getResourceTexture(String font, int fontSize) throws Exception {
        for(FontTexture t : LOADED_FONTS) {
            if(font.equals(t.font) && t.fontSize == fontSize) {
                return t;
            }
        }
        return new FontTexture(font, fontSize);
    }
    
    private static FontTexture DEFAULT_FONT;
    public static final FontTexture getDefault() {
        if(DEFAULT_FONT instanceof FontTexture) return DEFAULT_FONT;
        DEFAULT_FONT = new FontTexture("resource/font/Hind-Regular.ttf", 24);
        DEFAULT_FONT.loadLater();
        return DEFAULT_FONT;
    }
    
    //Instance
    private final String font;
    private final int fontSize;
    private final int glyphStart;
    private STBTTBakedChar.Buffer cdata;
    
    public FontTexture(String font, int fontSize) {
        this(font,fontSize,2048,2048);
    }
    
    public FontTexture(String font, int fontSize, int tWidth, int tHeight) {
        this(font,fontSize,tWidth,tHeight,32);
    }
    
    public FontTexture(String font, int fontSize, int tWidth, int tHeight, int glyphStart) {
        super();
        textureType = GL_ALPHA;
        textureFilter = GL_LINEAR;
        
        this.font = font;
        this.fontSize = fontSize;
        this.glyphStart = glyphStart;
        this.tWidth = tWidth;
        this.tHeight = tHeight;
        
        LOADED_FONTS.add(this);
    }
    
    public int getGlyphStart() {return this.glyphStart;}
    public int getFontSize() {return fontSize;}
    public STBTTBakedChar.Buffer getCharacterData() {return this.cdata;}
    
    @Override
    public void load() throws IOException {
        if(this.loaded || this.loading) return;
        this.loading = true;
        
        //Let's load a font
        cdata = STBTTBakedChar.malloc(96);
        ByteBuffer ttf = ioResourceToByteBuffer(font, 160 * 1024);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(getWidth() * getHeight());
        stbtt_BakeFontBitmap(ttf, fontSize, bitmap, getWidth(), getHeight(), glyphStart, cdata);
        
        //Bitmap is loaded, now we can push our ByteBuffer to the texture
        this.buff = bitmap;
        
        this.loaded = true;
        this.loading = false;
    }
}
