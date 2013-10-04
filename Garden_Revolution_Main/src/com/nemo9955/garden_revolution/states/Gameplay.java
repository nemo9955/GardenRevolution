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

    private float                rotateAngle  = 360f;
    public int                   rotateButton = Buttons.LEFT;
    private float                startX, startY;
    private boolean              moveByTouch  = true;
    private boolean              moveUp       = false, moveDown = false, moveLeft = false, moveRight = false;
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
        hud.debug();
        hud.setFillParent( true );
        stage.addActor( hud );

        final ImageButton optBut = new ImageButton( skin, "IGoptiuni" );
        hud.add( optBut ).expand().top().left();

        final Board optBoard = new Board();
        final ScrollPane optiuniIG = new ScrollPane( optBoard );
        optiuniIG.setWidget( optBoard );
        optiuniIG.setVisible( false );

        optBut.addListener( new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                if ( optBut.isPressed() ) {
                    hud.setVisible( false );
                    optiuniIG.setVisible( true );
                }
            }
        } );

        TextButton back = new TextButton( "Back", skin );
        back.addListener( new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hud.setVisible( true );

                optiuniIG.setVisible( false );
            }
        } );

        optBoard.addActor( back );

        stage.addActor( optiuniIG );
    }

    @Override
    public void show() {

        makeStage();
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
        updateCamera();
    }

    private void updateCamera() {

        final float toMove = 0.005f;

        if ( moveUp )
            moveCamera( toMove, 0 );
        if ( moveDown )
            moveCamera( -toMove, 0 );
        if ( moveLeft )
            moveCamera( 0, -toMove );
        if ( moveRight )
            moveCamera( 0, toMove );

        cam.update();
    }

    private void moveCamera(float amontX, float amontY) {
        tmpV1.set( cam.direction ).crs( cam.up ).y = 0f;
        cam.rotateAround( target, tmpV1.nor(), amontX *rotateAngle );
        cam.rotateAround( target, Vector3.Y, amontY *-rotateAngle );
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ( moveByTouch ) {
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

        if ( moveByTouch ) {
            deltaX = ( screenX -startX ) /scrw;
            deltaY = ( startY -screenY ) /scrh;
            startX = screenX;
            startY = screenY;
            moveCamera( deltaY, deltaX );
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
    public void hide() {
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                moveUp = true;
                break;
            case Keys.S:
                moveDown = true;
                break;
            case Keys.D:
                moveRight = true;
                break;
            case Keys.A:
                moveLeft = true;
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
                moveUp = false;
                break;
            case Keys.S:
                moveDown = false;
                break;
            case Keys.D:
                moveRight = false;
                break;
            case Keys.A:
                moveLeft = false;
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