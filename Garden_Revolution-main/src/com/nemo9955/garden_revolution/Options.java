package com.nemo9955.garden_revolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Vars;


public class Options implements Screen {

    private Stage              stage;
    private Skin               skin;


    private static final float rap = 1.3f;


    public Options() {

        skin = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
        stage = new Stage( Gdx.graphics.getWidth() *rap /Vars.densitate, Gdx.graphics.getHeight() *rap /Vars.densitate, true );

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
//            actButon[i].

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
        holder.setFillParent( true );
        holder.setSpacing( 20 );

        pane.setFillParent( true );
        name.setAlignment( Align.right );
        name.setSpacing( 40 );
        // name.setFillParent( true );
        action.setAlignment( Align.left );
        action.setSpacing( 40 );
        // action.setFillParent( true );

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
