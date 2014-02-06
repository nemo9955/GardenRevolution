package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Shot;


public enum ShotType {

    STANDARD {

        @Override
        protected void propShots() {
            damage = 35;
            speed = 30;
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
        public void hitActivity(Shot shot, World world) {
            super.hitActivity( shot, world );

            if ( shot.isDead() ) {
                for (Enemy fo : world.getEnemy() )
                    if ( fo.poz.dst2( shot.poz ) <=range *range )
                        fo.damage( (int) ( damage - ( damage * ( fo.poz.dst( shot.poz ) /range ) ) ) );
            }
        }

        @Override
        @SuppressWarnings("deprecation")
        public Vector3 getMove(Vector3 direction, float delta) {
            return direction.sub( 0, 0.7f *delta, 0 ).tmp().scl( delta *speed );
        }
    };

    public int range  = 0;
    public int damage = 10;
    public int speed  = 20;

    ShotType() {
        propShots();
    }

    protected abstract void propShots();

    @SuppressWarnings("deprecation")
    public Vector3 getMove(Vector3 direction, float delta) {
        return direction.tmp().scl( delta *speed );
    }

    public void hitActivity(Shot shot, World world) {
        if ( !shot.isDead() )
            for (Enemy fo : world.getEnemy() )
                if ( fo.box.contains( shot.box ) ) {
                    fo.damage( damage );
                    shot.setDead( true );
                    break;
                }
    }

}
