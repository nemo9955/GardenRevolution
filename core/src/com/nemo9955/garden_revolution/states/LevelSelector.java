package com.nemo9955.garden_revolution.states;

import java.util.Arrays;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.StageActorPointer;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class LevelSelector extends CustomAdapter implements Screen {

    private Stage              stage;
    private Table              table;
    private StageActorPointer  pointer;

    // private FileHandle lvlLoc;
    private String             toAcces;
    private SplitPane          lista;
    private static final float rap = 1.5f;
    private TextButton         start;
    private List<String>       elem;
    private TextButton         back;
    private Image              img;

    public LevelSelector() {
        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel( rap /Vars.densitate );
        stage = new Stage( viewport );

        table = new Table( GR.skin );
        table.setHeight( stage.getHeight() );
        pointer = new StageActorPointer( stage );
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
            case Keys.BACK:
                GR.game.setScreen( GR.menu );
                break;
        }
        return false;
    }

    @Override
    public void show() {
        table.clear();
        stage.clear();
        if ( lista !=null )
            lista.clear();

        String harti[] = null;

        if ( Gdx.app.getType() ==ApplicationType.Desktop ) {
            // System.out.println( Gdx.files.internal( "harti/lista nivele.txt" ).readString() );
            try {
                harti = Gdx.files.classpath( GR.levelsLocation ).readString().split( ".xml\n" );
                // for (String str : harti )
                // System.out.println( str );
            }
            catch (Exception e) {
                harti = Gdx.files.internal( "harti/lista nivele.txt" ).readString().split( "\n" );
                // new String[] { "1aaa level", "a1 nivelul 1" };
            }
        }
        else if ( Gdx.app.getType() ==ApplicationType.Android ) {
            FileHandle[] nivele;
            nivele = Gdx.files.internal( GR.levelsLocation ).list();
            harti = new String[nivele.length];
            for (int i = 0 ; i <harti.length ; i ++ )
                harti[i] = nivele[i].nameWithoutExtension();
        }

        Arrays.sort( harti );

        start = new TextButton( "Start", GR.skin );
        elem = new List<String>( GR.skin );
        elem.setItems( harti );
        final TextButton multy = new TextButton( "Multiplayer", GR.skin );

        back = new TextButton( "Back", GR.skin );


        table.add( "Select a LEVEL" ).expand().top().row();
        table.add( start ).expand().row();
        table.add( multy ).expand().row();
        table.add( back ).bottom().expand().right();

        lista = new SplitPane( elem, table, false, GR.skin );
        lista.setFillParent( true );
        lista.setMaxSplitAmount( 0.5f );
        lista.setMinSplitAmount( 0.3f );
        lista.setSplitAmount( 0.4f );

        elem.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                @SuppressWarnings("unchecked")
                List<String> lst = (List<String>) actor;
                toAcces = lst.getSelected();
            }
        } );


        toAcces = elem.getSelected();
        table.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( multy.isPressed() )
                    GR.game.setScreen( GR.multyplayer.init( Gdx.files.internal( GR.levelsLocation ).child( toAcces +".xml" ) ) );
                else if ( back.isPressed() )
                    GR.game.setScreen( GR.menu );
                else if ( start.isPressed() )
                    GR.game.setScreen( GR.gameplay.initAsSinglePlayer( Gdx.files.internal( GR.levelsLocation ).child( toAcces +".xml" ) ) );
            }
        } );
        img = new Image( GR.bg );
        stage.addActor( img );
        stage.addActor( lista );


        stage.draw();
        pointer.setSelectedActor( start );

        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this ) );
        if ( Func.isControllerUsable() ) {
            Controllers.addListener( this );
        }
    }

    @Override
    public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
        if ( value ==PovDirection.south ) {
            int index = elem.getSelectedIndex() +1;
            if ( index >=elem.getItems().size )
                index = 0;
            elem.setSelectedIndex( index );
        }
        if ( value ==PovDirection.north ) {
            int index = elem.getSelectedIndex() -1;
            if ( index <0 )
                index = elem.getItems().size -1;
            elem.setSelectedIndex( index );
        }
        return false;

    }


    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if ( buttonIndex ==CoButt.Fire.id )
            pointer.fireSelected();

        else if ( buttonIndex ==CoButt.Back.id )
            Func.click( back );

        else if ( buttonIndex ==CoButt.InvX.id )
            Vars.invertControlletX *= -1;
        else if ( buttonIndex ==CoButt.InvY.id )
            Vars.invertControlletY *= -1;

        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        value = MathUtils.clamp( value, -1f, 1f );

        pointer.updFromController( controller, axisCode, value );

        return false;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        stage.act( delta );
        stage.draw();
        pointer.draw();
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
    public void resize(int width, int height) {
        stage.getViewport().update( width, height, true );
        img.setSize( stage.getWidth(), stage.getHeight() );
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
