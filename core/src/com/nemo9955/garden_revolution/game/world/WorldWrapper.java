package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.files.FileHandle;
import com.nemo9955.garden_revolution.net.MultiplayerComponent;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;

public class WorldWrapper {

	private WorldBase	world		= new WorldBase();

	private WorldSP		spWorld		= new WorldSP();
	private WorldMP		mpWorld		= new WorldMP();
	private IWorldModel	defWorld	= spWorld;

	public WorldWrapper init( FileHandle location ) {
		spWorld.init(world);
		defWorld = spWorld;
		world.init(location, this);
		return this;
	}

	public WorldWrapper init( FileHandle location, MultiplayerComponent mp ) {
		mpWorld.init(world, mp);
		spWorld.init(world);
		defWorld = mpWorld;
		world.init(location, this);
		return this;
	}

	public WorldWrapper init( StartingServerInfo serverInfo, MultiplayerComponent mp ) {
		mpWorld.init(world, mp);
		spWorld.init(world);
		defWorld = mpWorld;
		world.init(serverInfo, this);
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
		return defWorld == mpWorld;
	}

	public boolean isSinglelayer() {
		return defWorld == spWorld;
	}

}
