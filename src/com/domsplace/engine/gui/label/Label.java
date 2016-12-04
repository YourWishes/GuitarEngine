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
package com.domsplace.engine.gui.label;

import com.domsplace.engine.gui.GUI;
import com.domsplace.engine.gui.GUIObject;
import com.domsplace.engine.text.FontTexture;
import com.domsplace.engine.text.GameText;
import com.domsplace.engine.scene.GameScene;
import java.awt.Color;


/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Label extends GUIObject {
    private final GameText text_object;
    
    public Label(GUI gui) {
        super(gui);
        this.text_object = new GameText(gui.getGameScene());
        this.setFont(FontTexture.getDefault());
    }
    
    @Override
    public void render(GameScene scene, double frame_took) {
        super.render(scene, frame_took);
        this.text_object.render();
    }

    @Override
    public int getWidth() {
        //return (int)this.text_object.width;
        if(!(getFont() instanceof FontTexture)) return 0;
        return (int)getFont().getWidth(getText());
    }

    @Override
    public int getHeight() {
        return (int)this.text_object.height;
    }
    
    public String getText() {
        return this.text_object.getText();
    }
    
    public FontTexture getFont() {
        if(!(this.text_object.getFont() instanceof FontTexture)) return null;
        return (FontTexture)this.text_object.getFont();
    }
    
    public Color getColor() {return this.text_object.color;}
    
    public String setText(String text) {
        this.text_object.setText(text);
        return "";//Needs to return string because of overridden classes.
    }
    
    public void setFont(FontTexture text) {
        this.text_object.setFont(text);
    }
    
    public void setColor(Color c) {this.text_object.color = c;}
    
    protected GameText getInternalObject() {return this.text_object;}
}
