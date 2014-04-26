package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;


public class Enemy extends LifeForm {


    public short              ID;

    public Path<Vector3>      path;
    public float              percent;
    public static final float STEP   = 1f /50f;
    public Vector3            flag;
    public Vector3            offset;
    private EnemyType         type;

    private boolean           paused = false;

    public Enemy(WorldWrapper worldModel) {
        super( worldModel );
        offset = new Vector3();
        flag = new Vector3();
    }

    public Enemy create(CatmullRomSpline<Vector3> path, EnemyType type) {
        this.type = type;
        ID = newGlobalID();
        super.init( path.valueAt( poz, 0 ) );

        this.life = type.life;

        offset.set( Func.getRandOffset(), 0, Func.getRandOffset() );
        this.path = path;
        percent = -STEP;

        lookAt( path.valueAt( flag, percent +STEP ) );
        flag.add( offset );

        return this;
    }

    public Enemy create(EnemyType type, Vector3 poz) {
        ID = newGlobalID();
        this.type = type;
        super.init( poz );
        this.life = type.life;

        offset.set( Func.getRandOffset(), 0, Func.getRandOffset() );
        this.path = world.getWorld().getClosestStartPath( poz );
        percent = -STEP;

        lookAt( path.valueAt( flag, percent +STEP ) );
        flag.add( offset );

        return this;
    }

    @Override
    public void reset() {
        super.reset();
        setPaused( false );
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return type.getModel( x, y, z );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        if ( ( Vars.updateUave ||Gdx.input.isKeyPressed( Keys.F12 ) ) )
            advance( delta );
        if ( isDead() )
            world.getDef().enemyKilled( this );
    }

    private void advance(float delta) {
        if ( !poz.epsilonEquals( flag, 0.5f ) ) {
            direction.set( flag ).sub( poz ).nor().scl( type.speed *delta );
            move( direction );
        }
        else if ( !isPaused() ) {
            percent += STEP;
            if ( percent >=1 ) {
                world.getDef().addLife( -type.strenght );
                setDead( true );
            }
            path.valueAt( flag, percent +STEP );
            lookAt( path.valueAt( flag, percent +STEP ) );
            flag.add( offset );
        }
    }

    private long lastAtack = 0;

    public void atack(Ally ally) {
        if ( System.currentTimeMillis() -lastAtack >Vars.enemyAttackInterval ) {
            ally.damage( type.strenght );
            lastAtack = System.currentTimeMillis();
        }
    }

    @Override
    public void setDead(boolean dead) {
        if ( dead ) {
            world.getDef().addMoney( type.value );
            System.out.println( "added money : " +type.value );
        }
        super.setDead( dead );
    }


    private static short globalID = -32768;

    public static short getGlobalID() {
        return globalID;
    }

    public static short newGlobalID() {
        return ++ globalID;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
