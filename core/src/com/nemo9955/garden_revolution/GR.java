package com.nemo9955.garden_revolution;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.LevelSelector;
import com.nemo9955.garden_revolution.states.Menu;
import com.nemo9955.garden_revolution.states.MultyplayerSelector;
import com.nemo9955.garden_revolution.states.Options;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;


public class GR {

    public static final String        TITLU          = "Garden Revolution";
    public static final String        VERSIUNE       = "alfa 0.90";

    public static Options             options;
    public static Gameplay            gameplay;
    public static Menu                menu;
    public static TestScene           test;
    public static SplashScreen        splash;
    public static LevelSelector       selecter;
    public static MultyplayerSelector multyplayer;

    public static Garden_Revolution   game;
    public static AssetManager        manager;
    public static final String        levelsLocation = "harti/nivele/";
    public static Skin                skin;

    public static final Ray           ray1           = new Ray( new Vector3(), new Vector3() );

    public static final Vector2       tmp1           = new Vector2();
    public static final Vector2       tmp2           = new Vector2();

    public static final Vector3       temp1          = new Vector3();
    public static final Vector3       temp2          = new Vector3();
    public static final Vector3       temp3          = new Vector3();
    public static final Vector3       temp4          = new Vector3();


    public static Texture             bg;
}
