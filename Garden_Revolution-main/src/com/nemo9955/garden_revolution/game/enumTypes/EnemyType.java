package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum EnemyType {


    ALUNA("aluna") {

        @Override
        public void propEnemys() {
            speed = 7;
            life = 1;
        }
    },

    ROSIE("rosie") {

        @Override
        public void propEnemys() {
            speed = 4;
            life = 100;
        }
    },

    MORCOV("morcov") {

        @Override
        public void propEnemys() {
            strenght = 5;
            speed = 6;
            life = 150;
        }
    };

    ModelInstance model;
    public int    strenght = 5;
    public float  speed    = 8;
    public String name     = "Plant";
    public int    life     = 50;

    EnemyType(String fileName) {
        name = name().charAt( 0 ) +"" +name().replaceFirst( name().charAt( 0 ) +"", "" ).toLowerCase();
        propEnemys();

        ModelInstance tmp = new ModelInstance( Garden_Revolution.getModel( Assets.INAMICI ) );
        Array<Node> toRemove = new Array<Node>( false, 1 );
        for (int i = 0 ; i <tmp.nodes.size ; i ++ )
            if ( !tmp.nodes.get( i ).id.equalsIgnoreCase( fileName ) )
                toRemove.add( tmp.nodes.get( i ) );
        tmp.nodes.removeAll( toRemove, false );
        model = tmp;
    }

    public ModelInstance getModel(float x, float y, float z) {
        model.transform.setToTranslation( x, y, z );
        return new ModelInstance( model );
    }

    protected abstract void propEnemys();


}
