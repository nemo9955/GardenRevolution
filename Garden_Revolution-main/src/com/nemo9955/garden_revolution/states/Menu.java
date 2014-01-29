package com.nemo9955.garden_revolution.states;


import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class Menu extends ControllerAdapter implements Screen {

    private TweenManager       tweeger;
    // private StageActorPointer pointer;
    private Stage              stage;
    private Skin               skin;

    private static final float rap = 1.3f;

    private TextButton         options;
    private ImageButton        play;
    private ImageButton        exit;
    private CheckBox           mode;
    private ImageButton        test;

    public Menu() {

        tweeger = new TweenManager();

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );
        // stage = new Stage();

        options = new TextButton( "Options", skin );
        play = new ImageButton( skin, "start" );
        exit = new ImageButton( skin, "exit" );
        mode = new CheckBox( "EXTERN", skin );
        test = new ImageButton( skin, "test" );

        final Table tab = new Table();
        tab.setFillParent( true );

        ChangeListener asc = new ChangeListener() {


            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( play.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.selecter );

                if ( test.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.test );

                if ( options.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.options );

                if ( exit.isPressed() )
                    Gdx.app.exit();

                if ( mode.isPressed() ) {
                    LevelSelector.internal = mode.isChecked();
                    if ( LevelSelector.internal )
                        mode.setText( "INTERN" );
                    else
                        mode.setText( "EXTERN" );
                }
            }
        };


        tab.addListener( asc );

        tab.defaults().pad( 25 );
        tab.add( play );
        tab.add( mode ).space( 10 );
        tab.row();
        tab.add( test );
        tab.add( options );
        tab.row();
        tab.add( exit );

        stage.addActor( tab );
        // pointer = new StageActorPointer( stage );
        // pointer.setSelectedActor( exit );
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if ( buttonIndex ==Vars.buton[2] )
            Functions.fire( test );

        if ( buttonIndex ==Vars.buton[3] )
            Functions.fire( options );

        if ( buttonIndex ==Vars.buton[4] )
            Functions.fire( play );

        if ( buttonIndex ==Vars.buton[5] )
            Functions.fire( mode );

        return false;

    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor( stage );
        if ( Functions.isControllerUsable() ) {
            Controllers.addListener( this );
        }
        // Gdx.input.setInputProcessor( new InputMultiplexer( stage, new InputAdapter() {
        //
        // Vector2 tmp = new Vector2();
        //
        // @Override
        // public boolean mouseMoved(int screenX, int screenY) {
        // stage.stageToScreenCoordinates( tmp.set( screenX, screenY ) );
        // Actor hit = stage.hit( tmp.x, tmp.y, false );
        // if ( hit !=null ){
        // pointer.setSelectedActor( hit );
        // System.out.println( hit );
        // }
        // return true;
        // }
        //
        // } ) );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        tweeger.update( delta );

        stage.act();
        stage.draw();
        // pointer.draw( delta );
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
        if ( Functions.isControllerUsable() ) {
            Controllers.removeListener( this );
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
