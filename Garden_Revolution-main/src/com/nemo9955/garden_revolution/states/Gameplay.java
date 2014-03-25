package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.net.GameClient;
import com.nemo9955.garden_revolution.net.Host;
import com.nemo9955.garden_revolution.net.MultiplayerComponent;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.StageActorPointer;
import com.nemo9955.garden_revolution.utility.GameStageMaker;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;

public class Gameplay extends CustomAdapter implements Screen {


    public GestureDetector      gestures;
    private ModelBatch          modelBatch;
    public ShapeRenderer        shape;
    public DecalBatch           decalBatch;
    private CameraGroupStrategy camGRStr;

    public float                movex          = 0;
    public float                movey          = 0;
    public boolean              updWorld       = true;
    private Vector3             dolly          = new Vector3();
    public static Vector2       tmp1           = new Vector2();
    public float                touchPadTimmer = 0;
    private TweenManager        tweeger;

    private Vector2             presDown       = new Vector2();
    public Label                viataTurn;
    public Label                fps;
    public Touchpad             mover;
    public Image                weaponCharger;
    public TextButton           ready;
    public Image                allyPlacer;
    public Stage                stage;

    public StageActorPointer    pointer;
    public boolean              showASA        = false;
    public final Vector3        onPath         = new Vector3();
    public Decal                allySpawnArea  = Decal.newDecal( 20, 20, Garden_Revolution.getMenuTexture( "mover-bg" ), true );

    public MultiplayerComponent mp             = null;

    public WorldWrapper         world          = new WorldWrapper();
    public Player               player;

    public Gameplay() {

        gestures = new GestureDetector( this );
        gestures.setLongPressSeconds( 0.5f );

        tweeger = new TweenManager();
        shape = new ShapeRenderer();
        modelBatch = new ModelBatch();

        allySpawnArea.setRotation( Vector3.Y, Vector3.Y );
        pointer = new StageActorPointer( stage );

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

        if ( updWorld ||mp !=null )
            updateTheGame( delta );
        else
            gestures.cancel();

        modelBatch.begin( player.getCamera() );
        shape.setProjectionMatrix( player.getCamera().combined );
        shape.begin( ShapeType.Line );

        world.getWorld().render( modelBatch, decalBatch );
        if ( showASA )
            decalBatch.add( allySpawnArea );
        if ( Vars.showDebug &&!Gdx.input.isKeyPressed( Keys.F9 ) )
            world.getWorld().renderDebug( player.getCamera(), shape );
        modelBatch.end();
        shape.end();
        decalBatch.flush();

        fps.setText( "FPS: " +Gdx.graphics.getFramesPerSecond() );

        stage.act();
        stage.draw();
        pointer.draw();
    }


    private void updateTheGame(float delta) {

        // if ( player.isInATower() &&weaponCharger.isVisible() &&player.getTower().fireChargedTime !=0 ) {
        // int time = (int) ( System.currentTimeMillis() -player.getTower().fireChargedTime );
        // time = MathUtils.clamp( time, 0, 2000 );
        // player.getTower().charge = time /2000f;
        // weaponCharger.setColor( ( player.getTower().charge !=1 ? 0 : 1 ), 0, 0, player.getTower().charge );
        // }

        player.getCamera().translate( dolly );
        if ( movex !=0 ||movey !=0 ||!dolly.isZero() )
            player.moveCamera( movex, movey );

        player.update( delta );


        if ( showASA &&!Gdx.input.isTouched() ) {
            Func.intersectLinePlane( GR.ray1.set( player.getCamera().position, player.getCamera().direction ), GR.temp4 );
            world.getWorld().getOnPath( GR.temp4, onPath, 150 );
            allySpawnArea.setPosition( onPath.x, 0.2f, onPath.z );
        }

        world.getWorld().update( delta );
    }

