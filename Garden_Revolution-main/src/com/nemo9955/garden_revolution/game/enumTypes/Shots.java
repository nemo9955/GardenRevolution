package com.nemo9955.garden_revolution.game.enumTypes;


public enum Shots {

    STANDARD {

        @Override
        protected void propShots() {
            damage = 35;
            speed = 30;
        }
    },

    GHIULEA {

        @Override
        protected void propShots() {
            damage = 80;
            range = 9;
            speed = 18;
        }
    };

    public int range  = 0;
    public int damage = 10;
    public int speed  = 20;

    Shots() {
        propShots();
    }

    protected abstract void propShots();


}
