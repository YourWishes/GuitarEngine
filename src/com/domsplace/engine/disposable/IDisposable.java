/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.disposable;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public interface IDisposable {
    boolean isDisposed();
    
    void dispose();
}
