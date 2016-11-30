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
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Weird, GLFW has all these cool classes, but no window class?! Oh well.
 * 
 * @author Dominic Masters <dominic@domsplace.com>
 */
public final class GLFWWindow extends GLFWFramebufferSizeCallback {
    private int width;
    private int height;
    private final long window_handle;
    
    public GLFWWindow(int width, int height, String title) throws Exception {
        this(width,height,title,false);
    }
    
    public GLFWWindow(int width, int height, String title, boolean fullscreen) throws Exception {
        this(width,height,title,fullscreen,glfwGetPrimaryMonitor());
    }
    
    public GLFWWindow(int width, int height, String title, boolean fullscreen, long monitor) throws Exception {
        this.width = width;
        this.height = height;
        
        glfwDefaultWindowHints();                   // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);   // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);  // the window will be resizable
        window_handle = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
        if (window_handle == NULL) {
            throw new Exception("Failed to create the GLFW window");
        }
        glfwSetFramebufferSizeCallback(window_handle, this);
    }
    
    public long getWindowHandle() {return window_handle;}
    public long getMonitor() {return glfwGetWindowMonitor(this.window_handle);}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    
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
    
    public void dispose() {
        glfwDestroyWindow(window_handle);
    }
    
    @Override
    public void invoke(long window, int width, int height) {
        onResize(window, width, height);
    }
    
    public void onResize(long window, int width, int height) {
        this.width = width;
        this.height = height;
    }
}
