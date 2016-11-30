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
package com.domsplace.engine.display.texture;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.utilities.FileUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GameTexture extends Texture {
    //Constants
    private static final List<GameTexture> RESOURCE_LOADED_TEXTURES = new ArrayList<GameTexture>();
    public static final List<GameTexture> getResourceLoadedTextures() {return new ArrayList<GameTexture>(RESOURCE_LOADED_TEXTURES);}
    
    public static final GameTexture getResourceTexture(String resource, double cols, double rows) throws Exception {
        for(GameTexture t : RESOURCE_LOADED_TEXTURES) {
            if(resource.equals(t.getResourceName())) {
                t.columns = cols;
                t.rows = rows;
                return t;
            }
        }
        return new GameTexture(resource, cols, rows);
    }
    
    public static final GameTexture getDefaultTexture() {
        try {
            GameTexture t = getResourceTexture("resource/hud/no_texture.png", 1, 1);
            //t.smooth = false;
            t.loadLater();
            return t;
        } catch(Exception e) {
            DisplayManager.getInstance().getLogger().log(Level.SEVERE, "Failed to load default texture!", e);
            return null;
        }
    }
    
    //Instance
    private String resource_name;
    public double columns;
    public double rows;
    public int spacing = 0;
    
    public GameTexture(String resource_name, double cols, double rows) throws IOException {
        this(FileUtilities.getResource(resource_name), cols, rows);
        this.resource_name = resource_name;
        
        RESOURCE_LOADED_TEXTURES.add(this);
    }
    
    public GameTexture(InputStream is, double cols, double rows) {
        super(is);
        this.columns = cols;
        this.rows = rows;
    }
    
    public String getResourceName() {return this.resource_name;}
    public double getColumns() {return this.columns;}
    public double getRows() {return this.rows;}
    
    public double getSizeX() {
        return this.getTexturePixelWidth()*this.getAdjustedWidth();
    }
    
    public double getSizeY() {
        return this.getTexturePixelHeight()*this.getAdjustedHeight();
    }
    
    public double getSpacingSizeX() {
        return getTexturePixelWidth() * this.spacing;
    }
    
    public double getSpacingSizeY() {
        return this.getTexturePixelHeight() * this.spacing;
    }
    
    public double getAdjustedWidth() {
        double x = this.getAdjustedWidthWithoutSpacing();
        x -= this.spacing;
        return x;
    }
    public double getAdjustedHeight() {
        double y = this.getAdjustedHeightWithoutSpacing();
        y -= this.spacing;
        return y;
    }
    
    private double getTexturePixelWidth() {
        return 1.0/this.getWidth();
    }
    
    private double getTexturePixelHeight() {
        return 1.0/this.getHeight();
    }
    
    public double getAdjustedWidthWithoutSpacing() {
        double x = (double)this.getWidth()-this.spacing;
        x /= (double)this.getColumns();
        return x;
    }
    public double getAdjustedHeightWithoutSpacing() {
        double y = (double)this.getHeight()-this.spacing;
        y /= (double)this.getRows();
        return y;
    }
    
    public double[] getCoordinates(double col, double row) {
        double s = (double)col * getSizeX();
        s += this.getSpacingSizeX()*(col+1);
        double t = (double)row * getSizeY();
        t += this.getSpacingSizeY()*(row+1);
        
        double ss = s + getSizeX();
        double ts = t + getSizeY();
        return new double[]{
            s, t, ss, ts
        };
    }
    
    public double[] getPositionsFromFrame(int frame) {
        double col = frame  % 10;
        double row = (frame - col) / 10;
        return new double[]{
            col, row
        };
    }
}
