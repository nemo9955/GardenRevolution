package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class Host extends Listener {

    public Server    server;

    private Gameplay gp;

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
    }

    @Override
    public void received(Connection connection, Object object) {
        if ( object instanceof String ) {
            gp.showMessage( "[H] : " +object.toString() );
        }
        else if ( object instanceof StartingServerInfo ) {
            StartingServerInfo srv = (StartingServerInfo) object;
            connection.sendTCP( gp.world.getWorldInfo( srv ) );
            gp.showMessage( "[H] sending map info to client " );
        }
    }

    public void stopHost() {
        server.stop();
    }

}
