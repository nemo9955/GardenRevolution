package com.nemo9955.garden_revolution.net;

import com.badlogic.gdx.files.FileHandle;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
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
    public boolean changeWeapon(byte towerID, WeaponType newWeapon) {

        System.out.println( "MP world changed weapon" );

        if ( super.changeWeapon( towerID, newWeapon ) ) {
            mp.sendTCP( Functions.getWCP( newWeapon.ordinal(), towerID ) );
            return true;
        }
        return false;
    }

    @Override
    public boolean upgradeTower(byte towerID, TowerType upgrade) {
        System.out.println( "MP world upgraded tower" );
        if ( super.upgradeTower( towerID, upgrade ) ) {
            mp.sendTCP( Functions.getTCP( upgrade.ordinal(), towerID ) );
            return true;
        }
        return false;
    }
}
