package com.nemo9955.garden_revolution.states;


import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.StageActorPointer;
import com.nemo9955.garden_revolution.utility.Vars;


public class Menu implements Screen {

    private StageActorPointer  pointer;
    private TweenManager       tweeger;
    private Stage              stage;
    private Skin               skin;

    private static final float rap = 1.3f;


    public Menu() {

        tweeger = new TweenManager();

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );

        final ImageButton start = new ImageButton( skin, "start" );
        final ImageButton exit = new ImageButton( skin, "exit" );
        final ImageButton test1 = new ImageButton( skin, "test" );

        final CheckBox optiuni = new CheckBox( "EXTERN", skin );

        final Table tab = new Table();
        tab.setFillParent( true );

        ChangeListener asc = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( start.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.selecter );

                if ( test1.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.test );

                if ( exit.isPressed() )
                    Gdx.app.exit();

                if ( optiuni.isPressed() ) {
                    LevelSelector.internal = optiuni.isChecked();
                    if ( LevelSelector.internal )
                        optiuni.setText( "INTERN" );
                    else
                        optiuni.setText( "EXTERN" );
                }
            }
        };


        tab.addListener( asc );

        tab.defaults().pad( 25 );
        tab.add( start );
        tab.add( optiuni ).space( 10 );
        tab.row();
        tab.add( test1 );
        tab.add( exit );

        stage.addActor( tab );
        pointer = new StageActorPointer( stage );
        pointer.setSelectedActor( start );
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( stage );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        tweeger.update( delta );

        stage.act();
        stage.draw();
        pointer.draw( delta );
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
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
