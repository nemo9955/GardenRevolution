package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;
import com.nemo9955.garden_revolution.states.Gameplay;


public class GameStageMaker {


    private static Board         hudGroup      = new Board();
    private static ImageButton   hudPauseBut   = new ImageButton( GR.skin, "IGpause" );
    public static ImageButton    hudTowerBut   = new ImageButton( GR.skin, "towerUpgrade" );

    private static Group         tuGroup       = new Group();
    private static TextButton    tuBackBut1    = new TextButton( "Bk", GR.skin );
    private static TextButton    tuBackBut2    = new TextButton( "Bk", GR.skin );
    private static TextButton    tuBasicBut    = new TextButton( "BASIC", GR.skin );
    private static ImageButton   tuMiniGunBut  = new ImageButton( IconType.SAGETI.getAsDrawable( GR.skin, 70, 70 ) );
    private static ImageButton   tuCannonBut   = new ImageButton( IconType.TUN.getAsDrawable( GR.skin, 70, 70 ) );
    private static CircularGroup tuTowerCircG;

    private static Table         optTab        = new Table();
    private static ScrollPane    optScrollP    = new ScrollPane( optTab, GR.skin );
    private static TextButton    optBackBut    = new TextButton( "Back", GR.skin, "demon" );
    private static CheckBox      optAutUpdBut  = Func.newCheckBox( "Auto-Update Wave", GR.skin );
    private static CheckBox      optShDebugBut = Func.newCheckBox( "Show Debug", GR.skin );
    private static CheckBox      optInvMX      = Func.newCheckBox( "Invert Drag X", GR.skin );
    private static CheckBox      optInvMY      = Func.newCheckBox( "Invert Drag Y", GR.skin );
    private static CheckBox      optInvPX      = Func.newCheckBox( "Invert TouchPad X", GR.skin );
    private static CheckBox      optInvPY      = Func.newCheckBox( "Invert TouchPad Y", GR.skin );

    private static Table         pauseTab      = new Table( GR.skin );
    private static TextButton    pauseBackBut  = new TextButton( "Resume play", GR.skin );
    private static TextButton    pauseOptBut   = new TextButton( "Options", GR.skin );
    private static TextButton    pauseMMenuBut = new TextButton( "Main menu", GR.skin );

    private static Gameplay      gp;


    public static void makeGamePlayStage(Gameplay gpt) {
        gp = gpt;
        // gp.stage.setViewport( new StretchViewport ( Gdx.graphics.getWidth() *1.5f /Vars.densitate, Gdx.graphics.getHeight() *1.5f /Vars.densitate , gp.player.getCamera()) );
        // gp.stage.setViewport();

        // gp.stage.getViewport().update();

        // clearStageRecurs( gp.stage.getRoot() );
        // gp.stage.clear();

        createHUD();

        createIGPause();

        createIGoptions();

        createTowerUpgrade();


        hudGroup.setName( "HUD" );
        tuGroup.setName( "Tower Upgrade" );
        optScrollP.setName( "Optiuni" );
        pauseTab.setName( "Pause" );

        hudPauseBut.setName( "Back" );
        optBackBut.setName( "Back" );
        tuBackBut1.setName( "Back" );
        pauseBackBut.setName( "Back" );

        gp.stage.addActor( hudGroup );
        gp.stage.addActor( tuGroup );
        gp.stage.addActor( optScrollP );
        gp.stage.addActor( pauseTab );
        gp.stage.addActor( gp.fps );

    }

    public static void restartStage() {

        hudGroup.setVisible( true );
        gp.weaponCharger.setVisible( false );
        gp.ready.setVisible( true );
        gp.ready.setText( "Start Wave!" );

        tuGroup.setVisible( false );

        optScrollP.setVisible( false );

        pauseTab.setVisible( false );
    }


    public static void resizeStage(int width, int height) {
        gp.fps.setPosition( gp.stage.getWidth() /2, gp.stage.getHeight() -gp.fps.getHeight() );
        gp.allyPlacer.setPosition( gp.stage.getWidth() *0.25f, 0 );
        gp.ready.setPosition( 0, gp.stage.getHeight() /2 );
        hudTowerBut.setPosition( gp.stage.getWidth() -hudTowerBut.getWidth(), 0 );
        gp.viataTurn.setFontScale( 0.6f );
        gp.viataTurn.setPosition( 10 *Vars.densitate, gp.stage.getHeight() -gp.viataTurn.getHeight() -10 *Vars.densitate );
        hudPauseBut.setPosition( gp.stage.getWidth() -hudPauseBut.getWidth(), gp.stage.getHeight() -hudPauseBut.getHeight() );
        gp.mover.setPosition( gp.stage.getWidth() *0.02f, gp.stage.getWidth() *0.02f );

        float freeSpace = 25 *Vars.densitate;
        tuTowerCircG.setAsCircle( (int) ( height /2 ), 70 );
        tuTowerCircG.setPosition( -tuTowerCircG.getRadius() + ( freeSpace *2.1f ) +tuBackBut1.getWidth(), gp.stage.getHeight() /2 );

        final float treapta = CircularGroup.aprxOptAngl( tuTowerCircG.getRadius(), tuBasicBut.getHeight() );
        float i = tuTowerCircG.minAngleInZon( Func.getStageZon( gp.stage ), treapta, 2 );
        tuTowerCircG.setActivInterval( i, 360 -i, true, treapta *1.4f, false );

        tuBackBut1.setPosition( 15 *Vars.densitate, gp.stage.getHeight() /2 -tuBackBut1.getHeight() /2 );
        tuBackBut2.setPosition( 15 *Vars.densitate, gp.stage.getHeight() /2 -tuBackBut2.getHeight() /2 );


        optScrollP.setBounds( gp.stage.getHeight() *0.1f, gp.stage.getHeight() *0.1f, gp.stage.getWidth() -gp.stage.getHeight() *0.2f, gp.stage.getHeight() *0.8f );
    }


