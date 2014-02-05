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
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireCharged;
import com.nemo9955.garden_revolution.net.GameClient;
import com.nemo9955.garden_revolution.net.Host;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.StageUtils;
import com.nemo9955.garden_revolution.utility.Vars;

public class Gameplay extends CustomAdapter implements Screen {


    public GestureDetector      gestures;
    private ModelBatch          modelBatch;
    public ShapeRenderer        shape;
    public DecalBatch           decalBatch;
    private CameraGroupStrategy camGRStr;

    public float                movex          = 0;
    public float                movey          = 0;
    public boolean              updWorld       = true;
    private final int           scrw           = Gdx.graphics.getWidth();
    public static Vector2       tmp2           = new Vector2();

    private Vector2             presDown       = new Vector2();
    public Stage                stage;
    public Label                viataTurn;
    public Label                fps;
    public Touchpad             mover;
    public Image                weaponCharger;

    public World                world;
    public float                touchPadTimmer = 0;
    private TweenManager        tweeger;

    private Vector3             dolly          = new Vector3();
    public Player               player;

    public Gameplay() {

        gestures = new GestureDetector( this );
        gestures.setLongPressSeconds( 0.5f );

        tweeger = new TweenManager();
        shape = new ShapeRenderer();
        modelBatch = new ModelBatch();

    }

    public FileHandle mapLoc = null;


    public void preInit() {

        if ( stage !=null ) {
            for (Actor actor : stage.getActors() )
                actor.clear();
            stage.clear();
        }
        stage = StageUtils.makeGamePlayStage( stage, this );
    }

    public void postInit(FileHandle nivel) {
        updWorld = true;

        if ( world !=null )
            world.dispose();
        world = new World( nivel );

        player = new Player( world );

        player.getCamera().position.set( world.overview );
        player.getCamera().lookAt( Vector3.Zero );
        player.getCamera().update();


        camGRStr = new CameraGroupStrategy( player.getCamera() );
        decalBatch = new DecalBatch( camGRStr );

    }

    private Dialog dialog = new Dialog( "titlu", Garden_Revolution.manager.get( Assets.SKIN_JSON.path(), Skin.class ) ) {

                              protected void result(Object object) {
                                  // TODO add something that clears all the buttons after one is pressed
                              };
                          };

    public void showMessage(String mesaj) {

        System.out.println( "Output : " +mesaj );

        dialog.setTitle( "Mesaj" );

        dialog.button( mesaj );

        dialog.pad( 50 );

        dialog.invalidate();

        dialog.show( stage );
    }

    private Host       host;
    private GameClient client;

    public Gameplay initAsSinglePlayer(FileHandle nivel) {
        preInit();
        postInit( nivel );
        return this;
    }

    public Gameplay initAsHost(FileHandle nivel) {
        preInit();
        mapLoc = nivel;

        // mapLoc = new FileHandle( nivel.path() );

        postInit( nivel );
        host = new Host( this );


        showMessage( "Created as HOST" );
        return this;
    }

    public Gameplay initAsClient(String ip) {
        preInit();
        client = new GameClient( this );
        client.connect( ip );

        client.getServerMap();

        showMessage( "Created as CLIENT" );
        return this;
    }

    @Override
    public void render(float delta) {

        if ( world ==null )
            return;

        Gdx.gl.glClearColor( .1f, .5f, .9f, 1 );
        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT |GL10.GL_DEPTH_BUFFER_BIT );
        Gdx.gl.glEnable( GL10.GL_DEPTH_TEST );

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

        modelBatch.begin( player.getCamera() );
        world.render( modelBatch, world.getEnvironment(), decalBatch );
        modelBatch.end();
        decalBatch.flush();

        if ( Vars.showDebug &&!Gdx.input.isKeyPressed( Keys.F9 ) )
            world.renderDebug( player.getCamera(), shape );

        fps.setText( "FPS: " +Gdx.graphics.getFramesPerSecond() );

