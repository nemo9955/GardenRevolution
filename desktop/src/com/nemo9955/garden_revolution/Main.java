package com.nemo9955.garden_revolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = GR.TITLU +" " +GR.VERSIUNE;
        cfg.resizable = true;
        cfg.width = 800;
        cfg.height = 500;

        try {
            new LwjglApplication( new Garden_Revolution(), cfg );
        }
        catch (Exception e) {
            Gdx.input.setCursorCatched( false );
        }
    }
}
