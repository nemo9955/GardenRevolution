package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.World;


public class MiniGun extends Arma {

    private static final int INTERVAL = 100;
    private long             time;


    public MiniGun(Vector3 poz) {
        super( poz );
    }

    @Override
    public void fireMain(World world, Ray ray) {
        if ( System.currentTimeMillis() -time >=INTERVAL ) {
            time = System.currentTimeMillis();

            float distance = -ray.origin.y /ray.direction.y;
            distance = Math.abs( distance );
            tmp = ray.getEndPoint( new Vector3(), distance );
            tmp.add( MathUtils.random( -2f, 2f ), MathUtils.random( -2f, 2f ), MathUtils.random( -2f, 2f ) );
            world.addShot( poz, tmp.sub( poz ).nor() );

        }
    }

    @Override
    protected FireState getFireState() {
        return FireState.CONTINUOUS;
    }

}
