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
package com.domsplace.engine.text;

import com.domsplace.engine.scene.gameobject.GameObject;
import com.domsplace.engine.scene.GameScene;
import java.awt.Color;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GameText extends GameObject {
    private String text = "Line 1111111111111111\nLine 2\nLine 3";
    
    //Instance
    private int fontSize = 12;
    
    public GameText(GameScene scene) {
        super(scene);
    }
    
    public FontTexture getFont() {return texture instanceof FontTexture ? (FontTexture)texture : null;}
    public String getText() {return text;}
    public int getFontSize() {return fontSize;}

    public void setFont(FontTexture font) {this.texture = font;}
    public void setText(String text) {this.text = text;}
    public void setFontSize(int fs) {this.fontSize = fs;}
    
    public final float getFontScale() {return fontSize * 0.055f;}

    @Override
    public void renderMesh() {
        if(!(getFont() instanceof FontTexture)) return;
        
        try (MemoryStack stack = stackPush()) {
            this.width = 0;
            this.height = getFont().getFontSize();
            
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats((float)(this.height*0.8f));

            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

            glBegin(GL_QUADS);
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c == '\n') {
                    this.height += getFont().getFontSize()*getFontScale();
                    
                    y.put(0, y.get(0) + getFont().getFontSize()*0.8f);
                    x.put(0, 0.0f);
                    continue;
                } else if (c < getFont().getGlyphStart() || 128 <= c) {
                    continue;
                }
                stbtt_GetBakedQuad(getFont().getCharacterData(), getFont().getWidth(), getFont().getHeight(), c - getFont().getGlyphStart(), x, y, q, true);
                
                glTexCoord2f(q.s0(), q.t0());
                glVertex2f(q.x0(), q.y0());

                glTexCoord2f(q.s1(), q.t0());
                glVertex2f(q.x1(), q.y0());

                glTexCoord2f(q.s1(), q.t1());
                glVertex2f(q.x1(), q.y1());

                glTexCoord2f(q.s0(), q.t1());
                glVertex2f(q.x0(), q.y1());
                
                this.width = Math.max(this.width, x.get(0)*getFontScale());
            }
            glEnd();
            
            this.width *= this.getFontScale();
            this.height *= this.getFontScale();
        }
    }
    
    @Override
    public void render() {
        glPushMatrix();
        glScaled(this.getFontScale(),this.getFontScale(),1);
        super.render();
        glPopMatrix();
    }
}
