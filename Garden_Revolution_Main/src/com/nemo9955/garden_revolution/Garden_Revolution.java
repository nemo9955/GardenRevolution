package com.nemo9955.garden_revolution;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.nemo9955.garden_revolution.states.BulletJocTest;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.Meniu;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.tween.FontTween;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;


public class Garden_Revolution extends Game {

    public static final String      TITLU    = "Garden Revolution";
    public static final String      VERSIUNE = "alfa 0.15";

    public static Gameplay          gameplay;
    public static Meniu             meniu;
    public static TestScene         test;
    public static SplashScreen      splash;
    public static BulletJocTest     bullet;

    public static Garden_Revolution game;
    public static AssetManager      manager;
    public static InputMultiplexer  multiplexer;

    @Override
    public void create() {

        manager = new AssetManager();

        TextureParameter param = new TextureParameter();
        param.minFilter = TextureFilter.Linear;
        param.magFilter = TextureFilter.Linear;

        for (Assets aset : Assets.values() ) {
            if ( aset.type() ==Texture.class )
                manager.load( aset.path(), Texture.class, param );
            else
                manager.load( aset.path(), aset.type() );
        }

        splash = new SplashScreen( this );
        setScreen( splash );

    }

    public void postLoading() {


        Tween.registerAccessor( Sprite.class, new SpriteTween() );
        Tween.registerAccessor( BitmapFont.class, new FontTween() );

        Texture.setEnforcePotImages( false );
        multiplexer = new InputMultiplexer();
        Bullet.init();


        test = new TestScene();
        gameplay = new Gameplay();
        meniu = new Meniu();
        bullet = new BulletJocTest();

        gameplay.manageModels();

        multiplexer.addProcessor( test );
        multiplexer.addProcessor( gameplay );
        Gdx.input.setInputProcessor( multiplexer );

        game = this;
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
