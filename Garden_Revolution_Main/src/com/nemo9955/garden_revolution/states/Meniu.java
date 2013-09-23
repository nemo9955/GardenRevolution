package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.GR_Start;
import com.nemo9955.garden_revolution.utility.Buton;


public class Meniu implements Screen {

    public GR_Start      game;
    private SpriteBatch  batch;
    private TweenManager tweeger;

    private Buton        butoane[] = new Buton[2];

    public Meniu(GR_Start game) {
        this.game = game;

        batch = new SpriteBatch();
        tweeger = new TweenManager();

        butoane[0] = new Buton( "play" ).setPozi( 50, 50 );
        butoane[1] = new Buton( "test" ).setPozi( 150, 150 );

    }

    @Override
    public void show() {
        Buton.tweeger = tweeger;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        tweeger.update( delta );

        if ( butoane[0].isPressed() )
            game.setScreen( game.gameplay );
        if ( butoane[0].isPressed() )
            game.setScreen( game.test );

        batch.begin();

        for (Buton buton : butoane )
            buton.render( delta, batch );

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
        game.dispose();
        batch.dispose();
        for (Buton buton : butoane )
            buton.dispose();
    }

}
