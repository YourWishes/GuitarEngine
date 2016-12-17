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
package com.domsplace.engine.utilities;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class TimeUtilities {
    public static long millisecondsToNanoseconds(long x) {
        return x * 1000000;
    }

    public static double nanosecondsToMilliseconds(long x) {
        return (double)x / 1000000d;
    }
    
    public static void sleepThread(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //sleepThreadNS(millisecondsToNanoseconds(milliseconds));
    }
}
