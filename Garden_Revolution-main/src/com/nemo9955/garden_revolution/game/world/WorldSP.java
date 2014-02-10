package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;


public class WorldSP implements IWorldModel {

    private WorldBase world;


    public WorldSP(WorldBase world) {
        this.world = world;
    }

    @Override
    public Ally addAlly(AllyType type, float x, float y, float z) {
        return world.addAlly( type, x, y, z );
    }

    @Override
    public Ally addAlly(Vector3 duty, AllyType type, float x, float y, float z) {
        return world.addAlly( duty, type, x, y, z );
    }

    @Override
    public FightZone addFightZone(Vector3 poz) {
        return addFightZone( poz );
    }

    @Override
    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path, float x, float y, float z) {
        return world.addFoe( type, path, x, y, z );
    }

    @Override
    public Enemy addFoe(EnemyType type, float x, float y, float z) {
        return world.addFoe( type, x, y, z );
    }

    @Override
    public Shot addShot(ShotType type, Ray ray, float charge) {
        return world.addShot( type, ray, charge );
    }

    @Override
    public BoundingBox addToColide(BoundingBox box) {
        return world.addToColide( box );
    }

    @Override
    public void addViata(int amount) {
        world.addViata( amount );
    }

    @Override
    public boolean canChangeTowers(byte current, byte next, String name) {
        return world.canChangeTowers( current, next, name );
    }

    @Override
    public boolean canWaveStart() {
        return world.canWaveStart();
    }

    @Override
    public boolean changeWeapon(byte id, WeaponType newWeapon) {
        return world.changeWeapon( id, newWeapon );
    }

    @Override
    public void dispose() {
        world.dispose();
    }

    @Override
    public Array<Ally> getAlly() {
        return world.getAlly();
    }


    @Override
    public Array<BoundingBox> getColide() {
        return world.getColide();
    }


    @Override
    public Array<Enemy> getEnemy() {
        return world.getEnemy();
    }


    @Override
    public Environment getEnvironment() {
        return world.getEnvironment();
    }

    @Override
    public Array<FightZone> getFightZones() {
        return world.getFightZones();
    }

    @Override
    public String getMapPath() {
        return world.getMapPath();
    }


    @Override
    public Vector3 getOverview() {
        return world.getOverview();
    }

    @Override
    public Array<CatmullRomSpline<Vector3>> getPaths() {
        return world.getPaths();
    }

    @Override
    public Array<Shot> getShot() {
        return world.getShot();
    }

    @Override
    public Tower getTowerHitByRay(Ray ray) {
        return world.getTowerHitByRay( ray );
    }

    @Override
    public Tower[] getTowers() {
        return world.getTowers();
    }


    @Override
    public int getViata() {
        return world.getViata();
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
    public void render(ModelBatch modelBatch, Environment env, DecalBatch decalBatch) {
        world.render( modelBatch, env, decalBatch );
    }

    @Override
    public void renderDebug(PerspectiveCamera cam, ShapeRenderer shape) {
        world.renderDebug( cam, shape );
    }

    @Override
    public void setCanWaveStart(boolean canWaveStart) {
        world.setCanWaveStart( canWaveStart );
    }

    @Override
    public void setMapPath(String mapPath) {
        world.setMapPath( mapPath );
    }

    @Override
    public void setViata(int viata) {
        world.setViata( viata );
    }


    @Override
    public void update(float delta) {
        world.update( delta );
    }

    @Override
    public boolean upgradeTower(byte id, TowerType upgrade) {
        return world.upgradeTower( id, upgrade );
    }

    @Override
    public Pool<Enemy> getEnemyPool() {
        return world.getEnemyPool();
    }

    @Override
    public Pool<Ally> getAliatPool() {
        return world.getAliatPool();
    }

    @Override
    public Pool<Shot> getShotPool() {
        return world.getShotPool();
    }

    @Override
    public Pool<FightZone> getFzPool() {
        return world.getFzPool();
    }

    public boolean changePlayerTower(Player player, byte next) {
        Tower tower = world.getTowers()[next];

        if ( tower.ocupier ==null ) {
            if ( player.isInTower() )
                player.getTower().ocupier = null;
            tower.ocupier = player.name;
            player.setTower( tower );
            return true;
        }
        return false;
    }

    @Override
    public void fireFromTower(Tower tower, float charge) {
        world.fireFromTower( tower, charge );
    }

}
