package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public class FightZone implements Poolable {

    private WorldWrapper     world;
    public Array<Ally>       allies   = new Array<Ally>( false, 10 );
    public Array<Enemy>      enemies  = new Array<Enemy>( false, 5 );

    private static Vector3   tmp      = new Vector3();
    public final Vector3     poz      = new Vector3();
    public BoundingBox       box      = new BoundingBox();

    private static final int halfSize = 8;

    public FightZone(WorldWrapper worldModel) {
        this.world = worldModel;
    }

    public FightZone create(Vector3 poz) {
        setPoz( poz );
        return this;
    }

    public void update(float delta) {

        for (Enemy enemy : world.getWorld().getEnemy() )
            if ( !enemy.paused &&box.contains( enemy.box ) ) {
                addEnemy( enemy );
                enemy.paused = true;
            }

        // TODO make this not random and dependent of the attack speed
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

        // TODO make this not random and dependent of the attack speed
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
        world.getWorld().getFzPool().free( this );
        world.getWorld().getFightZones().removeValue( this, false );
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

        for (int i = 0 ; i <5 ; i ++ )
            tmp.add( box.getCenter() );

        tmp.scl( 1f / ( allies.size +5 ) );
        setPoz( tmp );

    }

}
