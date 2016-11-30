/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.display;

import com.domsplace.engine.display.shader.ShaderProgram;
import com.domsplace.engine.display.texture.Texture;
import com.domsplace.engine.game.Game;
import com.domsplace.engine.input.KeyManager;
import com.domsplace.engine.scene.GameScene;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * DisplayManager handles the main graphics processing, and is what loops the 
 * main thread. DisplayManager handles a lot of control over to the Scene but is
 * mostly controlling everything.
 * 
 * @author domfi
 */
public final class DisplayManager {
    //Constants
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 800/16*9;
    
    //Static Methods
    public static DisplayManager INSTANCE;
    public static DisplayManager getInstance() {
        if(INSTANCE instanceof DisplayManager) return INSTANCE;
        return INSTANCE = new DisplayManager();
    }
    
    //Instance
    private Logger logger;
    private GLFWErrorCallback errorCallback;
    private GLFWWindow window;
    
    private DisplayManager() {this.logger = Logger.getLogger(this.getClass().getName());}
    
    public GLFWWindow getWindow() {return this.window;}
    public Logger getLogger() {return this.logger;}
    
    public void setup(Game game) throws Exception {
        if(!Thread.currentThread().equals(game.getMainThread())) throw new Exception("This is not the main thread, cannot setup.");
        if(errorCallback instanceof GLFWErrorCallback) throw new Exception("LWJGL already setup/failed.");
        
        //Attaches the logger to the game logger.
        this.logger.setParent(game.getLogger());
        
        //First let's check to see if LWJGL was installed
        Class lwjglVersion = Class.forName("org.lwjgl.Version");
        if(!(lwjglVersion instanceof Class)) {
            throw new Exception("Failed to find LWJGL.");
        }
        
        //LWJGL (should) be available.
        logger.log(Level.INFO, "Found LWJGL {0}.", org.lwjgl.Version.getVersion());
        
        //Create our error handler
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.out));
        
        //Init glfw
        if(!glfwInit()) {
            throw new Exception("Unable to initialize GLFW");
        }
        
        // Create the game window
        this.window = new GLFWWindow(WINDOW_WIDTH, WINDOW_HEIGHT, game.getName());
        this.window.setContextCurrent();
        
        //Setup our GL stuff
        GL.createCapabilities();
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glActiveTexture(GL_TEXTURE0);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        
        //We need to load our default Shader here.
        ShaderProgram.getDefaultShader();//Basically loads it.
    }
    
    public void start(Game game) throws Exception {
        if(!Thread.currentThread().equals(game.getMainThread())) throw new Exception("This is not the main thread, cannot start.");
        
        //DisplayManager was requested to start.
        window.show();
        window.setPosition(64, 64);
        
        //Can't remember what this does, but it's important!... probably
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//Set our default clear color.
        
        while(!window.isCloseRequested()) {
            try {
                //Do the render
                this.update(game);
                //We need to update our KeyManager as well.
                KeyManager.getInstance().update();
            } catch(Exception e) {
                logger.log(Level.SEVERE, "Failed to render!", e);
            }
        }
        logger.log(Level.INFO, "Window was requested to close.");
    }
    
    public void update(Game game) throws Exception {
        //Clear the bufferz
        glViewport(0, 0, window.getWidth(), window.getHeight());//Changes our viewport to be the size of the window.
        glClear(GL_COLOR_BUFFER_BIT);
        
        //Upload all pending textures.
        for(Texture t : Texture.getTexturesToUpload()) {
            if(t == null) continue;
            t.uploadMainThread();
        }
        
        //Now change our games rendering space
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, window.getWidth(), window.getHeight(), 0, -1.0, 10.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        
        //Rendering time baby
        GameScene.getActiveScene().render();
        
        //Unbind our texture (A bit of cleanup)
        Texture.unbind();
        
        //Swap and shuffle
        this.window.swapBuffers();
        glfwPollEvents();
    }
    
    public void dispose(Game game) throws Exception {
        if(!Thread.currentThread().equals(game.getMainThread())) throw new Exception("This is not the main thread, cannot stop.");
        
        if(this.window instanceof GLFWWindow) this.window.dispose();
        
        GLFW.glfwSetErrorCallback(null);
        errorCallback = null;
        
        glfwTerminate();
    }
}
