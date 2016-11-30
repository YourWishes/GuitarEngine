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
        
        //Now we need to iterate over our string, line by line
        String[] lines = file_data.split("\n");
        Map<String,String> data = this.getDefaults();//Stores our temporary values.
        for(int i = 0; i < lines.length; i++) {
            //Check empty string
            String line = lines[i];
            if(line.length() == 0 || line.replaceAll(" ", "").length() == 0) continue;
            //Line should be ok, ensure there is a "Value" and a "Property", the first = is the seperator
            String[] pairs = line.split("=");
            //Make sure there is a at least a key and a value
            if(pairs.length < 2) throw new IOException("Line " + (i+1) + " doesn't have a correct value/key pair!");
            //Should be ok, the length MAY be greater than 2 but unlikely.
            String key = pairs[0];
            String value = "";
            for(int j = 1; j < pairs.length; j++) {
                value += pairs[j];//
                if(j < (pairs.length-1)) value += "=";//Just reattaches = seperated values
            }
            //Now we can push our data.
            data.remove(key);//Remove previous entries... sorta needed for defaults.
            data.put(key, value);//Add our entry.
        }
        
        //Now our data is valid, we can store this.
        this.data = data;
    }
}
