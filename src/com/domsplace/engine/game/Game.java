/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.game;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.input.KeyManager;
import com.domsplace.engine.scene.GameScene;
import com.domsplace.engine.sound.SoundFactory;
import com.domsplace.engine.utilities.FileUtilities;
import com.domsplace.engine.utilities.ReflectionUtilities;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public abstract class Game {
    private final Logger logger;
    private final Thread mainThread;
    
    public Game() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.mainThread = Thread.currentThread();
    }
    
    public Logger getLogger() {return this.logger;}
    public String getName() {return GameInfo.getGameInfo().getValue("name");}
    public File getFolder() {return new File(this.getName());}
    public Thread getMainThread() {return this.mainThread;}
    
    public abstract GameScene getStartingScene();//This method must return the scene to be attached when the game is ready to be actually started.
    
    public void start() {
        //Attach our default starting scene
        GameScene initial = this.getStartingScene();
        GameScene.setActiveScene(initial);
        
        //Game is now running.
    }
    
    /**
     * Installs the game files. This can be overriden but probably shouldn't.
     * @throws IOException Failed to install libs.
     */
    public void install() throws IOException {
        //First we need to see if the installation directory is available.
        File installDir = this.getFolder();
        if(!installDir.exists()) {
            installDir.mkdir();
        }
        
        //Directory made, let's start copying files
        File native_dir = new File(installDir, "natives");
        if (!native_dir.exists()) {
            native_dir.mkdir();
        }
        //Now do the libs
        File lib_dir = new File(installDir, "lib");
        if(!lib_dir.exists()) lib_dir.mkdir();

        String[] files = new String[]{
            "lwjgl.jar",
            "lwjgl-bgfx-natives-linux.jar",
            "lwjgl-bgfx-natives-macos.jar",
            "lwjgl-bgfx-natives-windows.jar",
            "lwjgl-bgfx.jar",
            "lwjgl-egl.jar",
            "lwjgl-glfw-natives-linux.jar",
            "lwjgl-glfw-natives-macos.jar",
            "lwjgl-glfw-natives-windows.jar",
            "lwjgl-glfw.jar",
            "lwjgl-jawt.jar",
            "lwjgl-jemalloc-natives-linux.jar",
            "lwjgl-jemalloc-natives-macos.jar",
            "lwjgl-jemalloc-natives-windows.jar",
            "lwjgl-jemalloc.jar",
            "lwjgl-lmdb-natives-linux.jar",
            "lwjgl-lmdb-natives-macos.jar",
            "lwjgl-lmdb-natives-windows.jar",
            "lwjgl-lmdb.jar",
            "lwjgl-nanovg-natives-linux.jar",
            "lwjgl-nanovg-natives-macos.jar",
            "lwjgl-nanovg-natives-windows.jar",
            "lwjgl-nanovg.jar",
            "lwjgl-natives-linux.jar",
            "lwjgl-natives-macos.jar",
            "lwjgl-natives-windows.jar",
            "lwjgl-nfd-natives-linux.jar",
            "lwjgl-nfd-natives-macos.jar",
            "lwjgl-nfd-natives-windows.jar",
            "lwjgl-nfd.jar",
            "lwjgl-nuklear-natives-linux.jar",
            "lwjgl-nuklear-natives-macos.jar",
            "lwjgl-nuklear-natives-windows.jar",
            "lwjgl-nuklear.jar",
            "lwjgl-openal-natives-linux.jar",
            "lwjgl-openal-natives-macos.jar",
            "lwjgl-openal-natives-windows.jar",
            "lwjgl-openal.jar",
            "lwjgl-opencl.jar",
            "lwjgl-opengl.jar",
            "lwjgl-opengles.jar",
            "lwjgl-ovr-natives-windows.jar",
            "lwjgl-ovr.jar",
            "lwjgl-par-natives-linux.jar",
            "lwjgl-par-natives-macos.jar",
            "lwjgl-par-natives-windows.jar",
            "lwjgl-par.jar",
            "lwjgl-sse-natives-linux.jar",
            "lwjgl-sse-natives-macos.jar",
            "lwjgl-sse-natives-windows.jar",
            "lwjgl-sse.jar",
            "lwjgl-stb-natives-linux.jar",
            "lwjgl-stb-natives-macos.jar",
            "lwjgl-stb-natives-windows.jar",
            "lwjgl-stb.jar",
            "lwjgl-tinyfd-natives-linux.jar",
            "lwjgl-tinyfd-natives-macos.jar",
            "lwjgl-tinyfd-natives-windows.jar",
            "lwjgl-tinyfd.jar",
            "lwjgl-vulkan.jar",
            "lwjgl-xxhash-natives-linux.jar",
            "lwjgl-xxhash-natives-macos.jar",
            "lwjgl-xxhash-natives-windows.jar",
            "lwjgl-xxhash.jar",
            "lwjgl.jar"
        };
        for (String s : files) {
            File f = new File(lib_dir, s);
            getLogger().log(Level.INFO, "Exporting {0} to {1}", new String[]{s, f.getAbsolutePath()});
            FileUtilities.saveResourceToFile("/lib/"+s, f);
        }
        
        //Hacky Load JAR Method
        for(String s : files) {
            ReflectionUtilities.loadJAR(new File(lib_dir, s));
        }
    }
    
    public void run() throws Exception {
        //We need to update our KeyManager as well.
        KeyManager.getInstance().update();

        //Do the render
        DisplayManager.getInstance().update(this);
        
        //Now do sounds
        SoundFactory.getFactory().update(this);
    }
}
