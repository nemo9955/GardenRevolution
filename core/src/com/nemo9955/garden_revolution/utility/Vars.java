package com.nemo9955.garden_revolution.utility;

public class Vars {

	// modul de miscare a camerei
	public static boolean		moveByTouch			= false;
	public static float			tPadMinAlpha		= 0.1f;
	public static float			tPadAlphaDellay		= 2f;

	// ( 0 , 4 ] viteza de miscare a camerei
	public static float			modCamSpeedX		= 2f;
	public static float			modCamSpeedY		= 0.7f;

	// directia de miscare a camerei folosind TtouchPad-ul
	public static byte			invertPadX			= -1;
	public static byte			invertPadY			= 1;

	// directia de miscare a camerei folosind mouse-ul
	public static byte			invertDragX			= -1;
	public static byte			invertDragY			= -1;

	// pentru debuging
	public static boolean		updateUave			= true;
	public static boolean		showDebug			= false;

	// pentru controller
	public static float			invertControllerX	= -1;
	public static float			invertControllerY	= 1;
	public static float			multiplyControlletY	= 1;
	public static float			multiplyControlletX	= 1;
	public static float			deadZone			= 0.05f;
	public static final int		noButtons			= CoButt.values().length, noAxis = CoAxis.values().length;

	// pentru comportamentul entitatilor
	public static final long	enemyAttackInterval	= 868;
	public static final long	allyAttackInterval	= 696;

	// ally timers
	public static final float	allySpawnInterval	= 20;
	public static final float	allyAliveInterval	= 30;

	// variabile multyplayer
	public static int			TCPport				= 29955;
	public static int			UDPport				= 39955;
	public static final String	stringSeparator		= "-@-";

	public static enum CoButt {
		Fire(0, "Fire"), //
		Back(1, "Back"), //
		TowerUpgr(5, "Upgrade tower"), //
		NextT(3, "Next Tower"), //
		PrevT(2, "Last Tower"), //
		CallAlly(4, "Last Tower"), //
		InvY(7, "Invert Y"), //
		InvX(6, "Invert X"), //
		SpdUp(11, "Speed Up Cam"); //

		public int		id;
		public String	name;

		CoButt(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	public static enum CoAxis {
		mvY(1, "Move Y"), mvX(0, "Move X");

		public int		id;
		public String	name;

		CoAxis(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

}
