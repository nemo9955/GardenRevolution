package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum TowerType {

    VIEWPOINT(Assets.VIEW_POINT) {

        {
            setPrice( 0 );
            rank = 9000 +1;
            name = "View baloon";
        }
    },

    FUNDATION(Assets.TOWER_FUNDATION) {

        {
            setPrice( 0 );
            name = "Fundation";
            rank = 0;
        }
    },

    BASIC(Assets.TOWER_BASIC) {

        {
            name = "Basic tower";
            rank = 5;
            setPrice( 530 );
        }
    };

    Assets        type;
    public String name;
    public int    rank;
    public int    cost;
    public int    value;


    TowerType(Assets type) {
        this.type = type;
        setPrice( 100 );
    }

    public void setPrice(int cost) {
        this.cost = cost;
        this.value = (int) ( cost *0.60f );
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    public static boolean isValidForWeapon(TowerType tower) {
        return tower ==BASIC;
    }

}
