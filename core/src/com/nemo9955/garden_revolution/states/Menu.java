package com.nemo9955.garden_revolution.states;


import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.StageActorPointer;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class Menu extends CustomAdapter implements Screen {

    private TweenManager       tweeger;
    private StageActorPointer  pointer;
    private Stage              stage;

    private static final float rap = 1.3f;
    private TextButton         play;
    private Image              img;

    // private ImageTextButton options;
    // private ImageButton play;
    // private ImageButton exit;
    // private CheckBox mode;
    // private ImageButton test;


    public Menu() {

        tweeger = new TweenManager();

        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel( rap /Vars.densitate );
        stage = new Stage( viewport );

        final TextButton options = new TextButton( "Options", GR.skin, "earthDemon" );
        final TextButton sdr = new TextButton( "Shader", GR.skin, "earthDemon" );
        /*-------------*/play = new TextButton( "Play", GR.skin, "earthDemon" );
        final TextButton exit = new TextButton( "Exit", GR.skin, "earthDemon" );
        final TextButton test = new TextButton( "Credits", GR.skin, "earthDemon" );


        final Table tab = new Table( GR.skin );
        tab.setFillParent( true );

        ChangeListener asc = new ChangeListener() {


            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( play.isPressed() )
                    GR.game.setScreen( GR.selecter );

                if ( test.isPressed() )
                    GR.game.setScreen( GR.test );

                if ( options.isPressed() )
                    GR.game.setScreen( GR.options );

                if ( exit.isPressed() )
                    Gdx.app.exit();

                if ( sdr.isPressed() &&Func.isDesktop() )
                    GR.game.setScreen( new TestSceneShader() );

                // if ( mode.isPressed() ) {
                // LevelSelector.internal = mode.isChecked();
                // }
            }
        };


        tab.addListener( asc );

        tab.defaults().pad( 20 );
        tab.add( play ).bottom().colspan( 2 ).expand();
        tab.row();
        tab.add( sdr ).right();
        tab.add( options ).left();
        tab.row();
        tab.add( exit ).expand().align( Align.left |Align.bottom );
        tab.add( test ).expand().align( Align.right |Align.bottom );

        img = new Image( Garden_Revolution.getBG() );
        stage.addActor( img );
        stage.addActor( tab );

        pointer = new StageActorPointer( stage );

        Func.makePropTouch( stage.getRoot() );

    }

    @Override
    public void show() {

        stage.draw();
        pointer.setSelectedActor( play );

        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this ) );
        if ( Func.isControllerUsable() )
            Controllers.addListener( this );

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        tweeger.update( delta );

        stage.act();
        stage.draw();
        pointer.draw();
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        pointer.updFromController( controller, povCode, value );
        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if ( buttonIndex ==CoButt.Fire.id )
            pointer.fireSelected();
        else if ( buttonIndex ==CoButt.InvX.id )
            Vars.invertControllerX *= -1;
        else if ( buttonIndex ==CoButt.InvY.id )
            Vars.invertControllerY *= -1;
        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        pointer.updFromController( controller, axisCode, value );
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
            case Keys.BACK:
                Gdx.app.exit();
                break;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
        img.setSize( stage.getWidth(), stage.getHeight() );
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
        if ( Func.isControllerUsable() ) {
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
