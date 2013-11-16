package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.states.Gameplay;


public class StageUtils {


    public static Stage makeGamePlayStage(Stage stage, Skin skin, final Gameplay gameplay) {
        stage = new Stage();

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        final Touchpad mover = new Touchpad( 5, skin );
        final ImageButton pauseBut = new ImageButton( skin, "IGpause" );
        final ImageButton camLeft = new ImageButton( skin, "camLeft" );
        final ImageButton camRight = new ImageButton( skin, "camRight" );
        final TextButton backBut = new TextButton( "Back", skin, "demon" );
        final TextButton resumeBut = new TextButton( "Resume play", skin );
        final TextButton meniuBut = new TextButton( "Main menu", skin );

        final TextButton optBut = new TextButton( "Options", skin );

        final Board optFill = new Board();
        final Board hud = new Board();
        final ScrollPane optIG = new ScrollPane( optFill, skin );
        final Table pauseIG = new Table( skin );


        pauseIG.setVisible( false );
        pauseIG.addAction( Actions.alpha( 0 ) );
        pauseIG.setBackground( "pix30" );
        pauseIG.setFillParent( true );
        // pauseIG.debug();
        pauseIG.add( "PAUSE", "big-green" ).padBottom( stage.getHeight() *0.1f );
        pauseIG.row();
        pauseIG.add( resumeBut ).padBottom( stage.getHeight() *0.07f );
        pauseIG.row();
        pauseIG.add( optBut ).padBottom( stage.getHeight() *0.07f );
        pauseIG.row();
        pauseIG.add( meniuBut );


        optIG.addAction( Actions.alpha( 0 ) );
        optIG.setWidget( optFill );
        optIG.setVisible( false );
        optIG.setBounds( 100, 50, stage.getWidth() -200, stage.getHeight() -100 );
        backBut.setPosition( 50, 50 );
        optFill.addActor( backBut );

        Image tinta = new Image( skin, "tinta" );
        tinta.setX( stage.getWidth() -tinta.getWidth() /2 );
        tinta.setY( stage.getHeight() -tinta.getHeight() /2 );

        // hud.add();
        // hud.add( pauseBut ).top().right();
        // hud.row();
        // hud.add( camLeft ).expand().left();
        // hud.add( tinta );
        // hud.add( camRight ).expand().right();
        // hud.row();
        // hud.add( mover ).bottom().left().padLeft( stage.getWidth() *0.03f ).padBottom( stage.getWidth() *0.03f );

        pauseBut.setPosition( stage.getWidth() -pauseBut.getWidth(), stage.getHeight() -pauseBut.getHeight() );
        camLeft.setPosition( 0, stage.getHeight()/2 -camLeft.getHeight() /2 );
        camRight.setPosition( stage.getWidth() -camRight.getWidth() , stage.getHeight()/2 -camRight.getHeight() /2);
        tinta.setPosition( stage.getWidth() /2 -tinta.getWidth() /2, stage.getHeight() /2 -tinta.getHeight() /2 );
        mover.setPosition( stage.getWidth() *0.03f, stage.getWidth() *0.03f );

        hud.addActor( pauseBut );
        hud.addActor( camLeft );
        hud.addActor( camRight );
        hud.addActor( tinta );
        hud.addActor( mover );

        ChangeListener hudButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( pauseBut.isPressed() ) {
                    hud.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseIG.addAction( Actions.sequence( Actions.visible( true ), Actions.alpha( 0 ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gameplay.toUpdate = 1;
                }
            }
        };

        ChangeListener optButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( backBut.isPressed() ) {
                    optIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                }
            }
        };


        ChangeListener camBut = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( camLeft.isPressed() ) {
                    gameplay.world.prevCamera();
                }
                else if ( camRight.isPressed() ) {
                    gameplay.world.nextCamera();
                }
            }
        };

        ChangeListener pauseButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( optBut.isPressed() ) {
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    optIG.addAction( Actions.sequence( Actions.visible( true ), Actions.alpha( 0 ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gameplay.toUpdate = 1;
                }
                else if ( resumeBut.isPressed() ) {
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hud.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    gameplay.toUpdate = 0;
                }
                else if ( meniuBut.isPressed() ) {
                    hud.setVisible( true );
                    hud.addAction( Actions.alpha( 1 ) );
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ), Actions.run( new Runnable() {

                        @Override
                        public void run() {
                            Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
                        }
                    } ) ) );
                }
            }
        };

        mover.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameplay.movex = Mod.invertPadX *mover.getKnobPercentX() *Mod.modCamSpeedX;
                gameplay.movey = Mod.invertPadY *mover.getKnobPercentY() *Mod.modCamSpeedY;
            }
        } );

        camRight.addListener( camBut );
        camLeft.addListener( camBut );

        meniuBut.addListener( pauseButons );
        resumeBut.addListener( pauseButons );
        optBut.addListener( pauseButons );
        pauseBut.addListener( hudButons );
        backBut.addListener( optButons );
        optFill.pack();
        stage.addActor( hud );
        stage.addActor( optIG );
        stage.addActor( pauseIG );

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
            for (Actor child : getChildren() ) {
                if ( ( childXandWidth = child.getX() +child.getWidth() ) >width )
                    width = childXandWidth;

                if ( ( childYandHeight = child.getY() +child.getHeight() ) >height )
                    height = childYandHeight;
            }

            setSize( width, height );
        }

    }
}
