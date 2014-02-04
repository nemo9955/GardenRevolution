package com.nemo9955.garden_revolution.net;

import java.io.IOException;

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
    private boolean  isWaiting = false;

    public GameClient(Gameplay gp) {
        this.gp = gp;
        client = new Client();
        client.start();
        client.addListener( this );
        Functions.setSerializedClasses( client.getKryo() );

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
        else if ( object instanceof FileHandle ) {
            FileHandle theMap = (FileHandle) object;
            System.out.println( "[C] writing pab from server : " +theMap.path() );
            gp.mapLoc = new FileHandle( theMap.path() );
            isWaiting = false;
        }

    }

    public void getServerMap() {
        long time = System.currentTimeMillis();
        isWaiting = true;

        client.sendTCP( new MapOfServer() );

        while ( !isWaiting ||System.currentTimeMillis() -time >3000 ) {

        }
    }

    public void brodcast(String string) {
        client.sendTCP( string );
    }

    public void stopClient() {
        client.stop();
    }

}
