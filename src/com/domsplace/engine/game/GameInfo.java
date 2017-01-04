/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.game;

import com.domsplace.engine.utilities.FileUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public final class GameInfo {
    private static final GameInfo INFO = new GameInfo();
    public static GameInfo getGameInfo() {return INFO;}//Must be instanced early!!
    
    //Instance
    private Map<String, String> data;
    
    private GameInfo() {}
    
    public Map<String,String> getData() {return new HashMap<String,String>(data);}
    
    public String getValue(String key) {return this.data.get(key);}
    public Class getClassByKey(String key) throws ClassNotFoundException {return Class.forName(getValue(key));}
    
    public void setValue(String key, String value) {
        this.data.remove(key);
        this.data.put(key, value);
    }
    
    public boolean isValueSet(String key) {return this.data.containsKey(key);}
    
    /**
     * Returns the default values used in the GameInfo, overridden by the game.
     * @return A new Map<,> containing the default values.
     */
    public final Map<String,String> getDefaults() {
        Map<String,String> data = new HashMap<String,String>();
        
        data.put("fullscreen", "false");
        
        return data;
    }
    
    /**
     * Loads the GameInfo out of the gameinfo.txt resource.
     * @throws IOException If the file cannot load, or if the GameInfo is already loaded
     */
    public final void load() throws IOException {
        //Check for already loaded data.
        if(data instanceof Map) throw new IOException("Data already loaded.");
        
        //We need to access the resource and ensure it exists.
        InputStream stream = FileUtilities.getResource("gameinfo.txt");
        
        if(!(stream instanceof InputStream)) throw new IOException("gameinfo.txt is missing or unaccessible!");
        
        //Stream "should" be ok, let's start reading it.
        String file_data = FileUtilities.getInputStreamAsString(stream);//This can be done more efficiently.
        //Alternative would be to read char by char and look for \n and the seperator
        stream.close();//We're done with the stream now.
        
        this.data = FileUtilities.parseINI(file_data,this.getDefaults());
    }
}
