package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.files.FileHandle;
import com.nemo9955.garden_revolution.net.MultiplayerComponent;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;


public class WorldWrapper {

    private WorldBase       world;

    private IWorldModel defWorld;
    private WorldSP     spWorld;
    private WorldMP     mpWorld;


    public WorldWrapper(FileHandle location) {
        world = new WorldBase( location );
        spWorld = new WorldSP( world );
        defWorld = spWorld;
    }

    public WorldWrapper(FileHandle location, MultiplayerComponent mp) {
        world = new WorldBase( location );
        mpWorld = new WorldMP( world, mp );
        spWorld = new WorldSP( world );
        defWorld = mpWorld;
    }

    public WorldWrapper(StartingServerInfo serverInfo, MultiplayerComponent mp) {
        world = new WorldBase( serverInfo );
        mpWorld = new WorldMP( world, mp );
        spWorld = new WorldSP( world );
        defWorld = mpWorld;
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

}
