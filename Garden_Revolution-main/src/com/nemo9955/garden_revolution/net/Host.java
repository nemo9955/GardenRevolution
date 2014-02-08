package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.WeaponChangedPacket;
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
        }
        catch (final IOException e) {
            e.printStackTrace();
            System.out.println( "[H]Connection failed" );
        }

        Log.set( Log.LEVEL_DEBUG );
    }

    @Override
    public void connected(final Connection connection) {
        // gp.showMessage( "[H]Someone connected" );
    }

    @Override
    public void received(final Connection connection, final Object obj) {
        Gdx.app.postRunnable( new Runnable() {

            @Override
            public void run() {
                if ( obj instanceof String ) {
                    gp.showMessage( "[H] : " +obj.toString() );
                }
                else if ( obj instanceof StartingServerInfo ) {

                    if ( gp.world.isCanWaveStart() ) {
                        connection.sendTCP( msNetGR.YouCannotConnect );
                        connection.close();
                    }
                    else {
                        final StartingServerInfo srv = (StartingServerInfo) obj;
                        connection.sendTCP( gp.world.getWorldInfo( srv ) );
                        // gp.showMessage( "[H] sending map info to client " );
                    }
                }
                else if ( obj instanceof PlayerChangesTower ) {
                    PlayerChangesTower plr = (PlayerChangesTower) obj;
                    if ( gp.world.root.canChangeTowers( plr.current, plr.next, plr.name ) ) {
                        server.sendToAllExceptTCP( connection.getID(), plr );
                        connection.sendTCP( msNetGR.YouCanChangeTowers );
                    }
                    else {
                        connection.sendTCP( msNetGR.YouCanNOT_ChangeTowers );
                    }
                }
                else if ( obj instanceof WeaponChangedPacket ) {
                    WeaponChangedPacket weap = (WeaponChangedPacket) obj;
                    gp.world.changeWeapon( weap.towerID, WeaponType.values()[weap.eOrdinal] );
                    server.sendToAllExceptTCP( connection.getID(), weap );
                }
                else if ( obj instanceof TowerChangedPacket ) {
                    TowerChangedPacket twr = (TowerChangedPacket) obj;
                    gp.world.upgradeTower( twr.towerID, TowerType.values()[twr.eOrdinal] );
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
            gp.world.setCanWaveStart( true );
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
            if ( gp.world.root.canChangeTowers( ( plr.current ==-1 ? null : gp.world.towers[plr.current] ), gp.world.towers[plr.next], gp.player ) ) {
                server.sendToAllTCP( plr );
            }
        }
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

}
