package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.graphics.g3d.materials.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.btBoxShape;
import com.badlogic.gdx.physics.bullet.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.btCollisionShape;
import com.badlogic.gdx.physics.bullet.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.btMotionState;
import com.badlogic.gdx.physics.bullet.btRigidBody;
import com.badlogic.gdx.physics.bullet.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.btSphereShape;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;

/** @author xoppa */
public class BulletJocTest extends BulletTest implements Screen {

    // MotionState syncs the transform (position, rotation) between bullet and the model instance.
    public static class MotionState extends btMotionState {

        public Matrix4 transform;

        public MotionState(final Matrix4 transform) {
            this.transform = transform;
        }

        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set( transform );
        }

        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            transform.set( worldTrans );
        }
    }

    private ModelBatch                         modelBatch;
    private Lights                             lights       = new Lights( 0.2f, 0.2f, 0.2f ).add( new DirectionalLight().set( 0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f ) );
    private ModelBuilder                       modelBuilder = new ModelBuilder();

    private btCollisionConfiguration           collisionConfiguration;
    private btCollisionDispatcher              dispatcher;
    private btBroadphaseInterface              broadphase;
    private btConstraintSolver                 solver;
    private btDynamicsWorld                    collisionWorld;
    private Vector3                            gravity      = new Vector3( 0, -9.81f, 0 );
    private Vector3                            tempVector   = new Vector3();

    private Array<Model>                       models       = new Array<Model>();
    private Array<ModelInstance>               instances    = new Array<ModelInstance>();
    private Array<MotionState>                 motionStates = new Array<MotionState>();
    private Array<btRigidBodyConstructionInfo> bodyInfos    = new Array<btRigidBodyConstructionInfo>();
    private Array<btCollisionShape>            shapes       = new Array<btCollisionShape>();
    private Array<btRigidBody>                 bodies       = new Array<btRigidBody>();

    public BulletJocTest() {


        instructions = "Swipe for next test";
        // Set up the camera
        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        if ( width >height )
            camera = new PerspectiveCamera( 67f, 3f *width /height, 3f );
        else
            camera = new PerspectiveCamera( 67f, 3f, 3f *height /width );
        camera.position.set( 10f, 10f, 10f );
        camera.lookAt( 0, 0, 0 );
        camera.update();

        // Create the model batch
        modelBatch = new ModelBatch();

        // Create some basic models
        final Model groundModel = modelBuilder.createRect( 20f, 0f, -20f, -20f, 0f, -20f, -20f, 0f, 20f, 20f, 0f, 20f, 0, 1, 0, new Material( ColorAttribute.createDiffuse( Color.BLUE ), ColorAttribute.createSpecular( Color.WHITE ), FloatAttribute.createShininess( 16f ) ), Usage.Position |Usage.Normal );
        models.add( groundModel );
        final Model sphereModel = modelBuilder.createSphere( 1f, 1f, 1f, 10, 10, new Material( ColorAttribute.createDiffuse( Color.RED ), ColorAttribute.createSpecular( Color.WHITE ), FloatAttribute.createShininess( 64f ) ), Usage.Position |Usage.Normal );
        models.add( sphereModel );

        // Create the bullet world
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher( collisionConfiguration );
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld( dispatcher, broadphase, solver, collisionConfiguration );
        collisionWorld.setGravity( gravity );

        // Create the shapes and body construction infos
        btCollisionShape groundShape = new btBoxShape( tempVector.set( 20, 0, 20 ) );
        shapes.add( groundShape );
        btRigidBodyConstructionInfo groundInfo = new btRigidBodyConstructionInfo( 0f, null, groundShape, Vector3.Zero );
        bodyInfos.add( groundInfo );
        btCollisionShape sphereShape = new btSphereShape( 0.5f );
        shapes.add( sphereShape );
        sphereShape.calculateLocalInertia( 1f, tempVector );
        btRigidBodyConstructionInfo sphereInfo = new btRigidBodyConstructionInfo( 1f, null, sphereShape, tempVector );
        bodyInfos.add( sphereInfo );

        // Create the ground
        ModelInstance ground = new ModelInstance( groundModel );
        instances.add( ground );
        MotionState groundMotionState = new MotionState( ground.transform );
        motionStates.add( groundMotionState );
        btRigidBody groundBody = new btRigidBody( groundInfo );
        groundBody.setMotionState( groundMotionState );
        bodies.add( groundBody );
        collisionWorld.addRigidBody( groundBody );

        // Create the spheres
        for (float x = -10f ; x <=10f ; x += 2f ) {
            for (float y = 5f ; y <=15f ; y += 2f ) {
                for (float z = 0f ; z <=0f ; z += 2f ) {
                    ModelInstance sphere = new ModelInstance( sphereModel );
                    instances.add( sphere );
                    // sphere.transform.trn( x +0.1f *MathUtils.random(), y +0.1f *MathUtils.random(), z +0.1f *MathUtils.random() );
                    sphere.transform.trn( x, y, z );
                    MotionState sphereMotionState = new MotionState( sphere.transform );
                    motionStates.add( sphereMotionState );
                    btRigidBody sphereBody = new btRigidBody( sphereInfo );
                    sphereBody.setMotionState( sphereMotionState );
                    bodies.add( sphereBody );
                    collisionWorld.addRigidBody( sphereBody );
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport( 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );

        if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) )
            Garden_Revolution.game.setScreen( Garden_Revolution.meniu );

        fpsCounter.put( Gdx.graphics.getFramesPerSecond() );

        performanceCounter.tick();
        performanceCounter.start();
        ( (btDynamicsWorld) collisionWorld ).stepSimulation( Gdx.graphics.getDeltaTime(), 5 );
        performanceCounter.stop();

        modelBatch.begin( camera );
        modelBatch.render( instances, lights );
        modelBatch.end();

        performance.setLength( 0 );
        performance.append( "FPS: " ).append( fpsCounter.value ).append( ", Bullet: " ).append( (int) ( performanceCounter.load.value *100f ) ).append( "%" );
    }

    @Override
    public void dispose() {
        collisionWorld.dispose();
        solver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfiguration.dispose();

        for (btRigidBody body : bodies )
            body.dispose();
        bodies.clear();
        for (MotionState motionState : motionStates )
            motionState.dispose();
        motionStates.clear();
        for (btCollisionShape shape : shapes )
            shape.dispose();
        shapes.clear();
        for (btRigidBodyConstructionInfo info : bodyInfos )
            info.dispose();
        bodyInfos.clear();

        modelBatch.dispose();
        instances.clear();
        for (Model model : models )
            model.dispose();
        models.clear();
    }

    @Override
    public boolean longPress(float x, float y) {

        Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
        return false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor( this );
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
    }
}