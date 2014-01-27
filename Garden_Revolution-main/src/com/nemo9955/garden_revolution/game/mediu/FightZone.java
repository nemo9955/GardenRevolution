package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;


public class FightZone implements Poolable {

    private World            world;
    public Array<Ally>       allies   = new Array<Ally>( false, 10 );
    public Array<Enemy>      enemies  = new Array<Enemy>( false, 5 );

    private static Vector3   tmp      = new Vector3();
    public final Vector3     poz      = new Vector3();
    public BoundingBox       box      = new BoundingBox();

    private static final int halfSize = 8;

    public FightZone(World world) {
        this.world = world;
    }

    public FightZone create(Vector3 poz) {
        setPoz( poz );
        return this;
    }

    public void update(float delta) {

        for (Enemy enemy : world.enemy )
            if ( !enemy.paused &&box.contains( enemy.box ) ) {
                addEnemy( enemy );
                enemy.paused = true;
            }


        for (Ally ally : allies ) {
            Enemy enemy = enemies.random();
            if ( enemy !=null ) {
                ally.attack( enemy );
                if ( enemy.isDead() )
                    removeEnemy( enemy );
            }
            else
                break;
        }
        
        for (Enemy enemy : enemies ) {
            Ally ally = allies.random();
            if ( ally !=null ) {
                enemy.atack( ally );
                if ( ally.isDead() )
                    removeAlly( ally );
            }
            else
                break;
        }


    }

    @Override
    public void reset() {
        allies.clear();
        enemies.clear();
    }

    public void addEnemy(Enemy enemy) {
        enemies.add( enemy );
    }

    public void removeEnemy(Enemy enemy) {
        enemies.removeValue( enemy, false );
    }

    public void addAlly(Ally ally) {
        allies.add( ally );
    }

    public void removeAlly(Ally ally) {
        allies.removeValue( ally, false );
        if ( allies.size <=0 )
            removeFightZone();
    }

    public void removeFightZone() {
        for (Enemy enemy : enemies )
            enemy.paused = false;
        world.fzPool.free( this );
        world.fightZones.removeValue( this, false );
    }

    public boolean hasAllies() {
        return ! ( allies.size ==0 );
    }

    public boolean hasEnemies() {
        return ! ( enemies.size ==0 );
    }

    @SuppressWarnings("deprecation")
    public void setPoz(Vector3 poz) {
        this.poz.set( poz );
        box.set( poz.tmp().add( halfSize ), poz.tmp2().sub( halfSize ) );
    }

    public void aproximatePoz() {
        tmp.set( 0, 0, 0 );
        for (Ally ally : allies )
            tmp.add( ally.duty );
        tmp.scl( 1f /allies.size );
        setPoz( tmp );

    }

}
