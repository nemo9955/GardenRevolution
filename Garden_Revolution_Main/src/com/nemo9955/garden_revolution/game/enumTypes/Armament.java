package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.mediu.Arma;
import com.nemo9955.garden_revolution.game.mediu.Cannon;
import com.nemo9955.garden_revolution.game.mediu.MiniGun;


public enum Armament {
    MINIGUN {

        @Override
        protected void PropArme() {
            nume = "Mini Gun";
            descriere = "Small but vicious.";
        }
    },//
    
    CANON {

        @Override
        protected void PropArme() {
            nume="Cannon";
            descriere="Slow but powerfull.";
        }
    };

    public String nume;
    public String descriere;

    Armament() {
        PropArme();
    }

    public Arma getNewInstance(Vector3 poz) {

        switch (this) {
            case MINIGUN:
                return new MiniGun( this, poz );
            case CANON:
                return new Cannon(this , poz);
            default:
                return null;
        }
    }

    protected abstract void PropArme();

}
