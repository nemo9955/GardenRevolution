package com.nemo9955.garden_revolution.utility;


public class Vars {

    // densitatea ecranului
    public static float        densitate           = 1f;

    // modul de miscare a camerei
    public static boolean      moveByTouch         = false;
    public static float        tPadMinAlpha        = 0.2f;
    protected static float     tPadAlphaDellay     = 2f;

    // ( 0 , 3 ] viteza de miscare a camerei
    public static float        modCamSpeedX        = 2f;
    public static float        modCamSpeedY        = 0.7f;

    // directia de miscare a camerei folosind TtouchPad-ul
    public static byte         invertPadX          = -1;
    public static byte         invertPadY          = 1;

    // directia de miscare a camerei folosind mouse-ul
    public static byte         invertDragX         = -1;
    public static byte         invertDragY         = -1;

    // pentru debuging
    public static boolean      updateUave          = false;
    public static boolean      showDebug           = true;

    // pentru controller
    public static float        invertControlletY   = 1;
    public static float        invertControlletX   = -1;
    public static float        multiplyControlletY = 1;
    public static float        multiplyControlletX = 1;
    public static float        deadZone            = 0.01f;
    public static int          buton[]             = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
    public static final String butonName[]         = { "Fire", "2", "3", "4", "Previous camera", "Next Camera", "7", "8", "9", "10", "11", "12" };
    public static int          axis[]              = { 0, 1, 2, 3 };
    public static final String axisName[]          = { "Y Acceleration", "X Acceleration", "Y Movement", "X Movement" };
    public static final int    noButtons           = buton.length, noAxis = axis.length;

    // pentru comportamentul entitatilor
    public static final long   enemyAttackInterval = 2037;
    public static final long   allyAttackInterval  = 1956;

}
