package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.GR_Start;


public class SplashScreen implements Screen {

    public GR_Start     game;

    private SpriteBatch batch;
    private Sprite      fundal;
    private Texture     loader;


    public SplashScreen(GR_Start game) {
        this.game = game;
        batch = new SpriteBatch();

        loader = new Texture( "imagini/fundale/splash.png" );
        loader.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        fundal = new Sprite( loader );
        fundal.setPosition( Gdx.graphics.getWidth() /2 - ( fundal.getWidth() /2 ), Gdx.graphics.getHeight() /2 - ( fundal.getHeight() /2 ) );
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        if ( GR_Start.manager.update() ) {
            game.postLoading();
            game.setScreen( game.meniu );
        }


        batch.begin();
        fundal.draw( batch );
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        loader.dispose();
        batch.dispose();
    }

}
