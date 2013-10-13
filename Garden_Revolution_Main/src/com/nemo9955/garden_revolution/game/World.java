package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.btCollisionObject;
import com.badlogic.gdx.physics.bullet.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.btRigidBody;
import com.badlogic.gdx.physics.bullet.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * 
 * based on <b>xoppa</b>'s libGDX examples
 * 
 * @author nemo9955
 * 
 */
public class World implements Disposable {

    public final btCollisionConfiguration collisionConfiguration;
    public final btCollisionDispatcher    dispatcher;
    public final btBroadphaseInterface    broadphase;
    public final btConstraintSolver       solver;
    public final btDiscreteDynamicsWorld  world;

    public int                            maxSubSteps = 5;

    protected final Array<Entitate>       entities    = new Array<Entitate>();

    public World(final btCollisionConfiguration collisionConfiguration, final btCollisionDispatcher dispatcher, final btBroadphaseInterface broadphase, final btConstraintSolver solver, final btCollisionWorld world, final Vector3 gravity) {
        this.collisionConfiguration = collisionConfiguration;
        this.dispatcher = dispatcher;
        this.broadphase = broadphase;
        this.solver = solver;
        this.world = (btDiscreteDynamicsWorld) world;
        if ( world instanceof btDynamicsWorld )
            this.world.setGravity( gravity );
    }

    public World(final btCollisionConfiguration collisionConfiguration, final btCollisionDispatcher dispatcher, final btBroadphaseInterface broadphase, final btConstraintSolver solver, final btCollisionWorld world) {
        this( collisionConfiguration, dispatcher, broadphase, solver, world, new Vector3( 0, -10, 0 ) );
    }

    public World(final Vector3 gravity) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher( collisionConfiguration );
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        world = new btDiscreteDynamicsWorld( dispatcher, broadphase, solver, collisionConfiguration );
        world.setGravity( gravity );
    }

    public World() {
        this( new Vector3( 0, -9.81f, 0 ) );
    }


    public void add(final Entitate entity) {

        if ( entity.body !=null ) {
            if ( entity.body instanceof btRigidBody )
                world.addRigidBody( (btRigidBody) entity.body );
            else
                world.addCollisionObject( entity.body );
            entity.body.setUserValue( entities.size -1 );
        }

        entities.add( entity );
    }


    public void render(final ModelBatch batch, final Lights lights) {
        for (final Entitate e : entities ) {
            batch.render( e.modelInstance, lights );
        }
    }

    public void update(float delta) {
        world.stepSimulation( delta, maxSubSteps );
    }

    @Override
    public void dispose() {
        for (Entitate e : entities ) {
            btCollisionObject body = e.body;
            if ( body !=null ) {
                if ( body instanceof btRigidBody )
                    world.removeRigidBody( (btRigidBody) body );
                else
                    world.removeCollisionObject( body );
            }
        }

        for (Entitate e : entities )
            e.dispose();
        entities.clear();

        world.dispose();
        if ( solver !=null )
            solver.dispose();
        if ( broadphase !=null )
            broadphase.dispose();
        if ( dispatcher !=null )
            dispatcher.dispose();
        if ( collisionConfiguration !=null )
            collisionConfiguration.dispose();
    }
}
