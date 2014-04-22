package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.Model;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public enum TowerType {

    VIEWPOINT(Assets.VIEW_POINT) {

        @Override
        protected void propTowers() {
            rank = 6666666;
            name = "View baloon";
        }
    },

    FUNDATION(Assets.TOWER_FUNDATION) {

        @Override
        protected void propTowers() {
            name = "Fundation";
            rank = 0;
        }
    },

    BASIC(Assets.TOWER_BASIC) {

        @Override
        protected void propTowers() {
            name = "Basic tower";
            rank = 5;
        }
    };

    Assets        type;
    public String name;
    public int    rank;


    TowerType(Assets type) {
        this.type = type;
        propTowers();
    }

    public Model getModel() {
        return Garden_Revolution.getModel( type );
    }

    protected abstract void propTowers();
}