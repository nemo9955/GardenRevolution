package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;


public class WorldSP implements IWorldModel {

    private WorldBase world;


    public void init(WorldBase world) {
        this.world = world;
    }

    @Override
    public FightZone addFightZone(Vector3 poz) {
        return addFightZone( poz );
    }


    @Override
    public BoundingBox addToColide(BoundingBox box) {
        return world.addToColide( box );
    }

    @Override
    public void addLife(int amount) {
        world.addLife( amount );
    }

    @Override
    public boolean changeWeapon(Tower tower, WeaponType newWeapon) {
        return world.changeWeapon( tower, newWeapon );
    }


    @Override
    public StartingServerInfo getWorldInfo(StartingServerInfo out) {
        return world.getWorldInfo( out );
    }

    @Override
    public void removeColiders(Array<BoundingBox> box) {
        world.removeColiders( box );
    }


    @Override
    public void setCanWaveStart(boolean canWaveStart) {
        world.setCanWaveStart( canWaveStart );
    }


    @Override
    public void setLife(int viata) {
        world.setLife( viata );
    }


    @Override
    public boolean upgradeTower(Tower tower, TowerType upgrade) {
        return world.upgradeTower( tower, upgrade );
    }


    @Override
    public boolean canChangeTowers(byte current, byte next, String name) {
        return world.canChangeTowers( current, next, name );
    }

    public boolean changePlayerTower(Player player, byte next) {// /////////////////////////////////////////
        Tower nxt = world.getTowers()[next];

        if ( nxt.ocupier ==null ||next ==0 ) {
            player.getTower().ocupier = null;
            nxt.ocupier = player.name;
            player.setTower( nxt );
            return true;
        }
        return false;
    }

    @Override
    public void fireFromTower(Tower tower) {
        world.fireFromTower( tower );
    }

    @Override
    public void setTowerFireHold(Tower tower, boolean hold) {
        world.setTowerFireHold( tower, hold );
    }

    @Override
    public void reset() {
        if ( world !=null )
            world.reset();
    }

    @Override
    public Enemy addFoe(EnemyType type, Vector3 poz) {
        return world.addFoe( type, poz );
    }

    @Override
    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path) {
        return world.addFoe( type, path );
    }

    @Override
    public Ally addAlly(Vector3 duty, AllyType type) {
        return world.addAlly( duty, type );
    }

    @Override
    public void enemyKilled(Enemy enemy) {
        world.getEnemyPool().free( enemy );
        world.getEnemy().removeValue( enemy, false );
    }

    @Override
    public void allyKilled(Ally ally) {
        world.getAliatPool().free( ally );
        world.getAlly().removeValue( ally, false );
        if ( ally.zone !=null )
            ally.zone.removeAlly( ally );
    }

    public void killAlly(short ID) {
        for (Ally aly : world.getAlly() )
            if ( aly.ID ==ID )
                allyKilled( aly );

    }

    public void killEnemy(short ID) {
        for (Enemy enmy : world.getEnemy() )
            if ( enmy.ID ==ID )
                enemyKilled( enmy );
    }


}
