package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.GR_Start;

public class Gameplay implements Screen {

    public GR_Start               game;

    private PerspectiveCamera     cam;
    private CameraInputController camController;
    private ModelBatch            modelBatch;
    private Array<ModelInstance>  instances = new Array<ModelInstance>();
    private Lights                lights;

    private ModelInstance         cer;

    private float                 timer     = 0;

    public Gameplay(GR_Start game) {
        this.game = game;

        float amb = 0.4f, lum = 0.6f;

        modelBatch = new ModelBatch();
        lights = new Lights();
        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 5f ) );

        cam = new PerspectiveCamera( 67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.position.set( 0f, 15f, 0f );
        cam.lookAt( 40, 0, 0 );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController( cam );
        Gdx.input.setInputProcessor( camController );

    }

    @Override
    public void show() {
    }

    public void manageModels() {

        Model cuboid = GR_Start.manager.get( "modele/scena.g3db", Model.class );
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

        camController.update();


        if ( Gdx.input.isTouched() ) {
            timer -= delta;
            if ( timer <=2f )
                game.setScreen( game.meniu );
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
}