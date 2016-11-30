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

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.display.texture.GameTexture;
import com.domsplace.engine.scene.GameObject;
import com.domsplace.engine.scene.GameScene;
import java.util.logging.Level;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Scanlines extends GameObject {
    public static GameTexture SCANLINES_TEXTURE;
    public static final GameTexture getScanlinesTexture() {
        if(Scanlines.SCANLINES_TEXTURE instanceof GameTexture) return Scanlines.SCANLINES_TEXTURE;
        try {
            Scanlines.SCANLINES_TEXTURE = GameTexture.getResourceTexture("resource/hud/scanlines.png", 1, 1);
            Scanlines.SCANLINES_TEXTURE.load();
            Scanlines.SCANLINES_TEXTURE.upload();
        } catch(Exception e) {
            DisplayManager.getInstance().getLogger().log(Level.SEVERE, "Failed to load scanlines!", e);
        }
        return Scanlines.SCANLINES_TEXTURE;
    }
    
    public Scanlines(GameScene scene) {
        super(scene);
        this.texture = Scanlines.getScanlinesTexture();
        this.x = 0;
        this.y = 0;
    }
    
    @Override
    public void render() {
        super.render();
        this.width = getScene().getWidth();
        this.height = getScene().getHeight();
        this.adjustTextureCoordinatesToDimensions();
    }
}
