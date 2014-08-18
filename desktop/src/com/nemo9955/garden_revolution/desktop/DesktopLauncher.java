package com.nemo9955.garden_revolution.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;

public class DesktopLauncher {

	public static void main( String[] args ) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = GR.TITLU + " " + GR.VERSIUNE;
		cfg.resizable = true;
		cfg.width = 800;
		cfg.height = 500;
		cfg.foregroundFPS = 1000;
		// cfg.overrideDensity = 160;

		try {
			new LwjglApplication(new Garden_Revolution(), cfg);
		}
		catch ( Exception e ) {
		}
		finally {
			Gdx.input.setCursorCatched(false);
		}
	}
}
