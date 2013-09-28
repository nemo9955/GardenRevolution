package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;

public class Gameplay implements Screen, InputProcessor {

    public Garden_Revolution     game;

    private PerspectiveCamera    cam;
    private ModelBatch           modelBatch;
    private Array<ModelInstance> instances    = new Array<ModelInstance>();
    private Lights               lights;
    private ModelInstance        cer;
    private float                timer        = 0;


    private float                rotateAngle  = 360f;
    public int                   rotateButton = Buttons.LEFT;
    private final Vector3        tmpV1        = new Vector3();
    public Vector3               target       = new Vector3( 0, 15, 0 );
    private float                deltaX       = 0;
    private float                deltaY       = 0;

    private boolean              moveByTouch  = true;
    private float                startX, startY;
    private boolean              moveUp       = false, moveDown = false, moveLeft = false, moveRight = false;


    public Gameplay(Garden_Revolution game) {
        this.game = game;

        float amb = 0.4f, lum = 0.6f;

        modelBatch = new ModelBatch();
        lights = new Lights();
        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 5f ) );

        cam = new PerspectiveCamera( 67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.position.set( 0f, 15f, 0f );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

    }

    @Override
    public void show() {
    }

    public void manageModels() {

        Model cuboid = Garden_Revolution.manager.get( Garden_Revolution.MODELE +"scena.g3db", Model.class );
        for (int i = 0 ; i <cuboid.nodes.size ; i ++ ) {
            String id = cuboid.nodes.get( i ).id;

            ModelInstance instance = new ModelInstance( cuboid, id );
            Node node = instance.getNode( id );

            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();

            instance.calculateTransforms();

            if ( id.equals( "cer" ) ) {
                cer = instance;
                continue;
            }

            instances.add( instance );

        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport( 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );

        if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) )
            game.setScreen( game.meniu );

        updateCamera();

        if ( Gdx.input.isTouched() ) {
            timer -= delta;
            if ( timer <=2f ) {
                game.setScreen( game.meniu );
            }
        }
        else
            timer = 5f;

        modelBatch.begin( cam );
        for (ModelInstance instance : instances )
            modelBatch.render( instance, lights );
        if ( cer !=null )
            modelBatch.render( cer );
        modelBatch.end();
    }

    private void updateCamera() {
        
        final float toMove = 0.005f ;
        
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
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        game.dispose();
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
            deltaX = ( screenX -startX ) /Gdx.graphics.getWidth();
            deltaY = ( startY -screenY ) /Gdx.graphics.getHeight();
            startX = screenX;
            startY = screenY;
            moveCamera( deltaY, deltaX );
        }

        return false;
    }

    private void moveCamera(float amontX, float amontY) {
        tmpV1.set( cam.direction ).crs( cam.up ).y = 0f;
        cam.rotateAround( target, tmpV1.nor(), amontX *rotateAngle );
        cam.rotateAround( target, Vector3.Y, amontY *-rotateAngle );
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}