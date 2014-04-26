package com.nemo9955.garden_revolution.net.packets;

import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.net.packets.Packets.AllyKilled;
import com.nemo9955.garden_revolution.net.packets.Packets.ChangeWorldLife;
import com.nemo9955.garden_revolution.net.packets.Packets.ChangeWorldMoney;
import com.nemo9955.garden_revolution.net.packets.Packets.EnemyKilled;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerFireCharged;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerFireHold;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerDirectionChange;
import com.nemo9955.garden_revolution.net.packets.Packets.WeaponChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddAlly;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddEnemyOnPath;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddEnemyOnPoz;


public class PSender {


    private static WorldAddEnemyOnPath eOnPath = new WorldAddEnemyOnPath();

    public static WorldAddEnemyOnPath getEOnPath(EnemyType type, short ID, byte pathNo, Vector3 offset) {
        return eOnPath.getEOnPath( ID, (byte) type.ordinal(), pathNo, (byte) ( offset.x *10 ), (byte) ( offset.z *10 ) );
    }

    private static WorldAddEnemyOnPoz eOnPoz = new WorldAddEnemyOnPoz();

    public static WorldAddEnemyOnPoz getEOnPox(EnemyType type, short ID, Vector3 poz, Vector3 offset) {
        return eOnPoz.getEOnPoz( ID, (byte) type.ordinal(), poz.x, poz.y, poz.z, (byte) ( offset.x *10 ), (byte) ( offset.z *10 ) );
    }

    private static TowerDirectionChange tdc = new TowerDirectionChange();

    public static TowerDirectionChange getTDC(byte iD, Vector3 dir) {
        return tdc.getTDC( iD, dir );
    }

    private static AllyKilled allk = new AllyKilled();

    public static AllyKilled getAllyK(short ID) {
        return allk.getID( ID );
    }

    private static EnemyKilled enmyk = new EnemyKilled();

    public static EnemyKilled getEnmyyK(short ID) {
        return enmyk.getID( ID );
    }

    private static WeaponChangedPacket wcp = new WeaponChangedPacket();

    public static WeaponChangedPacket getWCP(byte towerID, int eOrdinal) {
        return wcp.getWCP( eOrdinal, towerID );
    }


    private static TowerChangedPacket tcp = new TowerChangedPacket();

    public static TowerChangedPacket getTCP(byte towerID, int eOrdinal) {
        return tcp.getTCP( eOrdinal, towerID );
    }


    private static PlayerChangesTower pct = new PlayerChangesTower();

    public static PlayerChangesTower getPCT(byte current, byte next, String name) {
        return pct.getPCT( current, next, name );
    }


    private static PlayerFireCharged pfc = new PlayerFireCharged();

    public static PlayerFireCharged getPFC(byte towerID, float info) {
        return pfc.getPFC( towerID, info );
    }

    private static PlayerFireHold pfh = new PlayerFireHold();

    public static PlayerFireHold getPFH(byte towerID, boolean isFiring) {
        return pfh.getPFH( towerID, isFiring );
    }

    private static WorldAddAlly wAddAl = new WorldAddAlly();

    public static WorldAddAlly getAddAl(AllyType type, short ID, Vector3 duty) {
        return wAddAl.getAddAl( ID, (byte) type.ordinal(), duty.x, duty.y, duty.z );
    }


    private static ChangeWorldLife cwl = new ChangeWorldLife();

    public static ChangeWorldLife getCWL(int life) {
        return cwl.getLife( life );
    }

    private static ChangeWorldMoney cwm = new ChangeWorldMoney();

    public static ChangeWorldMoney getCWM(int money, boolean isSetMoney) {
        if ( isSetMoney )
            return cwm.getSetMoney( money );
        else
            return cwm.getAddMoney( money );
    }
}
