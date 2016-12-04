/* Copyright (C) Dominic Masters - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Dominic Masters <dominic@domsplace.com>, November 2016
 */
package com.domsplace.engine;

import com.domsplace.engine.display.DisplayManager;
import com.domsplace.engine.game.Game;
import com.domsplace.engine.game.GameInfo;
import com.domsplace.engine.scene.GameScene;
import com.domsplace.engine.sound.SoundFactory;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominic Masters <dominic@domsplace.com>
 */
public class Main {
    /**
     * The main method that is called on first run.
     * 
     * @param args Contains arguments passed from the CLI
     */
    public static void main(String[] args) {
        //Before anything we need a logger, you know for errors 'n stuff...
        final Logger mainLogger = Logger.getLogger(Main.class.getName());
        
        //First load in our game info
        try {
            GameInfo.getGameInfo().load();
            if(!GameInfo.getGameInfo().isValueSet("name")) throw new IOException("Missing game name!");
            if(!GameInfo.getGameInfo().isValueSet("version")) throw new IOException("Missing game version!");
        } catch(IOException e) {
            mainLogger.log(Level.SEVERE, "Failed to load GameInfo!", e);
            return;
        }
        
        //GameInfo should be ready, let's confirm
        mainLogger.log(Level.INFO, "Starting {0} v{1}...", new Object[]{GameInfo.getGameInfo().getValue("name"), GameInfo.getGameInfo().getValue("version")});
        
        //Confirm we have the main class key set
        if(!GameInfo.getGameInfo().isValueSet("main")) {
            mainLogger.log(Level.SEVERE, "Failed to find \"main\" in GameInfo!");
            return;
        }
        
        String main = GameInfo.getGameInfo().getValue("main");
        //Now that we have the main class, we have to make sure it exists, is load(able/ed) and extends Game
        Game game;
        try {
            Class c = Class.forName(main);
            if(!(c instanceof Class)) throw new Exception("Invalid main class!");
            Class<? extends Game> gc = (Class<? extends Game>) c;
            Constructor con = gc.getConstructors()[0];
            game = (Game) con.newInstance();
        } catch(Exception e) {
            mainLogger.log(Level.SEVERE, "Failed to load main class!", e);
            return;
        }
        
        //Well we've made it this far, the game MUST exist.
        //NOW we need to start making our game preperations (Frames, display, audio, input etc)
        try  {
            game.install();//Try to install the game, method CAN but really shouldn't be overriden.
        } catch(Exception e) {//Catching general exception for unforseen things
            mainLogger.log(Level.SEVERE, "Failed to install game!", e);
            return;
        }
        
        //First let's ensure our libs are ok.
        try {
            //Sets the graphics up, doesn't actually do anything with them though
            DisplayManager.getInstance().setup(game);//Confirms LWJGL's availability, creates our stuff but nothing is displayed.
            //Sets the sound up, again doesn't do much but Sound tends to happen asynchronously.
            SoundFactory.getFactory().start(game);
        } catch(Exception e) {
            mainLogger.log(Level.SEVERE, "Failed to setup game.");
        }
        
        //Game is ready, we can start it
        game.start();//Start the games threads 'n stuff
        
        try {
            //Main loop is now out of our hands.
            DisplayManager.getInstance().start(game);
        } catch(Exception e) {
            mainLogger.log(Level.SEVERE, "Graphics failed to start.", e);
        }
        
        mainLogger.info("Game is now closing.");
        
        //Cleanup time
        try {
            GameScene.getActiveScene().dispose();
            
            SoundFactory.getFactory().stop();
            DisplayManager.getInstance().dispose(game);
        } catch(Exception e) {
            mainLogger.log(Level.SEVERE, "Graceful shutdown failed, game will now exit.", e);
            System.exit(0);
            return;
        }
        
        //Game gracefully closed.
        mainLogger.info("Game closed gracefully.");
    }
}

