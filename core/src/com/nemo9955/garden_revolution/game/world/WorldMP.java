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
import com.nemo9955.garden_revolution.net.packets.Packets.AddAlly;
import com.nemo9955.garden_revolution.net.packets.Packets.AddEnemyOnPath;
import com.nemo9955.garden_revolution.net.packets.Packets.AddEnemyOnPoz;
import com.nemo9955.garden_revolution.net.packets.Packets.AllyKilled;
import com.nemo9955.garden_revolution.net.packets.Packets.EnemyKilled;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerChangedWeapon;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerUpgrade;
import com.nemo9955.garden_revolution.net.packets.Packets.WeaponFireCharged;
import com.nemo9955.garden_revolution.net.packets.Packets.WeaponFireHold;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldChangeLife;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldChangeMoney;
import com.nemo9955.garden_revolution.utility.Func;

public class WorldMP implements IWorldModel {

	@Override
	public FightZone addFightZone( Vector3 poz ) {
		return addFightZone(poz);
	}

	@Override
	public BoundingBox addToColide( BoundingBox box ) {
		return WorldWrapper.instance.getWorld().addToColide(box);
	}

	@Override
	public void addLife( int amount ) {
		setLife(WorldWrapper.instance.getWorld().getLife() + amount);
	}

	@Override
	public boolean canChangeTowers( byte current, byte next, String name ) {
		GR.mp.sendTCP(PlayerChangesTower.getI(current, next, name));
		return false;
	}

	@Override
	public boolean changeWeapon( Tower tower, WeaponType newWeapon ) {

		if ( WorldWrapper.instance.getWorld().changeWeapon(tower, newWeapon) ) {
			GR.mp.sendTCP(TowerChangedWeapon.getI(tower.ID, newWeapon.ordinal()));
			return true;
		}
		return false;
	}

	@Override
	public StartingServerInfo getWorldInfo( StartingServerInfo out ) {
		return WorldWrapper.instance.getWorld().getWorldInfo(out);
	}

	@Override
	public void removeColiders( Array<BoundingBox> box ) {
		WorldWrapper.instance.getWorld().removeColiders(box);
	}

	@Override
	public void setCanWaveStart( boolean canWaveStart ) {
		WorldWrapper.instance.getWorld().setCanWaveStart(canWaveStart);
	}

	@Override
	public void setLife( int life ) {
		WorldWrapper.instance.getWorld().setLife(life);
		GR.mp.sendTCP(WorldChangeLife.getI(life));
	}

	@Override
	public void addMoney( int money ) {
		WorldWrapper.instance.getWorld().addMoney(money);
		GR.mp.sendTCP(WorldChangeMoney.getAddI(money));
	}

	@Override
	public void setMoney( int money ) {
		WorldWrapper.instance.getWorld().setMoney(money);
		GR.mp.sendTCP(WorldChangeMoney.getSetI(money));
	}

	@Override
	public boolean upgradeTower( Tower tower, TowerType upgrade ) {
		if ( WorldWrapper.instance.getWorld().upgradeTower(tower, upgrade) ) {
			GR.mp.sendTCP(TowerUpgrade.getI(tower.ID, upgrade.ordinal()));
			return true;
		}
		return false;
	}

	@Override
	public void fireFromTower( Tower tower ) {
		if ( WorldWrapper.instance.getWorld().fireFromTower(tower) )
			GR.mp.sendTCP(WeaponFireCharged.getI(tower.ID, tower.charge));
	}

	@Override
	public void setTowerFireHold( Tower tower, boolean hold ) {
		if ( WorldWrapper.instance.getWorld().setTowerFireHold(tower, hold) )
			GR.mp.sendTCP(WeaponFireHold.getI(tower.ID, hold));
	}

	@Override
	public void reset() {
		WorldWrapper.instance.getWorld().reset();
	}

	@Override
	public Enemy addFoe( EnemyType type, Vector3 poz ) {

		AddEnemyOnPoz ent = AddEnemyOnPoz.getI(type, Enemy.newGlobalID(), poz, GR.temp1.set(Func.getRandOffset(), 0, Func.getRandOffset()));
		GR.mp.sendTCP(ent);
		Enemy addFoe = WorldWrapper.instance.getWorld().addFoe(EnemyType.values()[ent.ordinal], poz);
		addFoe.offset.set(Func.getOffset(ent.ofsX), 0, Func.getOffset(ent.ofsZ));
		addFoe.ID = ent.ID;

		return addFoe;
	}

	@Override
	public Enemy addFoe( EnemyType type, CatmullRomSpline<Vector3> path ) {

		AddEnemyOnPath ent = AddEnemyOnPath.getI(type, Enemy.newGlobalID(), (byte) WorldWrapper.instance.getWorld().getPaths().indexOf(path, false),
				GR.temp1.set(Func.getRandOffset(), 0, Func.getRandOffset()));
		GR.mp.sendTCP(ent);

		Enemy addFoe = WorldWrapper.instance.getWorld().addFoe(EnemyType.values()[ent.ordinal], WorldWrapper.instance.getWorld().getPaths().get(ent.pathNo));
		addFoe.offset.set(Func.getOffset(ent.ofsX), 0, Func.getOffset(ent.ofsZ));
		addFoe.ID = ent.ID;
		return addFoe;
	}

	@Override
	public Ally addAlly( Vector3 duty, AllyType type ) {
		AddAlly addAl = AddAlly.getI(type, Ally.newGlobalID(), duty);
		GR.mp.sendTCP(addAl);
		Ally addAlly = WorldWrapper.instance.getWorld().addAlly(duty, type);
		addAlly.ID = addAl.ID;
		return addAlly;
	}

	@Override
	public void enemyKilled( Enemy enemy ) {
		GR.mp.sendTCP(EnemyKilled.getI(enemy.ID));
		WorldWrapper.instance.getWorld().getEnemyPool().free(enemy);
		WorldWrapper.instance.getWorld().getEnemy().removeValue(enemy, false);
	}

	@Override
	public void allyKilled( Ally ally ) {
		GR.mp.sendTCP(AllyKilled.getI(ally.ID));
		WorldWrapper.instance.getWorld().getAliatPool().free(ally);
		WorldWrapper.instance.getWorld().getAlly().removeValue(ally, false);

	}

}
