package com.nemo9955.garden_revolution;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

public class Main {

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = GR_Start.TITLU +" " +GR_Start.VERSIUNE;
        cfg.useGL20 = true;
        cfg.backgroundFPS = -1;
        cfg.resizable = false ;
        cfg.initialBackgroundColor = Color.BLUE;
        cfg.width = 1280;
        cfg.height = 720;

        new LwjglApplication( new GR_Start(), cfg );
    }
}
