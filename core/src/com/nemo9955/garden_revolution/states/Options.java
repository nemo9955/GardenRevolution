package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.StageActorPointer;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;


public class Options extends CustomAdapter implements Screen {

    private Stage              stage;
    private TextButton         back;

    private TextButton         current;
    private boolean            butSelected = false;
    private String             remName;
    private StageActorPointer  pointer;


    private static final float rap         = 1.3f;
    private Label              opt;
    private ScrollPane         pane;


    public Options() {

        ScreenViewport viewport = new ScreenViewport();
        viewport.setUnitsPerPixel( rap /Vars.densitate );
        stage = new Stage( viewport );

        back = new TextButton( "back", (Skin) GR.manager.get( Assets.SKIN_JSON.path() ) );
        pointer = new StageActorPointer( stage );

        final Table table = new Table( GR.skin );
        opt = new Label( "Options", GR.skin );


        final Slider contSensX = new Slider( 1, 10, 0.1f, false, GR.skin ) {

            @Override
            public float getPrefWidth() {
                return 300;
            }
        };
        contSensX.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Vars.multiplyControlletX = contSensX.getValue();
                pane.cancel();
            }
        } );


        final Slider contSensY = new Slider( 1, 10, 0.1f, false, GR.skin ) {

            @Override
            public float getPrefWidth() {
                return 300;
            }
        };
        contSensY.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Vars.multiplyControlletY = contSensY.getValue();
                pane.cancel();
            }
        } );

        final CheckBox invMX = new CheckBox( "Invert Drag X", GR.skin );
        final CheckBox invMY = new CheckBox( "Invert Drag Y", GR.skin );
        final CheckBox invPX = new CheckBox( "Invert TouchPad X", GR.skin );
        final CheckBox invPY = new CheckBox( "Invert TouchPad Y", GR.skin );

        ChangeListener invButtons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( invMX.isPressed() )
                    Vars.invertDragX = (byte) ( invMX.isChecked() ? -1 : 1 );
                else if ( invMY.isPressed() )
                    Vars.invertDragY = (byte) ( invMY.isChecked() ? -1 : 1 );
                else if ( invPX.isPressed() )
                    Vars.invertPadX = (byte) ( invPX.isChecked() ? -1 : 1 );
                else if ( invPY.isPressed() )
                    Vars.invertPadY = (byte) ( invPY.isChecked() ? -1 : 1 );
            }
        };

        invMX.setChecked( Vars.invertDragX <0 );
        invMX.addListener( invButtons );
        invMY.setChecked( Vars.invertDragY <0 );
        invMY.addListener( invButtons );

        invPX.setChecked( Vars.invertPadX <0 );
        invPX.addListener( invButtons );
        invPY.setChecked( Vars.invertPadY <0 );
        invPY.addListener( invButtons );

        table.defaults().pad( 20 );
        table.add( opt ).colspan( 2 ).pad( 40 ).row();

        table.add( invMX ).colspan( 2 ).row();
        table.add( invMY ).colspan( 2 ).row();
        table.add( invPX ).colspan( 2 ).row();
        table.add( invPY ).colspan( 2 ).row();


        table.add( "Controller X sensivity" ).colspan( 2 ).row();
        table.add( contSensX ).colspan( 2 ).row();
        table.add( "Controller Y sensivity" ).colspan( 2 ).row();
        table.add( contSensY ).colspan( 2 ).row();

        for (int i = 0 ; i <Vars.noButtons ; i ++ ) {
            TextButton button = new TextButton( "Button " +CoButt.values()[i].id, GR.skin );
            button.setUserObject( "Button" +Vars.stringSeparator +i +Vars.stringSeparator +"Controller" );
            button.getLabel().setTouchable( Touchable.disabled );

            table.add( new Label( CoButt.values()[i].name, GR.skin ) );
            table.add( button );
            table.row();
        }

        for (int i = 0 ; i <Vars.noAxis ; i ++ ) {
            TextButton axis = new TextButton( "Axis " +CoAxis.values()[i].id, GR.skin );
            axis.setUserObject( "Axis" +Vars.stringSeparator +i +Vars.stringSeparator +"Controller" );
            axis.getLabel().setTouchable( Touchable.disabled );

            table.add( new Label( CoAxis.values()[i].name, GR.skin ) );
            table.add( axis );
            table.row();
        }

        table.add( back ).colspan( 2 );


        pane = new ScrollPane( table, GR.skin, "clear" );
        pane.setFillParent( true );


        stage.addListener( new ChangeListener() {


            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if ( back.equals( actor ) )
                    GR.game.setScreen( GR.menu );// FIXME need to remake the conditions in the controller !!!
                if ( actor.getUserObject() !=null &&actor.getUserObject().toString().contains( "Controller" ) ) {
                    if ( butSelected &&current !=null ) {
                        current.setText( remName );
                        current.invalidateHierarchy();
                    }

                    if ( current !=null &&current.equals( actor ) &&butSelected ) {
                        current.setText( "None" );
                        current.invalidateHierarchy();
                        butSelected = false;
                        current = null;
                    }
                    else if ( current ==null ||!current.equals( actor ) ) {
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


            }
        } );

        pane.addListener( new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor actor = stage.hit( x, y, true );
                if ( ! ( actor.getUserObject() !=null &&actor.getUserObject().toString().contains( "Controller" ) ) )
                    if ( butSelected ) {
                        current.setText( remName );
                        current.invalidateHierarchy();
                        butSelected = false;
                        current = null;
                    }

                return false;
            }
        } );

        stage.addActor( pane );
        Func.makePropTouch( stage.getRoot() );
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {

        pointer.updFromController( controller, povCode, value );

        return false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {


        if ( butSelected ) {
            String[] parts = current.getUserObject().toString().split( Vars.stringSeparator );

            if ( parts[0].contains( "Button" ) ) {
                CoButt.values()[Integer.parseInt( parts[1] )].id = buttonIndex;
                current.setText( "Button " +buttonIndex );
                current.invalidateHierarchy();
                current = null;
                butSelected = false;
            }
        }
        else if ( buttonIndex ==CoButt.Fire.id )
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
        if ( butSelected &&Math.abs( value ) >0.2f ) {
            String[] parts = current.getUserObject().toString().split( Vars.stringSeparator );

            if ( parts[0].contains( "Axis" ) ) {
                CoAxis.values()[Integer.parseInt( parts[1] )].id = axisCode;
                current.setText( "Axis " +axisCode );
                current.invalidateHierarchy();
                current = null;
                butSelected = false;
            }
            return false;
        }

        pointer.updFromController( controller, axisCode, value );

        return false;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 0 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );


        // Rectangle zon = Func.getScrShrink( 0.8f, 0.8f );
        // stage.stageToScreenCoordinates( GR.tmp2.set( pointer.getPoint() ) );
        // if ( !zon.contains( GR.tmp2 ) ) {
        // GR.tmp2.sub( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        // // pane.moveBy(- GR.tmp2.x /3, -GR.tmp2.y /3 );
        // pane.setVelocityX( -GR.tmp2.x /3 );
        // pane.setVelocityY( -GR.tmp2.y /3 );
        // pane.f
        // // pointer.movePointer( GR.tmp2.x /3, GR.tmp2.y /3 );
        // System.out.println( GR.tmp2 );
        // }

        stage.act();
        stage.draw();
        pointer.draw();
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
        stage.getViewport().update( width, height, true );
    }

    @Override
    public void show() {

        stage.draw();
        pointer.setSelectedActor( opt );


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
