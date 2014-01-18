package com.nemo9955.garden_revolution;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = Garden_Revolution.TITLU +" " +Garden_Revolution.VERSIUNE;
        cfg.useGL20 = true;
        cfg.resizable = false ;
        cfg.width = 720;
        cfg.height = 480;

        new LwjglApplication( new Garden_Revolution(), cfg );
    }
}