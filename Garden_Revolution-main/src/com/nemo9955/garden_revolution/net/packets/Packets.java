package com.nemo9955.garden_revolution.net.packets;

import com.badlogic.gdx.math.Vector3;


public class Packets {

    public static class StartingServerInfo {

        public String   path;
        public String[] turnuri;
        public String[] players;
    }

    public static class PlayerFireCharged {

        public byte  towerID;
        public float charge;

        public PlayerFireCharged getPFC(byte towerID, float charge) {
            this.towerID = towerID;
            this.charge = charge;
            return this;
        }

    }

    public static class PlayerFireHold {

        public byte    towerID;
        public boolean isFiring;

        public PlayerFireHold getPFH(byte towerID, boolean isFiring) {
            this.towerID = towerID;
            this.isFiring = isFiring;
            return this;
        }

    }

    public static class TowerDirectionChange {

        public byte ID;
        public float x, y, z;

        public TowerDirectionChange getTDC(byte ID, float x, float y, float z) {
            this.ID = ID;
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public TowerDirectionChange getTDC(byte ID, Vector3 dir) {
            this.ID = ID;
            this.x = dir.x;
            this.y = dir.y;
            this.z = dir.z;
            return this;
        }
    }

    public static class WeaponChangedPacket {

        public byte eOrdinal;
        public byte towerID;

        public WeaponChangedPacket getWCP(int eOrdinal, byte towerID) {
            this.eOrdinal = (byte) eOrdinal;
            this.towerID = towerID;
            return this;
        }

    }

    public static class TowerChangedPacket {

        public byte eOrdinal;
        public byte towerID;

        public TowerChangedPacket getTCP(int eOrdinal, byte towerID) {
            this.eOrdinal = (byte) eOrdinal;
            this.towerID = towerID;
            return this;
        }
    }


    public static class PlayerChangesTower {

        public byte   current;
        public byte   next;
        public String name;

        public PlayerChangesTower getPCT(byte current, byte next, String name) {
            this.current = current;
            this.next = next;
            this.name = name;
            return this;
        }


    }

    public static enum msNetGR {
        IAmReady, YouCannotConnect, YouCanStartWaves, YouCanChangeTowers, YouCanNOT_ChangeTowers,
    };


    // public static class Foo {
    //
    // public Foo root;
    //
    // public Foo() {
    // root = this;
    // }
    //
    // public void method() {
    // System.out.println( "This is foo" );
    // }
    //
    // }
    //
    // public static class SuperFoo extends Foo {
    //
    // @Override
    // public void method() {
    // System.out.println( "this is super foo" );
    // super.method();
    // }
    //
    // }
    //
    // public static void main(String[] args) {
    // Foo foo = new SuperFoo();
    // foo.method();
    // }


}