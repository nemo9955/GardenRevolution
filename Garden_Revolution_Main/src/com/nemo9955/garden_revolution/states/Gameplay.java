package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
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
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.Shot;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.entitati.Rosie;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustShader;
import com.nemo9955.garden_revolution.utility.Mod;

public class Gameplay implements Screen, InputProcessor, GestureListener {


    private PerspectiveCamera       cam;
    private ModelBatch              modelBatch;
    private Lights                  lights   = new Lights();
    private Shader                  shader;

    private float                   startX, startY;
    private float                   movex    = 0, movey = 0;
    private final Vector3           tmpV1    = new Vector3();
    private Vector3                 target   = new Vector3( 0, 12, 0 );
    private float                   deltaX   = 0;
    private float                   deltaY   = 0;

    private short                   toUpdate = 0;
    private final int               scrw     = Gdx.graphics.getWidth(), scrh = Gdx.graphics.getHeight();
    private Stage                   stage;
    private Skin                    skin     = Garden_Revolution.manager.get( Assets.SKIN_JSON.path() );
    private final Touchpad          mover    = new Touchpad( 5, skin );

    public World                    world;
    private GestureDetector         gestures = new GestureDetector( this );
    @SuppressWarnings("unused")
    private CameraInputController   camco;
    private ImmediateModeRenderer20 renderer;
    public static TweenManager      tweeger;


    public Gameplay() {
        float amb = 0.4f, lum = 0.3f;
        tweeger = new TweenManager();

        modelBatch = new ModelBatch();

        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 0f ) );

        cam = new PerspectiveCamera( 67, scrw, scrh );
        cam.position.set( target );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        makeStage();

        camco = new CameraInputController( cam );

        shader = new CustShader();
        shader.init();

        gestures.setLongPressSeconds( 1f );

        renderer = new ImmediateModeRenderer20( true, true, 0 );

    }


    @Override
    public void show() {
         Gdx.input.setInputProcessor( new InputMultiplexer( stage, camco, this, gestures ) );
      //  Gdx.input.setInputProcessor( new InputMultiplexer( stage, this, gestures ) );
        toUpdate = 0;
        world = new World( Garden_Revolution.getModel( Assets.SCENA ) );
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
        world.render( modelBatch, lights, shader );
        modelBatch.end();

        renderer.begin( cam.combined, GL10.GL_LINE_STRIP );
        world.renderLines( renderer );
        renderer.end();

        stage.act();
        stage.draw();
        Table.drawDebug( stage );

    }


    private void updateGameplay(float delta) {
        if ( movex !=0 ||movey !=0 )
            moveCamera( movex, movey );
        world.update( delta );
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
                movex = -1.5f;
                break;
            case Keys.A:
                movex = 1.5f;
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
        world.dispose();
        renderer.dispose();
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
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        Ray ray = cam.getPickRay( x, y );
        world.addShot( new Shot( ray.origin, ray.direction ) );

        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        if ( Math.abs( Gdx.input.getX() -x ) <15 &&Math.abs( Gdx.input.getY() -y ) <15 ) {
            Ray ray = cam.getPickRay( x, y );
            float distance = -ray.origin.y /ray.direction.y;
            Vector3 poz = new Vector3( ray.getEndPoint( new Vector3(), distance ) );
            world.addFoe( new Rosie( world.path, poz.x, poz.y +1, poz.z ) );
            gestures.invalidateTapSquare();
        }
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}