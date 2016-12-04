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

import com.domsplace.engine.scene.GameObject;
import com.domsplace.engine.scene.GameScene;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Image extends GUIObject {
    //Instance
    private final GameObject go;
    
    public Image(GUI g, GameObject go) {
        super(g);
        this.go = go;
    }
    
    public final GameObject getGameObject() {return this.go;}

    @Override
    public int getWidth() {
        return (int)this.go.width;
    }

    @Override
    public int getHeight() {
        return (int)this.go.height;
    }
    
    @Override
    public void render(GameScene scene, double frame_took) {
        this.go.render();
        super.render(scene, frame_took);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        go.dispose();
    }
}
