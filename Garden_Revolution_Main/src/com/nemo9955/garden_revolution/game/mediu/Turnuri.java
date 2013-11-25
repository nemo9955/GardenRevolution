package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Turnuri {


    BASIC(Assets.TURN_BASIC);//


    Assets             type;
    public PropTurnuri prop;

    Turnuri(Assets type) {
        this.type = type;
        prop = new PropTurnuri( this );
    }

    
    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    public static class PropTurnuri {

        public String nume;
        public int    rank;

        public PropTurnuri(Turnuri type) {
            
            if ( type ==Turnuri.BASIC ) {
                nume = "Turn de baza";
                rank = 5;
            }
        }
    }
}
