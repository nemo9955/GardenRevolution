package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Inamici {


    ROSIE(Assets.ROSIE), //
    MORCOV(Assets.MORCOV);//

    Assets              type;
    public ProprInamici prop;

    Inamici(Assets type) {
        this.type = type;
        prop = new ProprInamici( this );
    }


    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    public static class ProprInamici {

        public int    force = 5;
        public float  speed = 8;
        public String nume  = "frunc";

        public ProprInamici(Inamici type) {

            nume = type.name().charAt( 0 ) +"" +type.name().replaceFirst( type.name().charAt( 0 ) +"", "" ).toLowerCase();

            if ( type ==Inamici.MORCOV ) {
                force = 7;
            }
            else if ( type ==Inamici.ROSIE ) {
                speed = 10;
            }
        }
    }
}
