package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.utility.Vars;


public class GameClient extends Listener {


    public Client    client;
    private Gameplay gp;

    public GameClient(Gameplay gp) {
        this.gp = gp;
        client = new Client();
        client.start();
        client.addListener( this );
        Kryo kryo = client.getKryo();
        kryo.register( String.class );

    }


    public void connect(String ip) {
        try {
            client.connect( 10000, ip, Vars.TCPport, Vars.UDPport );
        }
        catch (IOException e) {
            e.printStackTrace();
            gp.showMessage( "Couldn't connect" );
        }

    }


    @Override
    public void received(Connection connection, Object object) {
        if ( object instanceof String ) {
            gp.showMessage( "[C] : " +object.toString() );

        }

    }

    public void brodcast(String string) {
        client.sendTCP( string );
    }

    public void stopClient() {
        client.stop();
    }

}
