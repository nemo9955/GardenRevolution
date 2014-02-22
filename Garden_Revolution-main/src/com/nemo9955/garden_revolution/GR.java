package com.nemo9955.garden_revolution;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.LevelSelector;
import com.nemo9955.garden_revolution.states.Menu;
import com.nemo9955.garden_revolution.states.MultyplayerSelector;
import com.nemo9955.garden_revolution.states.Options;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;


public class GR {

    public static final String        TITLU          = "Garden Revolution";
    public static final String        VERSIUNE       = "alfa 0.666";

    public static Options             options;
    public static Gameplay            gameplay;
    public static Menu                menu;
    public static TestScene           test;
    public static SplashScreen        splash;
    public static LevelSelector       selecter;
    public static MultyplayerSelector multyplayer;

    public static Garden_Revolution   game;
    public static AssetManager        manager;
    public final static String        levelsLocation = "harti/nivele/";


    public static final Vector3       temp1          = new Vector3();
    public static final Vector3       temp2          = new Vector3();
    public static final Vector3       temp3          = new Vector3();

}
