/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.scene.gameobject.animated;

import com.domsplace.engine.scene.GameScene;
import com.domsplace.engine.scene.gameobject.GameObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class SimpleAnimatedGameObject extends GameObject {
    private List<SAGOAnimation> animations = new ArrayList<SAGOAnimation>();
    
    public SimpleAnimatedGameObject(GameScene scene) {
        super(scene);
    }
    
    private List<SAGOAnimation> getAnimations() {return new ArrayList<SAGOAnimation>(animations);}
    
    private void addAnimation(SAGOAnimation anim) {
        if(!(anim.go.equals(this))) throw new RuntimeException("Nice try wishes.");
        if(animations.contains(anim)) return;
        this.animations.add(anim);
        anim.run();
    }
    
    private void removeAnimation(SAGOAnimation anim) {
        if(!(anim.go.equals(this))) throw new RuntimeException("Nice try wishes.");
        this.animations.remove(anim);
    }
    
    public FadeAnimation fadeIn(){return fadeIn(true);}
    public FadeAnimation fadeIn(boolean blocking) {return fadeIn(0.4,blocking);}
    public FadeAnimation fadeIn(double duration, double delay) {return fadeIn(duration,delay,true);}
    public FadeAnimation fadeIn(double duration) {return fadeIn(duration,true);}
    public FadeAnimation fadeIn(double duration, boolean blocking) {return this.fadeIn(duration,0,blocking);}
    public FadeAnimation fadeIn(double duration, double delay, boolean blocking) {return this.fade(true,duration,delay,blocking);}
    
    public FadeAnimation fadeOut(){return fadeOut(true);}
    public FadeAnimation fadeOut(boolean blocking) {return fadeOut(0.4,blocking);}
    public FadeAnimation fadeOut(double duration, double delay) {return fadeOut(duration,delay,true);}
    public FadeAnimation fadeOut(double duration) {return fadeOut(duration,true);}
    public FadeAnimation fadeOut(double duration, boolean blocking) {return this.fadeOut(duration,0,blocking);}
    public FadeAnimation fadeOut(double duration, double delay, boolean blocking) {return this.fade(false,duration,delay,blocking);}
    
    public FadeAnimation fade(boolean fadeIn, double duration, double delay, boolean blocking) {
        FadeAnimation fa = new FadeAnimation(fadeIn,duration,delay,this,blocking);
        addAnimation(fa);
        return fa;
    }
    
    @Override
    public void render() {
        long time = System.currentTimeMillis();
        double dTime = ((double)time)/1000d;
        
        for(SAGOAnimation anim : getAnimations()) {
            if(!anim.goRender(dTime)) continue;
            animations.remove(anim);
        }
        
        super.render();
    }
}
