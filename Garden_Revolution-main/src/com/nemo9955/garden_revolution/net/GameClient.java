package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nemo9955.garden_revolution.Garden_Revolution;
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

            gp.showMessage( "[C] Created as CLIENT" );
        }
        catch (final IOException e) {
            // e.printStackTrace();
            // gp.showMessage( "[C] Couldn't connect" );
            Gdx.app.postRunnable( new Runnable() {

                public void run() {
                    Garden_Revolution.multyplayer.showMessage( "Couldn't connect to server !" );
                }
            } );

        }
    }


    @Override
    public void received(final Connection connection, final Object obj) {
        Gdx.app.postRunnable( new Runnable() {

            @Override
            public void run() {
                if ( obj instanceof String ) {
                    gp.showMessage( "[C] : " +obj.toString() );

                }
                else if ( obj instanceof StartingServerInfo ) {
                    gp.postInit( new WorldMP( (StartingServerInfo) obj, gp.mp ) );

                    Garden_Revolution.game.setScreen( Garden_Revolution.gameplay );


                }
                else if ( obj instanceof WeaponChangedPacket ) {
                    final WeaponChangedPacket weap = (WeaponChangedPacket) obj;

                    gp.world.root.changeWeapon( weap.towerID, WeaponType.values()[weap.eOrdinal] );

                }
                else if ( obj instanceof TowerChangedPacket ) {
                    final TowerChangedPacket twr = (TowerChangedPacket) obj;

                    gp.world.root.upgradeTower( twr.towerID, TowerType.values()[twr.eOrdinal] );

                }
                else if ( obj instanceof PlayerChangesTower ) {
                    PlayerChangesTower plr = (PlayerChangesTower) obj;
                    gp.world.root.canChangeTowers( plr.current, plr.next, plr.name );
                }
                else if ( obj instanceof msNetGR ) {
                    final msNetGR message = (msNetGR) obj;
                    switch (message) {
                        case YouCanStartWaves:
                            gp.world.root.setCanWaveStart( true );
                            gp.ready.setVisible( false );
                            break;
                        case YouCannotConnect:
                            Garden_Revolution.multyplayer.showMessage( "The game already started !" );
                            break;
                        case YouCanChangeTowers:
                            gp.world.canChangeTowers( gp.player.getTower(), gp.world.towers[towerToChange], gp.player );
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

}
