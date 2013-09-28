package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Buton;


public class Meniu implements Screen {

    public Garden_Revolution game;
    private SpriteBatch      batch;
    private TweenManager     tweeger;

    private Buton            butoane[] = new Buton[4];

    public Meniu(Garden_Revolution game) {
        this.game = game;

        batch = new SpriteBatch();
        tweeger = new TweenManager();

        butoane[0] = new Buton( "play" ).setPozi( 150, 230 );
        butoane[1] = new Buton( "test" ).setPozi( 50, 50 );
        butoane[2] = new Buton( "test" ).setPozi( 200, 50 );
        butoane[3] = new Buton( "exit" ).setPozi( 200, 140 );

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
        if ( butoane[1].isPressed() )
            game.setScreen( game.bullet );
        if ( butoane[2].isPressed() )
            game.setScreen( game.test );
        if ( butoane[3].isPressed() )
            Gdx.app.exit();

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
