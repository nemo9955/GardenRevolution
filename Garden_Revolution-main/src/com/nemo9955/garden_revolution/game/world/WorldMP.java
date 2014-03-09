package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.MultiplayerComponent;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddAlly;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddEnemyOnPath;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddEnemyOnPoz;
import com.nemo9955.garden_revolution.utility.Functions;


public class WorldMP implements IWorldModel {

    private MultiplayerComponent mp;
    private WorldBase            world;

    public void init(WorldBase world, MultiplayerComponent mp) {
        this.mp = mp;
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
        setLife( world.getLife() +amount );
    }

    @Override
    public boolean canChangeTowers(byte current, byte next, String name) {
        mp.sendTCP( Functions.getPCT( current, next, name ) );
        return false;
    }


    @Override
    public boolean changeWeapon(Tower tower, WeaponType newWeapon) {


        if ( world.changeWeapon( tower, newWeapon ) ) {
            mp.sendTCP( Functions.getWCP( tower.ID, newWeapon.ordinal() ) );
            return true;
        }
        return false;
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
        mp.sendTCP( Functions.getCWL( viata ) );
    }


    @Override
    public boolean upgradeTower(Tower tower, TowerType upgrade) {
        if ( world.upgradeTower( tower, upgrade ) ) {
            mp.sendTCP( Functions.getTCP( tower.ID, upgrade.ordinal() ) );
            return true;
        }
        return false;
    }


    @Override
    public void fireFromTower(Tower tower) {
        if ( world.fireFromTower( tower ) )
            mp.sendTCP( Functions.getPFC( tower.ID, tower.charge ) );
    }

    @Override
    public void setTowerFireHold(Tower tower, boolean hold) {
        if ( world.setTowerFireHold( tower, hold ) )
            mp.sendTCP( Functions.getPFH( tower.ID, hold ) );
    }

    @Override
    public void reset() {
        world.reset();
    }

    @Override
    public Enemy addFoe(EnemyType type, Vector3 poz) {

        WorldAddEnemyOnPoz ent = Functions.getEOnPox( type, Enemy.newGlobalID(), poz, GR.temp1.set( Functions.getRandOffset(), 0, Functions.getRandOffset() ) );
        mp.sendTCP( ent );
        Enemy addFoe = world.addFoe( EnemyType.values()[ent.ordinal], poz );
        addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
        addFoe.ID = ent.ID;

        return addFoe;
    }

    @Override
    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path) {

        WorldAddEnemyOnPath ent = Functions.getEOnPath( type, Enemy.newGlobalID(), (byte) world.getPaths().indexOf( path, false ), GR.temp1.set( Functions.getRandOffset(), 0, Functions.getRandOffset() ) );
        mp.sendTCP( ent );

        Enemy addFoe = world.addFoe( EnemyType.values()[ent.ordinal], world.getPaths().get( ent.pathNo ) );
        addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
        addFoe.ID = ent.ID;
        return addFoe;
    }

    @Override
    public Ally addAlly(Vector3 duty, AllyType type) {
        WorldAddAlly addAl = Functions.getAddAl( type, Ally.newGlobalID(), duty );
        mp.sendTCP( addAl );
        Ally addAlly = world.addAlly( duty, type );
        addAlly.ID = addAl.ID;
        return addAlly;
    }


    @Override
    public void enemyKilled(Enemy enemy) {
        mp.sendTCP( Functions.getEnmyyK( enemy.ID ) );
        world.getEnemyPool().free( enemy );
        world.getEnemy().removeValue( enemy, false );
    }

    @Override
    public void allyKilled(Ally ally) {
        mp.sendTCP( Functions.getAllyK( ally.ID ) );
        world.getAliatPool().free( ally );
        world.getAlly().removeValue( ally, false );

    }

}
