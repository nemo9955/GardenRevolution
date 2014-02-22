package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.WorldBase;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public enum WeaponType {

    NONE {

        @Override
        protected void loadWeapon() {
            name = "None";
            details = "You have no weapon equiped";
        }

        @Override
        public boolean fireProjectile(WorldWrapper world, Ray ray, float charge) {
            return false;
        }

        @Override
        public ModelInstance getModelInstance(Vector3 poz) {
            return null;
        }

        @Override
        public FireType getFireType() {
            return null;
        }

        @Override
        public void updateWeaponTargeting(Tower tower) {
        }
    },
    MINIGUN {

        public ModelInstance raza;

        @Override
        protected void loadWeapon() {
            name = "Mini Gun";
            details = "Small but vicious.";

            Model razaModel;
            ModelBuilder modelBuilder = new ModelBuilder();
            modelBuilder.begin();
            MeshPartBuilder builder = modelBuilder.part( "raza", GL10.GL_LINES, Usage.Position |Usage.Color, new Material() );
            builder.setColor( Color.RED );
            builder.line( 0, 0, 0, 0, 0, 10 );
            razaModel = modelBuilder.end();

            raza = new ModelInstance( razaModel );

            WorldBase.toDispose.add( razaModel );

        }

        @Override
        public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
            modelBatch.render( raza );
        }


        /*
         * <lordjone> nemo9955 crs the current z axis of the model with the vector you need to obtain a rotation axis and the dot the z axis of the
         * model with the vector you need to point to
         * <lordjone> nemo9955 the cross represents the rotation axis, the dot the rotation angle, then do quaternion.setFromAxis(axis, angle)
         * <nemo9955> lordjone, ok i'll try that , thanks :D
         * <lordjone> nemo9955 also normalize the axis after you have calculated the cross because cross product is not normalized
         */

//        private final Quaternion qt = new Quaternion();

        @Override
        public void updateWeaponTargeting(Tower tower) {
            // raza.transform.setTranslation( tower.place );
            // raza.transform.setToLookAt( tower.place, tower.getDirection(), Vector3.Y );
            // raza.transform.setToLookAt( tower.place, temp.set( tower.place ).add( tower.getDirection() ), Vector3.Y );


//            temp.set( raza.transform.val[Matrix4.M02], raza.transform.val[Matrix4.M12], raza.transform.val[Matrix4.M22] ).nor();
//
//            float angle = (float) ( MathUtils.radiansToDegrees *Math.acos( GR.temp1.set( tower.getDirection() ).nor().dot( temp ) ) );
//
//            qt.setFromAxis( GR.temp2.set( tower.getDirection().nor() ).crs( temp ).nor(), angle );
//
//            // raza.transform.set( qt );
//            raza.transform.setToRotation( GR.temp2.set( tower.getDirection().nor() ).crs( temp ).nor(), angle );

             temp.set( tower.getDirection().z, tower.getDirection().y, tower.getDirection().x );
            raza.transform.setToLookAt( tower.getDirection(), Vector3.Y );
            raza.transform.setTranslation( tower.place );



            // qt.setFromAxis( raza.transform.getTranslation( GR.temp2 ).crs( tower.getDirection() ).nor(), raza.transform.getTranslation( GR.temp1 ).dot( tower.getDirection() ) );

            // qt.setFromAxis( raza., angle );

            // raza.transform.set( GR.gameplay.player.getCamera().view );
            // raza.transform.set( qt );
            // raza.transform.set( tower.place, qt.set( tower.getDirection(), 0 ), temp.set( 1, 1, 1 ) );
        }

        @Override
        public boolean fireProjectile(WorldWrapper world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                myray.set( ray );
                temp.set( MathUtils.random() -0.5f, MathUtils.random() -0.5f, MathUtils.random() -0.5f ).scl( 2 );
                myray.origin.add( temp );

                world.getWorld().addShot( ShotType.STANDARD, myray, 0 );
                return true;
            }
            return false;
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
        protected void loadWeapon() {
            fireDellay = 1000;
            name = "Cannon";
            details = "Slow but powerfull.";
        }

        @Override
        public boolean fireProjectile(WorldWrapper world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                // float distance = -ray.origin.y /ray.direction.y;
                // tmp.set( ray.getEndPoint( tmp, distance ) );

                world.getWorld().addShot( ShotType.GHIULEA, ray, charge );
                return true;
            }
            return false;
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

        @Override
        public void updateWeaponTargeting(Tower tower) {
            // TODO Auto-generated method stub

        }
    };

    private static final Vector3 temp       = new Vector3();
    private static final Ray     myray      = new Ray( new Vector3(), new Vector3() );

    public String                name       = "weapon name";
    public String                details    = "weapon description";
    protected int                fireDellay = 100;
    protected long               fireTime   = 0;


    {
        loadWeapon();
    }


    public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
    }

    protected abstract void loadWeapon();

    public abstract void updateWeaponTargeting(Tower tower);

    public abstract boolean fireProjectile(WorldWrapper world, Ray ray, float charge);

    public abstract ModelInstance getModelInstance(Vector3 poz);

    public abstract FireType getFireType();

    public static enum FireType {
        FIREHOLD, FIRECHARGED;
    }

}
