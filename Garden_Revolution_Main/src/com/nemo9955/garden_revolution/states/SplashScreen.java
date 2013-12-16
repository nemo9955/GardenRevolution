package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.nemo9955.garden_revolution.Garden_Revolution;


public class SplashScreen implements Screen {

    public Garden_Revolution game;

    private SpriteBatch      batch;
    private Texture          loader;

    private Texture          bara;
    private Sprite           fundal;

    private BitmapFont       font;
    private String           mesaje[] = { "Fertilizing the enemy.", "Feeding the Queen.", "Planting new vegetables." };
    private String           mesaj;
    private TextBounds       mesMar   = new TextBounds();


    public SplashScreen(Garden_Revolution game) {
        this.game = game;
        batch = new SpriteBatch();

        bara = new Texture( "imagini/fundale/loading_bar.png" );
        bara.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );

        loader = new Texture( "imagini/fundale/loading_fundal.png" );
        loader.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        fundal = new Sprite( loader );
        fundal.setPosition( Gdx.graphics.getWidth() /2 - ( fundal.getWidth() /2 ), Gdx.graphics.getHeight() /2 - ( fundal.getHeight() /2 ) );

        font = new BitmapFont( Gdx.files.internal( "fonts/arial_32.fnt" ) );
        mesaj = mesaje[MathUtils.random( mesaje.length -1 )];
        mesMar = font.getBounds( mesaj, mesMar );
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        // if ( Garden_Revolution.manager.getProgress() <0.8f )
        if ( Garden_Revolution.manager.update() ) {
            game.postLoading();
            game.setScreen( Garden_Revolution.meniu );
        }
        font.setScale( 0.8f );

        batch.begin();
        fundal.draw( batch );
        batch.draw( bara, Gdx.graphics.getWidth() /2 - ( bara.getWidth() /2 ), Gdx.graphics.getHeight() /2 - ( bara.getHeight() /2 ), Garden_Revolution.manager.getProgress() *bara.getWidth(), bara.getHeight() +5 );
        font.draw( batch, String.format( "%d%% Completed", (int) ( Garden_Revolution.manager.getProgress() *100 ) ), Gdx.graphics.getWidth() /2 -95, Gdx.graphics.getHeight() /2 +12 );
        font.draw( batch, mesaj, Gdx.graphics.getWidth() /2 - ( mesMar.width /2 ), mesMar.height *2 );
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
        bara.dispose();
        fundal.getTexture().dispose();
        font.dispose();
        batch.dispose();
    }

}
