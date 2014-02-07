package com.nemo9955.garden_revolution.net.packets;


public class Packets {

    public static class StartingServerInfo {

        public String   path;
        public String[] turnuri;
    }

    public static class WeaponChangedPacket {

        public byte eOrdinal;
        public byte towerID;

        public WeaponChangedPacket getWCP(int eOrdinal, int towerID) {
            this.eOrdinal = (byte) eOrdinal;
            this.towerID = (byte) towerID;
            return this;
        }

    }

    public static class TowerChangedPacket {

        public byte eOrdinal;
        public byte towerID;

        public TowerChangedPacket getTCP(int eOrdinal, int towerID) {
            this.eOrdinal = (byte) eOrdinal;
            this.towerID = (byte) towerID;
            return this;
        }
    }

    public static enum msNetGR {
        IAmReady, YouCannotConnect, YouCanStartWaves,
    };

}
