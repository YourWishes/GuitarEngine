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
import java.util.logging.Level;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Shader {

    private int id = -1;

    public Shader() {
    }

    public int getID() {
        return this.id;
    }

    public boolean getCompiled() {
        return this.id != -1;
    }

    public void compile(String code, int shaderType) throws Exception {
        this.id = glCreateShader(shaderType);
        if (this.id == 0) {
            this.id = -1;
            throw new Exception("Failed to create shader. (ID Gen Failed)");
        }
        glShaderSource(this.id, code);
        
        int len = glGetShaderi(this.id, GL_INFO_LOG_LENGTH);
        String x = glGetShaderInfoLog(this.id, len);
        
        if(x.length() > 0) {
            DisplayManager.getInstance().getLogger().log(Level.SEVERE, x);
        }

        glCompileShader(this.id);
        if (glGetShaderi(this.id, GL_COMPILE_STATUS) == GL_FALSE) {
            this.dispose();
            throw new Exception("Failed to Compile Shader \"" + x + "\"");
        }
    }

    public void activate() {

    }

    public void dispose() {
        if (this.id == -1) {
            return;
        }
        glDeleteShader(this.id);
        this.id = -1;
    }
}
