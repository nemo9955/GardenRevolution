package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
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

    public GR_Start              game;

    public PerspectiveCamera     cam;
    public CameraInputController camController;
    public ModelBatch            modelBatch;
    public AssetManager          assets;
    public Array<ModelInstance>  instances = new Array<ModelInstance>();
    public Lights                lights;
    public boolean               loading;

    public Array<ModelInstance>  blocks    = new Array<ModelInstance>();
    public Array<ModelInstance>  invaders  = new Array<ModelInstance>();
    public ModelInstance         ship;
    public ModelInstance         cer;
    public ModelInstance         cub;

    private String               data      = "data";

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

        assets = new AssetManager();
        // assets.load( data +"/invaderscene.g3db", Model.class );
        assets.load( data +"/scena.g3db", Model.class );
        loading = true;

    }

    @Override
    public void show() {
        System.out.println( "play" );
    }

    private void doneLoading() {

        Model cuboid = assets.get( data +"/scena.g3db", Model.class );
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


        loading = false;
    }

    @Override
    public void render(float delta) {

        if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) )
            game.setScreen( game.meniu );

        if ( loading &&assets.update() )
            doneLoading();
        camController.update();

        Gdx.gl.glViewport( 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT |GL10.GL_DEPTH_BUFFER_BIT );


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
        assets.dispose();
        game.dispose();
    }
}