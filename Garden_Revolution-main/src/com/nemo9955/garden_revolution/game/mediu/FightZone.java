package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;


public class FightZone implements Poolable {

    private World          world;
    public Array<Ally>     allies = new Array<Ally>( false, 0 );
    public Array<Enemy>    enemy  = new Array<Enemy>( false, 0 );

    private static Vector3 tmp    = new Vector3();
    public final Vector3   poz    = new Vector3();
    public BoundingBox     box    = new BoundingBox();

    public FightZone(World world) {
        this.world = world;
    }

    public FightZone create(Vector3 poz) {
        this.poz.set( poz );
        box.set( tmp.set( poz ).add( 6 ), tmp.set( poz ).sub( 6 ) );
        return this;
    }

    @Override
    public void reset() {
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
        world.fzPool.free( this );
        world.fightZones.removeValue( this, false );
    }

}
