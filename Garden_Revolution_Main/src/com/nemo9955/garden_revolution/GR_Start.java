package com.nemo9955.garden_revolution;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.Meniu;
import com.nemo9955.garden_revolution.states.TestScene;


public class GR_Start extends Game {

    public static final String TITLU    = "Garden Revolution";
    public static final String VERSIUNE = "alfa 0.01";

    public Gameplay            gameplay;
    public Meniu               meniu;
    public TestScene           test;

    public static AssetManager manager;

    private boolean            first    = true;
    private SpriteBatch        batch;
    private Sprite             fundal;
    private Texture            loader;


    @Override
    public void create() {

        batch = new SpriteBatch();

        loader = new Texture( "imagini/fundale/splash.png" );
        loader.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        fundal = new Sprite( loader );
        fundal.setPosition( Gdx.graphics.getWidth() /2 - ( fundal.getWidth() /2 ), Gdx.graphics.getHeight() /2 - ( fundal.getHeight() /2 ) );

        manager = new AssetManager();

        manager.load( "imagini/butoane/test.png", Texture.class );
        manager.load( "imagini/butoane/play.png", Texture.class );
        manager.load( "imagini/butoane/meniu.png", Texture.class );
        manager.load( "imagini/butoane/back.png", Texture.class );


    }

    @Override
    public void render() {
         super.render();

        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
        if ( manager.update() ) {
            if ( first ) {
                first = false;
                gameplay = new Gameplay( this );
                meniu = new Meniu( this );
                test = new TestScene( this );
            setScreen( meniu );
            }

        }



        batch.begin();
        fundal.draw( batch );
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize( width, height );
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        loader.dispose();
        batch.dispose();
    }
}
