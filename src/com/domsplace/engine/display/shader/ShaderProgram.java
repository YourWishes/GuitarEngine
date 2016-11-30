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
package com.domsplace.engine.display.shader;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.utilities.FileUtilities;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class ShaderProgram {
    public static void unbindProgram() {
        glUseProgram(0);
    }
    
    //Predefined Shaders
    private static ShaderProgram defaultShader = null;
    public static ShaderProgram getDefaultShader() {
        if(defaultShader != null) return defaultShader;
        Shader frag = new Shader();
        Shader vert = new Shader();
        defaultShader = new ShaderProgram();
        try {
            vert.compile(FileUtilities.getResourceAsString("resource/shader/default.vert"), GL20.GL_VERTEX_SHADER);
            frag.compile(FileUtilities.getResourceAsString("resource/shader/default.frag"), GL20.GL_FRAGMENT_SHADER);
            defaultShader.addShader(frag).addShader(vert);
            defaultShader.compile();
        } catch(Exception e) {
            DisplayManager.getInstance().getLogger().log(Level.SEVERE, "Failed to load default shader", e);
        }
        return defaultShader;
    }
    
    //Instance
    private int program = -1;
    private List<Shader> shaders = new ArrayList<Shader>();
    private Map<String, Integer> variables;
    
    public ShaderProgram() {}
    
    public int getProgramID() {return this.program;}
    public ShaderProgram addShader(Shader shader) {this.shaders.add(shader); return this;}
    
    public void compile() throws Exception {        
        this.program = glCreateProgram();
        if(this.program == 0) {
            this.program = -1;
            throw new Exception("Failed to create ShaderProgram (Failed to Gen ID)");
        }
        variables = new HashMap<String, Integer>();
        
        for(Shader shader : this.shaders) {
            glAttachShader(this.program, shader.getID());
        }
        glLinkProgram(this.program);
        glValidateProgram(this.program);
    }
    
    public void bind() {
        glUseProgram(this.program);
    }
    
    public int getVariableID(String variable) {
        if(variables.containsKey(variable)) return variables.get(variable);
        int id = glGetUniformLocation(this.program, variable);
        variables.put(variable, id);
        return id;
    }
    
    public void setVariable(String variable, float[] data) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
        fb.put(data);
        fb.flip();
        setVariable(variable, fb);
    }
    
    public void setVariable(String variable, int data) {
        setVariableByID(getVariableID(variable), data);
    }
    
    public void setVariable(String variable, float data) {
        setVariableByID(getVariableID(variable), data);
    }
    
    public void setVariable(String variable, boolean data) {
        setVariableByID(getVariableID(variable), data ? 1 : 0);
    }
    
    public void setVariable(String variable, FloatBuffer stuff) {
        setVariableByID(getVariableID(variable), stuff);
    }
    
    public void setVariableByID(int id, FloatBuffer stuff) {
        int size = stuff.capacity();
        if(size == 1) {
            GL20.glUniform1fv(id, stuff);
        } else if(size == 2) {
            GL20.glUniform2fv(id, stuff);
        } else if(size == 3) {
            GL20.glUniform3fv(id, stuff);
        } else if(size == 4) {
            GL20.glUniform4fv(id, stuff);
        }
    }
    
    public void setVariableByID(int id, int stuff) {
        GL20.glUniform1i(id, stuff);
    }
    
    public void setVariableByID(int id, float stuff) {
        GL20.glUniform1f(id, stuff);
    }
}
