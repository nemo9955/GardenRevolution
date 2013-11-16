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


public class Meniu implements Screen {

    private TweenManager tweeger;
    private Stage        stage;
    private Skin         skin;


    public Meniu() {

        tweeger = new TweenManager();

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage();

        final ImageButton start = new ImageButton( skin, "start" );
        final ImageButton exit = new ImageButton( skin, "exit" );
        final ImageButton test1 = new ImageButton( skin, "test" );

        final CheckBox optiuni = new CheckBox( "INTERN", skin );

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
            }
        };

        optiuni.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (! optiuni.isChecked() ) {
                    optiuni.setText( "INTERN" );
                    LevelSelector.internal = true;
                }
                else {
                    optiuni.setText( "EXTERN" );
                    LevelSelector.internal = false;
                }
            }

        } );


        start.addListener( asc );
        exit.addListener( asc );
        test1.addListener( asc );
        exit.addListener( asc );

        tab.defaults().pad( 20 );
        tab.add( start ).colspan( 2 );
        tab.row();
        tab.add( optiuni ).colspan( 2 );
        tab.row();
        tab.add( test1 ).expandX();
        // tab.add( test2 ).expandX().left();
        tab.row();
        tab.add( exit ).colspan( 2 );

        stage.addActor( tab );
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
