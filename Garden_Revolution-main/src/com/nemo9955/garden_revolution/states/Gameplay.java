package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
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
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireCharged;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireHold;
import com.nemo9955.garden_revolution.utility.CustShader;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.StageUtils;
import com.nemo9955.garden_revolution.utility.Vars;

public class Gameplay extends CustomAdapter implements Screen {


    public GestureDetector gestures;
    private Shader         shader;
    private ModelBatch     modelBatch;
    public ShapeRenderer   shape;


    public float           movex          = 0;
    public float           movey          = 0;
    public boolean         updWorld       = true;
    private final int      scrw           = Gdx.graphics.getWidth(), scrh = Gdx.graphics.getHeight();
    public static Vector2  tmp2           = new Vector2();

    private Vector2        presDown       = new Vector2();
    public Stage           stage;
    public Label           viataTurn;
    public Label           fps;
    public Touchpad        mover;
    public Image           weaponCharger;

    public World           world;
    public float           touchPadTimmer = 0;
    private TweenManager   tweeger;
    private boolean        isPressed      = false;


    public Gameplay() {

        gestures = new GestureDetector( this );
        gestures.setLongPressSeconds( 1f );

        tweeger = new TweenManager();
        shape = new ShapeRenderer();
        modelBatch = new ModelBatch();

        shader = new CustShader();
        shader.init();

    }

    public Gameplay init(FileHandle nivel) {
        updWorld = true;

        if ( stage !=null ) {
            for (Actor actor : stage.getActors() )
                actor.clear();
            stage.clear();
        }
        stage = StageUtils.makeGamePlayStage( stage, this );

        if ( world !=null )
            world.dispose();
        world = new World( nivel );

        return this;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glViewport( 0, 0, scrw, scrh );
        Gdx.gl.glClearColor( .1f, .5f, .9f, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );

        tweeger.update( delta );

        if ( touchPadTimmer !=Vars.tPadMinAlpha &&!mover.isTouched() ) {
            touchPadTimmer -= delta;
            if ( touchPadTimmer <Vars.tPadMinAlpha )
                touchPadTimmer = Vars.tPadMinAlpha;
            if ( touchPadTimmer <1 )
                mover.addAction( Actions.alpha( touchPadTimmer ) );
        }

        if ( updWorld )
            updateTheGame( delta );
        else
            gestures.cancel();

        modelBatch.begin( world.getCamera() );
        world.render( modelBatch, world.getEnvironment(), shader );
        modelBatch.end();

        if ( Vars.showDebug &&!Gdx.input.isKeyPressed( Keys.F9 ) ) {
            world.renderDebug( shape );
        }

        fps.setText( "FPS: " +Gdx.graphics.getFramesPerSecond() );

        stage.act();
        stage.draw();
    }

    private Vector3    dolly = new Vector3();
    private Controller cont  = Controllers.getControllers().first();

    private void updateTheGame(float delta) {

        world.getCamera().translate( dolly );
        world.moveCamera( movex, movey );

        if ( world.isInTower() &&cont.getButton( Vars.buton[0] ) ) {
            Tower tower = world.getTower();

            if ( tower.getArma() instanceof FireHold )
                tower.fireNormal( world, world.getCamera().getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) );

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

    private float charge  = -1;


    private long  but0prs = 0;


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        presDown.set( screenX, screenY );
        isPressed = true;

        if ( updWorld &&world.isInTower() &&world.getTower().getArma() instanceof FireCharged &&screenX >scrw /2 ) {
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
            float distance = 150 *Vars.densitate;
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
                world.getTower().fireCharged( world, world.getCamera().getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ), charge );
                return true;
            }

        }

        return false;

    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if ( updWorld &&!weaponCharger.isVisible() &&x <scrw /2 ) {
            float difX = 0, difY = 0;
            difX = deltaX /10 *Vars.modCamSpeedX;
            difY = deltaY /7 *Vars.modCamSpeedY;
            world.moveCamera( difX *Vars.invertDragX, difY *Vars.invertDragY );
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
                Garden_Revolution.game.setScreen( Garden_Revolution.menu );
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
    public boolean buttonDown(Controller controller, int buttonCode) {
        if ( buttonCode ==Vars.buton[0] ) {

            weaponCharger.setColor( Color.CLEAR );
            weaponCharger.setVisible( true );
            charge = 0;


            tmp2.set( stage.screenToStageCoordinates( tmp2.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) ) );
            weaponCharger.setPosition( tmp2.x - ( weaponCharger.getWidth() /2 ), tmp2.y - ( weaponCharger.getHeight() /2 ) );

            but0prs = System.currentTimeMillis();
            return false;
        }

        if ( buttonCode ==Vars.buton[1] ) {
            return false;
        }

        if ( buttonCode ==Vars.buton[2] ) {
            return false;
        }

        if ( buttonCode ==Vars.buton[3] ) {
            return false;
        }

        return false;

    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if ( buttonCode ==Vars.buton[0] ) {
            if ( weaponCharger.isVisible() ) {
                weaponCharger.setVisible( false );
                if ( world.isInTower() &&world.getTower().getArma() instanceof FireCharged )
                    world.getTower().fireCharged( world, world.getCamera().getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ), charge );
            }
        }
        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {

        if ( axisCode ==0 )
            if ( value >0 )
                Vars.invertControlletY = value +0.5f;
            else if ( value <0 )
                Vars.invertControlletY = value -0.5f;


        if ( Math.abs( value ) <Vars.deadZone )
            value = 0f;


        if ( axisCode ==1 )
            if ( Math.abs( value ) <=1 )
                movex = value *Vars.invertControlletX /2;
            else
                movex = 0;

        if ( axisCode ==3 )
            if ( Math.abs( value ) <=1 )
                movex = value *Vars.invertControlletX;
            else
                movex = 0;

        if ( axisCode ==2 )
            if ( Math.abs( value ) <=1 )
                movey = value *Vars.invertControlletY;
            else
                movey = 0;


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
        if ( Gdx.app.getType() ==ApplicationType.Desktop )
            Controllers.removeListener( this );
        Gdx.input.setInputProcessor( null );
    }

    @Override
    public void show() {
        if ( Gdx.app.getType() ==ApplicationType.Desktop )
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