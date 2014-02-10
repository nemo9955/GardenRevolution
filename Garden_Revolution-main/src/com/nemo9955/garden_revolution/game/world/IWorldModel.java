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


public interface IWorldModel {

    public StartingServerInfo getWorldInfo(StartingServerInfo out);

    public void update(float delta);

    public void render(ModelBatch modelBatch, Environment env, DecalBatch decalBatch);

    public void renderDebug(PerspectiveCamera cam, ShapeRenderer shape);

    public Enemy addFoe(EnemyType type, float x, float y, float z);

    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path, float x, float y, float z);

    public Ally addAlly(AllyType type, float x, float y, float z);

    public Ally addAlly(Vector3 duty, AllyType type, float x, float y, float z);

    public Shot addShot(ShotType type, Ray ray, float charge);

    public FightZone addFightZone(Vector3 poz);

    public Tower getTowerHitByRay(Ray ray);

    public Environment getEnvironment();

    public Array<FightZone> getFightZones();

    public Array<CatmullRomSpline<Vector3>> getPaths();

    public Array<Ally> getAlly();

    public Array<BoundingBox> getColide();

    public BoundingBox addToColide(BoundingBox box);

    public void removeColiders(Array<BoundingBox> box);

    public Array<Enemy> getEnemy();

    public void addViata(int amount);

    public int getViata();

    public void setViata(int viata);

    public Array<Shot> getShot();

    public boolean canWaveStart();

    public void setCanWaveStart(boolean canWaveStart);

    public void dispose();

    public boolean upgradeTower(byte id, TowerType upgrade);

    public boolean changeWeapon(byte id, WeaponType newWeapon);

    // public boolean canChangeTowers(Tower current, Tower next, Player player);
    public boolean canChangeTowers(byte current, byte next, String name);

    public String getMapPath();

    public void setMapPath(String mapPath);

    public Tower[] getTowers();

    public Vector3 getOverview();

    public Pool<Enemy> getEnemyPool();

    public Pool<Ally> getAliatPool();

    public Pool<Shot> getShotPool();

    public Pool<FightZone> getFzPool();

    public void fireFromTower(Tower tower, float charge);


}