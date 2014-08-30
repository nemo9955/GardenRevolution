package com.nemo9955.garden_revolution.net;

import java.io.IOException;
import java.net.InetAddress;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.SuperPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.stage.GameStageMaker;

public class GameClient extends Listener implements MultiplayerComponent {

	public Client	client;

	public GameClient(final String ip) {
		client = new Client();
		Func.setSerializedClasses(client.getKryo());
		client.start();
		client.addListener(this);
		// Log.set( Log.LEVEL_DEBUG );

		try {
			client.connect(10000, InetAddress.getByName(ip), Vars.TCPport, Vars.UDPport);
		}
		catch ( final IOException e ) {
			// e.printStackTrace();
			// gp.showMessage( "[C] Couldn't connect" );TODO
			stop();
			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {
					GR.multyplayer.showMessage(e.getMessage());
				}
			});
			e.printStackTrace();
		}

		// gp.showMessage( "[C] Created as CLIENT" );
	}

	@Override
	public void received( final Connection connection, final Object obj ) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				if ( obj instanceof String ) {
					GR.gameplay.showMessage("[C] : " + obj.toString());

				} else if ( obj instanceof SuperPacket ) {
					SuperPacket sp = (SuperPacket) obj;
					sp.doForClient(connection, obj);

				} else if ( obj instanceof msNetGR ) {
					final msNetGR message = (msNetGR) obj;
					switch ( message ) {
						case YouCanStartWaves :
							WorldWrapper.instance.getSgPl().setCanWaveStart(true);
							GameStageMaker.hudReady.setVisible(false);
							break;
						case YouCannotConnect :
							GR.multyplayer.showMessage("The game already started !");
							break;
						case YouCanChangeTowers :
							WorldWrapper.instance.getSgPl().changePlayerTower(GR.gameplay.player, towerToChange);
							towerToChange = -1;
							break;
						case YouCanNOT_ChangeTowers :
							towerToChange = -1;
							break;
						default :
							break;
					}
				}
			}
		});
	}

	@Override
	public void stop() {
		client.stop();
	}

	@Override
	public void sendTCP( final Object obj ) {
		if ( processRecived(obj) )
			return;
		client.sendTCP(obj);
	}

	@Override
	public void sendUDP( final Object obj ) {
		if ( processRecived(obj) )
			return;
		client.sendUDP(obj);
	}

	private byte	towerToChange	= -1;

	private boolean processRecived( final Object obj ) {
		if ( obj instanceof PlayerChangesTower && towerToChange == -1 ) {
			PlayerChangesTower plr = (PlayerChangesTower) obj;
			towerToChange = plr.next;
		}

		return false;
	}

	@Override
	public boolean isHost() {
		return false;
	}

	@Override
	public Server getServer() {
		return null;
	}

	@Override
	public Client getClient() {
		return client;
	}

}
