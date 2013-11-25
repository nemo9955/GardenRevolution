package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Inamici {


    ALUNA("aluna"), //
    ROSIE("rosie"), //
    MORCOV("morcov");//

    Model               model;
    public ProprInamici prop;

    Inamici(String name) {
        Model tmp = new ModelInstance( Garden_Revolution.getModel( Assets.INAMICI ) ).model;
        // ModelInstance tmp = new ModelInstance( Garden_Revolution.getModel( Assets.INAMICI ) );
        Array<Node> rem = new Array<Node>( true, 1 );
        for (Node nod : tmp.nodes )
            if ( !nod.id.equalsIgnoreCase( name ) ) {
                rem.add( nod );
                // System.out.println( nod.id );
            }
            else
                System.out.println( name );
        tmp.nodes.removeAll( rem, false );
        model = tmp;
        prop = new ProprInamici( this );
    }

    public Model getModel() {
        return model;
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
