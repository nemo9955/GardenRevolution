package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;


public class Vietate extends Entitate {

    public AnimationController animation;

    public Vietate(Model model, float x, float y, float z) {
        super( model, x, y, z );

        animation = new AnimationController( this );

    }

    @Override
    public void update(float delta) {
        super.update( delta );
        animation.update( delta );
    }

}
