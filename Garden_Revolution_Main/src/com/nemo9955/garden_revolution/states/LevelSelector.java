package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public class LevelSelector extends InputAdapter implements Screen {

    private Stage stage;
    private Skin  skin;
    private Table table;

    public LevelSelector() {
        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage();
        table = new Table( skin );
        table.setFillParent( true );


        String harti[] = new String[] { "e4sgre324", "2sg34sg23", "23dfsg4" };

        List elem = new List( harti, skin );
        ScrollPane lista = new ScrollPane( elem, skin, "default" );

        TextButton play = new TextButton( "Play", skin );
        play.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO selecteaza nivelul pe baza folderului
            }
        } );

        TextButton back = new TextButton( "Menu", skin );
        back.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
            }
        } );

        table.add( lista ).expandY();
        table.add( "Select a LEVEL" );
        table.row();
        table.add( back );


        stage.addActor( table );
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( this );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        stage.act( delta );
        stage.draw();
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
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
