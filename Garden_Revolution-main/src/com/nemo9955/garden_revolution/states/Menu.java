package com.nemo9955.garden_revolution.states;


import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.StageActorPointer;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class Menu extends CustomAdapter implements Screen {

    private TweenManager       tweeger;
    private StageActorPointer  pointer;
    private Stage              stage;
    private Skin               skin;

    private static final float rap = 1.3f;
    private ImageButton        play;

    // private ImageTextButton options;
    // private ImageButton play;
    // private ImageButton exit;
    // private CheckBox mode;
    // private ImageButton test;

    public Menu() {

        tweeger = new TweenManager();

        skin = GR.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );
        // stage = new Stage();

        final ImageTextButton options = new ImageTextButton( "Options", skin );
        options.getImage().setTouchable( Touchable.disabled );
        final TextButton sdr = new TextButton( "Shader", skin );
        play = new ImageButton( skin, "start" );
        final ImageButton exit = new ImageButton( skin, "exit" );
        final ImageButton test = new ImageButton( skin, "test" );


        // mode = new CheckBox( "Use intern", skin );

        // mode.setChecked( LevelSelector.internal );

        final Table tab = new Table( skin );
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

        tab.defaults().pad( 25 );
        tab.add( play );
        tab.add( sdr );
        tab.row();
        tab.add( test );
        tab.add( options );
        tab.row();
        tab.add( exit );
        tab.add( " " );

        stage.addActor( tab );

        pointer = new StageActorPointer( stage );

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
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if ( buttonIndex ==CoButt.Fire.id )
            pointer.fireSelected();
        else if ( buttonIndex ==CoButt.InvX.id )
            Vars.invertControlletX *= -1;
        else if ( buttonIndex ==CoButt.InvY.id )
            Vars.invertControlletY *= -1;
        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        value = MathUtils.clamp( value, -1f, 1f );

        if ( Math.abs( value ) <Vars.deadZone ) {
            value = 0f;
            if ( Math.abs( controller.getAxis( CoAxis.mvX.id ) ) <Vars.deadZone )
                pointer.mvx = 0;
            if ( Math.abs( controller.getAxis( CoAxis.mvY.id ) ) <Vars.deadZone )
                pointer.mvy = 0;
        }
        else {
            if ( axisCode ==CoAxis.mvX.id )
                pointer.mvx = value *Vars.invertControlletX;
            if ( axisCode ==CoAxis.mvY.id )
                pointer.mvy = value *Vars.invertControlletY;
        }

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
