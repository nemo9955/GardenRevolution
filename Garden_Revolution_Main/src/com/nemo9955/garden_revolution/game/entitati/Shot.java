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
import com.nemo9955.garden_revolution.game.enumTypes.Shots;


public class Shot extends Entitate {

    private Vector3 direction;
    private float   life;
    public Shots    type;

    public int      damage;

    public Shot(World world) {
        super( world );
        direction = new Vector3();
    }

    public Shot create(Shots type, Vector3 position, Vector3 direction) {
        this.type = type;
        super.init( position );
        this.direction.set( direction );
        damage = type.damage;
        life = 5f;
        return this;
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        move( direction );
        life -= delta;
        if ( life <=0 ||poz.y <0 ) {
            dead = true;
        }

        for (BoundingBox col : world.colide )
            if ( col.intersects( box ) )
                dead = true;
        if ( !dead )
            for (Inamic fo : world.foe )
                if ( fo.box.intersects( box ) ) {
                    fo.damage( this );
                    dead = true;
                }


    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {

        return new ModelInstance( new ModelBuilder().createSphere( 0.5f, 0.5f, 0.5f, 12, 12, new Material( ColorAttribute.createDiffuse( Color.RED ) ), Usage.Position |Usage.Normal ), x, y, z );

    }

}
