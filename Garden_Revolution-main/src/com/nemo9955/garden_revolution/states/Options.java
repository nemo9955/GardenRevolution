package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class Options extends CustomAdapter implements Screen {

    private Stage              stage;
    private Skin               skin;
    private TextButton         back;

    private TextButton         current;
    private boolean            butSelected = false;
    private String             remName;


    private static final float rap         = 1.3f;


    public Options() {

        skin = GR.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );

        back = new TextButton( "back", (Skin) GR.manager.get( Assets.SKIN_JSON.path() ) );


        // final VerticalGroup name = new VerticalGroup();
        // final VerticalGroup action = new VerticalGroup();
        // final HorizontalGroup holder = new HorizontalGroup();
        final Table table = new Table( skin );

        table.defaults().pad( 20 );


        for (int i = 0 ; i <Vars.noButtons ; i ++ ) {
            TextButton button = new TextButton( "Button " +CoButt.values()[i].id, skin );
            button.setUserObject( "Button" +Vars.stringSeparator +i );


            table.add( new Label( CoButt.values()[i].name, skin ) );
            table.add( button );
            table.row();
        }

        for (int i = 0 ; i <Vars.noAxis ; i ++ ) {
            TextButton axis = new TextButton( "Axis " +CoAxis.values()[i].id, skin );
            axis.setUserObject( "Axis" +Vars.stringSeparator +i );

            table.add( new Label( CoAxis.values()[i].name, skin ) );
            table.add( axis );
            table.row();
        }

        table.add( back ).colspan( 2 );


        final ScrollPane pane = new ScrollPane( table, skin, "clear" );
        pane.setFillParent( true );

        pane.addListener( new InputListener() {

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor actor = stage.hit( x, y, true );
                if ( ! ( actor instanceof TextButton ) &&butSelected ) {
                    current.setText( remName );
                    current.invalidateHierarchy();
                }
                return false;
            }
        } );

        pane.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( back.equals( actor ) )
                    GR.game.setScreen( GR.menu );
                else if ( actor instanceof TextButton ) {
                    if ( butSelected &&current !=null ) {
                        current.setText( remName );
                        current.invalidateHierarchy();
                    }

                    current = (TextButton) actor;
                    butSelected = true;
                    remName = current.getText().toString();

                    if ( current.getUserObject().toString().contains( "Button" ) )
                        current.setText( "Press a button ..." );
                    else
                        current.setText( "Move an axis ..." );

                    current.invalidateHierarchy();
                }
            }
        } );


        stage.addActor( pane );
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {


        if ( butSelected ) {
            String[] parts = current.getUserObject().toString().split( Vars.stringSeparator );

            if ( parts[0].contains( "Button" ) ) {
                CoButt.values()[Integer.parseInt( parts[1] )].id = buttonIndex;
                current.setText( "Button " +buttonIndex );
                current.invalidateHierarchy();
                butSelected = false;
            }
        }
        else if ( buttonIndex ==CoButt.Back.id )
            Func.fire( back );

        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if ( butSelected &&Math.abs( value ) >0.2f ) {
            String[] parts = current.getUserObject().toString().split( Vars.stringSeparator );

            if ( parts[0].contains( "Axis" ) ) {
                CoAxis.values()[Integer.parseInt( parts[1] )].id = axisCode;
                current.setText( "Axis " +axisCode );
                current.invalidateHierarchy();
                butSelected = false;
            }
        }
        return false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        stage.act();
        stage.draw();

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
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

        if ( Func.isControllerUsable() )
            Controllers.addListener( this );
        Gdx.input.setInputProcessor( new InputMultiplexer( this, stage ) );
    }


    @Override
    public void hide() {
        if ( Func.isControllerUsable() )
            Controllers.removeListener( this );
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
