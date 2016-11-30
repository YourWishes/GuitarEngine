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
package com.domsplace.engine.gui;

import com.domsplace.engine.display.texture.FontTexture;
import com.domsplace.engine.game.GameText;
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
        return (int)this.text_object.width;
    }

    @Override
    public int getHeight() {
        return (int)this.text_object.height;
    }
    
    public String getText() {
        return this.text_object.getText();
    }
    
    public Color getColor() {return this.text_object.color;}
    
    public void setText(String text) {
        this.text_object.setText(text);
    }
    
    public void setFont(FontTexture text) {
        this.text_object.setFont(text);
    }
    
    public void setColor(Color c) {this.text_object.color = c;}
    
    protected GameText getInternalObject() {return this.text_object;}
}
