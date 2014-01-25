package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class Options extends ControllerAdapter implements Screen {

    private Stage              stage;
    private Skin               skin;
    private TextButton         back;


    private static final float rap = 1.3f;


    public Options() {

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );

        back = new TextButton( "back", (Skin) Garden_Revolution.manager.get( Assets.SKIN_JSON.path() ) );
        back.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Garden_Revolution.game.setScreen( Garden_Revolution.menu );
            }

        } );

        final VerticalGroup name = new VerticalGroup();
        final VerticalGroup action = new VerticalGroup();
        final HorizontalGroup holder = new HorizontalGroup();
        final ScrollPane pane = new ScrollPane( holder, skin, "clear" );


        Label numeButon[] = new Label[Vars.noButtons];
        TextField actButon[] = new TextField[Vars.noButtons];
        Label numeAxis[] = new Label[Vars.noAxis];
        TextField actAxis[] = new TextField[Vars.noAxis];

        for (int i = 0 ; i <Vars.noButtons ; i ++ ) {
            numeButon[i] = new Label( Vars.butonName[i], skin );
            actButon[i] = new TextField( "Button " + ( i +1 ), skin );
            // actButon[i].

            name.addActor( numeButon[i] );
            action.addActor( actButon[i] );
        }

        for (int i = 0 ; i <Vars.noAxis ; i ++ ) {
            numeAxis[i] = new Label( Vars.axisName[i], skin );
            actAxis[i] = new TextField( "Axa " + ( i +1 ), skin );

            name.addActor( numeAxis[i] );
            action.addActor( actAxis[i] );
        }

        holder.addActor( name );
        holder.addActor( action );
        holder.addActor( back );
        holder.setFillParent( true );
        holder.setSpacing( 20 );

        // pane.setFillParent( true );

        name.setAlignment( Align.right );
        name.setSpacing( 40 );
        // name.setFillParent( true );
        // name.pack();
        action.setAlignment( Align.left );
        action.setSpacing( 40 );
        // action.setFillParent( true );

        // pane.pack();
        pane.setHeight( stage.getHeight() );
        pane.setWidth( stage.getWidth() /2 );
        pane.setX( stage.getWidth() /2 -pane.getWidth() /2 );


        stage.addActor( pane );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );


        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
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
    public void dispose() {
        stage.dispose();
    }

}
