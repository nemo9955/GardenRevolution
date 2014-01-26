package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;


public class Ally extends LifeForm {

    public Vector3   duty;
    private AllyType type;


    public Ally(World world) {
        super( world );
        duty = new Vector3();
    }

    public Ally create(Vector3 duty, AllyType type, float x, float y, float z) {
        this.type = type;
        super.init( x, y, z );


        // this.strenght = type.strenght;
        this.speed = type.speed;
        this.life = type.life;


        this.duty.set( duty ).add( MathUtils.random( -3f, 3f ), 0, MathUtils.random( -3f, 3f ) );
        return this;
    }

    @Override
    protected void setBox(float x, float y, float z) {
        box.set( new Vector3( x -2, y -1.3f, z -2 ), new Vector3( x +2, y +6.7f, z +2 ) );
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return type.getModel( x, y, z );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        if ( !poz.epsilonEquals( duty, 1 ) ) {
            direction.set( duty ).sub( poz ).nor().scl( speed *delta );
            move( direction );
        }
    }

    @Override
    public void setDead(boolean dead) {
        super.setDead( dead );

        if ( isDead() ) {
            world.aliatPool.free( this );
            world.ally.removeValue( this, false );
        }
    }

}
