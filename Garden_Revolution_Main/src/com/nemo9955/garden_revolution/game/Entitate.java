package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.btCollisionObject;
import com.badlogic.gdx.physics.bullet.btMotionState;
import com.badlogic.gdx.physics.bullet.btRigidBody;
import com.badlogic.gdx.physics.bullet.btRigidBodyConstructionInfo;
import com.badlogic.gdx.utils.Disposable;

/**
 * 
 * based on <b>xoppa</b>'s libGDX examples
 * 
 * @author nemo9955
 * 
 */
public class Entitate implements Disposable {

    public Matrix4              transform;
    public ModelInstance        modelInstance;
    private Color               color = new Color( 1f, 1f, 1f, 1f );

    public Entitate.MotionState motionState;
    public btCollisionObject    body;

    public Entitate(final Model model, final btRigidBodyConstructionInfo bodyInfo, final float x, final float y, final float z) {
        this( model, bodyInfo ==null ? null : new btRigidBody( bodyInfo ), x, y, z );
    }

    public Entitate(final Model model, final btRigidBodyConstructionInfo bodyInfo, final Vector3 transform) {
        this( model, bodyInfo ==null ? null : new btRigidBody( bodyInfo ), transform );
    }

    public Entitate(final Model model, final btCollisionObject body, final float x, final float y, final float z) {
        this( model, body, new Vector3( x, y, z ) );
    }

    public Entitate(final Model model, final btCollisionObject body, final Vector3 pozition) {
        this( new ModelInstance( model, pozition ), body );
    }

    public Entitate(final ModelInstance modelInstance, final btCollisionObject body) {
        this.modelInstance = modelInstance;
        this.transform = this.modelInstance.transform;
        this.body = body;

        if ( body !=null ) {
            body.userData = this;
            if ( body instanceof btRigidBody ) {
                this.motionState = new MotionState( this.modelInstance.transform );
                ( (btRigidBody) this.body ).setMotionState( motionState );
            }
            else
                body.setWorldTransform( transform );
        }
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        setColor( color.r, color.g, color.b, color.a );
    }

    public void setColor(float r, float g, float b, float a) {
        color.set( r, g, b, a );
        if ( modelInstance !=null ) {
            for (Material m : modelInstance.materials ) {
                ColorAttribute ca = (ColorAttribute) m.get( ColorAttribute.Diffuse );
                if ( ca !=null )
                    ca.color.set( r, g, b, a );
            }
        }
    }

    @Override
    public void dispose() {
        // Don't rely on the GC
        if ( motionState !=null )
            motionState.dispose();
        if ( body !=null )
            body.dispose();
        // And remove the reference
        motionState = null;
        body = null;
    }

    static class MotionState extends btMotionState {

        private final Matrix4 transform;

        public MotionState(final Matrix4 transform) {
            this.transform = transform;
        }

        @Override
        public void getWorldTransform(final Matrix4 worldTrans) {
            worldTrans.set( transform );
        }

        @Override
        public void setWorldTransform(final Matrix4 worldTrans) {
            transform.set( worldTrans );
        }
    }
}
