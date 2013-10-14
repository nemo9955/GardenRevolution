package com.nemo9955.garden_revolution.states;

import java.util.Random;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.nemo9955.garden_revolution.utility.Mod;

public class Gameplay implements Screen, InputProcessor {


    private PerspectiveCamera    cam;
    private ModelBatch           modelBatch;
    private Lights               lights   = new Lights();
    private Array<ModelInstance> nori     = new Array<ModelInstance>();

    private float                startX, startY;
    private float                movex    = 0, movey = 0;
    private final Vector3        tmpV1    = new Vector3();
    private Vector3              target   = new Vector3( 0, 12, 0 );
    private float                deltaX   = 0;
    private float                deltaY   = 0;

    private short                toUpdate = 0;
    private final int            scrw     = Gdx.graphics.getWidth(), scrh = Gdx.graphics.getHeight();
    private TweenManager         tweeger;
    private Stage                stage;
    private Skin                 skin     = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
    private final Touchpad       mover    = new Touchpad( 5, skin );


    public Gameplay() {
        float amb = 0.4f, lum = 0.6f;
        tweeger = new TweenManager();

        modelBatch = new ModelBatch();

        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, 15f, 0f ) );

        cam = new PerspectiveCamera( 67, scrw, scrh );
        cam.position.set( target );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        makeStage();

        makeNori();

    }

    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Random zar = new Random();
        ModelInstance nor;

        int norx, norz;

        for (int i = 0 ; i <200 ; i ++ ) {
            norx = zar.nextInt( 500 ) -250;
            norz = zar.nextInt( 500 ) -250;
            for (int j = 1 ; j <=15 ; j ++ ) {
                nor = new ModelInstance( build.createSphere( 5, 5, 5, 12, 12, new Material( ColorAttribute.createDiffuse( Color.CYAN ) ), Usage.Position |Usage.Normal ) );
                nor.transform.translate( norx +zar.nextFloat() *7, 30, norz +zar.nextFloat() *7 );
                nori.add( nor );
            }
        }
    }

    private void makeStage() {
        stage = new Stage();

        final ImageButton optBut = new ImageButton( skin, "IGoptiuni" );
        final ImageButton pauseBut = new ImageButton( skin, "IGpause" );
        final TextButton backBut = new TextButton( "Back", skin, "demon" );
        final TextButton resumeBut = new TextButton( "Resume play", skin );
        final TextButton meniuBut = new TextButton( "Main menu", skin );

        final Board optFill = new Board();
        final Table hud = new Table();
        final ScrollPane optIG = new ScrollPane( optFill, skin );
        final Table pauseIG = new Table( skin );


        pauseIG.setVisible( false );
        pauseIG.addAction( Actions.alpha( 0 ) );
        pauseIG.setBackground( "pix30" );
        pauseIG.setFillParent( true );
        // pauseIG.debug();
        pauseIG.add( "PAUSE", "big-green" ).padBottom( stage.getHeight() *0.1f );
        pauseIG.row();
        pauseIG.add( resumeBut ).padBottom( stage.getHeight() *0.07f );
        pauseIG.row();
        pauseIG.add( meniuBut );


        optIG.addAction( Actions.alpha( 0 ) );
        optIG.setWidget( optFill );
        optIG.setVisible( false );
        optIG.setBounds( 100, 50, stage.getWidth() -200, stage.getHeight() -100 );
        backBut.setPosition( 50, 50 );
        optFill.addActor( backBut );

        // hud.debug();
        hud.setFillParent( true );
        hud.add( optBut ).expand().top().left();
        hud.add( pauseBut ).expand().top().right();
        hud.row();
        hud.add( mover ).bottom().left().padLeft( stage.getWidth() *0.03f ).padBottom( stage.getWidth() *0.03f );

        ChangeListener hudButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( optBut.isPressed() ) {
                    hud.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    optIG.addAction( Actions.sequence( Actions.visible( true ), Actions.alpha( 0 ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    toUpdate = 1;
                }
                if ( pauseBut.isPressed() ) {
                    hud.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    pauseIG.addAction( Actions.sequence( Actions.visible( true ), Actions.alpha( 0 ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    toUpdate = 1;
                }
            }
        };

        ChangeListener optButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( backBut.isPressed() ) {
                    optIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hud.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    toUpdate = 0;
                }
            }
        };

        ChangeListener pauseButons = new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ( resumeBut.isPressed() ) {
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ) ) );
                    hud.addAction( Actions.sequence( Actions.alpha( 0 ), Actions.visible( true ), Actions.delay( 0.2f ), Actions.alpha( 1, 0.5f ) ) );
                    toUpdate = 0;
                }
                if ( meniuBut.isPressed() ) {
                    hud.setVisible( true );
                    pauseIG.addAction( Actions.sequence( Actions.alpha( 0, 0.5f ), Actions.visible( false ), Actions.run( new Runnable() {

                        @Override
                        public void run() {
                            Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
                        }
                    } ) ) );
                }
            }
        };

        resetAfterOptions();

        meniuBut.addListener( pauseButons );
        resumeBut.addListener( pauseButons );
        pauseBut.addListener( hudButons );
        optBut.addListener( hudButons );
        backBut.addListener( optButons );
        optFill.pack();
        stage.addActor( hud );
        stage.addActor( optIG );
        stage.addActor( pauseIG );
    }

    public void resetAfterOptions() {
        // mover.getListeners().;

        mover.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                movex = Mod.invertPadX *mover.getKnobPercentX() *Mod.modCamSpeedX;
                movey = Mod.invertPadY *mover.getKnobPercentY() *Mod.modCamSpeedY;
            }
        } );

    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor( new InputMultiplexer( stage, this ) );
        toUpdate = 0;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glViewport( 0, 0, scrw, scrh );
        Gdx.gl.glClearColor( .1f, .5f, .9f, 1 );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );
        tweeger.update( delta );

        if ( toUpdate ==0 )
            updateGameplay( delta );

        modelBatch.begin( cam );
        for (ModelInstance instance : nori )
            modelBatch.render( instance );
        modelBatch.end();

        stage.act();
        stage.draw();
        Table.drawDebug( stage );

    }

    public void manageModels() {
        Model scena = (Model) Garden_Revolution.manager.get( Assets.SCENA.path(), Assets.SCENA.type() );

        // world.add( new Entitate( new ModelInstance( cuboid , new Vector3(0,-5,0)), null ) );

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            String id = scena.nodes.get( i ).id;

            ModelInstance instance = new ModelInstance( scena, id );

            Node node = instance.getNode( id );
            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();

            instance.calculateTransforms();
            System.out.println( node.id );

            if ( node.id.equals( "turn" ) ) {
                System.out.println( node.translation.x +" " +node.translation.y +" " +node.translation.z );
                continue;
            }

            if ( node.id.equals( "pamant" ) ) {
                System.out.println( node.translation.x +" " +node.translation.y +" " +node.translation.z );
                continue;
            }


        }
    }

    private void updateGameplay(float delta) {
        if ( movex !=0 ||movey !=0 )
            moveCamera( movex, movey );
    }

    private void moveCamera(float amontX, float amontY) {
        tmpV1.set( cam.direction ).crs( cam.up ).y = 0f;
        cam.rotateAround( target, tmpV1.nor(), amontY );
        cam.rotateAround( target, Vector3.Y, amontX );
        cam.update();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ( Mod.moveByTouch &&toUpdate ==0 ) {
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

        if ( Mod.moveByTouch &&toUpdate ==0 ) {
            deltaX = ( screenX -startX ) /10 *Mod.modCamSpeedX;
            deltaY = ( startY -screenY ) /7 *Mod.modCamSpeedY;
            startX = screenX;
            startY = screenY;
            moveCamera( deltaX *Mod.invertDragX, deltaY *Mod.invertDragY );
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
        Gdx.input.setInputProcessor( null );
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        stage.dispose();
        skin.dispose();
    }

    /**
     * https://bitbucket.org/dermetfan/somelibgdxtests/src/28080ff7dd7bd6d000ec8ba7f9514e177bb03e17/SomeLibgdxTests/src/net/dermetfan/someLibgdxTests/screens/TabsLeftTest.java?at=default
     * 
     * @author dermetfan
     * 
     */
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