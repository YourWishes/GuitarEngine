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
import com.domsplace.engine.game.GameInfo;
import com.domsplace.engine.input.KeyManager;
import com.domsplace.engine.scene.GameScene;
import com.domsplace.engine.utilities.ColorUtilities;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import org.lwjgl.opengl.GL14;

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
    private Thread knownMainThread;//Not guaranteed to be set but eh reliable enough.
    //If this isn't set then the game is probably too bugged to run so a crash is better tbh.
    
    private int width;
    private int height;
    
    private int sceneBufferFBID;//FrameBuffer ID
    private int sceneBufferCRID;//ColorBuffer ID
    private int sceneBufferDPID;//DepthBuffer ID
    
    private DisplayManager() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public GLFWWindow getWindow() {return this.window;}
    public Logger getLogger() {return this.logger;}
    public Thread getKnownMainThread() {return this.knownMainThread;}
    
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
    
    public void setup(Game game) throws Exception {
        if(!Thread.currentThread().equals(game.getMainThread())) throw new Exception("This is not the main thread, cannot setup.");
        if(errorCallback instanceof GLFWErrorCallback) throw new Exception("LWJGL already setup/failed.");
        
        this.knownMainThread = game.getMainThread();
        
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
        
        //Setup internal frame buffer
        try {
            width = Integer.parseInt(GameInfo.getGameInfo().getValue("width"));
            height = Integer.parseInt(GameInfo.getGameInfo().getValue("height"));
        } catch(Exception e) {
            //Cannot get internal buffer value?
            width = WINDOW_WIDTH;
            height = WINDOW_HEIGHT;
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
        ShaderProgram.getDefaultShader().bind();//Basically loads it.
        
        //Now we need the buffer that we're going to render the scene to.
        this.sceneBufferCRID = glGenTextures();
        this.sceneBufferDPID = glGenRenderbuffersEXT();
        this.sceneBufferFBID = glGenFramebuffersEXT();
        
        //Initialize the buffers
        float f = glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);//Texture filtering
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.sceneBufferFBID);
        glBindTexture(GL_TEXTURE_2D, this.sceneBufferCRID);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        if(f > 0) glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, f);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width,height, 0,GL_RGB, GL_INT, (ByteBuffer)null);//Use RGBA if you want THIS TEXTURE to have alpha channels stored.
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, sceneBufferCRID, 0);
        glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, sceneBufferDPID);
        glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, sceneBufferDPID);

        //Reset to main frame buffer
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }
    
    public void start(Game game) throws Exception {
        if(!Thread.currentThread().equals(game.getMainThread())) throw new Exception("This is not the main thread, cannot start.");
        
        //DisplayManager was requested to start.
        window.show();
        window.setPosition(64, 64);
        
        //Vsync?
        boolean vsync = false;
        if(GameInfo.getGameInfo().isValueSet("vsync")) vsync = GameInfo.getGameInfo().getValue("vsync").equalsIgnoreCase("true");
        if(vsync) glfwSwapInterval(1);
        
        //Can't remember what this does, but it's important!... probably
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//Set our default clear color.
        
        while(!window.isCloseRequested()) {
            try {
                //We need to update our KeyManager as well.
                KeyManager.getInstance().update();
                
                //Do the render
                this.update(game);
            } catch(Exception e) {
                logger.log(Level.SEVERE, "Failed to render!", e);
            }
        }
        if(GameScene.getActiveScene() instanceof GameScene) GameScene.getActiveScene().dispose();
        logger.log(Level.INFO, "Window was requested to close.");
    }
    
    public void update(Game game) throws Exception {
        this.window.update();
        GameScene scene = GameScene.getActiveScene();
        Color c = Color.white;
        if(scene instanceof GameScene) {
            c = scene.getBackgroundColor();
        }
        
        //Upload all pending textures.
        for(Texture t : Texture.getTexturesToUpload()) {
            if(t == null) continue;
            t.upload();
        }
        Texture.unbind();
        
        //Bind the Scene's FBO
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.sceneBufferFBID);
        
        //Now render the scene to the FBO
        glPushMatrix();
        glPushAttrib(GL_VIEWPORT_BIT);
        this.setupMatrices(width, height, c);
        if(scene instanceof GameScene) {
            GameScene.getActiveScene().render();
        }
        glPopAttrib();
        glPopMatrix();
        
        //Unbind our texture (A bit of cleanup)
        Texture.unbind();
        //Dunno why, the ShaderProgram was causing some issues
        ShaderProgram.unbindProgram();
        
        //Bind the FBO
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        glBindTexture(GL_TEXTURE_2D, this.sceneBufferCRID);
        
        //Now render the scenes fbo to a quad
        glPushMatrix();
        glPushAttrib(GL_VIEWPORT_BIT);
        this.setupMatrices(window.getWidth(), window.getHeight(), Color.BLACK);
        
        //Fun Part, Here we get to resize our quad
        float ww = (float)window.getWidth();
        float wh = (float)window.getHeight();
        float qw = 0;
        float qh = 0;
        float qx = 0;
        float qy = 0;
        float ratio = ((float)WINDOW_WIDTH)/((float)WINDOW_HEIGHT);
        if((ww/wh) > ((float)width/(float)height)) {
            qw = wh*ratio;
            qh = wh;
            qx = (ww/2) - (qw/2);
            qy = 0;
        } else {
            qw = ww;
            qh = ww/ratio;
            qx = 0;
            qy = (wh/2)-(qh/2);
        }
        simpleQuad(qx,qy,(int)qw,(int)qh,Color.white);
        
        glPopAttrib();
        glPopMatrix();
        
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
    
    public void setupMatrices(int width, int height, Color color) {
        float[] colors = ColorUtilities.getColorAdjust(color);
        glViewport(0, 0, width,height);//Changes our viewport to be the size of the window.
        glClearColor(colors[0],colors[1],colors[2],1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        //Now change our games rendering space
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0,width, height, 0, -1.0, 10.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }
    
    public void simpleQuad(float x, float y, int width, int height, Color color){
        float[] colors = ColorUtilities.getColorAdjust(color);
        glColor4f(colors[0],colors[1],colors[2],1);
        
        float s = 0;
        float t = 0;
        float ss = 1;
        float ts = 1;
        
        glBegin(GL_TRIANGLES);
        glTexCoord2d(s, ts);
        glVertex2d(x, y);

        glTexCoord2d(ss, ts);
        glVertex2d(x+width, y);

        glTexCoord2d(ss, t);
        glVertex2d(x+width, y+height);
        glEnd();

        //Second Triangle
        glBegin(GL_TRIANGLES);
        glTexCoord2d(s, ts);
        glVertex2d(x, y);

        glTexCoord2d(ss, t);
        glVertex2d(x+width, y+height);

        glTexCoord2d(s, t);
        glVertex2d(x, y+height);
        glEnd();
    }
}
