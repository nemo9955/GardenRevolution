package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public class Waves {

    private final WorldWrapper world;

    private Array<Wave>        wvs;
    private int                curent = 0;

    public Waves(WorldWrapper world) {
        this.world = world;
        wvs = new Array<Wave>( true, 1 );
    }

    public void update(float delta) {


        if ( wvs.get( curent ).delay <=0 ) {
            if ( wvs.get( curent ).finishedCooldown( delta ) ) {

                Array<Monstru> add = getMonsters();
                for (Monstru mo : add ) {
                    world.getDef().addFoe( mo.type, getPath( mo ) );
                }
            }

            if ( !wvs.get( curent ).hasMonsters() )
                curent ++;

        }
        else {
            wvs.get( curent ).delay -= delta;
        }
    }

    private CatmullRomSpline<Vector3> getPath(Monstru mo) {
        return world.getWorld().getPaths().get( mo.path );
    }

    private Array<Monstru> getMonsters() {
        return wvs.get( curent ).getMonsters();
    }

    public void addWave(float delay, float interval) {
        wvs.add( new Wave( delay, interval ) );
    }

    public void populate(int path, EnemyType type, int amont) {
        wvs.peek().populate( path, type, amont );
    }

    public boolean finishedWaves() {
        return curent >=wvs.size;
    }


    // ---------------------------------------------------------------------------------------------------------------------

    private class Wave {

        private Array<Array<Monstru>> monstrii;
        public float                  delay;
        private final float           INTERVAL;
        private float                 timer;


        public Wave(float delay, float interval) {
            monstrii = new Array<Array<Monstru>>( world.getWorld().getPaths().size );
            for (int i = 0 ; i <world.getWorld().getPaths().size ; i ++ ) {
                monstrii.add( new Array<Monstru>( 1 ) );
            }
            this.delay = delay;
            this.INTERVAL = interval;
            timer = interval;
        }

        public boolean hasMonsters() {
            for (Array<Monstru> monstr : monstrii ) {
                if ( monstr.size >0 )
                    return true;
            }
            return false;

        }

        public Array<Monstru> getMonsters() {
            Array<Monstru> tmp = new Array<Monstru>();
            for (Array<Monstru> monstr : monstrii )
                if ( monstr.size >0 )
                    tmp.add( monstr.removeIndex( 0 ) );
            return tmp;
        }

        public boolean finishedCooldown(float delta) {
            timer -= delta;
            if ( timer <=0 ) {
                timer = INTERVAL;
                return true;
            }
            return false;
        }

        public void populate(int path, EnemyType type, int amont) {
            for (int i = 0 ; i <amont ; i ++ )
                monstrii.get( path ).add( new Monstru( type, path ) );
        }

    }

    private class Monstru {

        public EnemyType type;
        public int       path;

        private Monstru(EnemyType type, int path) {
            this.type = type;
            this.path = path;
        }
    }

}
