package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
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
            e.printStackTrace();
            gp.showMessage( "[C] Couldn't connect" );
        }
    }


    @Override
    public void received(final Connection connection, final Object obj) {
        if ( obj instanceof String ) {
            gp.showMessage( "[C] : " +obj.toString() );

        }
        else if ( obj instanceof StartingServerInfo ) {
            Gdx.app.postRunnable( new Runnable() {

                @Override
                public void run() {
                    gp.postInit( new World( (StartingServerInfo) obj ) );

                    Garden_Revolution.game.setScreen( Garden_Revolution.gameplay );

                }
            } );
        }
        else if ( obj instanceof msNetGR ) {
            final msNetGR message = (msNetGR) obj;
            switch (message) {
                case YouCanStartWaves:
                    gp.world.setCanWaveStart( true );
                    gp.ready.setVisible( false );
                    break;
                case YouCannotConnect:
                    Gdx.app.postRunnable( new Runnable() {

                        @Override
                        public void run() {
                            Garden_Revolution.multyplayer.theGameStarted();
                            ;
                        }
                    } );
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void stop() {
        client.stop();
    }


    @Override
    public void sendTCP(final Object obj) {
        client.sendTCP( obj );
    }

    @Override
    public void sendUDP(final Object obj) {
        client.sendUDP( obj );
    }

}
