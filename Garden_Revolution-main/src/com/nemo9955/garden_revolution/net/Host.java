package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.ChangeWorldLife;
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


public class Host extends Listener implements MultiplayerComponent {

    public Server          server;
    private final Gameplay gp;
    public int             clientsReady = 0;

    public Host(final Gameplay gp) {
        this.gp = gp;
        server = new Server();
        Functions.setSerializedClasses( server.getKryo() );
        server.start();
        server.addListener( this );

        try {
            server.bind( Vars.TCPport, Vars.UDPport );
            Gdx.app.postRunnable( new Runnable() {

                public void run() {
                    GR.game.setScreen( GR.gameplay );
                }
            } );
        }
        catch (final IOException e) {
            server.close();
            Gdx.app.postRunnable( new Runnable() {

                public void run() {
                    GR.multyplayer.showMessage( e.getMessage() );
                }
            } );
        }
    }

    @Override
    public void connected(final Connection connection) {
        // gp.showMessage( "[H]Someone connected" );
    }

    @Override
    public void received(final Connection connection, final Object obj) {
        Gdx.app.postRunnable( new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if ( obj instanceof String ) {
                    gp.showMessage( "[H] : " +obj.toString() );
                }
                else if ( obj instanceof ChangeWorldLife ) {
                    ChangeWorldLife lf = (ChangeWorldLife) obj;
                    gp.world.getSgPl().setLife( lf.life );
                    server.sendToAllExceptTCP( connection.getID(), lf );
                }
                else if ( obj instanceof WorldAddEnemyOnPath ) {
                    WorldAddEnemyOnPath ent = (WorldAddEnemyOnPath) obj;
                    Enemy addFoe = gp.world.getSgPl().addFoe( EnemyType.values()[ent.ordinal], gp.world.getWorld().getPaths().get( ent.pathNo ) );
                    addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
                    server.sendToAllExceptTCP( connection.getID(), ent );
                }
                else if ( obj instanceof WorldAddEnemyOnPoz ) {
                    WorldAddEnemyOnPoz ent = (WorldAddEnemyOnPoz) obj;
                    Enemy addFoe = gp.world.getSgPl().addFoe( EnemyType.values()[ent.ordinal], Vector3.tmp3.set( ent.x, ent.y, ent.z ) );
                    addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
                    server.sendToAllExceptTCP( connection.getID(), ent );
                }
                else if ( obj instanceof WorldAddAlly ) {
                    WorldAddAlly waa = (WorldAddAlly) obj;
                    gp.world.getSgPl().addAlly( Vector3.tmp3.set( waa.x, waa.y, waa.z ), AllyType.values()[waa.ordinal] );
                    server.sendToAllExceptTCP( connection.getID(), waa );
                }
                else if ( obj instanceof StartingServerInfo ) {

                    if ( gp.world.getWorld().canWaveStart() ) {
                        connection.sendTCP( msNetGR.YouCannotConnect );
                        connection.close();
                    }
                    else {
                        final StartingServerInfo srv = (StartingServerInfo) obj;
                        connection.sendTCP( gp.world.getDef().getWorldInfo( srv ) );
                        // gp.showMessage( "[H] sending map info to client " );
                    }
                }
                else if ( obj instanceof TowerDirectionChange ) {
                    TowerDirectionChange tdr = (TowerDirectionChange) obj;
                    gp.world.getWorld().getTowers()[tdr.ID].setDirection( tdr.x, tdr.y, tdr.z );
                    // System.out.println( "[H] mod dir turn        " +tdr.ID +"        " +tdr.x +" " +tdr.y +" " +tdr.z );
                    server.sendToAllExceptTCP( connection.getID(), tdr );
                }
                else if ( obj instanceof PlayerFireCharged ) {
                    PlayerFireCharged pfa = (PlayerFireCharged) obj;
                    Tower tower = gp.world.getWorld().getTowers()[pfa.towerID];
                    if ( tower.isWeaponType( FireType.FIRECHARGED ) )
                        gp.world.getSgPl().fireFromTower( tower, pfa.charge );
                    server.sendToAllExceptTCP( connection.getID(), pfa );
                }
                else if ( obj instanceof PlayerFireHold ) {
                    PlayerFireHold pfh = (PlayerFireHold) obj;

                    Tower tower = gp.world.getWorld().getTowers()[pfh.towerID];
                    if ( tower.isWeaponType( FireType.FIREHOLD ) )
                        gp.world.getSgPl().setTowerFireHold( tower, pfh.isFiring );
                    server.sendToAllExceptTCP( connection.getID(), pfh );
                }
                else if ( obj instanceof PlayerChangesTower ) {
                    PlayerChangesTower plr = (PlayerChangesTower) obj;
                    if ( gp.world.getSgPl().canChangeTowers( plr.current, plr.next, plr.name ) ) {
                        server.sendToAllExceptTCP( connection.getID(), plr );
                        connection.sendTCP( msNetGR.YouCanChangeTowers );
                    }
                    else {
                        connection.sendTCP( msNetGR.YouCanNOT_ChangeTowers );
                    }
                }
                else if ( obj instanceof WeaponChangedPacket ) {
                    WeaponChangedPacket weap = (WeaponChangedPacket) obj;
                    gp.world.getDef().changeWeapon( gp.world.getWorld().getTowers()[weap.towerID], WeaponType.values()[weap.eOrdinal] );
                    server.sendToAllExceptTCP( connection.getID(), weap );
                }
                else if ( obj instanceof TowerChangedPacket ) {
                    TowerChangedPacket twr = (TowerChangedPacket) obj;
                    gp.world.getDef().upgradeTower( gp.world.getWorld().getTowers()[twr.towerID], TowerType.values()[twr.eOrdinal] );
                    server.sendToAllExceptTCP( connection.getID(), twr );

                }
                else if ( obj instanceof msNetGR ) {
                    final msNetGR message = (msNetGR) obj;
                    switch (message) {
                        case IAmReady:
                            addToReady();
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
        server.stop();
    }

    public void addToReady() {
        clientsReady ++;
        if ( clientsReady ==server.getConnections().length +1 ) {
            gp.world.getDef().setCanWaveStart( true );
            gp.ready.setVisible( false );
            server.sendToAllTCP( msNetGR.YouCanStartWaves );
        }
    }

    @Override
    public void sendTCP(final Object obj) {
        if ( precessRecived( obj ) )
            return;

        server.sendToAllTCP( obj );
    }

    @Override
    public void sendUDP(final Object obj) {
        if ( precessRecived( obj ) )
            return;

        server.sendToAllUDP( obj );
    }

    private boolean precessRecived(final Object obj) {

        if ( obj instanceof PlayerChangesTower ) {
            PlayerChangesTower plr = (PlayerChangesTower) obj;
            Tower next = gp.world.getWorld().getTowers()[plr.next];

            if ( gp.world.getSgPl().changePlayerTower( gp.player, next.ID ) ) {
                server.sendToAllTCP( plr );
            }
        }
        // else if ( obj instanceof WorldAddEnemyOnPath ) {
        // WorldAddEnemyOnPath ent = (WorldAddEnemyOnPath) obj;
        // Enemy addFoe = gp.world.getSgPl().addFoe( EnemyType.values()[ent.ordinal], gp.world.getWorld().getPaths().get( ent.pathNo ) );
        // addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
        // }
        // else if ( obj instanceof WorldAddEnemyOnPoz ) {
        // WorldAddEnemyOnPoz ent = (WorldAddEnemyOnPoz) obj;
        // Enemy addFoe = gp.world.getSgPl().addFoe( EnemyType.values()[ent.ordinal], Vector3.tmp3.set( ent.x, ent.y, ent.z ) );
        // addFoe.offset.set( Functions.getOffset( ent.ofsX ), 0, Functions.getOffset( ent.ofsZ ) );
        // }
        // else if ( obj instanceof WorldAddAlly ) {
        // WorldAddAlly waa = (WorldAddAlly) obj;
        // gp.world.getSgPl().addAlly( Vector3.tmp3.set( waa.x, waa.y, waa.z ), AllyType.values()[waa.ordinal] );
        // }
        else if ( obj instanceof msNetGR )
            switch ((msNetGR) obj) {
                case IAmReady:
                    addToReady();
                    return true;
                default:
                    break;
            }
        return false;
    }

    @Override
    public boolean isHost() {
        return true;
    }

}