    public void preInit() {

        if ( stage !=null ) {
            for (Actor actor : stage.getActors() )
                actor.clear();
            stage.clear();
        }
        stage = GameStageMaker.makeGamePlayStage( stage, this );
        stage.draw();
        pointer.setStage( stage );
        pointer.setVisible( false );
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
        mp = new GameClient( this, ip );

        mp.sendTCP( new StartingServerInfo() );
        ready.setText( "Ready!" );
        return this;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        presDown.set( screenX, screenY );

        if ( !updWorld ) {
        }
        else if ( Func.isAndroid() ||button ==Buttons.LEFT ) {

            if ( player.getTower().isWeaponType( FireType.FIRECHARGED ) ) {
                weaponCharger.setColor( Color.CLEAR );
                weaponCharger.setVisible( true );
                player.getTower().charge = 0;

                tmp1.set( presDown );
                stage.screenToStageCoordinates( tmp1 );
                weaponCharger.setPosition( tmp1.x - ( weaponCharger.getWidth() /2 ), tmp1.y - ( weaponCharger.getHeight() /2 ) );
            }

            if ( player.getTower().isWeaponType( FireType.FIREHOLD ) ) {
                player.getTower().setFiringHold( true );
                if ( Func.isDesktop() )
                    Gdx.input.setCursorCatched( true );
            }

        }
        else {
            if ( Func.isDesktop() &&Gdx.input.isButtonPressed( Buttons.RIGHT ) )
                Gdx.input.setCursorCatched( true );
        }
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if ( weaponCharger.isVisible() ) {
            float distance = 150 *Vars.densitate;
            player.getTower().charge = MathUtils.clamp( presDown.dst2( screenX, screenY ), 0, distance *distance );
            player.getTower().charge /= distance *distance;
            player.getTower().getWeapon().type.updateWeaponTargeting( player.getTower(), false );
            weaponCharger.setColor( ( player.getTower().charge !=1 ? 0 : 1 ), 0, 0, player.getTower().charge );
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if ( updWorld &&player.getTower().isFiringHold )
            player.getTower().setFiringHold( false );

        if ( weaponCharger.isVisible() ) {
            weaponCharger.setVisible( false );
            if ( world.getWorld().getTowerHitByRay( player.getCamera().getPickRay( screenX, screenY ) ) ==null ||player.getTower().charge >0.4f ) {
                player.getTower().getWeapon().type.updateWeaponTargeting( player.getTower(), false );
                world.getDef().fireFromTower( player.getTower() );
                player.getTower().charge = 0;
                return true;
            }
            else {
                player.getTower().charge = 0;
                player.getTower().getWeapon().type.updateWeaponTargeting( player.getTower(), false );
            }
        }

        if ( Func.isDesktop() )
            Gdx.input.setCursorCatched( false );

        return false;
    }


    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if ( updWorld && ( ( !weaponCharger.isVisible() && ( Gdx.input.isButtonPressed( Buttons.RIGHT ) ||Func.isAndroid() ) ) || ( Func.isDesktop() &&Gdx.input.isCursorCatched() ) ) ) {
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
                movey = 2f;
                break;
            case Keys.S:
                movey = -2f;
                break;
            case Keys.D:
                movex = -2f;
                break;
            case Keys.A:
                movex = 2f;
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
            // case Keys.F4:
            // world.getWorld().initEnv();
            // break;
            case Keys.H:
                mp.sendTCP( "a random message" );
                break;
            case Keys.ESCAPE:
            case Keys.BACK:
                GR.game.setScreen( GR.selecter );
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
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        pointer.updateFromController( controller, povCode, value );
        return false;
    }

    @Override
    public boolean buttonDown(Controller cont, int buttonCode) {

        if ( buttonCode ==CoButt.Fire.id ) {

            if ( pointer.isVisible() ) {
                pointer.fireSelected();
            }
            else if ( updWorld &&player.getTower().isWeaponType( FireType.FIREHOLD ) ) {
                player.getTower().setFiringHold( true );
            }
            else if ( updWorld &&player.getTower().isWeaponType( FireType.FIRECHARGED ) ) {
                weaponCharger.setColor( Color.CLEAR );
                weaponCharger.setVisible( true );
                player.getTower().charge = ( -cont.getAxis( CoAxis.mvY.id ) +1 ) /2;
                weaponCharger.setColor( ( player.getTower().charge !=1 ? 0 : 1 ), 0, 0, player.getTower().charge );
                player.getTower().fireChargedTime = System.currentTimeMillis();

                tmp1.set( stage.screenToStageCoordinates( tmp1.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) ) );
                weaponCharger.setPosition( tmp1.x - ( weaponCharger.getWidth() /2 ), tmp1.y - ( weaponCharger.getHeight() /2 ) );

                return false;
            }
        }
        else if ( buttonCode ==CoButt.InvX.id )
            Vars.invertControlletX *= -1;

        else if ( buttonCode ==CoButt.InvY.id )
            Vars.invertControlletY *= -1;

        else if ( buttonCode ==CoButt.PrevT.id &&Func.isCurrentState( stage, "HUD" ) )
            player.prevTower();

        else if ( buttonCode ==CoButt.NextT.id &&Func.isCurrentState( stage, "HUD" ) )
            player.nextTower();

        else if ( buttonCode ==CoButt.TowerUpgr.id &&Func.isCurrentState( stage, "HUD" ) )
            Func.click( GameStageMaker.hudTowerBut );

        else if ( buttonCode ==CoButt.CallAlly.id &&Func.isCurrentState( stage, "HUD" ) ) {
            showASA = true;
        }

        else if ( buttonCode ==CoButt.Back.id ) {
            if ( showASA ) {
                showASA = false;
            }
            else {
                Actor actBkBut = Func.getActorInActiveStage( stage, "Back" );
                if ( actBkBut !=null )
                    Func.click( actBkBut );
            }
        }

        return false;

    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        if ( buttonCode ==CoButt.Fire.id ) {
            if ( updWorld &&player.getTower().isWeaponType( FireType.FIREHOLD ) ) {
                player.getTower().setFiringHold( false );
            }
            if ( weaponCharger.isVisible() ) {
                weaponCharger.setVisible( false );
                // player.getTower().fireWeapon( world.getDef(), charge );
                world.getDef().fireFromTower( player.getTower() );
                player.getTower().fireChargedTime = 0;
                player.getTower().charge = 0;
            }
        }
        else if ( buttonCode ==CoButt.CallAlly.id &&showASA ) {
            showASA = false;

            onPath.y = 0;
            for (int i = 0 ; i <3 ; i ++ ) {
                GR.temp4.set( MathUtils.random( -5, 5 ), 0, MathUtils.random( -5, 5 ) );
                world.getDef().addAlly( GR.temp4.add( onPath ), AllyType.SOLDIER );
            }

        }
        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        value = MathUtils.clamp( value, -1f, 1f );// in case of abnormal values


        if ( pointer.isVisible() )
            pointer.updFromController( controller, axisCode, value );

        else {
            if ( Math.abs( value ) <Vars.deadZone ) {
                value = 0f;
                if ( Math.abs( controller.getAxis( CoAxis.mvX.id ) ) <Vars.deadZone )
                    movex = 0;
                if ( Math.abs( controller.getAxis( CoAxis.mvY.id ) ) <Vars.deadZone )
                    movey = 0;
            }
            else {
                if ( axisCode ==CoAxis.mvX.id )
                    movex = value *Vars.invertControlletX *Vars.multiplyControlletX;
                if ( axisCode ==CoAxis.mvY.id )
                    movey = value *Vars.invertControlletY *Vars.multiplyControlletY;
            }
        }


        return false;

    }

    private Dialog dialog = new Dialog( "titlu", GR.manager.get( Assets.SKIN_JSON.path(), Skin.class ) ) {

                              protected void result(Object object) {
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
        if ( Func.isControllerUsable() )
            Controllers.removeListener( this );
        Gdx.input.setInputProcessor( null );

        if ( mp !=null ) {
            mp.stop();
            mp = null;
        }

    }

    @Override
    public void show() {
        if ( Func.isControllerUsable() ) {
            Controllers.addListener( this );
        }
        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this, gestures ) );
    }

    @Override
    public void dispose() {

        if ( mp !=null ) {
            mp.stop();
            mp = null;
        }
        if ( modelBatch !=null )
            modelBatch.dispose();
        if ( stage !=null )
            stage.dispose();
        if ( world !=null ) {
            world.getDef().reset();
            world.getWorld().dispose();
        }
        if ( shape !=null )
            shape.dispose();
        if ( camGRStr !=null )
            camGRStr.dispose();
        if ( decalBatch !=null )
            decalBatch.dispose();
    }
}