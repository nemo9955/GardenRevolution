package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Turnuri {


    EMPTY("Nici un turn", 0, null), //
    BASIC("Turn de baza", 5, Assets.TURN_BASIC);//


    Assets              type;
    public final String nume;
    public final int    rank;


    Turnuri(String nume, int rank, Assets type) {
        this.nume = nume;
        this.type = type;
        this.rank = rank;
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }
}
