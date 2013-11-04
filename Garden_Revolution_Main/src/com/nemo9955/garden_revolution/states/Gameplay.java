package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.Shot;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.entitati.Knight;
import com.nemo9955.garden_revolution.game.entitati.Rosie;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustShader;
import com.nemo9955.garden_revolution.utility.Mod;
import com.nemo9955.garden_revolution.utility.StageUtils;

public class Gameplay implements Screen, InputProcessor, GestureListener {


    private PerspectiveCamera       cam;
    private Lights                  lights   = new Lights();
    private Shader                  shader;
    private ModelBatch              modelBatch;
    private ImmediateModeRenderer20 renderer;

    private float                   startX, startY;
    public float                    movex    = 0;
    public float                    movey    = 0;
    private float                   deltaX   = 0;
    private float                   deltaY   = 0;

    private Vector3                 dolly    = new Vector3();
    public short                    toUpdate = 0;
    private final int               scrw     = Gdx.graphics.getWidth(), scrh = Gdx.graphics.getHeight();
    private final Vector3           tmp      = new Vector3();
    private Stage                   stage;
    private Skin                    skin;
    private GestureDetector         gestures = new GestureDetector( this );
    private TweenManager            tweeger;

    public World                    world;

    public Gameplay() {
        float amb = 0.4f, lum = 0.3f;
        tweeger = new TweenManager();

        modelBatch = new ModelBatch();

        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 0f ) );

        cam = new PerspectiveCamera( 67, scrw, scrh );
        cam.position.set( 0, 12, 0 );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        stage = StageUtils.makeGamePlayStage( stage, skin, this );

        shader = new CustShader();
        shader.init();

        gestures.setLongPressSeconds( 1f );

        renderer = new ImmediateModeRenderer20( true, true, 0 );

    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this, gestures ) );
        toUpdate = 0;
        world = new World( Garden_Revolution.getModel( Assets.SCENA ), cam );
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glViewport( 0, 0, scrw, scrh );
        Gdx.gl.glClearColor( .1f, .5f, .9f, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );
        tweeger.update( delta );

        if ( toUpdate ==0 )
            updateGameplay( delta );

        modelBatch.begin( cam );
        world.render( modelBatch, lights, shader );
        modelBatch.end();

        renderer.begin( cam.combined, GL10.GL_LINE_STRIP );
        world.renderLines( renderer );
        renderer.end();

        stage.act();
        stage.draw();
        Table.drawDebug( stage );// TODO scapa de asta

    }


    private void updateGameplay(float delta) {
        if ( movex !=0 ||movey !=0 ||!dolly.isZero() )
            moveCamera( movex, movey );
        world.update( delta );
    }


    private void moveCamera(float amontX, float amontY) {

        cam.direction.rotate( cam.up, amontX );

        if ( ( amontY >0 &&cam.direction.y <0.3f ) || ( amontY <0 &&cam.direction.y >-0.9f ) ) {
            tmp.set( cam.direction ).crs( cam.up ).nor();
            cam.direction.rotate( tmp, amontY );
        }

        cam.translate( dolly );

        cam.update();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ( Mod.moveByTouch &&toUpdate ==0 ) {
            startX = screenX;
            startY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if ( Mod.moveByTouch &&toUpdate ==0 ) {
            deltaX = ( screenX -startX ) /10 *Mod.modCamSpeedX;
            deltaY = ( startY -screenY ) /7 *Mod.modCamSpeedY;
            startX = screenX;
            startY = screenY;
            moveCamera( deltaX *Mod.invertDragX, deltaY *Mod.invertDragY );
            return true;
        }

        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                movey = 1.5f;
                break;
            case Keys.S:
                movey = -1.5f;
                break;
            case Keys.D:
                movex = -1.5f;
                break;
            case Keys.A:
                movex = 1.5f;
                break;
            case Keys.NUMPAD_8:
                dolly.x = 0.3f;
                break;
            case Keys.NUMPAD_2:
                dolly.x = -0.3f;
                break;
            case Keys.NUMPAD_4:
                dolly.z = -0.3f;
                break;
            case Keys.NUMPAD_6:
                dolly.z = 0.3f;
                break;
            case Keys.NUMPAD_9:
                dolly.y = 0.3f;
                break;
            case Keys.NUMPAD_3:
                dolly.y = -0.3f;
                break;
            case Keys.ESCAPE:
                Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Keys.W:
            case Keys.S:
                movey = 0;
                break;
            case Keys.D:
            case Keys.A:
                movex = 0;
                break;
            case Keys.NUMPAD_6:
            case Keys.NUMPAD_4:
                dolly.z = 0;
                break;
            case Keys.NUMPAD_8:
            case Keys.NUMPAD_2:
                dolly.x = 0;
                break;
            case Keys.NUMPAD_9:
            case Keys.NUMPAD_3:
                dolly.y = 0;
                break;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        Ray ray = cam.getPickRay( x, y );
        world.addShot( new Shot( ray.origin, ray.direction ) );

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        if ( Math.abs( Gdx.input.getX() -x ) <20 &&Math.abs( Gdx.input.getY() -y ) <20 ) {
            Ray ray = cam.getPickRay( x, y );
            float distance = -ray.origin.y /ray.direction.y;
            Vector3 poz = ray.getEndPoint( new Vector3(), distance );
            if ( Gdx.input.isButtonPressed( Buttons.RIGHT ) )
                world.addFoe( new Knight( world.closestPath( poz ), poz.x, poz.y, poz.z ) );
            else
                world.addFoe( new Rosie( world.closestPath( poz ), poz.x, poz.y, poz.z ) );
            gestures.invalidateTapSquare();
        }
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if ( Math.abs( velocityX ) >5 ) {
            if ( velocityX >0 )
                world.nextCamera();
            if ( velocityX <0 )
                world.prevCamera();
        }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }


    @Override
    public void resize(int width, int height) {
    }


    @Override
    public void pause() {
    }


    @Override
    public void resume() {
    }


    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
    }


    @Override
    public void dispose() {
        modelBatch.dispose();
        stage.dispose();
        world.dispose();
        skin.dispose();
        renderer.dispose();
    }
}