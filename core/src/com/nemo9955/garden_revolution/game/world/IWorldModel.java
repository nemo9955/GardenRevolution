package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;


public interface IWorldModel {

    public StartingServerInfo getWorldInfo(StartingServerInfo out);

    public FightZone addFightZone(Vector3 poz);

    public BoundingBox addToColide(BoundingBox box);

    public void removeColiders(Array<BoundingBox> box);

    public void addLife(int amount);

    public void setLife(int life);

    public void addMoney(int money);

    public void setMoney(int money);

    public void setCanWaveStart(boolean canWaveStart);

    public void reset();

    public boolean upgradeTower(Tower tower, TowerType upgrade);

    public boolean changeWeapon(Tower tower, WeaponType newWeapon);

    public boolean canChangeTowers(byte current, byte next, String name);

    public void fireFromTower(Tower tower);

    public void setTowerFireHold(Tower tower, boolean hold);

    public Enemy addFoe(EnemyType type, Vector3 poz);

    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path);

    public Ally addAlly(Vector3 duty, AllyType type);

    public void enemyKilled(Enemy enemy);

    public void allyKilled(Ally ally);

}