        stage.act();
        stage.draw();
    }


    private void updateTheGame(float delta) {

        if ( player.isInTower() &&weaponCharger.isVisible() &&player.fireChargedTime !=0 ) {
            int time = (int) ( System.currentTimeMillis() -player.fireChargedTime );
            time = MathUtils.clamp( time, 0, 2000 );
            charge = time /2000f;
            weaponCharger.setColor( ( charge !=1 ? 0 : 1 ), 0, 0, charge );

        }

        player.getCamera().translate( dolly );
        if ( movex !=0 ||movey !=0 ||!dolly.isZero() )
            player.moveCamera( movex, movey );

        player.update( delta );

        world.update( delta );
    }

    private float charge = -1;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        presDown.set( screenX, screenY );

        if ( updWorld &&screenX >scrw /2 ) {
            player.setFiringHold( true );
        }
        if ( updWorld &&player.isInTower() &&player.getTower().getArma() instanceof FireCharged &&screenX >scrw /2 ) {
            weaponCharger.setColor( Color.CLEAR );
            weaponCharger.setVisible( true );
            charge = 0;

            tmp2.set( presDown );
            stage.screenToStageCoordinates( tmp2 );
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

        if ( updWorld &&player.isFiringHold() ) {
            player.setFiringHold( false );
        }

        if ( weaponCharger.isVisible() ) {
            weaponCharger.setVisible( false );
            if ( charge >0.01f ) {
                player.fireChargedWeapon( player.getCamera().getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ), charge );
                charge = -1;
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
            player.moveCamera( difX *Vars.invertDragX, difY *Vars.invertDragY );
            return true;
        }
        return false;

    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        return player.tap( x, y, count, button, gestures );

    }

    @Override
    public boolean longPress(float x, float y) {

        return player.longPress( x, y, gestures );

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
            case Keys.H:
                if ( host !=null )
                    host.brodcast( "HOST to all CLIENTS" );
                else
                    client.brodcast( "CLIENT to HOST" );
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

            if ( player.isInTower() &&player.getTower().getArma() instanceof FireCharged ) {
                weaponCharger.setColor( Color.CLEAR );
                weaponCharger.setVisible( true );
                charge = 0;
                player.fireChargedTime = System.currentTimeMillis();

                tmp2.set( stage.screenToStageCoordinates( tmp2.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) ) );
                weaponCharger.setPosition( tmp2.x - ( weaponCharger.getWidth() /2 ), tmp2.y - ( weaponCharger.getHeight() /2 ) );

                return false;
            }
        }


        if ( buttonCode ==Vars.buton[4] ) {
            if ( Functions.isCurrentState( stage, "HUD" ) )
                player.prevTower();
            return false;
        }

        if ( buttonCode ==Vars.buton[5] ) {
            if ( Functions.isCurrentState( stage, "HUD" ) )
                player.nextTower();
            return false;
        }

        if ( buttonCode ==Vars.buton[6] ) {
            Vars.invertControlletX *= -1;
            return false;
        }

        if ( buttonCode ==Vars.buton[7] ) {
            Vars.invertControlletY *= -1;
            return false;
        }

        return false;

    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if ( buttonCode ==Vars.buton[0] ) {
            if ( weaponCharger.isVisible() ) {
                weaponCharger.setVisible( false );
                player.fireChargedWeapon( player.getCamera().getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ), charge );
                player.fireChargedTime = 0;
                charge = -1;
            }
        }
        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        value = MathUtils.clamp( value, -1f, 1f );// in caz ca primeste valori anormale

        if ( axisCode ==Vars.axis[0] )
            Vars.multiplyControlletY = -value +1.5f;

        if ( axisCode ==Vars.axis[1] )
            if ( Math.abs( value ) >=Vars.deadZone )
                Vars.multiplyControlletX = ( Math.abs( value ) +0.5f ) *2;
            else
                Vars.multiplyControlletX = 1;

        if ( Math.abs( value ) <Vars.deadZone )
            value = 0f;

        if ( axisCode ==Vars.axis[1] &&Math.abs( controller.getAxis( Vars.axis[3] ) ) >Vars.deadZone &&controller.getAxis( Vars.axis[3] ) ==MathUtils.clamp( controller.getAxis( Vars.axis[3] ), -1, 1 ) )
            movex = controller.getAxis( Vars.axis[3] ) *Vars.invertControlletX *Vars.multiplyControlletX /2;
        if ( axisCode ==Vars.axis[3] )
            movex = value *Vars.invertControlletX *Vars.multiplyControlletX /2;

        if ( axisCode ==Vars.axis[0] &&Math.abs( controller.getAxis( Vars.axis[2] ) ) >Vars.deadZone &&controller.getAxis( Vars.axis[2] ) ==MathUtils.clamp( controller.getAxis( Vars.axis[2] ), -1, 1 ) )
            movey = controller.getAxis( Vars.axis[2] ) *Vars.invertControlletY *Vars.multiplyControlletY;
        if ( axisCode ==Vars.axis[2] )
            movey = value *Vars.invertControlletY *Vars.multiplyControlletY;


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
        if ( Functions.isControllerUsable() )
            Controllers.removeListener( this );
        Gdx.input.setInputProcessor( null );

        if ( client !=null ) {
            client.stopClient();
            client = null;
        }
        if ( host !=null ) {
            host.stopHost();
            host = null;
        }
    }

    @Override
    public void show() {
        if ( Functions.isControllerUsable() ) {
            Controllers.addListener( this );
        }
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
        camGRStr.dispose();
        decalBatch.dispose();

        if ( client !=null ) {
            client.stopClient();
            client = null;
        }
        if ( host !=null ) {
            host.stopHost();
            host = null;
        }
    }
}