package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.math.Vector3;


public enum Armament {
    MINIGUN;

    public String nume;
    public String descriere;

    Armament() {
        PropArme( this );
    }

    public Arma getNewInstance(Vector3 poz) {

        switch (this) {
            case MINIGUN:
                return new MiniGun( poz );
            default:
                return null;
        }
    }

    private void PropArme(Armament type) {

        if ( type ==Armament.MINIGUN ) {
            nume = "Mini Gun";
            descriere = "Small but vicious !";
        }
    }
}
