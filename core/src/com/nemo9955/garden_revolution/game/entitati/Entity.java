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

    // TODO get rid of these variables
    public float                  angle;
    public Vector3                poz   = new Vector3();

    public void init(Vector3 position) {
        init( position.x, position.y, position.z );
    }

    public void init(float x, float y, float z) {
        model = getModel( x, y, z );
        poz.set( x, y, z );
        setBox( x, y, z );
        setDead( false );
    }

    @Override
    public void reset() {
        angle = 0;
    }

    protected abstract ModelInstance getModel(float x, float y, float z);

    public static final float maxRap = 2f;

    // if the shape's proportions are like those of a cube , then the shape is treated like a sphere instead of a box
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

    // sphereInFrustum is more efficient than boundsInFrustum
    protected boolean isEntVisible(ModelBatch modelBatch) {
        if ( isColCubic )
            return modelBatch.getCamera().frustum.sphereInFrustum( box.getCenter(), colRadius );
        else
            return modelBatch.getCamera().frustum.boundsInFrustum( box );
    }

    // unused
    // considering the angle , it makes the entity go forward the set distance
    public void walk(float dist) {
        move( (float) Math.sin( angle *MathUtils.degRad ) *dist, 0, (float) Math.cos( angle *MathUtils.degRad ) *dist );
    }

    public void move(Vector3 move) {
        move( move.x, move.y, move.z );
    }

    public void move(float x, float y, float z) {
        model.transform.trn( x, y, z );
        addPoz( box, x, y, z );
        poz.add( x, y, z );
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
