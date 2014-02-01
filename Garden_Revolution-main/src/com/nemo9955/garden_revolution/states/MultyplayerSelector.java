package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class MultyplayerSelector extends ControllerAdapter implements Screen {

    private Stage              stage;
    private Skin               skin;
    private TextButton         back;

    private FileHandle         level;


    private static final float rap = 1.3f;


    public MultyplayerSelector() {

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );

        final Table table = new Table( skin );
        back = new TextButton( "Back", skin );
        final TextButton start = new TextButton( "Start", skin );
        final TextField ipInput = new TextField( "", skin );
        final CheckBox isHost = new CheckBox( "Is host", skin );
        final CheckBox isPublic = new CheckBox( "Is public", skin );
        final Label theIP = new Label( Functions.getIpAddress(), skin );

        theIP.setVisible( false );
        ipInput.setVisible( true );
        ipInput.setMessageText( "188.173.17.234" );
        ipInput.setMaxLength( 30 );
        ipInput.setWidth( 1000 );

        table.setFillParent( true );
        table.defaults().space( 50 );
        table.add( start ).row();
        table.add( isHost );
        table.add( "IP :" );
        table.add( ipInput );
        table.add( theIP ).row();
        table.add( isPublic ).row();
        table.add( back ).row();

        table.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if ( back.isPressed() )
                    Garden_Revolution.game.setScreen( Garden_Revolution.menu );
                else if ( start.isPressed() ) {
                    if ( isHost.isChecked() ) {
                        Garden_Revolution.game.setScreen( Garden_Revolution.gameplay.initAsHost( level ) );
                    }
                    else {
                        Garden_Revolution.game.setScreen( Garden_Revolution.gameplay.initAsClient( level, ipInput.getMessageText() ) );
                    }
                }

                if ( isHost.isChecked() ) {
                    ipInput.setVisible( false );
                    theIP.setVisible( true );
                    table.swapActor( ipInput, theIP );
                }
                else {
                    ipInput.setVisible( true );
                    theIP.setVisible( false );
                    table.swapActor( ipInput, theIP );
                }

            }

        } );

        stage.addActor( table );
    }

    public MultyplayerSelector init(FileHandle level) {
        this.level = level;
        return this;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );


        stage.act();
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( stage );
        if ( Functions.isControllerUsable() ) {
            Controllers.addListener( this );
        }
    }


    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {

        if ( buttonIndex ==Vars.buton[1] )
            Functions.fire( back );

        return false;

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
