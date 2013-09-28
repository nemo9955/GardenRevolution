package com.nemo9955.garden_revolution;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.nemo9955.garden_revolution.states.BulletJocTest;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.Meniu;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;
import com.nemo9955.garden_revolution.utility.tween.FontTween;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;


public class Garden_Revolution extends Game {

    public static final String     BUTOANE  = "imagini/butoane/";
    public static final String     FUNDALE  = "imagini/fundale/";
    public static final String     ELEMENTE = "imagini/elemente/";
    public static final String     FONTURI  = "fonts/";
    public static final String     MODELE   = "modele/";

    public static final String     TITLU    = "Garden Revolution";
    public static final String     VERSIUNE = "alfa 0.10";

    public Gameplay                gameplay;
    public Meniu                   meniu;
    public TestScene               test;
    public SplashScreen            splash;
    public BulletJocTest           bullet;

    public static AssetManager     manager;
    public static InputMultiplexer multiplexer;

    @Override
    public void create() {

        Tween.registerAccessor( Sprite.class, new SpriteTween() );
        Tween.registerAccessor( BitmapFont.class, new FontTween() );

        Texture.setEnforcePotImages( false );
        multiplexer = new InputMultiplexer();
        Bullet.init();

        manager = new AssetManager();
        manager.load( BUTOANE +"test.png", Texture.class );
        manager.load( BUTOANE +"play.png", Texture.class );
        manager.load( BUTOANE +"meniu.png", Texture.class );
        manager.load( BUTOANE +"back.png", Texture.class );
        manager.load( BUTOANE +"exit.png", Texture.class );

        manager.load( FONTURI +"font1.fnt", BitmapFont.class );


        manager.load( MODELE +"scena.g3db", Model.class );

        splash = new SplashScreen( this );
        setScreen( splash );

    }

    public void postLoading() {
        test = new TestScene( this );
        gameplay = new Gameplay( this );
        meniu = new Meniu( this );
        bullet = new BulletJocTest( this );

        gameplay.manageModels();

        multiplexer.addProcessor( gameplay );
        multiplexer.addProcessor( test );
        Gdx.input.setInputProcessor( multiplexer );
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
