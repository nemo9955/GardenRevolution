package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class TestScene extends CustomAdapter implements Screen {

    private Stage              stage;

    private TextButton         back;

    private Image              img;

    private static final float rap = 1.3f;

    public TestScene() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel( rap /Vars.densitate );
        stage = new Stage( viewport );

        back = new TextButton( "Back", GR.skin, "demon" );
        back.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GR.game.setScreen( GR.menu );
            }

        } );

        Table tab = new Table( GR.skin );
        tab.setFillParent( true );

        tab.defaults();

        tab.add( "Elev: Mogoi Adrian", "arial", Color.BLACK ).padBottom( 40 ).row();
        tab.add( "Profesor:", "arial", Color.BLACK ).row();
        tab.add( "Tomulescu Vasilica", "arial", Color.BLACK ).row();
        tab.add( "Colegiul National", "arial", Color.BLACK ).padTop( 40 ).row();
        tab.add( "\"Ecaterina Teodoroiu\"", "arial", Color.BLACK ).row();

        img = new Image( Garden_Revolution.getBG() );
        stage.addActor( img );
        stage.addActor( tab );
        stage.addActor( back );
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
            case Keys.BACK:
                GR.game.setScreen( GR.menu );
                break;
        }
        return false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( new InputMultiplexer( this, stage ) );
        if ( Func.isControllerUsable() )
            Controllers.addListener( this );
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );
        stage.act( delta );

        if ( Func.isAndroid() && ( Gdx.input.isKeyPressed( Keys.BACK ) ||Gdx.input.isKeyPressed( Keys.ESCAPE ) ) )
            GR.game.setScreen( GR.menu );

        /*
         * // normal image rendering
         * batch.setProjectionMatrix( cam.combined );
         * batch.begin();
         * font.draw( batch, String.format( "Densitate : %s", Vars.densitate ), 100, 200 );
         * font.draw( batch, String.format( "Height : %s", Gdx.graphics.getHeight() ), 100, 850 );
         * font.draw( batch, String.format( "Width    : %s", Gdx.graphics.getWidth() ), 100, 800 );
         * font.draw( batch, String.format( "Apasat?  : %s", Gdx.input.isTouched() ), 100, 700 );
         * font.draw( batch, String.format( "Format    : %s", Gdx.input.getNativeOrientation().toString() ), 100, 650 );
         * font.draw( batch, String.format( "Roll       : %f", Gdx.input.getRoll() ), 100, 550 );
         * font.draw( batch, String.format( "Pitch      : %f", Gdx.input.getPitch() ), 100, 500 );
         * font.draw( batch, String.format( "Azimuth : %f", Gdx.input.getAzimuth() ), 100, 450 );
         * font.draw( batch, String.format( "Acc Z : %f", Gdx.input.getAccelerometerZ() ), 100, 350 );
         * font.draw( batch, String.format( "Acc Y : %f", Gdx.input.getAccelerometerY() ), 100, 300 );
         * font.draw( batch, String.format( "Acc X : %f", Gdx.input.getAccelerometerX() ), 100, 250 );
         * batch.end();
         */

        stage.draw();

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if ( buttonIndex ==CoButt.Back.id )
            Func.click( back );
        return false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
        img.setSize( stage.getWidth(), stage.getHeight() );
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
        if ( Func.isControllerUsable() )
            Controllers.removeListener( this );
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


}