package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class TestScene extends CustomAdapter implements Screen {

    private SpriteBatch        batch;
    private ShapeRenderer      shape;
    private OrthographicCamera cam;

    private BitmapFont         font;
    private Stage              stage;

    private TextButton         back;

    private float              pozitie = 0;

    public TestScene() {
        font = GR.manager.get( Assets.ARIAL32.path() );

        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        cam = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        shape.setProjectionMatrix( cam.combined );
        batch.setProjectionMatrix( cam.combined );
        shape.setColor( Color.RED );
        cam.position.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2, 0 );
        cam.update();

        stage = new Stage( new ScreenViewport() );

        back = new TextButton( "back", (Skin) GR.manager.get( Assets.SKIN_JSON.path() ) );
        back.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GR.game.setScreen( GR.menu );
            }

        } );
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
        cam.position.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2, 0 );
        Gdx.input.setInputProcessor( new InputMultiplexer( this, stage ) );
        if ( Func.isControllerUsable() ) {
            Controllers.addListener( this );
        }
    }

    @Override
    public void render(float delta) {

        cam.update();
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );
        stage.act( delta );

        if ( Func.isAndroid() && ( Gdx.input.isKeyPressed( Keys.BACK ) ||Gdx.input.isKeyPressed( Keys.ESCAPE ) ) )
            GR.game.setScreen( GR.menu );

        // normal image rendering
        batch.setProjectionMatrix( cam.combined );
        batch.begin();


        font.draw( batch, String.format( "Densitate : %s", Vars.densitate ), 100, 200 );
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

        stage.draw();
        // shape rendering
        shape.setProjectionMatrix( cam.combined );
        shape.begin( ShapeType.Line );


        shape.end();
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
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
        if ( Func.isControllerUsable() ) {
            Controllers.removeListener( this );
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        pozitie = screenY;
        // System.out.println( ( screenX +cam.position.x - ( Gdx.graphics.getWidth() /2 ) ) +" " + ( -screenY + ( Gdx.graphics.getHeight() /2 ) +cam.position.y ) );
        // System.out.println( cam.position.x +" " +cam.position.y );
        return false;

    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        cam.translate( 0, - ( pozitie -screenY ) );
        pozitie = screenY;
        return false;

    }

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
        stage.dispose();
    }


}
