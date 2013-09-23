package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.GR_Start;
import com.nemo9955.garden_revolution.utility.Buton;


public class TestScene implements Screen {

    public GR_Start     game;
    private SpriteBatch batch;
    private Buton       butoane[] = new Buton[1];

    public TestScene(GR_Start game) {
        this.game = game;
        batch = new SpriteBatch();
        butoane[0] = new Buton( "back" ).setPozi( 10, 10 );
    }

    @Override
    public void show() {
        System.out.println( "test" );
    }

    @Override
    public void render(float delta) {
        
       // Gdx.gl.glViewport( 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT  );

        if ( butoane[0].isPressed() )
            game.setScreen( game.meniu );

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
        batch.dispose();
        game.dispose();
    }
}
