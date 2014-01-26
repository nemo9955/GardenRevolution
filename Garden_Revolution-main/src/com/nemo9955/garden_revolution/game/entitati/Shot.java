package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;


public class Shot extends Entity {

    private Vector3 direction;
    private float   life;
    public ShotType type;

    private float   charge;

    public Shot(World world) {
        super( world );
        direction = new Vector3();
    }

    public Shot create(ShotType type, Vector3 position, Vector3 direction) {
        this.type = type;
        super.init( position );
        this.direction.set( direction );

        life = 5f;

        return this;
    }

    public Shot create(ShotType type, Vector3 position, Vector3 direction, float charge) {
        create( type, position, direction );
        this.charge = charge;

        if ( type ==ShotType.GHIULEA ) {
            this.direction.y = 0;
            this.direction.add( 0, 1f, 0 ).nor();
        }

        this.direction.scl( 1f +this.charge );

        return this;
    }

    @Override
    public void reset() {
        super.reset();

    }

    @Override
    public void update(float delta) {
        super.update( delta );

        move( type.getMove( direction, delta ) );

        life -= delta;
        if ( life <=0 ||poz.y <0 ) {
            setDead( true );
        }

        for (BoundingBox col : world.colide )
            if ( col.intersects( box ) )
                setDead( true );

        type.hitActivity( this, world );
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {

        ModelInstance modelInstance = new ModelInstance( new ModelBuilder().createSphere( 0.5f, 0.5f, 0.5f, 12, 12, new Material( ColorAttribute.createDiffuse( Color.RED ) ), Usage.Position |Usage.Normal ), x, y, z );
        World.toDispose.add( modelInstance.model );
        return modelInstance;

    }

    @Override
    public void setDead(boolean dead) {
        super.setDead( dead );

        if ( isDead() ) {
            world.shotPool.free( this );
            world.shot.removeValue( this, false );
        }
    }

}
