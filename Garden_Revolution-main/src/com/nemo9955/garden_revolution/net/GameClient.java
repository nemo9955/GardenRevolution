package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.AllyKilled;
import com.nemo9955.garden_revolution.net.packets.Packets.ChangeWorldLife;
import com.nemo9955.garden_revolution.net.packets.Packets.EnemyKilled;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerFireCharged;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerFireHold;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerDirectionChange;
import com.nemo9955.garden_revolution.net.packets.Packets.WeaponChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddAlly;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddEnemyOnPath;
import com.nemo9955.garden_revolution.net.packets.Packets.WorldAddEnemyOnPoz;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class GameClient extends Listener implements MultiplayerComponent {


    public Client          client;
    private final Gameplay gp;

    public GameClient(final Gameplay gp, final String ip) {
        this.gp = gp;
        client = new Client();
        Functions.setSerializedClasses( client.getKryo() );
        client.setKeepAliveTCP( 9000 );
        client.start();
        client.addListener( this );
        // Log.set( Log.LEVEL_DEBUG );

        try {
            client.connect( 10000, ip, Vars.TCPport, Vars.UDPport );

        }
        catch (final IOException e) {
            // e.printStackTrace();
            // gp.showMessage( "[C] Couldn't connect" );TODO
            stop();
            Gdx.app.postRunnable( new Runnable() {

                public void run() {
                    GR.multyplayer.showMessage( e.getMessage() );
                }
            } );
        }

        // gp.showMessage( "[C] Created as CLIENT" );
    }


    @Override
    public void received(final Connection connection, final Object obj) {
        Gdx.app.postRunnable( new Runnable() {

            @Override
            public void run() {
                if ( obj instanceof String ) {
                    gp.showMessage( "[C] : " +obj.toString() );

                }
                else if ( obj instanceof ChangeWorldLife ) {
                    ChangeWorldLife lf = (ChangeWorldLife) obj;
                    gp.world.getSgPl().setLife( lf.life );
                }
                else if ( obj instanceof WorldAddEnemyOnPath ) {
                    WorldAddEnemyOnPath ent = (WorldAddEnemyOnPath) obj;
                    Enemy addFoe = gp.world.getSgPl().addFoe( EnemyType.values()[ent.ordinal], gp.world.getWorld().getPaths().get( ent.pathNo ) );
                    addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
                    addFoe.ID = ent.ID;
                }
                else if ( obj instanceof WorldAddEnemyOnPoz ) {
                    WorldAddEnemyOnPoz ent = (WorldAddEnemyOnPoz) obj;
                    Enemy addFoe = gp.world.getSgPl().addFoe( EnemyType.values()[ent.ordinal], GR.temp2.set( ent.x, ent.y, ent.z ) );
                    addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
                    addFoe.ID = ent.ID;
                }
                else if ( obj instanceof StartingServerInfo ) {
                    StartingServerInfo ssi = (StartingServerInfo) obj;
                    gp.postInit( gp.world.init( ssi, gp.mp ) );
                    GR.game.setScreen( GR.gameplay );
                }
                else if ( obj instanceof WorldAddAlly ) {
                    WorldAddAlly waa = (WorldAddAlly) obj;
                    gp.world.getSgPl().addAlly( GR.temp2.set( waa.x, waa.y, waa.z ), AllyType.values()[waa.ordinal] ).ID = waa.ID;
                }
                else if ( obj instanceof WeaponChangedPacket ) {
                    final WeaponChangedPacket weap = (WeaponChangedPacket) obj;
                    gp.world.getSgPl().changeWeapon( gp.world.getWorld().getTowers()[weap.towerID], WeaponType.values()[weap.eOrdinal] );
                }
                else if ( obj instanceof PlayerFireCharged ) {
                    PlayerFireCharged pfa = (PlayerFireCharged) obj;

                    Tower tower = gp.world.getWorld().getTowers()[pfa.towerID];
                    if ( tower.isWeaponType( FireType.FIRECHARGED ) ) {
                        tower.charge = pfa.charge;
                        gp.world.getSgPl().fireFromTower( tower );
                    }
                }
                else if ( obj instanceof PlayerFireHold ) {
                    PlayerFireHold pfh = (PlayerFireHold) obj;

                    Tower tower = gp.world.getWorld().getTowers()[pfh.towerID];
                    if ( tower.isWeaponType( FireType.FIREHOLD ) )
                        gp.world.getSgPl().setTowerFireHold( tower, pfh.isFiring );
                }
                else if ( obj instanceof TowerChangedPacket ) {
                    final TowerChangedPacket twr = (TowerChangedPacket) obj;
                    gp.world.getSgPl().upgradeTower( gp.world.getWorld().getTowers()[twr.towerID], TowerType.values()[twr.eOrdinal] );
                }
                else if ( obj instanceof TowerDirectionChange ) {
                    final TowerDirectionChange tdr = (TowerDirectionChange) obj;
                    gp.world.getWorld().getTowers()[tdr.ID].setDirection( tdr.x, tdr.y, tdr.z );
                    // System.out.println( "[C] modificat dir turn    " +tdr.ID +"      " +tdr.x +" " +tdr.y +" " +tdr.z );
                }
                else if ( obj instanceof PlayerChangesTower ) {
                    final PlayerChangesTower plr = (PlayerChangesTower) obj;
                    gp.world.getSgPl().canChangeTowers( plr.current, plr.next, plr.name );
                }
                else if ( obj instanceof AllyKilled ) {
                    AllyKilled aly = (AllyKilled) obj;
                    gp.world.getSgPl().killAlly( aly.ID );
                }
                else if ( obj instanceof EnemyKilled ) {
                    EnemyKilled enmy = (EnemyKilled) obj;
                    gp.world.getSgPl().killEnemy( enmy.ID );
                }
                else if ( obj instanceof msNetGR ) {
                    final msNetGR message = (msNetGR) obj;
                    switch (message) {
                        case YouCanStartWaves:
                            gp.world.getSgPl().setCanWaveStart( true );
                            gp.ready.setVisible( false );
                            break;
                        case YouCannotConnect:
                            GR.multyplayer.showMessage( "The game already started !" );
                            break;
                        case YouCanChangeTowers:
                            gp.world.getSgPl().changePlayerTower( gp.player, towerToChange );
                            towerToChange = -1;
                            break;
                        case YouCanNOT_ChangeTowers:
                            towerToChange = -1;
                            break;
                        default:
                            break;
                    }
                }

            }
        } );
    }

    @Override
    public void stop() {
        client.stop();
    }


    @Override
    public void sendTCP(final Object obj) {
        if ( precessRecived( obj ) )
            return;
        client.sendTCP( obj );
    }

    @Override
    public void sendUDP(final Object obj) {
        if ( precessRecived( obj ) )
            return;
        client.sendUDP( obj );
    }

    private byte towerToChange = -1;

    private boolean precessRecived(final Object obj) {
        if ( obj instanceof PlayerChangesTower &&towerToChange ==-1 ) {
            PlayerChangesTower plr = (PlayerChangesTower) obj;
            towerToChange = plr.next;
        }

        return false;
    }


    @Override
    public boolean isHost() {
        return false;
    }

}
