package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;


public class FightZone {

    public Array<Ally>  allies = new Array<Ally>( false, 0 );
    public Array<Enemy> enemy  = new Array<Enemy>( false, 0 );

    public Vector3      poz    = new Vector3();

    public FightZone(Vector3 poz) {
        this.poz = poz;
    }

}
