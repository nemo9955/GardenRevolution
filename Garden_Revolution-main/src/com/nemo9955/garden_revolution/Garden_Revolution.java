package com.nemo9955.garden_revolution;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.LevelSelector;
import com.nemo9955.garden_revolution.states.Menu;
import com.nemo9955.garden_revolution.states.MultyplayerSelector;
import com.nemo9955.garden_revolution.states.Options;
import com.nemo9955.garden_revolution.states.SplashScreen;
import com.nemo9955.garden_revolution.states.TestScene;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.tween.FontTween;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;

public class Garden_Revolution extends Game {

    @Override
    public void create() {

        GR.manager = new AssetManager();

        Texture.setEnforcePotImages( false );

        TextureParameter param = new TextureParameter();
        param.minFilter = TextureFilter.Linear;
        param.magFilter = TextureFilter.Linear;

        for (Assets aset : Assets.values() ) {
            try {
                if ( aset.type() ==Texture.class )
                    GR.manager.load( aset.path(), Texture.class, param );
                else
                    GR.manager.load( aset.path(), aset.type() );
            }
            catch (Exception e) {
                System.out.println( "problema la incarcarea assetului : " +aset.name() );
            }
        }

        GR.splash = new SplashScreen( this );
        setScreen( GR.splash );

    }

    public void postLoading() {

        if ( Func.isAndroid() )
            Gdx.input.setCatchBackKey( true );

        Tween.registerAccessor( Sprite.class, new SpriteTween() );
        Tween.registerAccessor( BitmapFont.class, new FontTween() );

        Texture.setEnforcePotImages( false );

        GR.skin = GR.manager.get( Assets.SKIN_JSON.path(), Skin.class );

        GR.multyplayer = new MultyplayerSelector();
        GR.options = new Options();
        GR.test = new TestScene();
        GR.gameplay = new Gameplay();
        GR.menu = new Menu();
        GR.selecter = new LevelSelector();

        GR.game = this;
    }

    public static Model getModel(Assets model) {
        return GR.manager.get( model.path(), Model.class );
    }

    public static AtlasRegion getGameTexture(String name) {
        return GR.manager.get( Assets.GAME_PACK.path(), TextureAtlas.class ).findRegion( name );
    }

    public static AtlasRegion getMenuTexture(String name) {
        return GR.manager.get( Assets.ELEMENTS_PACK.path(), TextureAtlas.class ).findRegion( name );
    }

    @Override
    public void dispose() {
        GR.gameplay.dispose();
        GR.menu.dispose();
        GR.selecter.dispose();
        GR.splash.dispose();
        GR.test.dispose();
        GR.options.dispose();
        GR.multyplayer.dispose();

        GR.manager.dispose();

        System.out.println( "Toate resursele au fost eliminate cu succes !" );
    }
}
