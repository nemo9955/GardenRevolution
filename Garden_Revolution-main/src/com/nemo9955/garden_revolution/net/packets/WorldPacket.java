package com.nemo9955.garden_revolution.net.packets;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.game.mediu.Tower;


public class WorldPacket {

    // public Array<ModelInstance> mediu = new Array<ModelInstance>( false, 10 );
    // public Array<Enemy> enemy = new Array<Enemy>( false, 10 );
    // public Array<Ally> ally = new Array<Ally>( false, 10 );
    // public Array<Shot> shot = new Array<Shot>( false, 10 );
    public Array<BoundingBox>               colide;
//    public Array<CatmullRomSpline<Vector3>> paths;

//    public Vector3                          overview;
//    public int                              viata;
    public Tower[]                          towers;
//    public boolean                          canWaveStart;
//    public Waves                            waves;

}
