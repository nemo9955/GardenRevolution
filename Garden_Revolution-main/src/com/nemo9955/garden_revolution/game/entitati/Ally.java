package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.utility.Vars;


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
        this.life = type.life;

        this.duty.set( duty ).add( MathUtils.random( -6f, 6f ), 0, MathUtils.random( -6f, 6f ) );

        return this;
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return type.getModel( x, y, z );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        if ( !poz.epsilonEquals( duty, 1 ) ) {
            direction.set( duty ).sub( poz ).nor().scl( type.speed *delta );
            move( direction );
        }
    }

    private long lastAtack = 0;

    public void attack(Enemy enemy) {
        if ( System.currentTimeMillis() -lastAtack >Vars.allyAttackInterval ) {
            enemy.damage( type.strenght );
            lastAtack = System.currentTimeMillis();
        }
    }

    @Override
    public void setDead(boolean dead) {
        super.setDead( dead );

        if ( isDead() ) {
            world.aliatPool.free( this );
            world.getAlly().removeValue( this, false );
        }
    }
}
