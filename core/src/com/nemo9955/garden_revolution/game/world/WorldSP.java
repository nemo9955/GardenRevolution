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
		WorldWrapper.instance.getWorld().addLife(amount);
	}

	@Override
	public boolean changeWeapon( Tower tower, WeaponType newWeapon ) {
		return WorldWrapper.instance.getWorld().changeWeapon(tower, newWeapon);
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
	public void setLife( int viata ) {
		WorldWrapper.instance.getWorld().setLife(viata);
	}

	@Override
	public void setMoney( int money ) {
		WorldWrapper.instance.getWorld().setMoney(money);
	}

	@Override
	public void addMoney( int money ) {
		WorldWrapper.instance.getWorld().addMoney(money);
	}

	@Override
	public boolean upgradeTower( Tower tower, TowerType upgrade ) {
		return WorldWrapper.instance.getWorld().upgradeTower(tower, upgrade);
	}

	@Override
	public boolean canChangeTowers( byte current, byte next, String name ) {
		return WorldWrapper.instance.getWorld().canChangeTowers(current, next, name);
	}

	public boolean changePlayerTower( Player player, byte next ) {// /////////////////////////////////////////
		Tower nxt = WorldWrapper.instance.getWorld().getTowers()[next];

		if ( nxt.ocupier == null || next == 0 ) {
			player.getTower().ocupier = null;
			nxt.ocupier = player.name;
			player.setTower(nxt);
			return true;
		}
		return false;
	}

	@Override
	public void fireFromTower( Tower tower ) {
		WorldWrapper.instance.getWorld().fireFromTower(tower);
	}

	@Override
	public void setTowerFireHold( Tower tower, boolean hold ) {
		WorldWrapper.instance.getWorld().setTowerFireHold(tower, hold);
	}

	@Override
	public void reset() {
		if ( WorldWrapper.instance.getWorld() != null )
			WorldWrapper.instance.getWorld().reset();
	}

	@Override
	public Enemy addFoe( EnemyType type, Vector3 poz ) {
		return WorldWrapper.instance.getWorld().addFoe(type, poz);
	}

	@Override
	public Enemy addFoe( EnemyType type, CatmullRomSpline<Vector3> path ) {
		return WorldWrapper.instance.getWorld().addFoe(type, path);
	}

	@Override
	public Ally addAlly( Vector3 duty, AllyType type ) {
		return WorldWrapper.instance.getWorld().addAlly(duty, type);
	}

	@Override
	public void enemyKilled( Enemy enemy ) {
		WorldWrapper.instance.getWorld().getEnemyPool().free(enemy);
		WorldWrapper.instance.getWorld().getEnemy().removeValue(enemy, false);
	}

	@Override
	public void allyKilled( Ally ally ) {
		WorldWrapper.instance.getWorld().getAliatPool().free(ally);
		WorldWrapper.instance.getWorld().getAlly().removeValue(ally, false);
		if ( ally.zone != null )
			ally.zone.removeAlly(ally);
	}

	public void killAlly( short ID ) {
		for (Ally aly : WorldWrapper.instance.getWorld().getAlly())
			if ( aly.ID == ID )
				allyKilled(aly);

	}

	public void killEnemy( short ID ) {
		for (Enemy enmy : WorldWrapper.instance.getWorld().getEnemy())
			if ( enmy.ID == ID )
				enemyKilled(enmy);
	}

}
