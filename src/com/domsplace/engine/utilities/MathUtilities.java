/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.utilities;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class MathUtilities {
    public static double clamp(double val, double min, double max) {
        return Math.max(Math.min(val, max), min);
    }
    
    public static float clamp(float val, float min, float max) {
        return Math.max(Math.min(val, max), min);
    }
}
