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

import com.domsplace.engine.disposable.IDisposable;
import com.domsplace.engine.scene.GameScene;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public abstract class GUIObject implements IDisposable {
    private final List<GUIObject> children;
    private GUIObject parent;
    private final GUI gui;
    
    private boolean disposed = false;
    
    public double x;
    public double y;
    public int zIndex;
    
    public GUIObject(GUI gui) {
        this.children = new ArrayList<GUIObject>();
        if(gui instanceof GUI && gui.getChildren() != null) {
            this.zIndex = gui.getChildren().size();
        }
        
        if(this instanceof GUI) {
            this.gui = (GUI)this;
        } else {
            this.gui = gui;
        }
    }
    
    public final GUIObject getParent() {return this.parent;}
    public final GUI getGUI() {return this.gui;}
    public final List<GUIObject> getChildren() {return new ArrayList<GUIObject>(this.children);}
    public abstract int getWidth();
    public abstract int getHeight();
    
    @Override public final boolean isDisposed() {return this.disposed;}
    
    public final GUIObject add(GUIObject object) {
        this.children.add(object);
        object.parent = this;
        return this;
    }
    
    public void remove(GUIObject object) {
        this.children.remove(object);
        object.parent = null;
    }
    
    public boolean contains(GUIObject object) {
        return this.children.contains(object);
    }
    
    public void render(GameScene scene, double frame_took) {
        List<GUIObject> objs = this.getChildren();
        objs.sort(new Comparator<GUIObject>(){
            @Override
            public int compare(GUIObject t, GUIObject t1) {
                return t.compare(t1);
            }
        });
        for(GUIObject go : objs) {
            glPushMatrix();
            glTranslated(go.x, go.y, 0d);
            go.render(scene, frame_took);
            glPopMatrix();
        }
    }
    
    public int compare(GUIObject o) {
        return this.zIndex > o.zIndex ? +1 : this.zIndex < o.zIndex ? -1 : 0;
    }
    
    @Override
    public void dispose() {
        this.disposed = true;
        this.getGUI().remove(this);
        if(this.parent instanceof GUIObject) {
            this.parent.remove(this);
        }
        for(GUIObject go : this.getChildren()) {
            go.remove(this);
        }
    }
}
