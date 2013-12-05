package com.nemo9955.garden_revolution.game.enumTypes;


public enum Shots {

    STANDARD {

        @Override
        protected void propShots() {
            damage = 35;
        }
    };//

    public int damage;

    Shots() {
        propShots();
    }

    protected abstract void propShots();


}
