package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.nemo9955.garden_revolution.net.Packets.MapOfServer;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.utility.Functions;
import com.nemo9955.garden_revolution.utility.Vars;


public class GameClient extends Listener {


    public Client    client;
    private Gameplay gp;

    public GameClient(Gameplay gp) {
        this.gp = gp;
        client = new Client();
        Functions.setSerializedClasses( client.getKryo() );
        client.start();
        client.addListener( this );

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
        else if ( object instanceof MapOfServer ) {
            final MapOfServer map = (MapOfServer) object;
            // gp.mapLoc = new FileHandle( theMap.path() );

            Gdx.app.postRunnable( new Runnable() {

                @Override
                public void run() {
                    gp.mapLoc = new FileHandle( map.path );
                    gp.showMessage( "[C] writing path from server : " +map.path );
                    gp.postInit( gp.mapLoc );

                }
            } );
        }

    }

    public void getServerMap() {
        client.sendTCP( new MapOfServer() );

    }

    public void brodcast(String string) {
        client.sendTCP( string );
    }

    public void stopClient() {
        client.stop();
    }

}
