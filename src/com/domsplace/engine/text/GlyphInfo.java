/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.text;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointHMetrics;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GlyphInfo {
    private final FontTexture font;
    private final char c;
    
    private int width;
    private int leftSideBearing;
    
    public GlyphInfo(FontTexture font, char c) {
        this.font = font;
        this.c = c;
    }
    
    public FontTexture getFont() {return font;}
    public char getCharacter() {return c;}
    public int getWidth() {return width;}
    public int getLeftSideBearing() {return leftSideBearing;}
    
    public void load(STBTTFontinfo fontInfo) {
        IntBuffer advancewidth = BufferUtils.createIntBuffer(1);
        IntBuffer leftsidebearing = BufferUtils.createIntBuffer(1);
        stbtt_GetCodepointHMetrics(fontInfo, c, advancewidth, leftsidebearing);
        width = advancewidth.get(0);
        leftSideBearing = leftsidebearing.get(0);
    }
}
