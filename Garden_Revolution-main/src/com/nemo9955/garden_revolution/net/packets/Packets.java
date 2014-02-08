package com.nemo9955.garden_revolution.net.packets;


public class Packets {

    public static class StartingServerInfo {

        public String   path;
        public String[] turnuri;
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