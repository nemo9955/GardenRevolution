package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.utility.Vars;


public class Ally extends LifeForm {

    public short     ID;
    public Vector3   duty;
    private AllyType type;
    public FightZone zone;


    public Ally(WorldWrapper worldModel) {
        super( worldModel );
        duty = new Vector3();
    }

    public Ally create(Vector3 duty, AllyType type) {
        ID = newGlobalID();
        this.type = type;
        super.init( duty );
        this.life = type.life;
        this.duty.set( duty );
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
        if ( isDead() )
            world.getDef().allyKilled( this );
    }

    private static short globalID = -32768;

    public static short getGlobalID() {
        return globalID;
    }

    public static short newGlobalID() {
        return ++ globalID;

    }
}
