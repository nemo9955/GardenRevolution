package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.game.entitati.Inamici;


public class Waves {

    private final World   world;

    private Array<Wave>   wvs;
    private Path<Vector3> drum;
    private int           next = 0;

    public Waves(World world, Path<Vector3> drum, int noWaves) {
        this.world = world;
        this.drum = drum;
        wvs = new Array<Wave>( true, noWaves );
    }

    public Waves addWave(float delay, float interval) {
        wvs.add( new Wave( delay, interval ) );
        return this;
    }


    public Inamici getMonster() {

        return wvs.get( next ).getMonster();
    }

    public class Wave {

        public Array<Inamici> monstrii;
        public float          delay;
        public final float    TERVAL;
        public float          timmer;
        public int            next = 0;


        public Wave(float delay, float interval) {
            monstrii = new Array<Inamici>();
            this.delay = delay;
            this.TERVAL = interval;
            timmer = interval;
        }

        public void populate(Inamici type, int amont) {
            for (int i = 0 ; i <amont ; i ++ )
                monstrii.add( type );
        }

        public Inamici getMonster() {
            next ++;
            if ( monstrii.get( next ) !=null ) {
                return monstrii.get( next );
            }
            return null;
        }

        public boolean canContinue(float delta) {
            timmer -= delta;
            if ( timmer <=0 )
                return true;

            return next <monstrii.size;
        }

    }

}
