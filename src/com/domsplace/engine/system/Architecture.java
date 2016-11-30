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
package com.domsplace.engine.system;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public final class Architecture {
    public static Architecture X_86 = new Architecture(0, "x86", "32-Bit");
    public static Architecture X_64 = new Architecture(0, "x64", "64-Bit");
    
    public static Architecture getCurrent() {
        String arch = System.getProperty("os.arch");
        if(arch.contains("32") || arch.contains("86")) return Architecture.X_86;
        if(arch.contains("64")) return Architecture.X_64;
        return null;
    }
    
    //Instance
    private int id;
    private String name;
    private String nameLong;
    
    private Architecture(int id, String name, String nameLong) {
        this.id = id;
        this.name = name;
        this.nameLong = nameLong;
    }
    
    public int getID() {return this.id;}
    public String getName() {return this.name;}
    public String getLongName() {return this.nameLong;}
}
