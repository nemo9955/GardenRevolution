package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Inamici {


    ROSIE("rosie", Assets.ROSIE), //
    MORCOV("morcov", Assets.MORCOV);//


    String nume;
    Assets type;

    Inamici(String nume, Assets type) {
        this.nume = nume;
        this.type = type;
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    public String getName() {
        return nume;
    }

}
