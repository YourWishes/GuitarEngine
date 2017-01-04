/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.gui.input;

import com.domsplace.engine.gui.GUI;
import com.domsplace.engine.gui.GUIObject;
import com.domsplace.engine.input.KeyListener;
import com.domsplace.engine.input.KeyManager;
import static com.domsplace.engine.input.KeyManager.BINDING_ACCEPT;
import com.domsplace.engine.scene.GameScene;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public abstract class Button extends GUIObject implements KeyListener {
    private boolean hoverLastFrame = false;
    private boolean pressedOnMe = false;
    
    public Button(GUI gui) {
        super(gui);
        KeyManager.getInstance().addListener(this);
    }
    
    @Override
    public void render(GameScene scene, double frame_took) {
        if(!getGUI().getGameScene().equals(GameScene.getActiveScene())) return;
        //Check the position of the cursor in SCREEN space
        double mouseX = getGUI().getGameScene().getMouseX();
        double mouseY = getGUI().getGameScene().getMouseY();
        
        if(!isInsideButton(mouseX,mouseY)) {
            //Outside the box.
            this.onMoveOutside();
            if(hoverLastFrame) this.onHoverOff();
            
            hoverLastFrame = false;
        } else {
            //Inside the box.
            this.onMoveHover();
            if(!hoverLastFrame) this.onHover();
            if(!this.pressedOnMe) this.onMouseUp();
            
            hoverLastFrame = true;
        }
        
        //Render
        super.render(scene, frame_took);
    }
    
    //Simple overridable incase.
    public boolean isInsideButton(double mouseX, double mouseY) {return this.equals(getGUI().getObjectAt(mouseX, mouseY));}
    
    public void onMoveHover() {}
    public void onMoveOutside() {}
    public void onHover() {}
    public void onHoverOff() {}
    public void onPress() {}
    public void onMouseDown() {}
    public void onMouseUp() {}//Similar to "onHover" but only called when the button WAS pressed
    public void onRelease() {}
    public void onReleaseOutside() {}//Called when the mouse was RELEASED ouside the button, doesn't include clicked

    @Override
    public void onKeyPress(KeyManager manager, int KEY) {
        if(!getGUI().getGameScene().equals(GameScene.getActiveScene())) return;
        if(manager.isBound(BINDING_ACCEPT, KEY)) {
            //First check if the mouse is within our bounds
            double mouseX = getGUI().getGameScene().getMouseX();
            double mouseY = getGUI().getGameScene().getMouseY();

            if(isInsideButton(mouseX,mouseY)) {
                this.onPress();
                pressedOnMe = true;
            }
        }
    }

    @Override
    public void onKeyRelease(KeyManager manager, int KEY) {
        if(!getGUI().getGameScene().equals(GameScene.getActiveScene())) return;
        if(manager.isBound(BINDING_ACCEPT, KEY) && pressedOnMe) {
            pressedOnMe = false;
            //First check if the mouse is within our bounds
            double mouseX = getGUI().getGameScene().getMouseX();
            double mouseY = getGUI().getGameScene().getMouseY();

            if(isInsideButton(mouseX,mouseY)) {
                this.onRelease();
            } else {
                this.onReleaseOutside();
            }
        }
    }

    @Override
    public void onKeyRepeat(KeyManager manager, int KEY) {
        if(!getGUI().getGameScene().equals(GameScene.getActiveScene())) return;
        if(manager.isBound(BINDING_ACCEPT, KEY)) {
            //First check if the mouse is within our bounds
            double mouseX = getGUI().getGameScene().getMouseX();
            double mouseY = getGUI().getGameScene().getMouseY();

            if(isInsideButton(mouseX,mouseY)) {
                this.onMouseDown();
            }
        }
    }

    @Override
    public void onKeyDown(KeyManager manager, int KEY) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void dispose() {
        KeyManager.getInstance().removeListener(this);
        super.dispose();
    }
}
