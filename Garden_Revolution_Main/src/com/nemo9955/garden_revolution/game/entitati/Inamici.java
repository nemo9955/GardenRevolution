package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Inamici {


    ROSIE("rosie", Assets.ROSIE), //
    MORCOV("morcov", Assets.MORCOV);//


    String             nume;
    Assets             type;
    public final int   force;
    public final float speed;

    Inamici(String nume, Assets type) {
        this.nume = nume;
        this.type = type;
        force = 5;
        speed = 8;
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    public String getName() {
        return nume;
    }


}
