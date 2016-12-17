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

import com.domsplace.engine.disposable.IDisposable;
import static com.domsplace.engine.sound.Sound.checkALError;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_SEC_OFFSET;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class SoundPlayer implements IDisposable {

    public static final int STATE_NULL = 0;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_FINISHED_PLAYING = 5;

    public static float MASTER_VOLUME = 0.1f;

    private static final List<SoundPlayer> LOADED_PLAYERS = new ArrayList<SoundPlayer>();

    static List<SoundPlayer> getAllPlayers() {
        return new ArrayList<SoundPlayer>(LOADED_PLAYERS);
    }

    public static final void cleanup() throws Exception {
        for (SoundPlayer sp : getAllPlayers()) {
            sp.dispose();
        }
        LOADED_PLAYERS.clear();
    }

    //Instance
    private final Sound sound;
    private int source = -1;
    private boolean bound = false;
    private boolean please_dispose = false;
    private boolean disposed;

    private float volume = 1.0f;

    private int state = STATE_NULL;

    public SoundPlayer(final Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return this.sound;
    }

    public float getPosition() {
        return AL10.alGetSourcef(source, AL_SEC_OFFSET);
    }

    public int getState() {
        return this.state;
    }

    public boolean isUploaded() {
        return this.source != -1;
    }

    public boolean isBound() {
        return this.bound;
    }

    public boolean isPlaying() {
        return this.state == STATE_PLAYING;
    }
    
    @Override
    public boolean isDisposed() {return disposed;}

    public void setVolume(float vol) {
        this.volume = vol;
        this.updateVolume();
    }

    private void updateVolume() {
        if (!isUploaded() || !isBound()) {
            return;
        }
        float volume = this.volume;
        volume *= MASTER_VOLUME;
        alSourcef(this.source, AL_GAIN, volume);
    }

    private void genSources() throws Exception {
        source = alGenSources();
        checkALError();
        LOADED_PLAYERS.add(this);
    }

    private void bindInput() throws Exception {
        if (isUploaded()) {
            return;
        }
        if (isBound()) {
            return;
        }
        this.genSources();
        alSourcei(source, AL_BUFFER, this.getSound().getBuffer());
        checkALError();
        this.updateVolume();
        checkALError();
        this.bound = true;
    }

    @Override
    public void dispose() {
        LOADED_PLAYERS.remove(this);

        alDeleteSources(source);
        try {checkALError();}catch(Exception e) {SoundFactory.getFactory().getLogger().log(Level.SEVERE, "Failed to cleanup sound!", e);}
        source = -1;

        this.state = STATE_NULL;
    }

    public void play() throws Exception {
        if (this.isPlaying()) {
            return;
        }
        this.bindInput();
        alSourcePlay(source);
        this.updateVolume();
        checkALError();
        this.state = STATE_PLAYING;
    }

    private int last_state;
    private float last_pos = 0f;

    public void tick() throws Exception {
        float pos = this.getPosition();
        float duration = this.getSound().getDuration();
        int state = this.getState();

        if (this.isPlaying()) {
            if (last_pos > 0 && pos == 0) {
                this.finish();
                this.state = STATE_FINISHED_PLAYING;
            }
        }

        last_pos = pos;
        last_state = state;
    }

    public void playThenDispose() throws Exception {
        //Queues this sound to play, then self disposes. (non thread locking)
        this.please_dispose = true;
        this.play();
    }

    public void finish() throws Exception {
        if (this.please_dispose) {
            this.dispose();
        }
    }
}
