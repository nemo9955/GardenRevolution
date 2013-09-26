package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private Lights               lights;
    private ModelInstance        cer;

    private float                timer     = 0;
    private final float          rotSpeed  = 0.05f;

    private Vector3              lookAt;
    private final float          raza      = 50;
    private float                unghi;
    private float                modAng, modHei;

    public Gameplay(Garden_Revolution game) {
        this.game = game;

        float amb = 0.4f, lum = 0.6f;

        modelBatch = new ModelBatch();
        lights = new Lights();
        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 5f ) );

        lookAt = new Vector3();
        lookAt.y = 50;
        unghi = 0;
        modAng = 0;
        modHei = 0;
        modView( 0, 0 );

        cam = new PerspectiveCamera( 67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.position.set( 0f, 15f, 0f );
        cam.lookAt( 40, 0, 0 );
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

        if ( modAng !=0 ||modHei !=0 )
            modView( modAng, modHei );
        cam.lookAt( lookAt );

        cam.update();

        if ( Gdx.input.isTouched( 1 ) ) {
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
            case Input.Keys.W:
                modHei = 3;
                break;
            case Input.Keys.S:
                modHei = -3;
                break;

            case Input.Keys.A:
                modAng = rotSpeed;
                break;
            case Input.Keys.D:
                modAng = -rotSpeed;
                break;
        }

        return false;

    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Input.Keys.W:
            case Input.Keys.S:
                modHei = 0;
                break;
            case Input.Keys.A:
            case Input.Keys.D:
                modAng = 0;
                break;
        }
        return false;

    }

    private void modView(float angle, float height) {

        unghi += angle;
        if ( unghi >=360 )
            unghi -= 360;

        lookAt.x = (float) ( raza *Math.cos( unghi ) );
        lookAt.z = (float) ( raza *Math.sin( unghi ) );

        if ( lookAt.y +height >-20 &&lookAt.y +height <50 )
            lookAt.y += height;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

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
}