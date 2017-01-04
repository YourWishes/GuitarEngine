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
import com.domsplace.engine.game.Game;
import com.domsplace.engine.scene.gameobject.GameObject;
import com.domsplace.engine.scene.GameScene;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GUI extends GUIFrame {
    private static GameTexture TEXTURE;
    public static final GameTexture getGUITexture() {
        if(TEXTURE instanceof GameTexture) return TEXTURE;
        try {
            TEXTURE = new GameTexture("resource/hud/gui.png", 3, 3);
            TEXTURE.load();
            TEXTURE.upload();
            return TEXTURE;
        } catch(Exception e) {
            DisplayManager.getInstance().getLogger().log(Level.SEVERE, "Can't find GUI Texture.", e);
            return null;
        }
    }
    
    //Instance
    private final GameScene s;
    
    public GUI(GameScene s) {
        super(null);
        this.s = s;
    }
    
    public final GameScene getGameScene() {return this.s;}
    public final Game getGame() {return s.getGame();}
    
    public GUIObject getObjectAt(double x, double y) {
        List<GUIObject> objs = this.getObjectsAt(x, y);
        if(objs.size() == 0) return null;
        objs.sort(new Comparator<GUIObject>(){
            @Override
            public int compare(GUIObject t, GUIObject t1) {
                return t.compare(t1);
            }
        });
        
        return objs.get(objs.size()-1);
    }
    
    public List<GUIObject> getObjectsAt(double x, double y) {
        List<GUIObject> objs = new ArrayList<GUIObject>();
        
        for(GUIObject o : this.getChildren()) {
            if(!(o instanceof GUIObject)) continue;
            double ox = o.x;
            double oy = o.y;
            GUIObject parent = o.getParent();
            while(parent instanceof GUIObject) {
                ox += parent.x;
                oy += parent.y;
                parent = parent.getParent();
            }
            if(x < ox || y < oy || x > ox+o.getWidth() || y > oy+o.getHeight()) continue;
            objs.add(o);
        }
        
        return objs;
    }
}
