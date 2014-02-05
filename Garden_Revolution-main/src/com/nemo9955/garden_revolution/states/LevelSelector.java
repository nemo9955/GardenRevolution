package com.nemo9955.garden_revolution.states;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class LevelSelector extends ControllerAdapter implements Screen {

    private Stage              stage;
    private Skin               skin;
    private Table              table;
    // private StageActorPointer pointer;

    public final static String levelsLocation = "harti/nivele";
    public static boolean      internal       = false;

    private FileHandle         lvlLoc;
    private String             toAcces;
    private SplitPane          lista;
    private static final float rap            = 1.5f;
    private TextButton         start;
    private List               elem;
    private TextButton         back;

    public LevelSelector() {
        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );
        table = new Table( skin );
        table.setHeight( stage.getHeight() );
        // pointer = new StageActorPointer( stage );
    }

    @Override
    public void show() {
        table.clear();
        stage.clear();
        if ( lista !=null )
            lista.clear();

        // if ( internal )
        // nivelLoc = Gdx.files.internal( "harti/nivele" );
        // else
        // nivelLoc = Gdx.files.local( "harti/nivele" );
        //
        // System.out.println( nivelLoc );
        //
        // if ( Gdx.app.getType() ==ApplicationType.Desktop &&internal )
        // lvlLoc = Gdx.files.internal( "./bin/" +nivelLoc.path() );
        // else if ( Gdx.app.getType() ==ApplicationType.Android &&!internal )
        // lvlLoc = Gdx.files.internal( "./bin/" +nivelLoc.path() );
        // else
        // lvlLoc = nivelLoc;

        // System.out.println( "Intern " +internal );

        if ( internal ) {
            lvlLoc = Gdx.files.internal( levelsLocation +"/" );
        }
        else {
            lvlLoc = Gdx.files.local( levelsLocation );
        }

        // System.out.println( Gdx.files.getLocalStoragePath() +"     " +lvlLoc +"\n" );

        FileHandle nivele[] = lvlLoc.list();
        String harti[] = new String[nivele.length];


        for (int i = 0 ; i <harti.length ; i ++ )
            harti[i] = nivele[i].nameWithoutExtension();
        Arrays.sort( harti );

        start = new TextButton( "Start", skin );
        elem = new List( harti, skin );
        final TextButton multy = new TextButton( "Multiplayer", skin );

        back = new TextButton( "Back", skin );


        table.add( "Select a LEVEL" ).expand().top().row();
        table.add( start ).expand().row();
        table.add( multy ).expand().row();
        table.add( back ).bottom().expand().right();

        lista = new SplitPane( elem, table, false, skin );
        lista.setFillParent( true );
        lista.setMaxSplitAmount( 0.5f );
        lista.setMinSplitAmount( 0.3f );
        lista.setSplitAmount( 0.4f );

        elem.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toAcces = ( (List) actor ).getSelection();
            }
        } );


        toAcces = elem.getSelection();
        table.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( multy.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.multyplayer.init( lvlLoc.child( toAcces +".xml" ) ) );
                else if ( back.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.menu );
                else if ( start.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.gameplay.initAsSinglePlayer( lvlLoc.child( toAcces +".xml" ) ) );
            }
        } );

        stage.addActor( lista );
        // pointer.setSelectedActor( start );

        Gdx.input.setInputProcessor( stage );
        if ( Functions.isControllerUsable() ) {
            Controllers.addListener( this );
        }
    }

    @Override
    public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
        if ( value ==PovDirection.south ) {
            int index = elem.getSelectedIndex() +1;
            if ( index >=elem.getItems().length )
                index = 0;
            elem.setSelectedIndex( index );
        }
        if ( value ==PovDirection.north ) {
            int index = elem.getSelectedIndex() -1;
            if ( index <=0 )
                index = elem.getItems().length -1;
            elem.setSelectedIndex( index );
        }
        return false;

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {

        if ( Vars.buton[0] ==buttonIndex )
            Functions.fire( start );
        if ( Vars.buton[1] ==buttonIndex )
            Functions.fire( back );


        return false;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        stage.act( delta );
        stage.draw();
        // pointer.draw( delta );
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
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
