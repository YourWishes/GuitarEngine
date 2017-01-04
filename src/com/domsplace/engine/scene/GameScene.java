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
package com.domsplace.engine.scene;

import com.domsplace.engine.scene.gameobject.GameObject;
import com.domsplace.engine.gui.GUI;
import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.disposable.IDisposable;
import com.domsplace.engine.game.Game;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GameScene implements IDisposable {
    public static GameScene ACTIVE_SCENE = null;
    public static GameScene getActiveScene() {
        if(ACTIVE_SCENE instanceof GameScene && ACTIVE_SCENE.isDisposed()) return ACTIVE_SCENE = null;
        return ACTIVE_SCENE;
    }
    public static void setActiveScene(GameScene scene) {
        if(DisplayManager.getInstance().isDisposed()) {scene.dispose();}
        if(scene.isDisposed()) return;
        
        if(ACTIVE_SCENE instanceof GameScene) {
            for(GameObject go : ACTIVE_SCENE.getGameObjects()) {
                go.onSceneChange(scene);
            }
        }
        ACTIVE_SCENE = scene;
    }
    
    //Instance
    private final Game game;
    private final GUI gui;
    private final List<GameObject> objects;
    private Color backgroundColor;
    public float x;
    public float y;
    private boolean disposed = false;
    private String title = null;
    
    public long last_frame = -1;

    public GameScene(Game game) {
        this.game = game;
        this.objects = new ArrayList<GameObject>();
        this.gui = new GUI(this);
        this.backgroundColor = Color.WHITE;
    }

    public final GUI getGUI() {return this.gui;}
    public final Game getGame() {return this.game;}
    public final List<GameObject> getGameObjects() {return new ArrayList<GameObject>(this.objects);}
    public final int getWidth() {return DisplayManager.getInstance().getWidth();}
    public final int getHeight() {return DisplayManager.getInstance().getHeight();}
    public final Color getBackgroundColor(){return this.backgroundColor;}
    public final String getTitle() {return title;}
    
    //Mouse Coordinates are in Window Space, they need to be in Scene space
    public final double getMouseX() {
        double mpc = DisplayManager.getInstance().getWindow().getMouseX()/(double)DisplayManager.getInstance().getWindow().getWidth();
        return ((double)getWidth())*mpc;
    }
    public final double getMouseY() {
        double mpc = DisplayManager.getInstance().getWindow().getMouseY()/(double)DisplayManager.getInstance().getWindow().getHeight();
        return ((double)getHeight())*mpc;
    }
    
    @Override public final boolean isDisposed() {return this.disposed;}
    
    public final void setBackgroundColor(Color c) {this.backgroundColor = c;}
    public final void setTitle(String title) {this.title = title;}

    public void addGameObject(GameObject object) {
        this.objects.add(object);
        sortList();
    }

    public void removeGameObject(GameObject object) {
        this.objects.remove(object);
        sortList();
    }
    
    /**
     * Used to sort the internal ArrayList of GameObjects by order they should
     * be rendered in first.
     */
    public void sortList() {
        this.objects.sort(new Comparator<GameObject>(){
            @Override
            public int compare(GameObject t, GameObject t1) {
                return Integer.compare(t.zindex, t1.zindex);
            }
        });
    }
    
    public void render() {
        //Timers
        long now = System.nanoTime();
        if(this.last_frame == -1) {
            this.last_frame = now;
        }
        long diff = now-last_frame;
        last_frame = now;
        
        //In Screen Space
        sortList();
        
        //In World Space
        glPushMatrix();
        glTranslatef(-x, -y, 0f);
        
        for(GameObject object : this.getGameObjects()) {
            object.tick();
            object.render();
        }
        glPopMatrix();
        
        glPushMatrix();
        this.getGUI().render(this, diff);
        glPopMatrix();
    }
    
    @Override
    public void dispose() {
        if(this.isDisposed()) return;
        this.gui.dispose();
        for(GameObject go : this.getGameObjects()) {
            go.dispose();
        }
        if(this.equals(GameScene.ACTIVE_SCENE)) {
            GameScene.ACTIVE_SCENE = null;
        }
        this.disposed = true;
    }
}
