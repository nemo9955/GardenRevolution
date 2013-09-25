package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.nemo9955.garden_revolution.GR_Start;
import com.nemo9955.garden_revolution.utility.Buton;


public class TestScene implements Screen {

    public GR_Start            game;
    private SpriteBatch        batch;
    private ShapeRenderer      shape;
    private OrthographicCamera cam;

    private TweenManager       tweeger;
    private Buton              butoane[] = new Buton[1];

    public TestScene(GR_Start game) {
        this.game = game;
        tweeger = new TweenManager();

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        cam = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        shape.setProjectionMatrix( cam.combined );
        shape.setColor( Color.WHITE );
        cam.position.set( 100, 0, 0 );

        butoane[0] = new Buton( "back" ).setPozi( 5, 5 ).setOrigin( 0, 0 );
    }

    @Override
    public void show() {
        Buton.tweeger = tweeger;
    }

    @Override
    public void render(float delta) {

        cam.update( false );
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT |GL10.GL_DEPTH_BUFFER_BIT ); //

        tweeger.update( delta );

        if ( butoane[0].isPressed() )
            game.setScreen( game.meniu );

        // normal image rendering
        batch.begin();
        for (Buton buton : butoane )
            buton.render( delta, batch );
        batch.end();

        // shape rendering
        shape.begin( ShapeType.Line );
        shape.line( 0, 0, 90, -100 );
        shape.line( 0, 0, 110, 50 );
        shape.line( 0, 0, -100, 100 );
        shape.line( 0, 0, -100, -60 );
        shape.end();
        
        if(Gdx.input.isKeyPressed( Input.Keys.W ))
            cam.translate( 0, 5 );
        if(Gdx.input.isKeyPressed( Input.Keys.S ))
            cam.translate( 0, -5 );
        
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
        shape.dispose();
        for (Buton buton : butoane )
            buton.dispose();
    }
}
