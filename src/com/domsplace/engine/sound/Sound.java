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

import static com.domsplace.engine.utilities.FileUtilities.ioResourceToByteBuffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import org.lwjgl.stb.STBVorbisInfo;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Sound {
    private static final List<Sound> LOADED_SOUNDS = new ArrayList<Sound>();
    
    public static final void cleanup() throws Exception {
        for(Sound sound : new ArrayList<Sound>(LOADED_SOUNDS)) {
            sound.unload();
        }
    }
    
    public static Sound getSound(String res) throws Exception {
        for(Sound s : new ArrayList<Sound>(LOADED_SOUNDS)) {
            if(s.res.equalsIgnoreCase(res)) return s;
        }
        return new Sound().load(res);
    }

    static void checkALError() throws Exception {
        int error;
        if ((error = alGetError()) != AL_NO_ERROR) {
            throw new Exception("OpenAL Error: " + error);
        }
    }

    //Instance
    private int buffer = -1;
    private String res;
    private float duration_cache = -1;

    public Sound() {
    }

    public int getBuffer() {return this.buffer;}
    
    public float getDuration() {
        if(duration_cache != -1) return duration_cache;
        int size = alGetBufferi(buffer, AL_SIZE);
        int bits = alGetBufferi(buffer, AL_BITS);
        int channels = alGetBufferi(buffer, AL_CHANNELS);
        int freq = alGetBufferi(buffer, AL_FREQUENCY);
        if(alGetError() != AL_NO_ERROR)
            return -1.0f;

        return duration_cache = (float)(size/channels/(bits/8)) / (float)freq;
    }

    public boolean isLoaded() {
        return buffer != -1;
    }

    public Sound load(String res) throws Exception {
        if (isLoaded()) {
            return this;
        }
        this.res = res;

        STBVorbisInfo info = STBVorbisInfo.malloc();

        ByteBuffer vorbis;
        vorbis = ioResourceToByteBuffer(res, 32 * 1024);

        IntBuffer error = BufferUtils.createIntBuffer(1);
        long decoder = stb_vorbis_open_memory(vorbis, error, null);
        if (decoder == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }

        stb_vorbis_get_info(decoder, info);

        int channels = info.channels();
        int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
        
        ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);
        pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
        stb_vorbis_close(decoder);

        buffer = alGenBuffers();
        checkALError();

        //copy to buffer
        int format;
        if (channels == 1) {
            format = AL10.AL_FORMAT_MONO16;
        } else {
            format = AL10.AL_FORMAT_STEREO16;
        }
        alBufferData(buffer, format, pcm, info.sample_rate());

        checkALError();
        info.free();
        LOADED_SOUNDS.add(this);
        return this;
    }

    public SoundPlayer genPlayer() {
        SoundPlayer ps = new SoundPlayer(this);
        return ps;
    }

    public void unload() throws Exception {
        if (!isLoaded()) {
            return;
        }
        alDeleteBuffers(buffer);
        checkALError();
        buffer = -1;
        LOADED_SOUNDS.remove(this);
    }
}
