package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public class SplashScreen implements Screen {

    public Garden_Revolution game;

    private SpriteBatch      batch;
    private Texture          loader;

    private Texture          bara;
    private Sprite           fundal;

    private BitmapFont       font;


    public SplashScreen(Garden_Revolution game) {
        this.game = game;
        batch = new SpriteBatch();

        bara = new Texture( Assets.LOADING_BAR.path() );
        bara.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );

        loader = new Texture( Assets.LOADING_BG.path() );
        loader.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        fundal = new Sprite( loader );
        fundal.setPosition( Gdx.graphics.getWidth() /2 - ( fundal.getWidth() /2 ), Gdx.graphics.getHeight() /2 - ( fundal.getHeight() /2 ) );

        font = new BitmapFont( Gdx.files.internal( "fonts/arial_32.fnt" ) );
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        if ( Garden_Revolution.manager.update() ) {
            game.postLoading();
            game.setScreen( Garden_Revolution.meniu );
        }
        font.setScale( 0.8f );

        batch.begin();
        fundal.draw( batch );
        batch.draw( bara, Gdx.graphics.getWidth() /2 - ( bara.getWidth() /2 ), Gdx.graphics.getHeight() /2 - ( bara.getHeight() /2 ), Garden_Revolution.manager.getProgress() *bara.getWidth(), bara.getHeight() );
        font.draw( batch, String.format( "%d%% Completed", (int) ( Garden_Revolution.manager.getProgress() *100 ) ), Gdx.graphics.getWidth() /2 -80, Gdx.graphics.getHeight() /2 +18 );
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
