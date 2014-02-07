package com.nemo9955.garden_revolution.net;


public interface MultiplayerComponent {

    public void sendTCP(Object obj);

    public void sendUDP(Object obj);

    public void stop();

}
