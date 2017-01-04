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

import com.domsplace.engine.scene.gameobject.GameObject;
import com.domsplace.engine.scene.GameScene;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GUIBorder extends GUIObject {
    public static final int BORDER_PADDING = 11;
    
    private final double tw;
    private final double th;
    private final GameObject TOP_LEFT;
    private final GameObject MIDDLE_LEFT;
    private final GameObject BOTTOM_LEFT;
    private final GameObject TOP_MIDDLE;
    private final GameObject TOP_RIGHT;
    private final GameObject MIDDLE_RIGHT;
    private final GameObject BOTTOM_RIGHT;
    private final GameObject BOTTOM_MIDDLE;
    private final GameObject CENTER;
    
    private List<GameObject> BORDER_OBJECTS;
    
    public GUIBorder(GUI gui) {
        super(gui);
        BORDER_OBJECTS = new ArrayList<GameObject>();
        
        BORDER_OBJECTS.add(TOP_LEFT = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(MIDDLE_LEFT = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(BOTTOM_LEFT = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(TOP_MIDDLE = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(TOP_RIGHT = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(MIDDLE_RIGHT = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(BOTTOM_RIGHT = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(BOTTOM_MIDDLE = new GameObject(gui.getGameScene()));
        BORDER_OBJECTS.add(CENTER = new GameObject(gui.getGameScene()));
        
        for(GameObject go : BORDER_OBJECTS) {
            go.texture = GUI.getGUITexture();
            go.resizeToTexture();
        }
        
        this.tw = TOP_LEFT.width;
        this.th = TOP_LEFT.height;
        
        this.setWidth(400);
        this.setHeight(200);
    }
    
    @Override
    public void render(GameScene scene, double frame_took) {
        glPushMatrix();
        for(GameObject go : new ArrayList<GameObject>(BORDER_OBJECTS)) {
            go.render();
        }
        glPopMatrix();
        super.render(scene, frame_took);
    }

    @Override
    public int getWidth() {
        return (int) ((int)CENTER.width + (TOP_LEFT.width * 2.0));
    }

    @Override
    public int getHeight() {
        return (int) ((int)CENTER.height + (TOP_LEFT.height * 2.0));
    }
    
    public void setWidth(int width) {
        if(width < tw * 2) return;
        TOP_MIDDLE.width = BOTTOM_MIDDLE.width = CENTER.width = width-(tw*2.0);
        TOP_MIDDLE.x = BOTTOM_MIDDLE.x = CENTER.x = tw;
        TOP_RIGHT.x = MIDDLE_RIGHT.x = BOTTOM_RIGHT.x = TOP_MIDDLE.width + TOP_MIDDLE.x;
        adjustCoordinates();
    }
    
    public void setHeight(int height) {
        double individual_height = TOP_LEFT.height;
        if(height < individual_height * 2) {
            double h = (double)height/2;
            TOP_LEFT.height = TOP_MIDDLE.height = TOP_RIGHT.height = BOTTOM_LEFT.height = BOTTOM_MIDDLE.height = BOTTOM_RIGHT.height = h;
            BOTTOM_LEFT.y = BOTTOM_MIDDLE.y = BOTTOM_RIGHT.y = h;
            MIDDLE_LEFT.height = CENTER.height = MIDDLE_RIGHT.height = 0;
            adjustCoordinates();
        } else {
            MIDDLE_LEFT.height = CENTER.height = MIDDLE_RIGHT.height = height-(th*2.0);
            MIDDLE_LEFT.y = CENTER.y = MIDDLE_RIGHT.y = th;
            BOTTOM_LEFT.y = BOTTOM_MIDDLE.y = BOTTOM_RIGHT.y = MIDDLE_LEFT.height + MIDDLE_LEFT.y;
            adjustCoordinates();
        }
    }
    
    private void adjustCoordinates() {
        TOP_LEFT.adjustTextureCoordinatesTo(0, 0);
        TOP_MIDDLE.adjustTextureCoordinatesTo(1, 0);
        TOP_RIGHT.adjustTextureCoordinatesTo(2, 0);
        
        MIDDLE_LEFT.adjustTextureCoordinatesTo(0, 1);
        CENTER.adjustTextureCoordinatesTo(1, 1);
        MIDDLE_RIGHT.adjustTextureCoordinatesTo(2, 1);
        
        BOTTOM_LEFT.adjustTextureCoordinatesTo(0, 2);
        BOTTOM_MIDDLE.adjustTextureCoordinatesTo(1, 2);
        BOTTOM_RIGHT.adjustTextureCoordinatesTo(2, 2);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        for(GameObject go : this.BORDER_OBJECTS) {
            go.dispose();
        }
        BORDER_OBJECTS.clear();
    }
}
