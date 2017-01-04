/*
 * Copyright 2014 Dominic Masters.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class FileUtilities {

    public static final List<InputStream> OPEN_STREAMS = new ArrayList<InputStream>();

    public static InputStream getResource(String resource) throws IOException {
        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }
        URL url = FileUtilities.class.getResource(resource);
        InputStream is = url.openStream();
        OPEN_STREAMS.add(is);
        return is;
    }

    public static String getResourceAsString(String resource) throws IOException {
        InputStream is = getResource(resource);
        String s = getInputStreamAsString(is);
        is.close();
        OPEN_STREAMS.remove(is);
        return s;
    }

    public static String getInputStreamAsString(InputStream is) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer content = new StringBuffer();
        char[] buffer = new char[1024];
        int n;

        while ((n = reader.read(buffer)) != -1) {
            content.append(buffer, 0, n);
        }

        return content.toString();
    }

    public static String loadFileToString(String modelobj) throws Exception {
        return loadFileToString(new File(modelobj));
    }

    public static String loadFileToString(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        return getInputStreamAsString(is);
    }

    public static void saveResourceToFile(String resource, File file) throws IOException {
        file.getParentFile().mkdirs();
        if (file.exists()) {
            return;
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        InputStream is = getResource(resource);

        int read = 0;
        byte[] buffer = new byte[1024];

        while ((read = is.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        is.close();
    }

    public static void saveStringsToFile(List<String> strings, File file) throws Exception {
        try {
            file.getParentFile().mkdirs();
        } catch (Throwable t) {
        }
        //if(file.exists()) return;
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);

        for (String s : strings) {
            fos.write((s + "\n").getBytes());
        }

        fos.flush();
        fos.close();
    }

    public static void saveStringToFile(String t, File file) throws Exception {
        List<String> strings = new ArrayList<String>();
        for (String s : t.replaceAll("\r", "\n").split("\n")) {
            strings.add(s);
        }
        saveStringsToFile(strings, file);
    }

    public static void readStreamToFile(InputStream is, File file) throws Exception {
        file.getParentFile().mkdirs();
        if (file.exists()) {
            return;
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);

        int read = 0;
        byte[] buffer = new byte[1024];

        while ((read = is.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        File file = new File(resource);
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();

            buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);

            while (fc.read(buffer) != -1) ;

            fis.close();
            fc.close();
        } else {
            buffer = BufferUtils.createByteBuffer(bufferSize);

            InputStream source = FileUtilities.getResource(resource);
            if (source == null) {
                throw new FileNotFoundException(resource);
            }

            try {
                ReadableByteChannel rbc = Channels.newChannel(source);
                try {
                    while (true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1) {
                            break;
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                        }
                    }
                } finally {
                    rbc.close();
                }
            } finally {
                source.close();
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
    
    public static Map<String,String> parseINI(String data) throws IOException {
        return parseINI(data, new HashMap<String,String>());
    }
    
    public static Map<String,String> parseINI(String string_data, Map<String,String> defaults) throws IOException {
        String[] lines = string_data.split("\n");
        Map<String,String> data = defaults;//Stores our temporary values.
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
        return data;
    }
}
