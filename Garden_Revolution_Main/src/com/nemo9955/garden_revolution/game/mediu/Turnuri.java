package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Turnuri {


    BASIC("Turn de baza", Assets.TURN_BASIC);//


    Assets              type;
    public final String nume;


    Turnuri(String nume, Assets type) {
        this.nume = nume;
        this.type = type;
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }
}
