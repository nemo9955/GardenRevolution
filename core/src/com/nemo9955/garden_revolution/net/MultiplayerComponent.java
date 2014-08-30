package com.nemo9955.garden_revolution.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public interface MultiplayerComponent {

	public void sendTCP( Object obj );

	public void sendUDP( Object obj );

	public void stop();

	public boolean isHost();

	public Server getServer();

	public Client getClient();

}
