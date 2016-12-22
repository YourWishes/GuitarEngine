/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.utilities;

import java.security.SecureRandom;
import java.util.Random;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class MathUtilities {
    private final static SecureRandom RANDOM = new SecureRandom();
    
    public static double clamp(double val, double min, double max) {
        return Math.max(Math.min(val, max), min);
    }
    
    public static float clamp(float val, float min, float max) {
        return Math.max(Math.min(val, max), min);
    }
    
    //This random is INSECURE! only use for insecure means
    public static double RandomDouble(double rangeMin, double rangeMax) {
        return rangeMin + (rangeMax - rangeMin) * RANDOM.nextDouble();
    }
    
    public static long RandomLong(long rangeMin, long rangeMax) {
        return (long)RandomDouble(rangeMin,rangeMax);
    }
    
    public static int RandomInt(int rangeMin, int rangeMax) {
        return rangeMin + (rangeMax - rangeMin) * RANDOM.nextInt();
    }
    
    public static boolean RandomBoolean() {
        return RANDOM.nextBoolean();
    }
    
    public static boolean RandomBoolean(int odds) {
        return RandomInt(0, odds) == 0;
    }
}
