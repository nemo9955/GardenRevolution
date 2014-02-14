package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class Enemy extends LifeForm {


    public Path<Vector3>      path;
    public float              percent;
    public static final float STEP   = 1f /50f;
    public Vector3            flag;
    public Vector3            offset;
    private EnemyType         type;

    public boolean            paused = false;

    public Enemy(WorldWrapper worldModel) {
        super( worldModel );
        offset = new Vector3();
        flag = new Vector3();
    }

    public Enemy create(CatmullRomSpline<Vector3> path, EnemyType type) {
        this.type = type;

        super.init( path.valueAt( poz, 0 ) );

        this.life = type.life;

        offset.set( Functions.getRandOffset(), 0, Functions.getRandOffset() );
        this.path = path;
        percent = -STEP;

        lookAt( path.valueAt( flag, percent +STEP ) );
        flag.add( offset );

        return this;
    }

    public Enemy create(EnemyType type, Vector3 poz) {
        this.type = type;
        super.init( poz );
        this.life = type.life;

        offset.set( Functions.getRandOffset(), 0, Functions.getRandOffset() );
        this.path = world.getWorld().getClosestStartPath( poz );
        percent = -STEP;

        lookAt( path.valueAt( flag, percent +STEP ) );
        flag.add( offset );

        return this;
    }

    @Override
    public void reset() {
        super.reset();
        paused = false;
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return type.getModel( x, y, z );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        if ( !paused && ( Vars.updateUave ||Gdx.input.isKeyPressed( Keys.F12 ) ) )
            advance( delta );
    }

    private void advance(float delta) {
        direction.set( flag ).sub( poz ).nor().scl( type.speed *delta );
        move( direction );
        if ( flag.epsilonEquals( poz, 1f ) ) {
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
        super.setDead( dead );


        if ( isDead() ) {
            world.getWorld().getEnemyPool().free( this );
            world.getWorld().getEnemy().removeValue( this, false );
        }

    }

}
