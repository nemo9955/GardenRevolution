package com.nemo9955.garden_revolution;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.LevelSelector;
import com.nemo9955.garden_revolution.states.Menu;
import com.nemo9955.garden_revolution.states.MultyplayerSelector;
import com.nemo9955.garden_revolution.states.Options;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.tween.FontTween;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;

public class Garden_Revolution extends Game {

    public static final String        TITLU    = "Garden Revolution";
    public static final String        VERSIUNE = "alfa 0.666";

    public static Options             options;
    public static Gameplay            gameplay;
    public static Menu                menu;
    public static TestScene           test;
    public static SplashScreen        splash;
    public static LevelSelector       selecter;
    public static MultyplayerSelector multyplayer;

    public static Garden_Revolution   game;
    public static AssetManager        manager;

    @Override
    public void create() {

        manager = new AssetManager();

        Texture.setEnforcePotImages( false );

        TextureParameter param = new TextureParameter();
        param.minFilter = TextureFilter.Linear;
        param.magFilter = TextureFilter.Linear;

        for (Assets aset : Assets.values() ) {
            try {
                if ( aset.type() ==Texture.class )
                    manager.load( aset.path(), Texture.class, param );
                else
                    manager.load( aset.path(), aset.type() );
            }
            catch (Exception e) {
                System.out.println( "problema la incarcarea assetului : " +aset.name() );
            }
        }

        splash = new SplashScreen( this );
        setScreen( splash );

    }

    public void postLoading() {

        Tween.registerAccessor( Sprite.class, new SpriteTween() );
        Tween.registerAccessor( BitmapFont.class, new FontTween() );

        Texture.setEnforcePotImages( false );

        multyplayer = new MultyplayerSelector();
        options = new Options();
        test = new TestScene();
        gameplay = new Gameplay();
        menu = new Menu();
        selecter = new LevelSelector();

        game = this;
    }

    public static Model getModel(Assets model) {
        return manager.get( model.path(), Model.class );
    }

    public static AtlasRegion getGameTexture(String name) {
        return manager.get( Assets.GAME_PACK.path(), TextureAtlas.class ).findRegion( name );
    }

    @Override
    public void dispose() {
        gameplay.dispose();
        menu.dispose();
        selecter.dispose();
        splash.dispose();
        test.dispose();
        options.dispose();
        multyplayer.dispose();

        manager.dispose();

        System.out.println( "Toate resursele au fost eliminate cu succes !" );
    }
}
