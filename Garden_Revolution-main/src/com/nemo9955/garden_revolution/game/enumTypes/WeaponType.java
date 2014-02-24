package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.WorldBase;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public enum WeaponType {

    NONE {

        {
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

        private Decal   raza;
        private float   opac     = -1f;
        private boolean atop     = false;
        private boolean drawRaza = false;

        {
            name = "Mini Gun";
            details = "Small but vicious.";
            raza = Decal.newDecal( Garden_Revolution.getMenuTexture( "pix50" ), true );
            raza.setDimensions( 200, 0.1f );
            raza.setColor( 1, 0, 0, 0 );
        }

        @Override
        public void updateWeaponTargeting(Tower tower) {
            GR.temp1.set( tower.place ).add( GR.temp2.set( tower.getDirection() ).nor().scl( raza.getWidth() /2 ) );
            raza.setPosition( GR.temp1.x, GR.temp1.y, GR.temp1.z );
            raza.setRotation( tower.getDirection(), Vector3.Y );
            raza.rotateY( 90 );
            atop = false;
            drawRaza = tower.isFiringHold;
        }

        @Override
        public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
            if ( drawRaza ) {
                decalBatch.add( raza );
                raza.setColor( 1, 0, 0, opac );

                if ( atop )
                    opac -= Gdx.graphics.getDeltaTime() *3;
                else
                    opac += Gdx.graphics.getDeltaTime() *3;

                if ( opac >1 ) {
                    opac = 1f;
                    atop = true;
                }
                else if ( opac <0 ) {
                    drawRaza = false;
                    opac = 0;
                }
            }
        }


        @Override
        public boolean fireProjectile(WorldWrapper world, Ray ray, float charge) {
            if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
                fireTime = System.currentTimeMillis();

                myray.set( ray );
                GR.temp2.set( MathUtils.random() -0.5f, MathUtils.random() -0.5f, MathUtils.random() -0.5f ).scl( 2 );
                myray.origin.add( GR.temp2 );

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


        private Decal   spot;
        private boolean draw;

        {
            fireDellay = 1000;
            name = "Cannon";
            details = "Slow but powerfull.";

            spot = Decal.newDecal( Garden_Revolution.getMenuTexture( "mover-bg" ), true );
            spot.setRotation( Vector3.Y, Vector3.Y );
            spot.setDimensions( 20, 20 );
            spot.setColor( 1, 1, 1, 0 );

        }

        @Override
        public void updateWeaponTargeting(Tower tower) {

            ShotType.GHIULEA.getInitialDir( GR.temp1.set( tower.getDirection() ), tower.charge );
            GR.temp4.set( tower.place );

            do {
                ShotType.GHIULEA.makeMove( GR.temp1, GR.temp3, Gdx.graphics.getDeltaTime() );
                GR.temp4.add( GR.temp3 );
            } while ( GR.temp4.y >0 );
            spot.setPosition( GR.temp4.x, 0f, GR.temp4.z );
            spot.setColor( Color.WHITE );
            draw = true;
        }

        @Override
        public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
            if ( draw )
                decalBatch.add( spot );
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

    };

    private static final Ray myray      = new Ray( new Vector3(), new Vector3() );

    public String            name       = "weapon name";
    public String            details    = "weapon description";
    protected int            fireDellay = 100;
    protected long           fireTime   = 0;


    public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
    }

    public abstract void updateWeaponTargeting(Tower tower);

    public abstract boolean fireProjectile(WorldWrapper world, Ray ray, float charge);

    public abstract ModelInstance getModelInstance(Vector3 poz);

    public abstract FireType getFireType();

    public static enum FireType {
        FIREHOLD, FIRECHARGED;
    }

}

/*
 * <lordjone> nemo9955 crs the current z axis of the model with the vector you need to obtain a rotation axis and the dot the z axis of the
 * model with the vector you need to point to
 * <lordjone> nemo9955 the cross represents the rotation axis, the dot the rotation angle, then do quaternion.setFromAxis(axis, angle)
 * <nemo9955> lordjone, ok i'll try that , thanks :D
 * <lordjone> nemo9955 also normalize the axis after you have calculated the cross because cross product is not normalized
 * // private final Quaternion qt = new Quaternion();
 * // raza.transform.setTranslation( tower.place );
 * // raza.transform.setToLookAt( tower.place, tower.getDirection(), Vector3.Y );
 * // raza.transform.setToLookAt( tower.place, temp.set( tower.place ).add( tower.getDirection() ), Vector3.Y );
 * // temp.set( raza.transform.val[Matrix4.M02], raza.transform.val[Matrix4.M12], raza.transform.val[Matrix4.M22] ).nor();
 * //
 * // float angle = (float) ( MathUtils.radiansToDegrees *Math.acos( GR.temp1.set( tower.getDirection() ).nor().dot( temp ) ) );
 * //
 * // qt.setFromAxis( GR.temp2.set( tower.getDirection().nor() ).crs( temp ).nor(), angle );
 * //
 * // // raza.transform.set( qt );
 * // raza.transform.setToRotation( GR.temp2.set( tower.getDirection().nor() ).crs( temp ).nor(), angle );
 * // GR.temp2.set( tower.getDirection().z, tower.getDirection().y, tower.getDirection().x );
 * // raza.transform.setToLookAt( tower.getDirection(), Vector3.Y );
 * // raza.transform.setTranslation( tower.place );
 * // qt.setFromAxis( raza.transform.getTranslation( GR.temp2 ).crs( tower.getDirection() ).nor(), raza.transform.getTranslation( GR.temp1 ).dot( tower.getDirection() ) );
 * // qt.setFromAxis( raza., angle );
 * // raza.transform.set( GR.gameplay.player.getCamera().view );
 * // raza.transform.set( qt );
 * // raza.transform.set( tower.place, qt.set( tower.getDirection(), 0 ), temp.set( 1, 1, 1 ) );
 */