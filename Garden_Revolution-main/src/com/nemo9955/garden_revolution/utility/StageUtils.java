package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;
import com.nemo9955.garden_revolution.states.Gameplay;


public class StageUtils {


    public static Stage makeGamePlayStage(Stage stage, final Gameplay gp) {
        stage = new Stage( Gdx.graphics.getWidth() *1.5f /Vars.densitate, Gdx.graphics.getHeight() *1.5f /Vars.densitate, true );
        final Skin skin = GR.manager.get( Assets.SKIN_JSON.path(), Skin.class );


        gp.weaponCharger = new Image( skin, "mover-knob" );
        gp.weaponCharger.setVisible( false );

        gp.fps = new Label( "FPS: ", skin );
        gp.fps.setScale( Vars.densitate );
        gp.fps.setPosition( stage.getWidth() /2, stage.getHeight() -gp.fps.getHeight() );

        final Board hud = new Board(); // aici e tot ce e legat de HUD ------------------------------------------------------------------------------
        final ImageButton pauseBut = new ImageButton( skin, "IGpause" );
        final ImageButton turnIG = new ImageButton( skin, "towerUpgrade" );
        gp.allyPlacer = new Image( IconType.TINTA.getAsDrawable( skin, 60f, 60f ) );
        gp.allyPlacer.setPosition( stage.getWidth() *0.25f, 0 );

        gp.ready = new TextButton( "Start Wave!", skin );
        gp.ready.setTouchable( Touchable.enabled );
        gp.ready.setPosition( 0, stage.getHeight() /2 );

        final Image tinta = new Image( skin, "tinta" );
        gp.viataTurn = new Label( "Life ", skin );
        gp.mover = new Touchpad( 1, skin );
        gp.mover.setVisible( true );

        turnIG.setPosition( stage.getWidth() -turnIG.getWidth(), 0 );
        gp.viataTurn.setFontScale( 0.6f );
        gp.viataTurn.setPosition( 10 *Vars.densitate, stage.getHeight() -gp.viataTurn.getHeight() -10 *Vars.densitate );
        pauseBut.setPosition( stage.getWidth() -pauseBut.getWidth(), stage.getHeight() -pauseBut.getHeight() );

        gp.mover.setPosition( stage.getWidth() *0.02f, stage.getWidth() *0.02f );
        gp.mover.addAction( Actions.alpha( Vars.tPadMinAlpha ) );
        tinta.setPosition( stage.getWidth() /2 -tinta.getWidth() /2, stage.getHeight() /2 -tinta.getHeight() /2 );

        hud.addActor( gp.ready );
        hud.addActor( gp.viataTurn );
        hud.addActor( pauseBut );
        hud.addActor( tinta );
        hud.addActor( gp.mover );
        hud.addActor( turnIG );
        hud.addActor( gp.weaponCharger );
        hud.addActor( gp.allyPlacer );


        final Table pauseIG = new Table( skin ); // aici e tot ce are legatura cu meniul de pauza --------------------------------------------------------------------
        final TextButton resumeBut = new TextButton( "Resume play", skin );
        final TextButton optBut = new TextButton( "Options", skin );
        final TextButton meniuBut = new TextButton( "Main menu", skin );

        resumeBut.setName( "resume" );
        // pauseIG.setTouchable( Touchable. );

        pauseIG.setVisible( false );
        pauseIG.setFillParent( true );
        pauseIG.setBackground( "pix30" );
        pauseIG.add( "PAUSE", "big-green" ).padBottom( stage.getHeight() *0.1f );
        pauseIG.row();
        pauseIG.add( resumeBut ).padBottom( stage.getHeight() *0.07f );
        pauseIG.row();
        pauseIG.add( optBut ).padBottom( stage.getHeight() *0.07f );
        pauseIG.row();
        pauseIG.add( meniuBut );


        final Table optContinut = new Table();// aici e tot ce tine de optiunile in-game ---------------------------------------------------------------------------------
        final ScrollPane optiuni = new ScrollPane( optContinut, skin );
        final TextButton backBut = new TextButton( "Back", skin, "demon" );
        final CheckBox updWaves = new CheckBox( "Auto-Update Wave", skin );
        updWaves.setChecked( Vars.updateUave );
        final CheckBox debug = new CheckBox( "Show Debug", skin );
        debug.setChecked( Vars.showDebug );

        optiuni.setVisible( false );
        optiuni.setWidget( optContinut );
        optiuni.setBounds( stage.getHeight() *0.1f, stage.getHeight() *0.1f, stage.getWidth() -stage.getHeight() *0.2f, stage.getHeight() *0.8f );

        optContinut.setFillParent( true );
        optContinut.defaults().space( 70 *Vars.densitate );
        optContinut.add( updWaves ).row();
        optContinut.add( debug ).row();
        optContinut.add( backBut ).row();


        final Group upgradeTower = new Group();// aici e tot ce tine de meniul de upgradare a turnurilor ------------------------------------------------------------------
        final TextButton backTowe1 = new TextButton( "Bk", skin );// FIXME repara afiseara butonului
        final TextButton backTowe2 = new TextButton( "Bk", skin );
        final TextButton basicT = new TextButton( "BASIC", skin );
        final ImageButton miniGun = new ImageButton( IconType.SAGETI.getAsDrawable( skin, 70, 70 ) );
        final ImageButton cannon = new ImageButton( IconType.TUN.getAsDrawable( skin, 70, 70 ) );
        final CircularGroup mainUpgrades = new CircularGroup( gp.shape );

        float freeSpace = 25 *Vars.densitate;

        backTowe1.setPosition( 15 *Vars.densitate, stage.getHeight() /2 -backTowe1.getHeight() /2 );
        backTowe2.setPosition( 15 *Vars.densitate, stage.getHeight() /2 -backTowe2.getHeight() /2 );

        mainUpgrades.setDraggable( true );
        mainUpgrades.setAsCircle( 300, 70 );
        mainUpgrades.setPosition( -mainUpgrades.getRadius() + ( freeSpace *2.1f ) +backTowe1.getWidth(), stage.getHeight() /2 );
        // mainUpgrades.setPosition( stage.getWidth() /2, stage.getHeight() /2 );
        float i;
        final float treapta = CircularGroup.aprxOptAngl( mainUpgrades.getRadius(), basicT.getHeight() );
        final Rectangle zon = new Rectangle( 0, 0, stage.getWidth(), stage.getHeight() );
        i = mainUpgrades.minAngleInZon( zon, treapta, 2 );
        mainUpgrades.setActivInterval( i, 360 -i, true, treapta *1.4f, false );
        // mainUpgrades.setActivInterval( 30, -30, true, 30, false );


        mainUpgrades.addActor( basicT );
        mainUpgrades.addActor( miniGun );
        mainUpgrades.addActor( cannon );


        upgradeTower.addActor( backTowe1 );
        upgradeTower.addActor( mainUpgrades );
        upgradeTower.addActor( backTowe2 );
        upgradeTower.setVisible( false );

        // pentru elementele din HUD +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        final ChangeListener hudButons = new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( pauseBut.isPressed() ) {
                    hud.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = false;
                }
                else if ( turnIG.isPressed() ) {
                    hud.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    upgradeTower.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = false;
                }
                else if ( gp.ready.isPressed() ) {
                    // singleplayer part
                    if ( gp.mp ==null ) {
                        gp.ready.setVisible( false );
                        gp.world.getSgPl().setCanWaveStart( true );
                    }
                    // multyplayer part
                    else {
                        gp.ready.setText( Vars.waitingMessage );
                        gp.ready.setTouchable( Touchable.disabled );

                        gp.mp.sendTCP( msNetGR.IAmReady );

                    }
                    gp.ready.pack();
                }

            }
        };

        final Rectangle scrZon = new Rectangle( 5, 5, stage.getWidth() -10, stage.getHeight() -10 );
        gp.allyPlacer.addListener( new InputListener() {

            private final Vector2 tmp   = new Vector2();
            private final Vector3 temp1 = new Vector3();
            private final Vector3 temp2 = new Vector3();

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gp.showASA = true;
                updatePoz( x, y );
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updatePoz( x, y );

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gp.showASA = false;
                gp.allyPlacer.setPosition( gp.stage.getWidth() *0.25f, 0 );

                // tmp.set( x, y );
                // gp.allyPlacer.localToParentCoordinates( tmp );
                // if ( event.getStageX() >0 &&event.getStageX() <gp.stage.getWidth() &&event.getStageY() >0 &&event.getStageY() <gp.stage.getHeight() ) {

                if ( scrZon.contains( event.getStageX(), event.getStageY() ) ) {
                    temp2.y = 0;
                    for (int i = 0 ; i <3 ; i ++ ) {
                        temp1.set( MathUtils.random( -5, 5 ), 0, MathUtils.random( -5, 5 ) );
                        gp.world.getDef().addAlly( temp1.add( temp2 ), AllyType.SOLDIER );
                    }
                }
                // System.out.println( tmp.x +" " +tmp.y );
            }

            private void updatePoz(float x, float y) {
                tmp.set( x, y );
                gp.allyPlacer.localToParentCoordinates( tmp );
                tmp.sub( gp.allyPlacer.getWidth() /2, gp.allyPlacer.getHeight() /2 );
                gp.allyPlacer.setPosition( tmp.x, tmp.y );

                tmp.add( gp.allyPlacer.getWidth() /2, gp.allyPlacer.getHeight() /2 );
                gp.stage.stageToScreenCoordinates( tmp );
                Functions.intersectLinePlane( gp.player.getCamera().getPickRay( tmp.x, tmp.y ), temp1 );
                gp.world.getWorld().getOnPath( temp1, temp2, 150 );
                gp.allySpawnArea.setPosition( temp2.x, 2, temp2.z );
            }
        } );

        // pentru butoanele din pause
        final ChangeListener pauseButons = new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( optBut.isPressed() ) {
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    optiuni.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = false;
                }
                else if ( resumeBut.isPressed() ) {
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hud.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = true;
                }
                else if ( meniuBut.isPressed() ) {
                    hud.setVisible( true );
                    hud.addAction( Actions.alpha( 1 ) );
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ), Actions.run( new Runnable() {

                        @Override
                        public void run() {
                            GR.game.setScreen( GR.menu );
                        }
                    } ) ) );
                }
            }
        };


        // pentru tot ce tine de upgradarea turnurilor
        final ChangeListener turnButons = new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( backTowe1.isPressed() ||backTowe2.isPressed() ) {
                    upgradeTower.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hud.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = true;
                }
                else if ( basicT.isPressed() )
                    gp.player.upgradeCurentTower( TowerType.BASIC );
                else if ( miniGun.isPressed() )
                    gp.player.changeCurrentWeapon( WeaponType.MINIGUN );
                else if ( cannon.isPressed() )
                    gp.player.changeCurrentWeapon( WeaponType.CANNON );

            }
        };


        // pentru elementele din optiuni
        final ChangeListener optButons = new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( backBut.isPressed() ) {
                    optiuni.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                }
                else if ( updWaves.isPressed() ) {
                    Vars.updateUave = updWaves.isChecked();
                }
                else if ( debug.isPressed() ) {
                    Vars.showDebug = debug.isChecked();
                }
            }
        };

        gp.mover.addListener( new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                gp.movex = Vars.invertPadX *gp.mover.getKnobPercentX() *Vars.modCamSpeedX;
                gp.movey = Vars.invertPadY *gp.mover.getKnobPercentY() *Vars.modCamSpeedY;
                gp.touchPadTimmer = Vars.tPadAlphaDellay;
                gp.mover.addAction( Actions.alpha( 1 ) );
            }
        } );

        pauseIG.addListener( pauseButons );

        upgradeTower.addListener( turnButons );
        hud.addListener( hudButons );
        optiuni.addListener( optButons );

        hud.setName( "HUD" );
        upgradeTower.setName( "Tower Upgrade" );
        optiuni.setName( "Optiuni" );
        pauseIG.setName( "Pause" );

        stage.addActor( hud );
        stage.addActor( upgradeTower );
        stage.addActor( optiuni );
        stage.addActor( pauseIG );
        stage.addActor( gp.fps );


        return stage;
    }

    /**
     * https://bitbucket.org/dermetfan/somelibgdxtests/src/28080ff7dd7bd6d000ec8ba7f9514e177bb03e17/SomeLibgdxTests/src/net/dermetfan/someLibgdxTests/screens/TabsLeftTest.java?at=default
     * 
     * @author dermetfan
     * 
     */
    public static class Board extends Group {

        public void pack() {
            float width = Float.NEGATIVE_INFINITY, height = Float.NEGATIVE_INFINITY, childXandWidth, childYandHeight;
            for (final Actor child : getChildren() ) {
                if ( ( childXandWidth = child.getX() +child.getWidth() ) >width )
                    width = childXandWidth;

                if ( ( childYandHeight = child.getY() +child.getHeight() ) >height )
                    height = childYandHeight;
            }

            setSize( width, height );
        }

    }
}
