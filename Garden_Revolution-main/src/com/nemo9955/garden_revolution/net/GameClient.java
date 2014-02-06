package com.nemo9955.garden_revolution.net;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
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
        client.setKeepAliveTCP( 9000 );
        client.start();
        client.addListener( this );
        Log.set( Log.LEVEL_DEBUG );

    }


    public void connect(String ip) {
        try {
            client.connect( 10000, ip, Vars.TCPport, Vars.UDPport );

            gp.showMessage( "[C] Created as CLIENT" );
        }
        catch (IOException e) {
            e.printStackTrace();
            gp.showMessage( "[C] Couldn't connect" );
        }

    }


    @Override
    public void received(Connection connection, final Object object) {
        if ( object instanceof String ) {
            gp.showMessage( "[C] : " +object.toString() );

        }
        else if ( object instanceof StartingServerInfo ) {
            Gdx.app.postRunnable( new Runnable() {

                @Override
                public void run() {
                    gp.postInit( new World((StartingServerInfo)object) );

                    Garden_Revolution.game.setScreen( Garden_Revolution.gameplay );

                }
            } );
        }

    }

    public void getServerMap() {
        client.sendTCP( new StartingServerInfo() );

    }

    public void brodcast(String string) {
        client.sendTCP( string );
    }

    public void stopClient() {
        client.stop();
    }

}
