package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public abstract class Entity implements Poolable {

    protected ModelInstance       model;

    private boolean               dead;
    public BoundingBox            box   = new BoundingBox();
    public float                  colRadius;
    public boolean                isColCubic;

    protected static WorldWrapper world = GR.gameplay.world;

    // TODO remove these variables
    public float                  angle;
    public Vector3                poz   = new Vector3();

    public void init(Vector3 position) {
        init( position.x, position.y, position.z );
    }

    // principalul
    public void init(float x, float y, float z) {
        model = getModel( x, y, z );
        poz.set( x, y, z );
        setBox( x, y, z );
        setDead( false );
        angle = 0;
    }

    @Override
    public void reset() {
    }

    protected abstract ModelInstance getModel(float x, float y, float z);

    public static final float maxRap = 2f;

    protected void setBox(float x, float y, float z) {
        model.calculateBoundingBox( box );
        addPoz( box, x, y, z );
        colRadius = box.getDimensions().len() /2f;

        isColCubic = true;

        if ( Math.max( box.getDimensions().x, box.getDimensions().y ) /Math.min( box.getDimensions().x, box.getDimensions().y ) >maxRap )
            isColCubic = false;
        if ( Math.max( box.getDimensions().x, box.getDimensions().z ) /Math.min( box.getDimensions().x, box.getDimensions().z ) >maxRap )
            isColCubic = false;
        if ( Math.max( box.getDimensions().z, box.getDimensions().y ) /Math.min( box.getDimensions().z, box.getDimensions().y ) >maxRap )
            isColCubic = false;
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
        if ( isEntVisible( modelBatch ) )
            modelBatch.render( model, light );
    }

    protected boolean isEntVisible(ModelBatch modelBatch) {
        if ( isColCubic )
            return modelBatch.getCamera().frustum.sphereInFrustum( box.getCenter(), colRadius );
        else
            return modelBatch.getCamera().frustum.boundsInFrustum( box );
    }

    public void move(Vector3 move) {
        move( move.x, move.y, move.z );
    }

    public void walk(float dist) {
        move( (float) Math.sin( angle *MathUtils.degRad ) *dist, 0, (float) Math.cos( angle *MathUtils.degRad ) *dist );
    }

    public void move(float x, float y, float z) {
        model.transform.trn( x, y, z );
        addPoz( box, x, y, z );
        poz.add( x, y, z );
    }

    public void rotate(float x, float y, float z) {
        if ( x !=0 )
            model.transform.rotate( 1, 0, 0, x );
        if ( y !=0 ) {
            model.transform.rotate( 0, 1, 0, y );
            angle += y;
        }
        if ( z !=0 )
            model.transform.rotate( 0, 0, 1, z );


        if ( angle >=360 )
            angle -= 360;
        if ( angle <0 )
            angle += 360;
    }

    public static void addPoz(BoundingBox box, Vector3 poz) {
        box.set( box.min.add( poz ), box.max.add( poz ) );
    }

    public static void addPoz(BoundingBox box, float x, float y, float z) {
        box.set( box.min.add( x, y, z ), box.max.add( x, y, z ) );
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

}
