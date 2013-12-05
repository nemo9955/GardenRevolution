package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Turnuri {


    BASIC(Assets.TURN_BASIC) {

        @Override
        protected void PropTurnuri() {
            nume = "Turn de baza";
            rank = 5;
        }
    };//

    Assets        type;
    public String nume;
    public int    rank;


    Turnuri(Assets type) {
        this.type = type;
        PropTurnuri();
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    protected abstract void PropTurnuri();
}
