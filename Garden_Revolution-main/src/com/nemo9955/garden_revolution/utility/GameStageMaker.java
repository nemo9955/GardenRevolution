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


public class GameStageMaker {


    private static Board         hudGroup;
    private static ImageButton   hudPauseBut;
    public static ImageButton    hudTowerBut;

    private static Group         tuGroup;
    private static TextButton    tuBackBut1;
    private static TextButton    tuBackBut2;
    private static TextButton    tuBasicBut;
    private static ImageButton   tuMiniGunBut;
    private static ImageButton   tuCannonBut;
    private static CircularGroup tuTowerCircG;

    private static Table         optTab;
    private static ScrollPane    optScrollP;
    private static TextButton    optBackBut;
    private static CheckBox      optAutUpdBut;
    private static CheckBox      optShDebugBut;
    private static CheckBox      optInvMX;
    private static CheckBox      optInvMY;
    private static CheckBox      optInvPX;
    private static CheckBox      optInvPY;

    private static Table         pauseTab;
    private static TextButton    pauseBackBut;
    private static TextButton    pauseOptBut;
    private static TextButton    pauseMMenuBut;

    public static Stage makeGamePlayStage(Stage stage, final Gameplay gp) {
        stage = new Stage( Gdx.graphics.getWidth() *1.5f /Vars.densitate, Gdx.graphics.getHeight() *1.5f /Vars.densitate, true );


        createHUD( stage, gp );

        createIGPause( stage, gp );

        createIGoptions( stage, gp );

        createTowerUpgrade( stage, gp );


        hudGroup.setName( "HUD" );
        tuGroup.setName( "Tower Upgrade" );
        optScrollP.setName( "Optiuni" );
        pauseTab.setName( "Pause" );

        hudPauseBut.setName( "Back" );
        optBackBut.setName( "Back" );
        tuBackBut1.setName( "Back" );
        pauseBackBut.setName( "Back" );

        stage.addActor( hudGroup );
        stage.addActor( tuGroup );
        stage.addActor( optScrollP );
        stage.addActor( pauseTab );
        stage.addActor( gp.fps );


        return stage;
    }

