/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.text;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.display.texture.Texture;
import static com.domsplace.engine.utilities.FileUtilities.ioResourceToByteBuffer;
import com.domsplace.engine.utilities.TimeUtilities;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

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
        //We really need this loaded ASAP, so.. we're gonna be a bit cheeky here.
        DEFAULT_FONT = new FontTexture("resource/font/Hind-Regular.ttf", 24);
        //If this is the main thread we're going to force a load now.
        if(Thread.currentThread().equals(DisplayManager.getInstance().getKnownMainThread())) {
            try {
                DEFAULT_FONT.load();
                DEFAULT_FONT.uploadMainThread();
            } catch(Exception e) {
                DisplayManager.getInstance().getLogger().log(Level.SEVERE, "I tried naughty Font Loading and it went REAL bad.", e);
            }
        } else {
            //Not the main thread? But ok we will play the waiting game.
            DEFAULT_FONT.loadLater();
            while(!DEFAULT_FONT.isUploaded()) {
                //This may lock the game up
                TimeUtilities.sleepThread(1);
            }
        }
        
        return DEFAULT_FONT;
    }
    
    //Instance
    private final String font;
    private final int fontSize;
    private final int glyphStart;
    
    private STBTTBakedChar.Buffer cdata;
    private STBTTFontinfo fontInfo;
    private GlyphInfo[] glyphs;
    private float fontSizeScale;
    
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
        
        this.glyphs = new GlyphInfo[glyphStart + 128];
        
        LOADED_FONTS.add(this);
    }
    
    public int getGlyphStart() {return this.glyphStart;}
    public int getFontSize() {return fontSize;}
    public STBTTBakedChar.Buffer getCharacterData() {return this.cdata;}
    
    public float getWidth(String text) {
        //I have a feeling this is not returning an accurate number, but I don't know what will to be honest, it's pretty close.
        if(text.contains("\n")) {
            String[] lines = text.split("\n");
            float ml = 0;
            for(int i = 0; i < lines.length; i++) {
                ml = Math.max(ml, (float)getWidth(lines[i]));
            }
            return ml;
        } else {
            //We can maybe improve efficiency here a tad by using a cache and storing FontMetrics on texture load?
            float length = 0f;
            for (int i = 0; i < text.length(); i++) {
                GlyphInfo gi = this.glyphs[(int)text.charAt(i)];
                if(!(gi instanceof GlyphInfo)) continue;
                length += gi.getWidth() + gi.getLeftSideBearing();
            }
            return length*fontSizeScale;//Magic number is to try and correct the error, I guess
        }
    }
    
    @Override
    public void load() throws IOException {
        if(this.loaded || this.loading) return;
        this.loading = true;
        
        //Let's load a font
        cdata = STBTTBakedChar.malloc(96);
        ByteBuffer ttf = ioResourceToByteBuffer(font, 160 * 1024);
        
        //Load our FontInfo (https://javadoc.lwjgl.org/org/lwjgl/stb/STBTruetype.html)
        ByteBuffer fontInfoBuffer = BufferUtils.createByteBuffer(1<<25);
        fontInfo = new STBTTFontinfo(fontInfoBuffer);
        stbtt_InitFont(fontInfo, ttf);

        //Load our Bitmap Data
        ByteBuffer bitmap = BufferUtils.createByteBuffer(getWidth() * getHeight());
        stbtt_BakeFontBitmap(ttf, this.getFontSize(), bitmap, getWidth(), getHeight(), glyphStart, cdata);
        
        //Setup our character data.
        fontSizeScale = stbtt_ScaleForPixelHeight(fontInfo,this.getFontSize());
        
        for(int c = glyphStart; c < glyphStart+128; c++) {
            char ch = (char)c;
            this.glyphs[c] = new GlyphInfo(this,ch);
            this.glyphs[c].load(fontInfo);
        }
        
        //Bitmap is loaded, now we can push our ByteBuffer to the texture
        this.buff = bitmap;
        
        this.loaded = true;
        this.loading = false;
    }
}
