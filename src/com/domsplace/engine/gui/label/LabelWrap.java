/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine.gui.label;

import com.domsplace.engine.gui.GUI;
import com.domsplace.engine.scene.GameScene;
import com.domsplace.engine.sound.Sound;
import com.domsplace.engine.sound.SoundPlayer;
import com.domsplace.engine.utilities.TimeUtilities;
import java.util.logging.Level;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class LabelWrap extends Label {
    public static final int DEFAULT_SCROLL_SPEED = 25;
    
    //Instance
    private int maxWidth = 400;
    
    private String finalText = "Hello Wrap.";   //The actual text to be printed.
    
    private int char_index = 0;     //Current Character Index from the String
    private long last_render;       //Timestamp of last printChar()
    public int scroll_speed = DEFAULT_SCROLL_SPEED;   //Time (in milliseconds) between Characters
    public boolean faster_scroll;   //If true 3 characters will be printed at a time
    private int maxLines = 0;       //If Greater than zero this will only hold this many lines of text, setText will return the over trim
    private Sound charSound;
    
    public LabelWrap(GUI gui) {
        super(gui);
        this.setText("Hello Wrap.");
        
        try {
            charSound = Sound.getSound("resource/font/text_deep.ogg");
        } catch(Exception e) {
            getGUI().getGame().getLogger().log(Level.SEVERE, "Failed to load Character sound", e);
        }
    }
    
    @Override public String getText() {return this.finalText;}
    public String getCurrentText() {return super.getText();}
    public int getScrollSpeed() {return this.scroll_speed;}
    public int getMaxLines() {return this.maxLines;}
    public int getMaxWidth() {return this.maxWidth;}
    public Sound getCharacterScrollSound() {return this.charSound;}
    @Override public int getWidth() {return maxWidth;}
    @Override public int getHeight() {
        if(maxLines > 0) {
            return maxLines * getFont().getFontSize();
        } else {
            //We have to calculate height?! what is this.
            String[] lines = this.finalText.split("\n");
            return lines.length*getFont().getFontSize();
        }
    }
    
    public void setCharacterScrollSound(Sound s) {this.charSound = s;}
    protected void setCurrentText(String text) {super.setText(text);}
    public void setMaxLines(int maxLines) {this.maxLines = maxLines; this.setText(finalText,true);}
    public void setMaxWidth(int maxWidth) {this.maxWidth = maxWidth; this.setText(finalText,true);}
    
    public boolean hasFinishedScrolling() {return this.char_index >= this.finalText.length();}
    
    @Override
    public String setText(String text) {
        return this.setText(text,false);
    }
    
    private String setText(String text,boolean preserve) {
        if(!preserve) {
            char_index = 0;
            last_render = 0;
        }
        
        //OK (third time's the charm...) so we need to split our Strings by "newlines"
        String buffer = "";
        
        String[] lines = text.split("\n");
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];//Now we need to split by "word"
            String[] words = line.split(" ");
            
            String currentLine = "";
            
            for(int w = 0; w < words.length; w++) {
                String word = words[w];//Word.
                
                String expectedLine = currentLine + word;
                float expectedWidth = getFont().getWidth(expectedLine);
                
                //Check the expected width...
                if(expectedWidth > maxWidth) {
                    //This word puts this line over the maxWidth limit... rip
                    buffer += currentLine + "\n";//Add the current line to the buffer
                    currentLine = word + " ";//Clear the currentLine, and make it contain only our word
                    continue;//Next word.
                }
                
                //This word is NOT over the expected length (Fuckin' yay) so we can add it
                currentLine = expectedLine + " ";
            }
            
            //Alright we have some stuff left over
            currentLine = currentLine.replaceAll("\\s+$", "");//Remove the space, euck.
            buffer += currentLine + "\n";
        }
        while(buffer.endsWith("\n")) buffer = buffer.substring(0, buffer.length()-1);//Remove the newline, euck.
        
        //Buff now contains the text but we need to check a few things.
        lines = buffer.split("\n");//Split again by newline
        if(lines.length < 1) {//Make sure there's at least one freaking line...
            this.finalText = "";
            this.setCurrentText("");
            return "";
        }
        
        //All good we can set the buffer, asuming there's not too many lines
        
        if(this.maxLines > 0 && lines.length > this.maxLines) {
            String buffComplete = "";
            String remaining = "";
            for(int i = 0; i < lines.length; i++) {
                if(i < this.maxLines) {
                    buffComplete += lines[i] + "\n";
                } else {
                    remaining = lines[i] + "\n";
                }
            }
            if(remaining.endsWith("\n")) remaining = remaining.substring(0, remaining.length()-1);
            if(buffComplete.endsWith("\n")) buffComplete = buffComplete.substring(0, buffComplete.length()-1);
            this.finalText = buffComplete;
            this.setCurrentText("");
            return remaining;
        }
        this.finalText = buffer;
        this.setCurrentText("");
        
        return "";
    }
    

    @Override
    public void render(GameScene scene, double frame_took) {
        long now = System.nanoTime();
        long speed = TimeUtilities.millisecondsToNanoseconds(scroll_speed);
        
        if ((now - last_render) > speed) {
            int c = 1;
            if(this.faster_scroll) c = 3;
            while(c > 0 && this.char_index < this.getText().length()) {
                this.printChar();
                c--;
            }
        }
        
        super.render(scene, frame_took);
    }
    
    private void printChar() {
        char last_char = ' ';
        String s = "";
        for(int i = 0; i < this.char_index; i++) {
            char c = this.getText().charAt(i);
            s += this.getText().charAt(i);
            last_char = c;
        }
        this.setCurrentText(s);
        char_index ++;
        last_render = System.nanoTime();
        if (this.char_index >= this.finalText.length()) {
            this.onFinish();
        } else {
            this.onCharacter(last_char);
        }
    }
    
    public void onFinish() {
    }
    
    public void onCharacter(char c) {
        try {
            SoundPlayer player = this.charSound.genPlayer();
            player.playThenDispose();
        } catch(Exception e) {
            this.getGUI().getGame().getLogger().log(Level.SEVERE, "Failed to play scroll sound!", e);
        }
    }
}
