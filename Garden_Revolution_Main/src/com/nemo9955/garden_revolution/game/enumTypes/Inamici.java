package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum Inamici {


    ALUNA("aluna") {

        @Override
        public void propInamici() {
            speed = 10;
            viata = 75;
        }
    }, //

    ROSIE("rosie") {

        @Override
        public void propInamici() {
            speed = 9;
            viata = 100;
        }
    }, //

    MORCOV("morcov") {

        @Override
        public void propInamici() {
            force = 7;
            speed = 5;
            viata = 150;
        }
    };//

    ModelInstance model;
    public int    force = 5;
    public float  speed = 8;
    public String nume  = "frunc";
    public int    viata = 50;

    Inamici(String name) {
        nume = name().charAt( 0 ) +"" +name().replaceFirst( name().charAt( 0 ) +"", "" ).toLowerCase();
        propInamici();

        ModelInstance tmp = new ModelInstance( Garden_Revolution.getModel( Assets.INAMICI ) );
        Array<Node> rem = new Array<Node>( false, 1 );
        for (int i = 0 ; i <tmp.nodes.size ; i ++ )
            if ( !tmp.nodes.get( i ).id.equalsIgnoreCase( name ) )
                rem.add( tmp.nodes.get( i ) );
        tmp.nodes.removeAll( rem, false );
        model = tmp;
    }

    public ModelInstance getModel(float x, float y, float z) {
        model.transform.setToTranslation( x, y, z );
        return new ModelInstance( model );
    }

    protected abstract void propInamici();


}
