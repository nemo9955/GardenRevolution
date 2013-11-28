package com.nemo9955.garden_revolution.utility;

public class Mod {

    // densitatea ecranului
    public static float    densitate       = 1f;

    // modul de miscare a camerei
    public static boolean  moveByTouch     = false;
    public static float    tPadMinAlpha    = 0.2f;
    protected static float tPadAlphaDellay = 2f;

    // ( 0 , 3 ] viteza de miscare a camerei
    public static float    modCamSpeedX    = 2f;
    public static float    modCamSpeedY    = 0.7f;

    // directia de miscare a camerei folosind TtouchPad-ul
    public static byte     invertPadX      = -1;
    public static byte     invertPadY      = 1;

    // directia de miscare a camerei folosind mouse-ul
    public static byte     invertDragX     = -1;
    public static byte     invertDragY     = 1;

    // pentru debuging
    public static boolean  updateUave      = false;
    public static boolean  showDebug       = true;


}