    private static void createHUD(Stage stage, final Gameplay gp) {
        gp.weaponCharger = new Image( GR.skin, "mover-knob" );
        gp.weaponCharger.setVisible( false );

        gp.fps = new Label( "FPS: ", GR.skin );
        gp.fps.setScale( Vars.densitate );
        gp.fps.setPosition( stage.getWidth() /2, stage.getHeight() -gp.fps.getHeight() );

        hudGroup = new Board();
        hudPauseBut = new ImageButton( GR.skin, "IGpause" );
        hudTowerBut = new ImageButton( GR.skin, "towerUpgrade" );
        gp.allyPlacer = new Image( IconType.TINTA.getAsDrawable( GR.skin, 60f, 60f ) );
        gp.allyPlacer.setPosition( stage.getWidth() *0.25f, 0 );

        gp.ready = new TextButton( "Start Wave!", GR.skin );
        gp.ready.setTouchable( Touchable.enabled );
        gp.ready.setPosition( 0, stage.getHeight() /2 );

        // final Image tinta = new Image( skin, "tinta" );
        gp.viataTurn = new Label( "Life ", GR.skin );
        gp.mover = new Touchpad( 1, GR.skin );
        gp.mover.setVisible( true );

        hudTowerBut.setPosition( stage.getWidth() -hudTowerBut.getWidth(), 0 );
        gp.viataTurn.setFontScale( 0.6f );
        gp.viataTurn.setPosition( 10 *Vars.densitate, stage.getHeight() -gp.viataTurn.getHeight() -10 *Vars.densitate );
        hudPauseBut.setPosition( stage.getWidth() -hudPauseBut.getWidth(), stage.getHeight() -hudPauseBut.getHeight() );

        gp.mover.setPosition( stage.getWidth() *0.02f, stage.getWidth() *0.02f );
        gp.mover.addAction( Actions.alpha( Vars.tPadMinAlpha ) );
        // tinta.setPosition( stage.getWidth() /2 -tinta.getWidth() /2, stage.getHeight() /2 -tinta.getHeight() /2 );

        hudGroup.addActor( gp.ready );
        hudGroup.addActor( gp.viataTurn );
        hudGroup.addActor( hudPauseBut );
        // hud.addActor( tinta );
        hudGroup.addActor( gp.mover );
        hudGroup.addActor( hudTowerBut );
        hudGroup.addActor( gp.weaponCharger );
        hudGroup.addActor( gp.allyPlacer );

        hudGroup.addListener( new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( hudPauseBut.isPressed() ) {
                    hudGroup.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseTab.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = false;
                    gp.pointer.setVisible( true );
                    gp.pointer.setSelectedActor( pauseBackBut );
                }
                else if ( hudTowerBut.isPressed() ) {
                    hudGroup.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    tuGroup.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = false;
                    gp.pointer.setVisible( true );
                    // gp.pointer.setSelectedActor( tuMiniGunBut );
                }
                else if ( gp.ready.isPressed() ) {
                    // singleplayer part
                    if ( gp.mp ==null ) {
                        gp.ready.setVisible( false );
                        gp.world.getSgPl().setCanWaveStart( true );
                    }
                    // multyplayer part
                    else {
                        gp.ready.setText( "Waiting for others ..." );
                        gp.ready.setTouchable( Touchable.disabled );

                        gp.mp.sendTCP( msNetGR.IAmReady );

                    }
                    gp.ready.pack();
                }

            }
        } );


        gp.allyPlacer.addListener( new InputListener() {

            private final Vector2 tmp    = new Vector2();
            private final Vector3 onPath = new Vector3();

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

                if ( Func.getStageZon( gp.stage ).contains( event.getStageX(), event.getStageY() ) ) {
                    onPath.y = 0;
                    for (int i = 0 ; i <3 ; i ++ ) {
                        GR.temp4.set( MathUtils.random( -5, 5 ), 0, MathUtils.random( -5, 5 ) );
                        gp.world.getDef().addAlly( GR.temp4.add( onPath ), AllyType.SOLDIER );
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
                Func.intersectLinePlane( gp.player.getCamera().getPickRay( tmp.x, tmp.y ), GR.temp4 );
                gp.world.getWorld().getOnPath( GR.temp4, onPath, 150 );
                gp.allySpawnArea.setPosition( onPath.x, 0.2f, onPath.z );
            }
        } );


        gp.mover.addListener( new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                gp.movex = Vars.invertPadX *gp.mover.getKnobPercentX() *Vars.modCamSpeedX;
                gp.movey = Vars.invertPadY *gp.mover.getKnobPercentY() *Vars.modCamSpeedY;
                gp.touchPadTimmer = Vars.tPadAlphaDellay;
                gp.mover.addAction( Actions.alpha( 1 ) );
            }
        } );


    }

    private static void createTowerUpgrade(Stage stage, final Gameplay gp) {
        tuGroup = new Group();
        tuBackBut1 = new TextButton( "Bk", GR.skin );
        tuBackBut2 = new TextButton( "Bk", GR.skin );
        tuBasicBut = new TextButton( "BASIC", GR.skin );
        tuMiniGunBut = new ImageButton( IconType.SAGETI.getAsDrawable( GR.skin, 70, 70 ) );
        tuCannonBut = new ImageButton( IconType.TUN.getAsDrawable( GR.skin, 70, 70 ) );
        tuTowerCircG = new CircularGroup( gp.shape );

        float freeSpace = 25 *Vars.densitate;

        tuBackBut1.setPosition( 15 *Vars.densitate, stage.getHeight() /2 -tuBackBut1.getHeight() /2 );
        tuBackBut2.setPosition( 15 *Vars.densitate, stage.getHeight() /2 -tuBackBut2.getHeight() /2 );

        tuTowerCircG.setDraggable( true );
        tuTowerCircG.setAsCircle( 300, 70 );
        tuTowerCircG.setPosition( -tuTowerCircG.getRadius() + ( freeSpace *2.1f ) +tuBackBut1.getWidth(), stage.getHeight() /2 );
        // mainUpgrades.setPosition( stage.getWidth() /2, stage.getHeight() /2 );
        float i;
        final float treapta = CircularGroup.aprxOptAngl( tuTowerCircG.getRadius(), tuBasicBut.getHeight() );
        final Rectangle zon = new Rectangle( 0, 0, stage.getWidth(), stage.getHeight() );
        i = tuTowerCircG.minAngleInZon( zon, treapta, 2 );
        tuTowerCircG.setActivInterval( i, 360 -i, true, treapta *1.4f, false );
        // mainUpgrades.setActivInterval( 30, -30, true, 30, false );


        tuTowerCircG.addActor( tuBasicBut );
        tuTowerCircG.addActor( tuMiniGunBut );
        tuTowerCircG.addActor( tuCannonBut );


        tuGroup.addActor( tuBackBut1 );
        tuGroup.addActor( tuTowerCircG );
        tuGroup.addActor( tuBackBut2 );
        tuGroup.setVisible( false );

        tuGroup.addListener( new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( tuBackBut1.isPressed() ||tuBackBut2.isPressed() ) {
                    tuGroup.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hudGroup.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = true;
                    gp.pointer.setVisible( false );
                }
                else if ( tuBasicBut.isPressed() )
                    gp.player.upgradeCurentTower( TowerType.BASIC );
                else if ( tuMiniGunBut.isPressed() )
                    gp.player.changeCurrentWeapon( WeaponType.MINIGUN );
                else if ( tuCannonBut.isPressed() )
                    gp.player.changeCurrentWeapon( WeaponType.CANNON );

            }
        } );
    }

    private static void createIGoptions(Stage stage, final Gameplay gp) {
        optTab = new Table();
        optScrollP = new ScrollPane( optTab, GR.skin );
        optBackBut = new TextButton( "Back", GR.skin, "demon" );
        optAutUpdBut = new CheckBox( "Auto-Update Wave", GR.skin );
        optAutUpdBut.setChecked( Vars.updateUave );
        optShDebugBut = new CheckBox( "Show Debug", GR.skin );
        optShDebugBut.setChecked( Vars.showDebug );


        optInvMX = new CheckBox( "Invert Drag X", GR.skin );
        optInvMY = new CheckBox( "Invert Drag Y", GR.skin );
        optInvPX = new CheckBox( "Invert TouchPad X", GR.skin );
        optInvPY = new CheckBox( "Invert TouchPad Y", GR.skin );

        ChangeListener invButtons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( optInvMX.isPressed() )
                    Vars.invertDragX = (byte) ( optInvMX.isChecked() ? -1 : 1 );
                else if ( optInvMY.isPressed() )
                    Vars.invertDragY = (byte) ( optInvMY.isChecked() ? -1 : 1 );
                else if ( optInvPX.isPressed() )
                    Vars.invertPadX = (byte) ( optInvPX.isChecked() ? -1 : 1 );
                else if ( optInvPY.isPressed() )
                    Vars.invertPadY = (byte) ( optInvPY.isChecked() ? -1 : 1 );
            }
        };

        optInvMX.setChecked( Vars.invertDragX <0 );
        optInvMX.addListener( invButtons );
        optInvMY.setChecked( Vars.invertDragY <0 );
        optInvMY.addListener( invButtons );

        optInvPX.setChecked( Vars.invertPadX <0 );
        optInvPX.addListener( invButtons );
        optInvPY.setChecked( Vars.invertPadY <0 );
        optInvPY.addListener( invButtons );

        optScrollP.setVisible( false );
        optScrollP.setWidget( optTab );
        optScrollP.setBounds( stage.getHeight() *0.1f, stage.getHeight() *0.1f, stage.getWidth() -stage.getHeight() *0.2f, stage.getHeight() *0.8f );

        // optTab.setFillParent( true );
        optTab.defaults().pad( 20 );
        optTab.add( optAutUpdBut ).row();
        optTab.add( optShDebugBut ).row();
        optTab.add( optInvMX ).row();
        optTab.add( optInvMY ).row();
        optTab.add( optInvPX ).row();
        optTab.add( optInvPY ).row();
        optTab.add( optBackBut ).row();


        optScrollP.addListener( new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( optBackBut.isPressed() ) {
                    optScrollP.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseTab.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                }
                else if ( optAutUpdBut.isPressed() ) {
                    Vars.updateUave = optAutUpdBut.isChecked();
                }
                else if ( optShDebugBut.isPressed() ) {
                    Vars.showDebug = optShDebugBut.isChecked();
                }
            }
        } );
    }

    private static void createIGPause(Stage stage, final Gameplay gp) {
        pauseTab = new Table( GR.skin );
        pauseBackBut = new TextButton( "Resume play", GR.skin );
        pauseOptBut = new TextButton( "Options", GR.skin );
        pauseMMenuBut = new TextButton( "Main menu", GR.skin );


        pauseTab.setVisible( false );
        pauseTab.setFillParent( true );
        pauseTab.setBackground( "pix30" );
        pauseTab.add( "PAUSE", "big-green" ).padBottom( stage.getHeight() *0.1f );
        pauseTab.row();
        pauseTab.add( pauseBackBut ).padBottom( stage.getHeight() *0.07f );
        pauseTab.row();
        pauseTab.add( pauseOptBut ).padBottom( stage.getHeight() *0.07f );
        pauseTab.row();
        pauseTab.add( pauseMMenuBut );

        pauseTab.addListener( new ChangeListener() {

            @Override
            public void changed(final ChangeEvent event, final Actor actor) {
                if ( pauseOptBut.isPressed() ) {
                    pauseTab.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    optScrollP.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = false;
                    gp.pointer.setVisible( true );
                    gp.pointer.setSelectedActor( optBackBut );
                }
                else if ( pauseBackBut.isPressed() ) {
                    pauseTab.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hudGroup.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gp.updWorld = true;
                    gp.pointer.setVisible( false );
                }
                else if ( pauseMMenuBut.isPressed() ) {
                    gp.pointer.setVisible( false );
                    hudGroup.setVisible( true );
                    hudGroup.addAction( Actions.alpha( 1 ) );
                    pauseTab.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ), Actions.run( new Runnable() {

                        @Override
                        public void run() {
                            GR.game.setScreen( GR.menu );
                        }
                    } ) ) );
                }
            }
        } );

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
