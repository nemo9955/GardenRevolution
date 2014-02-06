package com.nemo9955.garden_revolution.net.packets;


public class Packets {

    public static class StartingServerInfo {

        public String   path;
        public String[] turnuri;
    }

    public static enum msClient {
        IAmReady,
    };

    public static enum msHost {
        YouCannotConnect, YouCanStartWaves,
    };

}
