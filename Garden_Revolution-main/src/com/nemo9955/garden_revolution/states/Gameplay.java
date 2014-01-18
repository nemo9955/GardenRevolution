package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.mediu.Arma.FireCharged;
import com.nemo9955.garden_revolution.game.mediu.Arma.FireHold;
import com.nemo9955.garden_revolution.game.mediu.Turn;
import com.nemo9955.garden_revolution.utility.CustShader;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Mod;
import com.nemo9955.garden_revolution.utility.StageUtils;

public class Gameplay extends CustomAdapter implements Screen {

    public GestureDetector    gestures;
    private PerspectiveCamera cam;
    private Environment       lights         = new Environment();
    private Shader            shader;
    private ModelBatch        modelBatch;
    public ShapeRenderer      shape;

    private Vector2           presDown       = new Vector2();
    public float              movex          = 0;
    public float              movey          = 0;

    private Vector3           dolly          = new Vector3();
    public short              toUpdate       = 0;
    private final int         scrw           = Gdx.graphics.getWidth(), scrh = Gdx.graphics.getHeight();
    private static Vector3    tmp3           = new Vector3();
    private static Vector2    tmp2           = new Vector2();
    public Stage              stage;
    public Label              viataTurn;
    public Label              fps;
    public Touchpad           mover;
    public Image              weaponCharger;

    private TweenManager      tweeger;

    public World              world;
    public float              touchPadTimmer = 0;
    private boolean           isPressed      = false;


    public Gameplay() {

        gestures = new GestureDetector( this );
        gestures.setLongPressSeconds( 1f );

        tweeger = new TweenManager();
        shape = new ShapeRenderer();
        modelBatch = new ModelBatch();

        // lights.
        lights.add( new PointLight().set( Color.BLUE, new Vector3( 5, -10, 5 ), 100 ) );
        lights.add( new DirectionalLight().set( Color.WHITE, new Vector3( 1, -1, 0 ) ) );
        lights.add( new DirectionalLight().set( Color.WHITE, new Vector3( 0, -1, 1 ) ) );

        cam = new PerspectiveCamera( 67, scrw, scrh );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        shader = new CustShader();
        shader.init();

    }

    public Gameplay init(FileHandle nivel) {
        toUpdate = 0;

        cam.position.set( 0, 0, 0 );
        cam.direction.set( 0, 0, -1 );
        cam.up.set( 0, 1, 0 );

        if ( stage !=null ) {
            for (Actor actor : stage.getActors() )
                actor.clear();
            stage.clear();
        }
        stage = StageUtils.makeGamePlayStage( stage, this );

        if ( world !=null )
            world.dispose();
        world = new World( nivel, cam );

        return this;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glViewport( 0, 0, scrw, scrh );
        Gdx.gl.glClearColor( .1f, .5f, .9f, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );

        tweeger.update( delta );

        if ( touchPadTimmer !=Mod.tPadMinAlpha &&!mover.isTouched() ) {
            touchPadTimmer -= delta;
            if ( touchPadTimmer <Mod.tPadMinAlpha )
                touchPadTimmer = Mod.tPadMinAlpha;
            if ( touchPadTimmer <1 )
                mover.addAction( Actions.alpha( touchPadTimmer ) );
        }

        if ( toUpdate ==0 )
            updateTheGame( delta );
        else
            gestures.cancel();

        modelBatch.begin( cam );
        world.render( modelBatch, lights, shader );
        modelBatch.end();

        if ( Mod.showDebug &&!Gdx.input.isKeyPressed( Keys.F9 ) ) {
            world.renderDebug( shape );
        }

        fps.setText( "FPS: " +Gdx.graphics.getFramesPerSecond() );

        stage.act();
        stage.draw();

    }

    private Controller cont = Controllers.getControllers().first();

    private void updateTheGame(float delta) {
        moveCamera( movex, movey );

        if ( world.isInTower() &&cont.getButton( Mod.buton[0] ) ) {
            Turn tower = world.getTower();

            if ( tower.getArma() instanceof FireHold )
                tower.fireNormal( world, cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) );

            if ( tower.getArma() instanceof FireCharged ) {
                int time = (int) ( System.currentTimeMillis() -but0prs );
                time = MathUtils.clamp( time, 0, 3000 );
                charge = time /3000f;
                weaponCharger.setColor( ( charge !=1 ? 0 : 1 ), 0, 0, charge );

            }

        }


