package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.net.packets.Packets.msClient;
import com.nemo9955.garden_revolution.net.packets.Packets.msHost;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class Host extends Listener {

    public Server    server;
    private Gameplay gp;
    public int       clientsReady = 0;

    public Host(Gameplay gp) {
        this.gp = gp;
        server = new Server();
        Functions.setSerializedClasses( server.getKryo() );
        server.start();
        server.addListener( this );

        try {
            server.bind( Vars.TCPport, Vars.UDPport );
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println( "[H]Connection failed" );
        }

        Log.set( Log.LEVEL_DEBUG );
    }

    public void brodcast(String mesaj) {
        server.sendToAllTCP( mesaj );
    }

    @Override
    public void connected(Connection connection) {
        gp.showMessage( "[H]Someone connected" );
        if ( gp.world.isCanWaveStart() ) {
            connection.sendTCP( msHost.YouCannotConnect );
            connection.close();
        }
    }

    @Override
    public void received(Connection connection, Object obj) {
        if ( obj instanceof String ) {
            gp.showMessage( "[H] : " +obj.toString() );
        }
        else if ( obj instanceof StartingServerInfo ) {
            StartingServerInfo srv = (StartingServerInfo) obj;
            connection.sendTCP( gp.world.getWorldInfo( srv ) );
//            gp.showMessage( "[H] sending map info to client " );
        }
        else if ( obj instanceof msClient ) {
            msClient message = (msClient) obj;
            switch (message) {
                case IAmReady:
                    addToReady();
                    break;
                default:
                    break;
            }
        }
    }

    public void sendToAllTCP(Object obj) {
        server.sendToAllTCP( obj );
    }

    public void sendToAllUDP(Object obj) {
        server.sendToAllTCP( obj );
    }

    public void stopHost() {
        server.stop();
    }

    public void addToReady() {
        clientsReady ++;
        if ( clientsReady ==server.getConnections().length +1 ) {
            gp.world.setCanWaveStart( true );
            gp.ready.setVisible( false );
            server.sendToAllTCP( msHost.YouCanStartWaves );
        }
    }

}
