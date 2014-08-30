package com.nemo9955.garden_revolution.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.SuperPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.stage.GameStageMaker;

public class Host extends Listener implements MultiplayerComponent {

	public Server	server;
	public int		clientsReady	= 0;

	public Host() {
		server = new Server();
		Func.setSerializedClasses(server.getKryo());
		server.start();
		server.addListener(this);

		try {
			// server.bind(Vars.TCPport, Vars.UDPport);
			server.bind(new InetSocketAddress(Func.getIpAddress(), Vars.TCPport), new InetSocketAddress(Func.getIpAddress(), Vars.UDPport));
			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {
					GR.game.setScreen(GR.gameplay);
				}
			});
		}
		catch ( final IOException e ) {
			stop();
			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {
					GR.multyplayer.showMessage(e.getMessage());
				}
			});
		}
	}

	@Override
	public void connected( final Connection connection ) {
		GR.gameplay.showMessage("[H]Someone connected : " + connection.getID());
	}

	@Override
	public void received( final Connection connection, final Object obj ) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				if ( obj instanceof String ) {
					GR.gameplay.showMessage("[H] : " + obj.toString());

				} else if ( obj instanceof SuperPacket ) {
					SuperPacket sp = (SuperPacket) obj;
					sp.doForHost(connection, obj);

				} else if ( obj instanceof msNetGR ) {
					final msNetGR message = (msNetGR) obj;
					switch ( message ) {
						case IAmReady :
							addToReady();
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
		server.stop();
	}

	public void addToReady() {
		clientsReady++;
		if ( clientsReady == server.getConnections().length + 1 ) {
			WorldWrapper.instance.getDef().setCanWaveStart(true);
			GameStageMaker.hudReady.setVisible(false);
			server.sendToAllTCP(msNetGR.YouCanStartWaves);
		}
	}

	@Override
	public void sendTCP( final Object obj ) {
		if ( processRecived(obj) )
			return;

		server.sendToAllTCP(obj);
	}

	@Override
	public void sendUDP( final Object obj ) {
		if ( processRecived(obj) )
			return;

		server.sendToAllUDP(obj);
	}

	private boolean processRecived( final Object obj ) {

		if ( obj instanceof PlayerChangesTower ) {
			PlayerChangesTower plr = (PlayerChangesTower) obj;
			Tower next = WorldWrapper.instance.getWorld().getTowers()[plr.next];

			if ( WorldWrapper.instance.getSgPl().changePlayerTower(GR.gameplay.player, next.ID) ) {
				server.sendToAllTCP(plr);
			}

		} else if ( obj instanceof msNetGR )
			switch ( (msNetGR) obj ) {
				case IAmReady :
					addToReady();
					return true;
				default :
					break;
			}
		return false;
	}

	@Override
	public boolean isHost() {
		return true;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public Client getClient() {
		return null;
	}

}
