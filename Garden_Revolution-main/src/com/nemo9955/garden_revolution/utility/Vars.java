package com.nemo9955.garden_revolution.utility;


public class Vars {

    // densitatea ecranului
    public static float           densitate           = 1f;

    // modul de miscare a camerei
    public static boolean         moveByTouch         = false;
    public static float           tPadMinAlpha        = 0.1f;
    protected static float        tPadAlphaDellay     = 2f;

    // ( 0 , 3 ] viteza de miscare a camerei
    public static float           modCamSpeedX        = 2f;
    public static float           modCamSpeedY        = 0.7f;

    // directia de miscare a camerei folosind TtouchPad-ul
    public static byte            invertPadX          = -1;
    public static byte            invertPadY          = 1;

    // directia de miscare a camerei folosind mouse-ul
    public static byte            invertDragX         = -1;
    public static byte            invertDragY         = -1;

    // pentru debuging
    public static boolean         updateUave          = true;
    public static boolean         showDebug           = false;

    // pentru controller
    public static float           invertControlletY   = 1;
    public static float           invertControlletX   = -1;
    public static float           multiplyControlletY = 1;
    public static float           multiplyControlletX = 1;
    public static float           deadZone            = 0.01f;
    // public static int buton[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
    // public static final String butonName[] = { "Fire", "2", "3", "4", "Previous camera", "Next Camera", "7", "8", "9", "10", "11", "12" };
    // public static int axis[] = { 2, 3, 1, 0 };
    // public static final String axisName[] = { "Y Acceleration", "X Acceleration", "Y Movement", "X Movement" };
    public static final int       noButtons           = CoButt.values().length, noAxis = CoAxis.values().length;

    // pentru comportamentul entitatilor
    public static final long      enemyAttackInterval = 868;
    public static final long      allyAttackInterval  = 696;


    // variabile multyplayer
    public static int             TCPport             = 29955;
    public static int             UDPport             = 39955;
    public static final String    stringSeparator     = "---";

    protected static final String waitingMessage      = "Waiting for others ...";


    public enum CoButt {
        Fire(0, "Fire"), Back(1, "Back"), NextT(2, "Next Tower"), PrevT(3, "Previews Tower"), A(4, "A Button"), B(5, "B Button"), X(6, "X Button"), Y(7, "Y Button");

        public int    id;
        public String name;

        CoButt(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public enum CoAxis {
        mvY(1, "Move Y"), mvX(0, "Move X");

        public int    id;
        public String name;

        CoAxis(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


}
