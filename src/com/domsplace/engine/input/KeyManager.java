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
package com.domsplace.engine.input;

import com.domsplace.engine.scene.GameScene;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public final class KeyManager {
    //Satics
    private static final Map<Integer, List<Integer>> KEY_BINDINGS = new HashMap<Integer, List<Integer>>();
    
    public static final int BINDING_EXAMPLE = 0;//Do not remove, but do not use either.
    public static final int BINDING_ACCEPT  = 1;
    
    private static final KeyManager INSTANCE = new KeyManager();
    public static KeyManager getInstance() {return KeyManager.INSTANCE;}
    
    //Instance
    private List<Integer> KEY_DOWN_STATES = new ArrayList<Integer>();
    private List<KeyListener> LISTENERS = new ArrayList<KeyListener>();
    
    private KeyManager() {
        this.addBinding(BINDING_EXAMPLE, GLFW.GLFW_KEY_BACKSPACE);
        
        this.addBinding(BINDING_ACCEPT, GLFW.GLFW_KEY_SPACE);
        this.addBinding(BINDING_ACCEPT, GLFW.GLFW_KEY_E);
        this.addBinding(BINDING_ACCEPT, GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }
    
    public List<Integer> getBindings(int BINDING) {
        if(KeyManager.KEY_BINDINGS.containsKey(BINDING)) {
            return KEY_BINDINGS.get(BINDING);
        }
        List<Integer> list = new ArrayList<Integer>();
        KEY_BINDINGS.put(BINDING, list);
        return list;
    }
    
    public List<KeyListener> getListeners() {return new ArrayList<KeyListener>(LISTENERS);}
    
    public List<Integer> addBinding(int BINDING, int KEY) {
        List<Integer> binds = this.getBindings(BINDING);
        binds.add(KEY);
        return binds;
    }
    public void addListener(KeyListener listener) {this.LISTENERS.add(listener);}
    
    public void removeListener(KeyListener listener) {this.LISTENERS.remove(listener);}
    
    public boolean isBound(int BINDING, int KEY) {
        Object o = (Object) KEY;
        return this.getBindings(BINDING).contains(o);
    }
    
    public void handleKey(int key, int action) {
        GameScene gs = GameScene.getActiveScene();
        if(gs == null) return;
        if(action == GLFW_RELEASE && KEY_DOWN_STATES.contains((Integer)key)) {
            KEY_DOWN_STATES.remove((Integer)key);
            for(KeyListener ks : this.getListeners()) {
                if(!(ks instanceof KeyListener)) continue;
                ks.onKeyRelease(this, key);
            }
        }
        if(action == GLFW_PRESS && !KEY_DOWN_STATES.contains((Integer)key)) {
            KEY_DOWN_STATES.add((Integer)key);
            for(KeyListener ks : this.getListeners()) {
                if(!(ks instanceof KeyListener)) continue;
                ks.onKeyPress(this, key);
            }
        } else if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            for(KeyListener ks : this.getListeners()) {
                if(!(ks instanceof KeyListener)) continue;
                ks.onKeyRepeat(this, key);
            }
        }
    }
    
    public void handleMouse(int mouse, int action) {
        /* 
            At the time of writing this (December 2016) Mouse and Key indexes do
            not collide so we can use the same impl for them both.
        
            If they do in the future a more complicated set of key bindings for
            mouse actions will need to be written.
        */
        this.handleKey(mouse, action);
    }
    
    public void update() {
        GameScene gs = GameScene.getActiveScene();
        if(gs == null) return;
        for(KeyListener ks : this.getListeners()) {
            if(!(ks instanceof KeyListener)) continue;
            for(Integer key : KEY_DOWN_STATES) {
                ks.onKeyDown(this, key);
            }
        }
    }
}
