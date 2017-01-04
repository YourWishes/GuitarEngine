/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.scene.gameobject.animated;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class FadeAnimation extends SAGOAnimation {
    private final boolean fadeIn;
    
    public FadeAnimation(boolean fadeIn, double duration, double delay, SimpleAnimatedGameObject go, boolean blocking) {
        super(duration, delay, go, blocking);
        this.fadeIn = fadeIn;
        if(fadeIn) {
            go.alpha = 0;
        } else {
            go.alpha = 1;
        }
    }
    
    public boolean isFadeIn() {return fadeIn;}
    public boolean isFadeOut() {return !isFadeIn();}

    @Override
    public void render(double now) {
        double percent = (now-this.getStart())/this.getDuration();
        if(isFadeIn()) {
            this.go.alpha = (float)percent;
        } else {
            this.go.alpha = 1-(float)percent;
        }
    }

    @Override
    public void finish(double now) {
        this.go.alpha = 1;
    }
}
