package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Inamici {


    ALUNA("aluna"), //
    ROSIE("rosie"), //
    MORCOV("morcov");//

    ModelInstance       model;
    public ProprInamici prop;

    Inamici(String name) {
        ModelInstance tmp = new ModelInstance( Garden_Revolution.getModel( Assets.INAMICI ) );
        // ModelInstance tmp = new ModelInstance( Garden_Revolution.getModel( Assets.INAMICI ) );
        Array<Node> rem = new Array<Node>( false, 1 );
        for (int i = 0 ; i <tmp.nodes.size ; i ++ )
            if ( !tmp.nodes.get( i ).id.equalsIgnoreCase( name ) ) {
                rem.add( tmp.nodes.get( i ) );
                // System.out.println( nod.id );
            }
        // else
        // System.out.println( name );
        tmp.nodes.removeAll( rem, false );
        model = tmp;
        prop = new ProprInamici( this );
    }

    public ModelInstance getModel(float x, float y, float z) {
        model.transform.setToTranslation( x, y, z );
        return new ModelInstance( model );
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
