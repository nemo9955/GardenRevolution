package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
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


public class LevelSelector implements Screen {

    private Stage stage;
    private Skin  skin;
    private Table table;

    public LevelSelector() {
        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage();
        table = new Table( skin );
        table.setFillParent( true );

    }

    @Override
    public void show() {
        table.clear();
        stage.clear();

        FileHandle lvlLoc;
        if ( Gdx.app.getType() ==ApplicationType.Android ) {
            lvlLoc = Garden_Revolution.maploc.child( "nivele" );
        }
        else {
            lvlLoc = Gdx.files.internal( String.format( "./bin/%s", Garden_Revolution.maploc.child( "nivele" ).path() ) );
        }

        System.out.println(lvlLoc);

        FileHandle nivele[] = lvlLoc.list();

        String harti[] = new String[nivele.length];

        for (int i = 0 ; i <harti.length ; i ++ ) {
            harti[i] = nivele[i].name();
            System.out.println( nivele[i] );
        }

        List elem = new List( harti, skin );
        ScrollPane lista = new ScrollPane( elem, skin, "default" );

        TextButton play = new TextButton( "Play", skin );
        play.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Garden_Revolution.game.setScreen( Garden_Revolution.gameplay );
            }
        } );

        TextButton back = new TextButton( "Back", skin );
        back.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
            }
        } );

        table.add( lista ).expand().left().fillY();
        table.add( "Select a LEVEL" ).expand().top();
        table.add( play );
        table.add( back ).expand().bottom().right();

        stage.addActor( table );

        Gdx.input.setInputProcessor( stage );
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
