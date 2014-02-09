package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.world.IWorldModel;
import com.nemo9955.garden_revolution.game.world.WorldBase;


public enum WeaponType {

    NONE {

        @Override
        protected void propWeapons() {
            name = "None";
            details = "You have no weapon equiped";
        }

        @Override
        public void fireProjectile(IWorldModel world, Ray ray, float charge) {
        }

        @Override
        public ModelInstance getModelInstance(Vector3 poz) {
            return null;
        }

        @Override
        public FireType getFireType() {
            return null;
        }
    },
    MINIGUN {

        @Override
        protected void propWeapons() {
            name = "Mini Gun";
            details = "Small but vicious.";
        }

        @Override
        public void fireProjectile(IWorldModel world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                // -ray.origin.y /ray.direction.y;
                // tmp.set( ray.getEndPoint( tmp, distance ) );
                // tmp.add( MathUtils.random( -2f, 2f ), MathUtils.random( -1.5f, 3f ), MathUtils.random( -2f, 2f ) );
                // float distance = Functions.intersectLinePlane( ray, tmp );

                myray.set( ray );
                tmp.set( MathUtils.random() -0.5f, MathUtils.random() -0.5f, MathUtils.random() -0.5f ).scl( 2 );
                myray.origin.add( tmp );

                world.addShot( ShotType.STANDARD, myray, 0 );
            }
        }

        @Override
        public ModelInstance getModelInstance(Vector3 poz2) {
            ModelBuilder build = new ModelBuilder();
            Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
            WorldBase.toDispose.add( sfera );

            return new ModelInstance( sfera, poz2 );

        }

        @Override
        public FireType getFireType() {
            return FireType.FIREHOLD;
        }
    },
    CANNON {

        @Override
        protected void propWeapons() {
            fireDellay = 200;
            name = "Cannon";
            details = "Slow but powerfull.";
        }

        @Override
        public void fireProjectile(IWorldModel world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                // float distance = -ray.origin.y /ray.direction.y;
                // tmp.set( ray.getEndPoint( tmp, distance ) );

                world.addShot( ShotType.GHIULEA, ray, charge );
            }
        }

        @Override
        public ModelInstance getModelInstance(Vector3 poz) {
            ModelBuilder build = new ModelBuilder();
            Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.DARK_GRAY ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
            WorldBase.toDispose.add( sfera );

            return new ModelInstance( sfera, poz );

        }

        @Override
        public FireType getFireType() {
            return FireType.FIRECHARGED;
        }
    };

    private static final Vector3 tmp        = new Vector3();
    private static final Ray     myray      = new Ray( new Vector3(), new Vector3() );

    public String                name       = "weapon name";
    public String                details    = "weapon description";
    protected int                fireDellay = 100;
    protected long               fireTime   = 100;


    protected abstract void propWeapons();

    public abstract void fireProjectile(IWorldModel world, Ray ray, float charge);

    public abstract ModelInstance getModelInstance(Vector3 poz);

    public abstract FireType getFireType();

    public static enum FireType {
        FIREHOLD, FIRECHARGED;
    }

}
