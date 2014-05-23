package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public enum ShotType {

    STANDARD {

        {
            damage = 25;
            speed = 80;
        }

    },

    GHIULEA {

        {
            damage = 80;
            range = 9;
            speed = 18;
        }

        @Override
        public void hitActivity(Shot shot, WorldWrapper world) {
            super.hitActivity( shot, world );

            if ( shot.isDead() ) {
                for (Enemy fo : world.getWorld().getEnemy() )
                    if ( fo.poz.dst2( shot.poz ) <=range *range )
                        fo.damage( (int) ( damage - ( damage * ( fo.poz.dst( shot.poz ) /range ) ) ) );
            }
        }

        @Override
        public Vector3 getInitialDir(Vector3 direction, float charge) {
            direction.y /= 2;
            direction.y += 0.45f;
            direction.nor().scl( 1f +charge );
            return direction;
        }

        @Override
        public Vector3 makeMove(Vector3 direction, Vector3 out, float delta) {
            return out.set( direction.sub( 0, flyRap, 0 ) ).scl( delta *speed );
        }
    };

    public final float flyRap = 0.01f;

    public int         range  = 0;
    public int         damage = 10;
    public int         speed  = 20;

    public Vector3 makeMove(Vector3 direction, Vector3 out, float delta) {
        return out.set( direction ).nor().scl( delta *speed );
    }

    public void hitActivity(Shot shot, WorldWrapper world) {
        if ( !shot.isDead() )
            for (Enemy fo : world.getWorld().getEnemy() )
                if ( fo.box.intersects( shot.box ) ) {
                    fo.damage( damage );
                    shot.setDead( true );
                    break;
                }
    }

    public Vector3 getInitialDir(Vector3 direction, float charge) {
        return direction.nor();
    }
}
