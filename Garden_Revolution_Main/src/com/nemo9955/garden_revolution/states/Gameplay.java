package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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

public class Gameplay implements Screen {

    public Game                  game;

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

    @Override
    public void show() {
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

    private void doneLoading() {
        /*
         * Model model = assets.get( data +"/invaderscene.g3db", Model.class );
         * for (int i = 0 ; i <model.nodes.size ; i ++ ) {
         * String id = model.nodes.get( i ).id;
         * ModelInstance instance = new ModelInstance( model, id );
         * Node node = instance.getNode( id );
         * instance.transform.set( node.globalTransform );
         * node.translation.set( 0, 0, 0 );
         * node.scale.set( 1, 1, 1 );
         * node.rotation.idt();
         * instance.calculateTransforms();
         * if ( id.equals( "space" ) ) {
         * space = instance;
         * continue;
         * }
         * instances.add( instance );
         * if ( id.equals( "ship" ) )
         * ship = instance;
         * else if ( id.startsWith( "block" ) )
         * blocks.add( instance );
         * else if ( id.startsWith( "invader" ) )
         * invaders.add( instance );
         * }
         */

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
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
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

    public Gameplay(Game game) {
        this.game = game;
    }
}