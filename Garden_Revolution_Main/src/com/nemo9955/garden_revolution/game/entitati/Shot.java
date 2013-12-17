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

    private float   charge;
    public int      damage;
    private int     range;
    private int     speed;

    public Shot(World world) {
        super( world );
        direction = new Vector3();
    }

    public Shot create(Shots type, Vector3 position, Vector3 direction) {
        this.type = type;
        super.init( position );
        this.direction.set( direction );
        this.damage = type.damage;
        this.range = type.range;
        this.speed = type.speed;
        life = 5f;

        return this;
    }

    public Shot create(Shots type, Vector3 position, Vector3 direction, float charge) {
        create( type, position, direction );
        this.charge = charge;

        if ( type ==Shots.GHIULEA )
            this.direction.add( 0, 1f, 0 ).nor();

        this.direction.scl( 1f +this.charge );

        return this;
    }

    @Override
    public void reset() {
        super.reset();
        speed = -1;
        damage = -1;
        range = -1;
        charge = -1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void update(float delta) {
        super.update( delta );

        switch (type) {
            case GHIULEA:
                move( direction.sub( 0, 0.02f, 0 ).tmp().scl( delta *speed ) );
                // System.out.println("ghiulea");
                break;
            default:
                move( direction.tmp().scl( delta *speed ) );
                break;
        }

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
        if ( dead ==true &&type ==Shots.GHIULEA ) {
            for (Inamic fo : world.foe )
                if ( fo.poz.dst2( poz ) <=range *range )
                    fo.damage( (int) ( damage - ( damage * ( fo.poz.dst( poz ) /range ) ) ) );
        }
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {

        return new ModelInstance( new ModelBuilder().createSphere( 0.5f, 0.5f, 0.5f, 12, 12, new Material( ColorAttribute.createDiffuse( Color.RED ) ), Usage.Position |Usage.Normal ), x, y, z );

    }

}
