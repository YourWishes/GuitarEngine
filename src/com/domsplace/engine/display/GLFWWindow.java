/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.display;

import static com.domsplace.engine.display.DisplayManager.WINDOW_HEIGHT;
import static com.domsplace.engine.display.DisplayManager.WINDOW_WIDTH;
import com.domsplace.engine.input.KeyManager;
import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Weird, GLFW has all these cool classes, but no window class?! Oh well.
 * 
 * @author Dominic Masters <dominic@domsplace.com>
 */
public final class GLFWWindow {
    private int width;
    private int height;
    private final long window_handle;
    
    private double mx;
    private double my;
    
    //GLFW Callbacks
    private final GLFWErrorCallback errorCallback;
    private final GLFWKeyCallback keyCallback;
    private final GLFWFramebufferSizeCallback framebufferSizeCallback;
    
    public GLFWWindow(int width, int height, String title) throws Exception {
        this(width,height,title,false);
    }
    
    public GLFWWindow(int width, int height, String title, boolean fullscreen) throws Exception {
        this(width,height,title,fullscreen,glfwGetPrimaryMonitor());
    }
    
    public GLFWWindow(int width, int height, String title, boolean fullscreen, long monitor) throws Exception {
        this.width = width;
        this.height = height;
        
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        
        glfwDefaultWindowHints();                   // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);   // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);  // the window will be resizable
        window_handle = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
        if (window_handle == NULL) {
            throw new Exception("Failed to create the GLFW window");
        }
        
        //Setup Events
        glfwSetKeyCallback(window_handle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                onKey(window,key,scancode,action,mods);
            }
        });
        
        glfwSetFramebufferSizeCallback(window_handle, (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                onResize(window, width, height);
            }
        }));
    }
    
    public long getWindowHandle() {return window_handle;}
    public long getMonitor() {return glfwGetWindowMonitor(this.window_handle);}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public double getMouseX() {return this.mx;}
    public double getMouseY() {return this.my;}
    
    public void setPosition(int x, int y) {glfwSetWindowPos(this.window_handle,x,y);}
    public void setContextCurrent() {glfwMakeContextCurrent(window_handle);}//Basically "RENDER TO ME!"
    
    public boolean isCloseRequested() {return glfwWindowShouldClose(window_handle);}
    
    public void center() {center(glfwGetPrimaryMonitor());}
    public void center(long monitor) {
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);
        this.setPosition((vidmode.width() - WINDOW_WIDTH) / 2,(vidmode.height() - WINDOW_HEIGHT) / 2);
    }
    
    public void swapBuffers() {
        glfwSwapBuffers(window_handle);
    }
    
    public void show() {
        glfwShowWindow(window_handle);
    }
    
    //Handle Update stuff (mostly event listeners)
    public void update() {
        //Mouse State
        for(int i = 0; i < 8; i++) {
            //Refer to GLFW.GLFW_MOUSE_BUTTON_i (Where i is 1 > 8)
            int result = glfwGetMouseButton(window_handle, i);
            KeyManager.getInstance().handleMouse(i,result);
        }
        
        
        //Mouse Position
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window_handle, x, y);
        x.rewind();
        y.rewind();

        mx = x.get();
        my = y.get();
    }
    
    public void dispose() {
        glfwDestroyWindow(window_handle);
    }
    
    //Events
    public void onResize(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void onKey(long window, int key, int scancode, int action, int mods) {
        KeyManager.getInstance().handleKey(key, action);
    }
}
