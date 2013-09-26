package com.nemo9955.garden_revolution;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.Meniu;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;
import com.nemo9955.garden_revolution.utility.tween.FontTween;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;


public class GR_Start extends Game {

    public static final String TITLU    = "Garden Revolution";
    public static final String VERSIUNE = "alfa 0.08";

    public Gameplay            gameplay;
    public Meniu               meniu;
    public TestScene           test;
    public SplashScreen        splash;

    public static AssetManager manager;

    @Override
    public void create() {

        Tween.registerAccessor( Sprite.class, new SpriteTween() );
        Tween.registerAccessor( BitmapFont.class, new FontTween() );

        Texture.setEnforcePotImages( false );

        manager = new AssetManager();
        manager.load( "imagini/butoane/test.png", Texture.class );
        manager.load( "imagini/butoane/play.png", Texture.class );
        manager.load( "imagini/butoane/meniu.png", Texture.class );
        manager.load( "imagini/butoane/back.png", Texture.class );
        manager.load( "fonts/font1.fnt", BitmapFont.class );

        manager.load( "modele/scena.g3db", Model.class );

        splash = new SplashScreen( this );
        setScreen( splash );

    }

    public void postLoading() {
        test = new TestScene( this );
        gameplay = new Gameplay( this );
        meniu = new Meniu( this );

        gameplay.manageModels();
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
