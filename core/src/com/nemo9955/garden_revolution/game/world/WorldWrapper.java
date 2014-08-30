package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.files.FileHandle;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;

public class WorldWrapper {

	public static WorldWrapper	instance	= new WorldWrapper();

	private WorldBase			world		= new WorldBase();

	private WorldSP				spWorld		= new WorldSP();
	private WorldMP				mpWorld		= new WorldMP();
	private IWorldModel			defWorld	= spWorld;

	// host / singleplayer
	public WorldWrapper init( FileHandle location, boolean multiplayer ) {
		if ( multiplayer )
			defWorld = mpWorld;
		getWorld().init(location);
		return this;
	}

	// client
	public WorldWrapper init( StartingServerInfo serverInfo ) {
		defWorld = mpWorld;
		getWorld().init(serverInfo);
		return this;
	}

	public IWorldModel getDef() {
		return defWorld;
	}

	public WorldSP getSgPl() {
		return spWorld;
	}

	public WorldMP getMpPl() {
		return mpWorld;
	}

	public WorldBase getWorld() {
		return world;
	}

	public boolean isMultiplayer() {
		return defWorld == spWorld;
	}

	public boolean isSinglelayer() {
		return defWorld == mpWorld;
	}

}
