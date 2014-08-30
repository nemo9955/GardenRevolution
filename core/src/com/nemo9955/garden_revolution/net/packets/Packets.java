package com.nemo9955.garden_revolution.net.packets;

import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.utility.Func;

public class Packets {

	public static class StartingServerInfo extends SuperPacket {

		public String	path;
		public String[]	turnuri;
		public String[]	players;

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			StartingServerInfo ssi = (StartingServerInfo) obj;
			GR.gameplay.postInit(WorldWrapper.instance.init(ssi));
			GR.game.setScreen(GR.gameplay);
		}

		@Override
		public void doForHost( Connection connection, Object obj ) {

			if ( WorldWrapper.instance.getWorld().canWaveStart() ) {
				connection.sendTCP(msNetGR.YouCannotConnect);
				connection.close();
			} else {
				final StartingServerInfo srv = (StartingServerInfo) obj;
				connection.sendTCP(WorldWrapper.instance.getDef().getWorldInfo(srv));
				// gp.showMessage( "[H] sending map info to client " );
			}
		}
	}

	public static class WeaponFireCharged extends SuperPacket {

		public byte							towerID;
		public float						charge;
		private static WeaponFireCharged	i	= new WeaponFireCharged();

		public static WeaponFireCharged getI( byte towerID, float charge ) {
			i.towerID = towerID;
			i.charge = charge;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			WeaponFireCharged pfa = (WeaponFireCharged) obj;

			Tower tower = WorldWrapper.instance.getWorld().getTowers()[pfa.towerID];
			if ( tower.isWeaponType(FireType.FIRECHARGED) ) {
				tower.charge = pfa.charge;
				WorldWrapper.instance.getSgPl().fireFromTower(tower);
			}
		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			WeaponFireCharged pfa = (WeaponFireCharged) obj;
			Tower tower = WorldWrapper.instance.getWorld().getTowers()[pfa.towerID];
			if ( tower.isWeaponType(FireType.FIRECHARGED) ) {
				tower.charge = pfa.charge;
				WorldWrapper.instance.getSgPl().fireFromTower(tower);
			}
			super.doForHost(connection, obj);
		}

	}

	public static class WorldChangeLife extends SuperPacket {

		public int						life;

		private static WorldChangeLife	i	= new WorldChangeLife();

		public static WorldChangeLife getI( int life ) {
			i.life = life;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {

			WorldChangeLife lf = (WorldChangeLife) obj;
			WorldWrapper.instance.getSgPl().setLife(lf.life);

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {
			WorldChangeLife lf = (WorldChangeLife) obj;
			WorldWrapper.instance.getSgPl().setLife(lf.life);
			super.doForHost(connection, obj);
		}
	}

	public static class WorldChangeMoney extends SuperPacket {

		public int						money;
		public boolean					isSetMoney;
		private static WorldChangeMoney	i	= new WorldChangeMoney();

		public static WorldChangeMoney getSetI( int money ) {
			i.money = money;
			i.isSetMoney = true;
			return i;
		}

		public static WorldChangeMoney getAddI( int money ) {
			i.money = money;
			i.isSetMoney = false;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			WorldChangeMoney mny = (WorldChangeMoney) obj;
			if ( mny.isSetMoney )
				WorldWrapper.instance.getSgPl().setMoney(mny.money);
			else
				WorldWrapper.instance.getSgPl().addMoney(mny.money);

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {
			WorldChangeMoney mny = (WorldChangeMoney) obj;
			if ( mny.isSetMoney )
				WorldWrapper.instance.getSgPl().setMoney(mny.money);
			else
				WorldWrapper.instance.getSgPl().addMoney(mny.money);
			super.doForHost(connection, obj);
		}
	}

	public static class AllyKilled extends SuperPacket {

		public short				ID;
		private static AllyKilled	i	= new AllyKilled();

		public static AllyKilled getI( short ID ) {
			i.ID = ID;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			AllyKilled aly = (AllyKilled) obj;
			WorldWrapper.instance.getSgPl().killAlly(aly.ID);
		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			AllyKilled aly = (AllyKilled) obj;
			WorldWrapper.instance.getSgPl().killAlly(aly.ID);
			super.doForHost(connection, obj);
		}
	}

	public static class EnemyKilled extends SuperPacket {

		public short				ID;
		private static EnemyKilled	i	= new EnemyKilled();

		public static EnemyKilled getI( short ID ) {
			i.ID = ID;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			EnemyKilled enmy = (EnemyKilled) obj;
			WorldWrapper.instance.getSgPl().killEnemy(enmy.ID);
		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			EnemyKilled enmy = (EnemyKilled) obj;
			WorldWrapper.instance.getSgPl().killEnemy(enmy.ID);
			super.doForHost(connection, obj);
		}
	}

	public static class AddEnemyOnPath extends SuperPacket {

		public short					ID;
		public byte						ordinal;
		public byte						pathNo;
		public byte						ofsX, ofsZ;

		private static AddEnemyOnPath	i	= new AddEnemyOnPath();

		public static AddEnemyOnPath getI( EnemyType type, short id, byte pathNo, Vector3 offset ) {
			i.ID = id;
			i.ordinal = (byte) type.ordinal();
			i.pathNo = pathNo;
			i.ofsX = (byte) (offset.x * 10);
			i.ofsZ = (byte) (offset.z * 10);
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			AddEnemyOnPath ent = (AddEnemyOnPath) obj;
			Enemy addFoe = WorldWrapper.instance.getSgPl().addFoe(EnemyType.values()[ent.ordinal], WorldWrapper.instance.getWorld().getPaths().get(ent.pathNo));
			addFoe.offset.set(Func.getOffset(ent.ofsX), 0, Func.getOffset(ent.ofsZ));
			addFoe.ID = ent.ID;
		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {
			AddEnemyOnPath ent = (AddEnemyOnPath) obj;
			Enemy addFoe = WorldWrapper.instance.getSgPl().addFoe(EnemyType.values()[ent.ordinal], WorldWrapper.instance.getWorld().getPaths().get(ent.pathNo));
			addFoe.offset.set(Func.getOffset(ent.ofsX), 0, Func.getOffset(ent.ofsZ));
			addFoe.ID = ent.ID;
			super.doForHost(connection, obj);
		}
	}

	public static class AddEnemyOnPoz extends SuperPacket {

		public short					ID;
		public byte						ordinal;
		public float					x, y, z;
		public byte						ofsX, ofsZ;

		private static AddEnemyOnPoz	i	= new AddEnemyOnPoz();

		public static AddEnemyOnPoz getI( EnemyType type, short ID, Vector3 poz, Vector3 offset ) {
			i.ID = ID;
			i.ordinal = (byte) type.ordinal();
			i.x = poz.x;
			i.y = poz.y;
			i.z = poz.z;
			i.ofsX = (byte) (offset.x * 10);
			i.ofsZ = (byte) (offset.z * 10);
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			AddEnemyOnPoz ent = (AddEnemyOnPoz) obj;
			Enemy addFoe = WorldWrapper.instance.getSgPl().addFoe(EnemyType.values()[ent.ordinal], GR.temp2.set(ent.x, ent.y, ent.z));
			addFoe.offset.set(Func.getOffset(ent.ofsX), 0, Func.getOffset(ent.ofsZ));
			addFoe.ID = ent.ID;
		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			AddEnemyOnPoz ent = (AddEnemyOnPoz) obj;
			Enemy addFoe = WorldWrapper.instance.getSgPl().addFoe(EnemyType.values()[ent.ordinal], GR.temp2.set(ent.x, ent.y, ent.z));
			addFoe.offset.set(Func.getOffset(ent.ofsX), 0, Func.getOffset(ent.ofsZ));
			addFoe.ID = ent.ID;
			super.doForHost(connection, ent);
		}
	}

	public static class AddAlly extends SuperPacket {

		public short			ID;
		public byte				ordinal;
		public float			x, y, z;
		private static AddAlly	i	= new AddAlly();

		public static AddAlly getI( AllyType type, short ID, Vector3 duty ) {
			i.ordinal = (byte) type.ordinal();
			i.ID = ID;
			i.x = duty.x;
			i.y = duty.y;
			i.z = duty.z;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {
			AddAlly waa = (AddAlly) obj;
			WorldWrapper.instance.getSgPl().addAlly(GR.temp2.set(waa.x, waa.y, waa.z), AllyType.values()[waa.ordinal]).ID = waa.ID;
		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {
			AddAlly waa = (AddAlly) obj;
			WorldWrapper.instance.getSgPl().addAlly(GR.temp2.set(waa.x, waa.y, waa.z), AllyType.values()[waa.ordinal]).ID = waa.ID;
			super.doForHost(connection, waa);
		}
	}

	public static class WeaponFireHold extends SuperPacket {

		public byte						towerID;
		public boolean					isFiring;
		private static WeaponFireHold	i	= new WeaponFireHold();

		public static WeaponFireHold getI( byte towerID, boolean isFiring ) {
			i.towerID = towerID;
			i.isFiring = isFiring;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {

			WeaponFireHold pfh = (WeaponFireHold) obj;

			Tower tower = WorldWrapper.instance.getWorld().getTowers()[pfh.towerID];
			if ( tower.isWeaponType(FireType.FIREHOLD) )
				WorldWrapper.instance.getSgPl().setTowerFireHold(tower, pfh.isFiring);

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			WeaponFireHold pfh = (WeaponFireHold) obj;

			Tower tower = WorldWrapper.instance.getWorld().getTowers()[pfh.towerID];
			if ( tower.isWeaponType(FireType.FIREHOLD) )
				WorldWrapper.instance.getSgPl().setTowerFireHold(tower, pfh.isFiring);

			super.doForHost(connection, obj);
		}

	}

	public static class TowerDirectionChange extends SuperPacket {

		public byte							ID;
		public float						x, y, z;

		private static TowerDirectionChange	i	= new TowerDirectionChange();

		public static TowerDirectionChange getI( byte ID, Vector3 dir ) {
			i.ID = ID;
			i.x = dir.x;
			i.y = dir.y;
			i.z = dir.z;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {

			final TowerDirectionChange tdr = (TowerDirectionChange) obj;
			WorldWrapper.instance.getWorld().getTowers()[tdr.ID].setDirection(tdr.x, tdr.y, tdr.z);
			// System.out.println( "[C] modificat dir turn    " +tdr.ID
			// +"      " +tdr.x +" " +tdr.y +" " +tdr.z );

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			TowerDirectionChange tdr = (TowerDirectionChange) obj;
			WorldWrapper.instance.getWorld().getTowers()[tdr.ID].setDirection(tdr.x, tdr.y, tdr.z);
			// System.out.println( "[H] mod dir turn        " +tdr.ID
			// +"        " +tdr.x +" " +tdr.y +" " +tdr.z );
			super.doForHost(connection, obj);
		}
	}

	public static class TowerChangedWeapon extends SuperPacket {

		public byte							eOrdinal;
		public byte							towerID;
		private static TowerChangedWeapon	i	= new TowerChangedWeapon();

		public static TowerChangedWeapon getI( byte towerID, int eOrdinal ) {
			i.eOrdinal = (byte) eOrdinal;
			i.towerID = towerID;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {

			final TowerChangedWeapon weap = (TowerChangedWeapon) obj;
			WorldWrapper.instance.getSgPl().changeWeapon(WorldWrapper.instance.getWorld().getTowers()[weap.towerID], WeaponType.values()[weap.eOrdinal]);

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			TowerChangedWeapon weap = (TowerChangedWeapon) obj;
			WorldWrapper.instance.getDef().changeWeapon(WorldWrapper.instance.getWorld().getTowers()[weap.towerID], WeaponType.values()[weap.eOrdinal]);

			super.doForHost(connection, obj);
		}
	}

	public static class TowerUpgrade extends SuperPacket {

		public byte					eOrdinal;
		public byte					towerID;

		private static TowerUpgrade	i	= new TowerUpgrade();

		public static TowerUpgrade getI( byte towerID, int eOrdinal ) {
			i.eOrdinal = (byte) eOrdinal;
			i.towerID = towerID;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {

			final TowerUpgrade twr = (TowerUpgrade) obj;
			WorldWrapper.instance.getSgPl().upgradeTower(WorldWrapper.instance.getWorld().getTowers()[twr.towerID], TowerType.values()[twr.eOrdinal]);

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			TowerUpgrade twr = (TowerUpgrade) obj;
			WorldWrapper.instance.getDef().upgradeTower(WorldWrapper.instance.getWorld().getTowers()[twr.towerID], TowerType.values()[twr.eOrdinal]);
			super.doForHost(connection, obj);
		}
	}

	public static class PlayerChangesTower extends SuperPacket {

		public byte							current;
		public byte							next;
		public String						name;
		private static PlayerChangesTower	i	= new PlayerChangesTower();

		public static PlayerChangesTower getI( byte current, byte next, String name ) {
			i.current = current;
			i.next = next;
			i.name = name;
			return i;
		}

		@Override
		public void doForClient( final Connection connection, final Object obj ) {

			final PlayerChangesTower plr = (PlayerChangesTower) obj;
			WorldWrapper.instance.getSgPl().canChangeTowers(plr.current, plr.next, plr.name);

		}

		@Override
		public void doForHost( final Connection connection, final Object obj ) {

			PlayerChangesTower plr = (PlayerChangesTower) obj;
			if ( WorldWrapper.instance.getSgPl().canChangeTowers(plr.current, plr.next, plr.name) ) {
				GR.mp.getServer().sendToAllExceptTCP(connection.getID(), plr);
				connection.sendTCP(msNetGR.YouCanChangeTowers);
			} else {
				connection.sendTCP(msNetGR.YouCanNOT_ChangeTowers);
			}
		}
	}

	public static enum msNetGR {
		IAmReady, YouCannotConnect, YouCanStartWaves, YouCanChangeTowers, YouCanNOT_ChangeTowers,
	}

	public static class SuperPacket {

		public void doForHost( final Connection connection, final Object obj ) {
			GR.mp.getServer().sendToAllExceptTCP(connection.getID(), obj);
		}

		public void doForClient( final Connection connection, final Object obj ) {

		}
	}

}
