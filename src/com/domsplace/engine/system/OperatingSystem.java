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
public final class OperatingSystem {
    public static final OperatingSystem WINDOWS = new OperatingSystem(0, "Windows");
    public static final OperatingSystem MAC_OS_X = new OperatingSystem(1, "Mac OSX");
    public static final OperatingSystem LINUX = new OperatingSystem(2, "Linux");
    
    public static OperatingSystem getCurrent() {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("windows")) return OperatingSystem.WINDOWS;
        if(os.contains("mac")) return OperatingSystem.MAC_OS_X;
        if(os.contains("linux")) return OperatingSystem.LINUX;
        return null;
    }
    
    //Instance
    private int id;
    private String name;
    
    private OperatingSystem(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getID() {return this.id;}
    public String getName() {return this.name;}
}
