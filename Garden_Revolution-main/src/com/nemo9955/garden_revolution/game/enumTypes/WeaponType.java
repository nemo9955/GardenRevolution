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
import com.nemo9955.garden_revolution.game.World;


public enum WeaponType {


    MINIGUN {

        @Override
        protected void propWeapons() {
            name = "Mini Gun";
            details = "Small but vicious.";
        }

        @Override
        public void fireProjectile(World world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                float distance = -ray.origin.y /ray.direction.y;
                tmp.set( ray.getEndPoint( tmp, distance ) );
                tmp.add( MathUtils.random( -2f, 2f ), MathUtils.random( -1.5f, 3f ), MathUtils.random( -2f, 2f ) );

                if ( distance >=0 )
                    world.addShot( ShotType.STANDARD, ray, 0 );
                else
                    world.addShot( ShotType.STANDARD, ray, 0 );
            }
        }

        @Override
        public ModelInstance getModelInstance(Vector3 poz2) {
            ModelBuilder build = new ModelBuilder();
            Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
            World.toDispose.add( sfera );

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
        public void fireProjectile(World world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                float distance = -ray.origin.y /ray.direction.y;
                tmp.set( ray.getEndPoint( tmp, distance ) );

                if ( distance >=0 )
                    world.addShot( ShotType.GHIULEA, ray, charge );
                else
                    world.addShot( ShotType.GHIULEA, ray, charge );
            }
        }

        @Override
        public ModelInstance getModelInstance(Vector3 poz) {
            ModelBuilder build = new ModelBuilder();
            Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.DARK_GRAY ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
            World.toDispose.add( sfera );

            return new ModelInstance( sfera, poz );

        }

        @Override
        public FireType getFireType() {
            return FireType.FIRECHARGED;
        }
    };

    private static final Vector3 tmp        = new Vector3();

    public String                name       = "weapon name";
    public String                details    = "weapon description";
    protected int                fireDellay = 100;
    protected long               fireTime   = 100;


    protected abstract void propWeapons();

    public abstract void fireProjectile(World world, Ray ray, float charge);

    public abstract ModelInstance getModelInstance(Vector3 poz);

    public abstract FireType getFireType();

    public static enum FireType {
        FIREHOLD, FIRECHARGED;
    }

}
