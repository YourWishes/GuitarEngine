/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.scene.gameobject.animated;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.utilities.TimeUtilities;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public abstract class SAGOAnimation {
    
    //Instance
    private final double start;
    private final double duration;
    public final SimpleAnimatedGameObject go;
    private boolean finished = false;
    private final boolean blocking;
    
    public SAGOAnimation(double duration, double delay, SimpleAnimatedGameObject go, boolean blocking) {
        this.start = ((double)System.currentTimeMillis())/1000d + delay;
        this.duration = duration;
        this.go = go;
        this.blocking = blocking;
    }
    
    public final double getStart() {return start;}
    public final double getDuration() {return duration;}
    public final double getEndTime() {return start+duration;}
    public final boolean isFinished() {return finished;}
    public final boolean isBlocking() {return blocking;}
    
    public void run() {
        //Check Blocking
        if(blocking) {
            if(Thread.currentThread().equals(DisplayManager.getInstance().getKnownMainThread())) {
                //Shit, this is the main thread... :^)
            } else {
                while(!isFinished()) {
                    if(TimeUtilities.sleepThread(1)) break;
                }
            }
        }
    }
    
    public final boolean goRender(double now) {
        this.finished = false;
        if(now > getEndTime()) {
            this.finished = true;
            finish(now);
            return true;
        } else if(now > getStart()) {
            render(now);
            return false;
        }
        return false;
    }
    
    public abstract void render(double now);
    public abstract void finish(double now);
}