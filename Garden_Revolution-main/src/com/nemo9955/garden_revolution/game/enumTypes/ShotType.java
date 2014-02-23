package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public enum ShotType {

    STANDARD {

        @Override
        protected void propShots() {
            damage = 35;
            speed = 50;
        }

    },

    GHIULEA {


        @Override
        protected void propShots() {
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
        public Vector3 getMove(Vector3 direction, float delta) {
            return GR.temp2.set( direction.sub( 0, 0.7f *delta, 0 ) ).scl( delta *speed );
        }
    };

    public int range  = 0;
    public int damage = 10;
    public int speed  = 20;


    ShotType() {
        propShots();
    }

    protected abstract void propShots();

    public Vector3 getMove(Vector3 direction, float delta) {
        return GR.temp2.set( direction ).nor().scl( delta *speed );
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

}
