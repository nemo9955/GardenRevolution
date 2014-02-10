package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.world.IWorldModel;
import com.nemo9955.garden_revolution.game.world.WorldBase;


public class Shot extends Entity {

    private Vector3 direction;
    private float         life;
    public ShotType       type;
    private float         charge;

    public Shot(IWorldModel world) {
        super( world );
        direction = new Vector3();
    }

    public Shot create(ShotType type, Ray ray, float charge) {

        this.type = type;
        super.init( ray.origin );
        this.direction.set( ray.direction ).nor();

        life = 5f;
        this.charge = charge;

        if ( type ==ShotType.GHIULEA ) {
            this.direction.y = 0.5f;
            this.direction.nor().scl( 1f +this.charge );
        }
        return this;
    }

    @Override
    public void reset() {
        super.reset();
        charge = 0;
        life = 0;
    }

    @Override
    public void update(float delta) {
        super.update( delta );

        move( type.getMove( direction, delta ) );

        life -= delta;
        if ( life <=0 ||poz.y <0 ) {
            setDead( true );
        }

        for (BoundingBox col : world.getColide() )
            if ( col.intersects( box ) )
                setDead( true );

        type.hitActivity( this, world );
    }


    private static Model model = createModel();

    private static Model createModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createSphere( 0.5f, 0.5f, 0.5f, 12, 12, new Material( ColorAttribute.createDiffuse( Color.RED ) ), Usage.Position |Usage.Normal );
        WorldBase.toDispose.add( model );
        // System.out.println( "creat" );
        return model;
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return new ModelInstance( model, x, y, z );

    }

    @Override
    public void setDead(boolean dead) {
        super.setDead( dead );

        if ( isDead() ) {
            world.getShotPool().free( this );
            world.getShot().removeValue( this, false );
        }
    }

}
