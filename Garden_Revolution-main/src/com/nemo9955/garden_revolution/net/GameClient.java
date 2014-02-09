package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
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

        }
        catch (final IOException e) {
            // e.printStackTrace();
            // gp.showMessage( "[C] Couldn't connect" );TODO
            client.close();
            Gdx.app.postRunnable( new Runnable() {

                public void run() {
                    Garden_Revolution.multyplayer.showMessage( e.getMessage() );
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
                else if ( obj instanceof StartingServerInfo ) {
                    gp.postInit( new WorldWrapper( (StartingServerInfo) obj, gp.mp ) );
                    Garden_Revolution.game.setScreen( Garden_Revolution.gameplay );
                }
                else if ( obj instanceof WeaponChangedPacket ) {
                    final WeaponChangedPacket weap = (WeaponChangedPacket) obj;

                    gp.world.getSgPl().changeWeapon( weap.towerID, WeaponType.values()[weap.eOrdinal] );

                }
                else if ( obj instanceof TowerChangedPacket ) {
                    final TowerChangedPacket twr = (TowerChangedPacket) obj;

                    gp.world.getSgPl().upgradeTower( twr.towerID, TowerType.values()[twr.eOrdinal] );

                }
                else if ( obj instanceof PlayerChangesTower ) {
                    PlayerChangesTower plr = (PlayerChangesTower) obj;
                    gp.world.getSgPl().canChangeTowers( plr.current, plr.next, plr.name );
                }
                else if ( obj instanceof msNetGR ) {
                    final msNetGR message = (msNetGR) obj;
                    switch (message) {
                        case YouCanStartWaves:
                            gp.world.getSgPl().setCanWaveStart( true );
                            gp.ready.setVisible( false );
                            break;
                        case YouCannotConnect:
                            Garden_Revolution.multyplayer.showMessage( "The game already started !" );
                            break;
                        case YouCanChangeTowers:
                            // gp.world.getSgPl().canChangeTowers( gp.player.getTower(), gp.world.getDef().getTowers()[towerToChange], gp.player );
                            // gp.player.canChangeTower( gp.world.getDef().getTowers()[towerToChange] );
                            // gp.player.changeTower( towerToChange );
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

}