        world.update( delta );
        if ( isPressed )
            world.isTouched( Gdx.input.getX(), Gdx.input.getY() );
    }

    private void moveCamera(float amontX, float amontY) {

        cam.rotateAround( world.getCameraRotAround(), Vector3.Y, amontX );
        if ( ( amontY >0 &&cam.direction.y <0.7f ) || ( amontY <0 &&cam.direction.y >-0.9f ) ) {
            tmp3.set( cam.direction ).crs( cam.up ).y = 0f;
            cam.rotateAround( world.getCameraRotAround(), tmp3.nor(), amontY );
        }

        cam.translate( dolly );

        cam.update();
    }

    private float charge = -1;


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        presDown.set( screenX, screenY );
        isPressed = true;

        if ( toUpdate ==0 &&world.isInTower() &&world.getTower().getArma() instanceof FireCharged &&screenX >scrw /2 ) {
            weaponCharger.setColor( Color.CLEAR );
            weaponCharger.setVisible( true );
            charge = 0;

            tmp2.set( stage.screenToStageCoordinates( presDown ) );
            weaponCharger.setPosition( tmp2.x - ( weaponCharger.getWidth() /2 ), tmp2.y - ( weaponCharger.getHeight() /2 ) );
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if ( weaponCharger.isVisible() ) {
            float distance = 150 *Mod.densitate;
            charge = MathUtils.clamp( presDown.dst2( screenX, screenY ), 0, distance *distance );
            charge /= distance *distance;
            weaponCharger.setColor( ( charge !=1 ? 0 : 1 ), 0, 0, charge );
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isPressed = false;
        if ( weaponCharger.isVisible() ) {
            weaponCharger.setVisible( false );
            if ( world.isInTower() &&presDown.epsilonEquals( screenX, screenY, 3 ) &&world.getTower().getArma() instanceof FireCharged ) {
                world.getTower().fireCharged( world, cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ), charge );
                return true;
            }

        }

        return false;

    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if ( toUpdate ==0 &&!weaponCharger.isVisible() &&x <scrw /2 ) {
            float difX = 0, difY = 0;
            difX = deltaX /10 *Mod.modCamSpeedX;
            difY = deltaY /7 *Mod.modCamSpeedY;
            moveCamera( difX *Mod.invertDragX, difY *Mod.invertDragY );
            return true;
        }
        return super.pan( x, y, deltaX, deltaY );

    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        return world.tap( x, y, count, button, gestures );

    }

    @Override
    public boolean longPress(float x, float y) {

        return world.longPress( x, y, gestures );

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

    private long but0prs = 0;

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if ( buttonCode ==Mod.buton[0] ) {

            weaponCharger.setColor( Color.CLEAR );
            weaponCharger.setVisible( true );
            charge = 0;


            tmp2.set( stage.screenToStageCoordinates( tmp2.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) ) );
            weaponCharger.setPosition( tmp2.x - ( weaponCharger.getWidth() /2 ), tmp2.y - ( weaponCharger.getHeight() /2 ) );

            but0prs = System.currentTimeMillis();
            return false;
        }

        if ( buttonCode ==Mod.buton[1] ) {
            return false;
        }

        if ( buttonCode ==Mod.buton[2] ) {
            return false;
        }

        if ( buttonCode ==Mod.buton[3] ) {
            return false;
        }

        return false;

    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if ( buttonCode ==Mod.buton[0] ) {
            if ( weaponCharger.isVisible() ) {
                weaponCharger.setVisible( false );
                if ( world.isInTower() &&world.getTower().getArma() instanceof FireCharged )
                    world.getTower().fireCharged( world, cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ), charge );
            }
        }
        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if ( axisCode ==0 )
            if ( Math.abs( value ) <=1 )
                movey = -value;
            else
                movey = 0;

        if ( axisCode ==1 )
            if ( Math.abs( value ) <=1 )
                movex = -value;
            else
                movex = 0;


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
        Controllers.removeListener( this );
    }

    @Override
    public void show() {

        Controllers.addListener( this );
        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this, gestures ) );
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        if ( stage !=null )
            stage.dispose();
        if ( world !=null )
            world.dispose();
        shape.dispose();
    }
}