package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.Buton;

public class Gameplay implements Screen, InputProcessor {


    private PerspectiveCamera    cam;
    private ModelBatch           modelBatch;
    private Array<ModelInstance> instances    = new Array<ModelInstance>();
    private Lights               lights;
    private ModelInstance        cer;
    private final int            scrw, scrh;

    public int                   rotateButton = Buttons.LEFT;
    private float                startX, startY;
    private boolean              moveByTouch  = true;
    private float                movex        = 0, movey = 0;
    private final Vector3        tmpV1        = new Vector3();
    public Vector3               target       = new Vector3( 0, 15, 0 );
    private float                deltaX       = 0;
    private float                deltaY       = 0;

    private short                toUpdate     = 0;
    private TweenManager         tweeger;
    private Stage                stage;
    private Skin                 skin;


    public Gameplay() {
        scrw = Gdx.graphics.getWidth();
        scrh = Gdx.graphics.getHeight();
        float amb = 0.4f, lum = 0.6f;
        tweeger = new TweenManager();

        modelBatch = new ModelBatch();
        lights = new Lights();
        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 5f ) );

        cam = new PerspectiveCamera( 67, scrw, scrh );
        cam.position.set( 0f, 15f, 0f );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();


    }

    private void makeStage() {
        stage = new Stage();
        Garden_Revolution.multiplexer.addProcessor( stage );

        skin = new Skin( Gdx.files.internal( Assets.LOC.ELEMENT.getLink() +"mainJSkins.json" ), (TextureAtlas) Garden_Revolution.manager.get( Assets.ELEMENTS_PACK.path() ) );
        final Table hud = new Table();
        final Touchpad mover = new Touchpad( 5, skin );

        final ImageButton optBut = new ImageButton( skin, "IGoptiuni" );
        final TextButton backBut = new TextButton( "Back", skin, "demon" );

        Board optFill = new Board();
        final ScrollPane optIG = new ScrollPane( optFill, skin );

        optIG.setWidget( optFill );
        optIG.setVisible( false );
        optIG.setBounds( 100, 50, stage.getWidth() -200, stage.getHeight() -100 );

        hud.debug();
        hud.setFillParent( true );
        hud.add( optBut ).expand().top().left();
        hud.row();
        hud.row();
        hud.add( mover ).expand().bottom().left();
        hud.add().expand();
        hud.add().expand();
        hud.add().expand();

        backBut.setPosition( 50, 50 );
        optFill.addActor( backBut );

        ChangeListener hudButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( optBut.isPressed() ) {
                    hud.setVisible( false );
                    optIG.setVisible( true );
                    toUpdate = 1;
                }

            }
        };

        ChangeListener optButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( backBut.isPressed() ) {
                    hud.setVisible( true );
                    toUpdate = 0;
                    optIG.setVisible( false );
                }
            }
        };

        // TODO mover.
        mover.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                movex = mover.getKnobPercentX() *2;
                movey = mover.getKnobPercentY() /3;
            }
        } );


        backBut.addListener( optButons );
        optBut.addListener( hudButons );

        optFill.pack();
        stage.addActor( hud );
        stage.addActor( optIG );
    }


    @Override
    public void show() {

        makeStage();

        Garden_Revolution.multiplexer.addProcessor( this );

        Buton.tweeger = tweeger;
        toUpdate = 0;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glViewport( 0, 0, scrw, scrh );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );
        tweeger.update( delta );

        if ( toUpdate ==0 )
            updateGameplay( delta );

        modelBatch.begin( cam );
        for (ModelInstance instance : instances )
            modelBatch.render( instance, lights );
        if ( cer !=null )
            modelBatch.render( cer );
        modelBatch.end();

        stage.act();
        stage.draw();
        Table.drawDebug( stage );

    }

    public void manageModels() {

        Model cuboid = (Model) Garden_Revolution.manager.get( Assets.SCENA.path(), Assets.SCENA.type() );
        for (int i = 0 ; i <cuboid.nodes.size ; i ++ ) {
            String id = cuboid.nodes.get( i ).id;

            ModelInstance instance = new ModelInstance( cuboid, id );
            Node node = instance.getNode( id );

            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();

            instance.calculateTransforms();
            // System.out.println(node.id);FIXME

            if ( id.equals( "cer" ) ) {
                cer = instance;
                continue;
            }

            instances.add( instance );

        }
    }

    private void updateGameplay(float delta) {
        if ( movex !=0 ||movey !=0 )
            moveCamera( movex, movey );
    }

    private void moveCamera(float amontX, float amontY) {
        System.out.println( amontX +" " +amontY );
        tmpV1.set( cam.direction ).crs( cam.up ).y = 0f;
        cam.rotateAround( target, tmpV1.nor(), amontY );
        cam.rotateAround( target, Vector3.Y, -amontX );
        cam.update();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ( moveByTouch &&toUpdate ==0 ) {
            startX = screenX;
            startY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if ( moveByTouch &&toUpdate ==0 ) {
            deltaX = ( screenX -startX ) /5;
            deltaY = ( startY -screenY ) /10;
            startX = screenX;
            startY = screenY;
            moveCamera( deltaX, deltaY );
            return true;
        }

        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                movey = 1.5f;
                break;
            case Keys.S:
                movey = -1.5f;
                break;
            case Keys.D:
                movex = 1.5f;
                break;
            case Keys.A:
                movex = -1.5f;
                break;
            case Keys.ESCAPE:
                Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Keys.W:
            case Keys.S:
                movey = 0;
                break;
            case Keys.D:
            case Keys.A:
                movex = 0;
                break;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        stage.dispose();
        skin.dispose();
    }


    public static class Board extends Group {

        public void pack() {
            float width = Float.NEGATIVE_INFINITY, height = Float.NEGATIVE_INFINITY, childXandWidth, childYandHeight;
            for (Actor child : getChildren() ) {
                if ( ( childXandWidth = child.getX() +child.getWidth() ) >width )
                    width = childXandWidth;

                if ( ( childYandHeight = child.getY() +child.getHeight() ) >height )
                    height = childYandHeight;
            }

            setSize( width, height );
        }

    }
}