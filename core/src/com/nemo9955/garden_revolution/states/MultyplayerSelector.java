package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class MultyplayerSelector extends CustomAdapter implements Screen {

    private Stage              stage;
    private TextButton         back;

    private FileHandle         level;


    private static final float rap = 1.3f;
    private Table              table;
    public TextField           ipInput;
    public TextField           portInput;

    private Dialog             dialog;
    private TextButton         start;
    private CheckBox           isHost;
    private CheckBox           isPublic;
    private Label              theIP;


    public MultyplayerSelector() {


        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel( rap /Vars.densitate );
        stage = new Stage( viewport );

        table = new Table( GR.skin );
        back = new TextButton( "Back", GR.skin );
        start = new TextButton( "Start", GR.skin );
        ipInput = new TextField( "", GR.skin ) {

            @Override
            public float getPrefWidth() {
                return 250;
            }

        };
        portInput = new TextField( "", GR.skin ) {

            public float getPrefWidth() {
                return 95;
            };
        };
        isHost = Func.newCheckBox( "Is host", GR.skin );
        isPublic = Func.newCheckBox( "Is public", GR.skin );
        theIP = new Label( Func.getIpAddress(), GR.skin );


        ipInput.setText( "188.173.17.234" );
        portInput.setText( "29955" );

        makeLayout();

        table.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if ( back.isPressed() )
                    GR.game.setScreen( GR.selecter );
                else if ( start.isPressed() ) {
                    try {
                        Vars.TCPport = Integer.parseInt( portInput.getText() );
                        Vars.UDPport = Vars.TCPport +10000;

                        if ( isHost.isChecked() )
                            startAsHost();
                        else
                            startAsClient( ipInput.getMessageText() );
                    }
                    catch (Exception e) {
                        showMessage( e.getMessage() );
                    }

                }
                if ( isHost.isChecked() ) {
                    makeLayout();
                }
                else {
                    makeLayout();
                }


            }

        } );

        stage.addActor( table );

        dialog = new Dialog( "You cannot connect !", GR.skin, "error" ) {

            {
                pad( 50 );
                button( "OK" );
            }

            protected void result(Object object) {
            };
        };

    }

    private void makeLayout() {

        table.clearChildren();

        table.setFillParent( true );
        table.defaults().space( 50 );
        table.add( start ).colspan( 3 ).row();
        table.add( isHost );
        if ( isHost.isChecked() )
            table.add( theIP ).size( 250, theIP.getHeight() );
        else
            table.add( ipInput );
        table.row();
        table.add( isPublic );
        table.add( portInput ).row();
        table.add( back ).colspan( 3 ).row();

        table.invalidateHierarchy();
    }

    public MultyplayerSelector init(FileHandle level) {
        this.level = level;
        return this;

    }

    private void startAsClient(String textIP) {
        GR.gameplay.initAsClient( textIP );
    }

    private void startAsHost() {
        GR.gameplay.initAsHost( level );
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
                GR.game.setScreen( GR.selecter );
                break;
        }
        return false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this ) );
        if ( Func.isControllerUsable() ) {
            Controllers.addListener( this );
        }
        ipInput.setText( "188.173.17.234" );
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {

        if ( buttonIndex ==CoButt.Back.id )
            Func.click( back );

        return false;

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
        if ( Func.isControllerUsable() ) {
            Controllers.removeListener( this );
        }
    }

    public void showMessage(String message) {
        dialog.setTitle( message );
        dialog.show( stage );
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
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