    private static void createHUD() {

        // gp.fps.setScale( Vars.densitate );

        gp.mover.addAction( Actions.alpha( Vars.tPadMinAlpha ) );

        hudGroup.addActor( gp.ready );
        hudGroup.addActor( gp.viataTurn );
        hudGroup.addActor( hudPauseBut );
        hudGroup.addActor( gp.mover );
        hudGroup.addActor( hudTowerBut );
        hudGroup.addActor( gp.weaponCharger );
        hudGroup.addActor( gp.allyPlacer );

        hudGroup.addListener( hudListener );

        gp.allyPlacer.addListener( allyPlacerListener );

        gp.mover.addListener( moverListener );


    }

    private static InputListener  allyPlacerListener = new InputListener() {

                                                         private final Vector2 tmp = new Vector2();

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
                                                                 gp.onPath.y = 0;
                                                                 for (int i = 0 ; i <3 ; i ++ ) {
                                                                     GR.temp4.set( MathUtils.random( -5, 5 ), 0, MathUtils.random( -5, 5 ) );
                                                                     gp.world.getDef().addAlly( GR.temp4.add( gp.onPath ), AllyType.SOLDIER );
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
                                                             gp.world.getWorld().getOnPath( GR.temp4, gp.onPath, 150 );
                                                             gp.allySpawnArea.setPosition( gp.onPath.x, 0.2f, gp.onPath.z );
                                                         }
                                                     };
    private static ChangeListener moverListener      = new ChangeListener() {

                                                         @Override
                                                         public void changed(final ChangeEvent event, final Actor actor) {
                                                             gp.movex = Vars.invertPadX *gp.mover.getKnobPercentX() *Vars.modCamSpeedX;
                                                             gp.movey = Vars.invertPadY *gp.mover.getKnobPercentY() *Vars.modCamSpeedY;
                                                             gp.touchPadTimmer = Vars.tPadAlphaDellay;
                                                             gp.mover.addAction( Actions.alpha( 1 ) );
                                                         }
                                                     };
    private static ChangeListener hudListener        = new ChangeListener() {

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
                                                                 gp.pointer.setSelectedActor( tuBasicBut );
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
                                                     };

    private static void createTowerUpgrade() {

        tuTowerCircG = new CircularGroup( gp.shape );
        tuTowerCircG.setDraggable( true );

        tuTowerCircG.addActor( tuBasicBut );
        tuTowerCircG.addActor( tuMiniGunBut );
        tuTowerCircG.addActor( tuCannonBut );

        tuGroup.addActor( tuBackBut1 );
        tuGroup.addActor( tuTowerCircG );
        tuGroup.addActor( tuBackBut2 );


        tuGroup.addListener( towerUpListener );
    }

    private static ChangeListener towerUpListener = new ChangeListener() {

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
                                                  };

    private static void createIGoptions() {

        optAutUpdBut.setChecked( Vars.updateUave );
        optShDebugBut.setChecked( Vars.showDebug );

        optInvMX.setChecked( Vars.invertDragX <0 );
        optInvMX.addListener( invButtons );
        optInvMY.setChecked( Vars.invertDragY <0 );
        optInvMY.addListener( invButtons );

        optInvPX.setChecked( Vars.invertPadX <0 );
        optInvPX.addListener( invButtons );
        optInvPY.setChecked( Vars.invertPadY <0 );
        optInvPY.addListener( invButtons );

        optScrollP.setWidget( optTab );

        // optTab.setFillParent( true );
        optTab.defaults().pad( 20 );
        optTab.add( optAutUpdBut ).row();
        optTab.add( optShDebugBut ).row();
        optTab.add( optInvMX ).row();
        optTab.add( optInvMY ).row();
        optTab.add( optInvPX ).row();
        optTab.add( optInvPY ).row();
        optTab.add( optBackBut ).row();


        optScrollP.addListener( optScrollListener );
    }

    private static ChangeListener optScrollListener = new ChangeListener() {

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
                                                    };

    private static ChangeListener invButtons        = new ChangeListener() {

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

    private static void createIGPause() {


        pauseTab.setFillParent( true );
        pauseTab.setBackground( "pix30" );
        pauseTab.add( "PAUSE", "big-green" ).padBottom( gp.stage.getHeight() *0.1f );
        pauseTab.row();
        pauseTab.add( pauseBackBut ).padBottom( gp.stage.getHeight() *0.07f );
        pauseTab.row();
        pauseTab.add( pauseOptBut ).padBottom( gp.stage.getHeight() *0.07f );
        pauseTab.row();
        pauseTab.add( pauseMMenuBut );


        pauseTab.addListener( pauseListener );

    }

    private static ChangeListener pauseListener = new ChangeListener() {

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
                                                };


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
