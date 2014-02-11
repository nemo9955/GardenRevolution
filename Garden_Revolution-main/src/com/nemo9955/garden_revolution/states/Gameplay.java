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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.net.GameClient;
import com.nemo9955.garden_revolution.net.Host;
import com.nemo9955.garden_revolution.net.MultiplayerComponent;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
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
    private Vector3             dolly          = new Vector3();
    public static Vector2       tmp2           = new Vector2();
    public float                touchPadTimmer = 0;
    private TweenManager        tweeger;

    private Vector2             presDown       = new Vector2();
    public Stage                stage;
    public Label                viataTurn;
    public Label                fps;
    public Touchpad             mover;
    public Image                weaponCharger;
    public TextButton           ready;
    private float               charge         = -1;

    public MultiplayerComponent mp             = null;

    public WorldWrapper         world          = new WorldWrapper();
    public Player               player;

    public Gameplay() {

        gestures = new GestureDetector( this );
        gestures.setLongPressSeconds( 0.5f );

        tweeger = new TweenManager();
        shape = new ShapeRenderer();
        modelBatch = new ModelBatch();

    }

    @Override
    public void render(float delta) {

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
        world.getWorld().render( modelBatch, world.getWorld().getEnvironment(), decalBatch );
        modelBatch.end();
        decalBatch.flush();

        if ( Vars.showDebug &&!Gdx.input.isKeyPressed( Keys.F9 ) )
            world.getWorld().renderDebug( player.getCamera(), shape );

        fps.setText( "FPS: " +Gdx.graphics.getFramesPerSecond() );

        stage.act();
        stage.draw();
    }


    private void updateTheGame(float delta) {

        if ( player.isInTower() &&weaponCharger.isVisible() &&player.getTower().fireChargedTime !=0 ) {
            int time = (int) ( System.currentTimeMillis() -player.getTower().fireChargedTime );
            time = MathUtils.clamp( time, 0, 2000 );
            charge = time /2000f;
            weaponCharger.setColor( ( charge !=1 ? 0 : 1 ), 0, 0, charge );

        }

        player.getCamera().translate( dolly );
        if ( movex !=0 ||movey !=0 ||!dolly.isZero() )
            player.moveCamera( movex, movey );

        player.update( delta );

        world.getWorld().update( delta );
    }

    public void preInit() {

        if ( stage !=null ) {
            for (Actor actor : stage.getActors() )
                actor.clear();
            stage.clear();
        }
        stage = StageUtils.makeGamePlayStage( stage, this );
    }

    public void postInit(WorldWrapper newWorld) {
        updWorld = true;

        world = newWorld;

        player = new Player( world );

        // player.getCamera().position.set( world.getWorld().getOverview() );
        player.setTower( world.getWorld().getTowers()[0] );
        player.getCamera().lookAt( Vector3.Zero );
        player.getCamera().update();


        camGRStr = new CameraGroupStrategy( player.getCamera() );
        decalBatch = new DecalBatch( camGRStr );

    }

    public Gameplay initAsSinglePlayer(FileHandle nivel) {
        Gdx.graphics.setTitle( GR.TITLU +" " +GR.VERSIUNE );
        preInit();
        postInit( world.init( nivel ) );
        return this;
    }

    public Gameplay initAsHost(FileHandle nivel) {
        Gdx.graphics.setTitle( "[H] " +GR.TITLU +" " +GR.VERSIUNE );
        preInit();

        mp = new Host( this );
        postInit( world.init( nivel, mp ) );


        ready.setText( "Ready!" );
        // showMessage( "Created as HOST" );
        return this;
    }

    public Gameplay initAsClient(String ip) {
        Gdx.graphics.setTitle( "[C] " +GR.TITLU +" " +GR.VERSIUNE );
        preInit();
        preInit();
        mp = new GameClient( this, ip );

        mp.sendTCP( new StartingServerInfo() );
        ready.setText( "Ready!" );
        return this;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        presDown.set( screenX, screenY );

        if ( player.isInTower() &&updWorld &&screenX >scrw /2 ) {
            player.getTower().setFiringHold( true );
        }
        if ( updWorld &&player.isInTower() &&player.getTower().isWeaponType( FireType.FIRECHARGED ) &&screenX >scrw /2 ) {
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

        if ( updWorld &&player.isInTower() &&player.getTower().isFiringHold ) {
            player.getTower().setFiringHold( false );
        }

        if ( weaponCharger.isVisible() ) {
            weaponCharger.setVisible( false );
            if ( charge >0.01f ) {
                // player.getTower().fireWeapon( world.getDef(), charge );
                world.getDef().fireFromTower( player.getTower(), charge );
                charge = -1;
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if ( ( updWorld &&!weaponCharger.isVisible() &&x <scrw /2 ) || ( Gdx.app.getType() ==ApplicationType.Desktop &&Gdx.input.isCursorCatched() ) ) {
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
                GR.game.setScreen( GR.menu );
                break;
            case Keys.H:
                mp.sendTCP( "a random message" );
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

            if ( player.isInTower() &&player.getTower().isWeaponType( FireType.FIRECHARGED ) ) {
                weaponCharger.setColor( Color.CLEAR );
                weaponCharger.setVisible( true );
                charge = 0;
                player.getTower().fireChargedTime = System.currentTimeMillis();

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
            if ( player.isInTower() &&weaponCharger.isVisible() ) {
                weaponCharger.setVisible( false );
                // player.getTower().fireWeapon( world.getDef(), charge );
                world.getDef().fireFromTower( player.getTower(), charge );
                player.getTower().fireChargedTime = 0;
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

    private Dialog dialog = new Dialog( "titlu", GR.manager.get( Assets.SKIN_JSON.path(), Skin.class ) ) {

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

        if ( mp !=null )
            mp.stop();

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

        if ( mp !=null )
            mp.stop();

        if ( modelBatch !=null )
            modelBatch.dispose();
        if ( stage !=null )
            stage.dispose();
        if ( world !=null )
            world.getDef().reset();
        if ( shape !=null )
            shape.dispose();
        if ( camGRStr !=null )
            camGRStr.dispose();
        if ( decalBatch !=null )
            decalBatch.dispose();
    }
}