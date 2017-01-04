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
package com.domsplace.engine.sound;

import com.domsplace.engine.game.Game;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC10.alcProcessContext;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public final class SoundFactory {
    private static final SoundFactory INSTANCE = new SoundFactory();

    public static SoundFactory getFactory() {
        return INSTANCE;
    }
    
    //Instance
    private final Logger logger;
    private long device = -1;
    private long context;
    private Game game;
    
    private SoundFactory() {
        this.logger = Logger.getLogger(SoundFactory.class.getName());
    }
    
    public Logger getLogger() {return logger;}
    
    public boolean isSetup() {
        return this.device != -1 && this.game instanceof Game;
    }
    
    public void setup(Game game) {
        if(this.isSetup()) {
            return;
        }
        this.game = game;
        this.logger.setParent(game.getLogger());

        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);

        context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(context);
        alcProcessContext(context);
        AL.createCapabilities(deviceCaps);
    }
    
    private void destroyAL() {
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
    
    public void stop() throws Exception {
        SoundPlayer.cleanup();
        this.destroyAL();
    }
    
    public void update(Game game) throws Exception {
        List<SoundPlayer> players = SoundPlayer.getAllPlayers();
        for(SoundPlayer player : players) {
            if(player == null) continue;
            try {
                player.tick();
            } catch(Exception e) {
                logger.log(Level.SEVERE, "SoundPlayer failed to tick!", e);
            }
        }
    }
}
