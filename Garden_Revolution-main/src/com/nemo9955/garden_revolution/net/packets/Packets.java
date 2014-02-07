package com.nemo9955.garden_revolution.net.packets;


public class Packets {

    public static class StartingServerInfo {

        public String   path;
        public String[] turnuri;
    }

    public static enum msNetGR {
      IAmReady,  YouCannotConnect, YouCanStartWaves,
    };

}
