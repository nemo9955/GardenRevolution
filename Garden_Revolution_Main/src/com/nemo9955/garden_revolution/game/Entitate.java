package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;


public class Entitate extends ModelInstance {

    public Entitate(Model model, Vector3 position) {
        this( model, position.x, position.y, position.z );
    }

    public Entitate(Model model) {
        this( model, 0, 0, 0 );
    }

    public Entitate(ModelInstance copyFrom) {
        this( copyFrom.model, copyFrom.transform.getTranslation( Vector3.Zero ) );
    }

    // constructorul principal
    public Entitate(Model model, float x, float y, float z) {
        super( model, x, y, z );

    }

    public void update(float delta) {

    }

}
