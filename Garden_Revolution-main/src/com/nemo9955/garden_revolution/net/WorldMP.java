package com.nemo9955.garden_revolution.net;

import com.badlogic.gdx.files.FileHandle;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.utility.Functions;


public class WorldMP extends World {

    private MultiplayerComponent mp;

    public WorldMP(FileHandle location, MultiplayerComponent mp) {
        super( location );
        this.mp = mp;
    }

    public WorldMP(StartingServerInfo info, MultiplayerComponent mp) {
        super( info );
        this.mp = mp;
    }

    @Override
    public void setViata(int viata) {
        super.setViata( viata );
    }

    @Override
    public void addViata(int amount) {
        super.addViata( amount );
    }

    @Override
    public boolean canChangeTowers(Tower current, Tower next, Player player) {
        mp.sendTCP( Functions.getPCT( ( current ==null ? -1 : current.ID ), next.ID, player.name ) );
        return false;
    }

    @Override
    public boolean changeWeapon(byte towerID, WeaponType newWeapon) {


        if ( super.changeWeapon( towerID, newWeapon ) ) {
            System.out.println( "MP world changed weapon" );
            mp.sendTCP( Functions.getWCP( towerID, newWeapon.ordinal() ) );
            return true;
        }
        return false;
    }

    @Override
    public boolean upgradeTower(byte towerID, TowerType upgrade) {
        if ( super.upgradeTower( towerID, upgrade ) ) {
            System.out.println( "MP world upgraded tower" );
            mp.sendTCP( Functions.getTCP( towerID, upgrade.ordinal() ) );
            return true;
        }
        return false;
    }
}
