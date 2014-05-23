package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.utility.Vars;


public class Ally extends LifeForm {

    public final Vector3 duty = new Vector3();
    private AllyType     type;
    public FightZone     zone;

    public float         timeLeft;


    public Ally create(Vector3 duty, AllyType type) {
        this.type = type;
        super.init( duty );
        this.life = type.life;
        this.duty.set( duty );
        timeLeft = Vars.allyAliveInterval;
        return this;
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return type.getModel( x, y, z );
    }

    @Override
    public void update(float delta) {// TODO make this more efficient
        super.update( delta );
        // if ( !poz.epsilonEquals( duty, 1 ) ) {
        // direction.set( duty ).sub( poz ).nor().scl( type.speed *delta );
        // move( direction );
        // }
        if ( timeLeft >0 )
            timeLeft -= delta;
        else
            setDead( true );

        if ( isDead() )
            world.getDef().allyKilled( this );
    }

    private long lastAtack = 0;

    public void attack(Enemy enemy) {
        if ( System.currentTimeMillis() -lastAtack >Vars.allyAttackInterval ) {
            enemy.damage( type.strenght );
            lastAtack = System.currentTimeMillis();
        }
    }


}
