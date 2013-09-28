package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Buton;


public class TestScene implements Screen, InputProcessor {

    public Garden_Revolution   game;
    private SpriteBatch        batch;
    private ShapeRenderer      shape;
    private OrthographicCamera cam;

    private BitmapFont         font;

    private TweenManager       tweeger;
    private Buton              butoane[] = new Buton[1];

    private float              pozitie   = 0;

    public TestScene(Garden_Revolution game) {
        this.game = game;
        tweeger = new TweenManager();
        font = Garden_Revolution.manager.get( "fonts/font1.fnt" );

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        cam = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        shape.setProjectionMatrix( cam.combined );
        batch.setProjectionMatrix( cam.combined );
        shape.setColor( Color.RED );
        cam.position.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2, 0 );
        cam.update();

        butoane[0] = new Buton( "back" ).setPozi( 100, 100 ).setOrigin( 0, 0 );
        for (Buton buton : butoane )
            buton.setCamera( cam );
    }

    @Override
    public void show() {
        Buton.tweeger = tweeger;
        cam.position.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2, 0 );
    }

    @Override
    public void render(float delta) {

        cam.update();
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );

        tweeger.update( delta );

        if ( butoane[0].isPressed() ) {
            // Gdx.input.vibrate( 500 );
            game.setScreen( game.meniu );
        }

        // normal image rendering
        batch.setProjectionMatrix( cam.combined );
        batch.begin();
        for (Buton buton : butoane )
            buton.render( delta, batch );

        font.draw( batch, String.format( "Height : %s", Gdx.graphics.getHeight() ), 100, 850 );
        font.draw( batch, String.format( "Width    : %s", Gdx.graphics.getWidth() ), 100, 800 );

        font.draw( batch, String.format( "Apasat?  : %s", Gdx.input.isTouched() ), 100, 700 );
        font.draw( batch, String.format( "Format    : %s", Gdx.input.getNativeOrientation().toString() ), 100, 650 );

        font.draw( batch, String.format( "Roll       : %f", Gdx.input.getRoll() ), 100, 550 );
        font.draw( batch, String.format( "Pitch      : %f", Gdx.input.getPitch() ), 100, 500 );
        font.draw( batch, String.format( "Azimuth : %f", Gdx.input.getAzimuth() ), 100, 450 );


        font.draw( batch, String.format( "Acc Z : %f", Gdx.input.getAccelerometerZ() ), 100, 350 );
        font.draw( batch, String.format( "Acc Y : %f", Gdx.input.getAccelerometerY() ), 100, 300 );
        font.draw( batch, String.format( "Acc X : %f", Gdx.input.getAccelerometerX() ), 100, 250 );

        batch.end();

        // shape rendering
        shape.setProjectionMatrix( cam.combined );
        shape.begin( ShapeType.Line );


        shape.end();
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
        font.dispose();
        game.dispose();
        shape.dispose();
        for (Buton buton : butoane )
            buton.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {

        return false;

    }

    @Override
    public boolean keyUp(int keycode) {

        return false;

    }

    @Override
    public boolean keyTyped(char character) {

        return false;

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        pozitie = screenY;
        // System.out.println( ( screenX +cam.position.x - ( Gdx.graphics.getWidth() /2 ) ) +" " + ( -screenY + ( Gdx.graphics.getHeight() /2 ) +cam.position.y ) );
        // System.out.println( cam.position.x +" " +cam.position.y );
        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        cam.translate( 0, - ( pozitie -screenY ) );
        pozitie = screenY;
        return false;

    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;

    }

    @Override
    public boolean scrolled(int amount) {
        return false;

    }
}
