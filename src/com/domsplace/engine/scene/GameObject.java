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
package com.domsplace.engine.scene;

import com.domsplace.engine.display.shader.ShaderProgram;
import com.domsplace.engine.display.texture.GameTexture;
import com.domsplace.engine.display.texture.Texture;
import com.domsplace.engine.utilities.ColorUtilities;
import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class GameObject {

    public static final double TRIANGLE_WIDTH = 1.0;
    public static final double TRIANGLE_HEIGHT = 1.0;

    //Instance
    private final GameScene scene;

    public double x;
    public double y;
    public double width = TRIANGLE_WIDTH;
    public double height = TRIANGLE_HEIGHT;

    public int zindex = -1;
    public Color color = Color.BLACK;
    public float alpha = 1.0f;
    public Texture texture;

    public double s = 0;
    public double t = 0;
    public double ss = 1;
    public double ts = 1;

    public boolean flipX = false;
    public boolean flipY = false;

    public GameObject(final GameScene scene) {
        this.scene = scene;
    }

    public GameScene getScene() {
        return this.scene;
    }

    public final Texture getTexture() {
        return this.texture;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public void resizeToTexture() {
        if(getTexture() instanceof GameTexture) {
            GameTexture gt = (GameTexture)getTexture();
            this.width = gt.getAdjustedWidth();
            this.height = gt.getAdjustedHeight();
        } else {
            this.width = this.getTexture().getWidth();
            this.height = this.getTexture().getHeight();
        }
    }

    public void adjustTextureCoordinatesTo(double col, double row) {
        if(!(getTexture() instanceof GameTexture)) return;
        GameTexture gt = (GameTexture) getTexture();
        double[] coords = gt.getCoordinates(col, row);
        this.s = coords[0];
        this.t = coords[1];
        this.ss = coords[2];
        this.ts = coords[3];
    }

    //Changes the TextureCoordinates to repeat properly based on the object dimensions
    public void adjustTextureCoordinatesToDimensions() {
        //e.g. if you have a texture that's 16x16 but the object is 32x32, then repeat it 2x2 times
        this.ss = (double) this.width / (double) this.getTexture().getWidth();
        this.ts = (double) this.height / (double) this.getTexture().getHeight();
    }

    public void render() {
        glPushMatrix();
        //glTranslated(Math.round(x), Math.round(y), 0);
        glTranslated(x, y, 0);

        ShaderProgram.getDefaultShader().bind();
        if (this.texture instanceof Texture) {
            this.texture.bind();
            ShaderProgram.getDefaultShader().setVariable("isTextured", true);
        } else {
            ShaderProgram.getDefaultShader().setVariable("isTextured", false);
        }

        float[] colors = ColorUtilities.getColorAdjust(this.color);
        glColor4f(colors[0], colors[1], colors[2], alpha);

        this.renderMesh();

        glPopMatrix();
    }

    public void renderMesh() {
        double s = this.s;
        double ss = this.ss;
        double t = this.t;
        double ts = this.ts;
        if (flipX) {
            s = this.ss;
            ss = this.s;
        }
        if (flipY) {
            t = this.t;
            ts = this.ts;
        }

        //First Triangle
        glBegin(GL_TRIANGLES);
        glTexCoord2d(s, t);
        glVertex2d(0, 0);

        glTexCoord2d(ss, t);
        glVertex2d(width, 0);

        glTexCoord2d(ss, ts);
        glVertex2d(width, height);

        glEnd();

        //Second Triangle
        glBegin(GL_TRIANGLES);
        glTexCoord2d(s, t);
        glVertex2d(0, 0);

        glTexCoord2d(ss, ts);
        glVertex2d(width, height);

        glTexCoord2d(s, ts);
        glVertex2d(0, height);
        glEnd();

    }

    public void onSceneChange(GameScene newsc) {
    }

    public void tick() {

    }
}
