package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.Vector3;


public class Entitate extends ModelInstance {

    public boolean dead = false;

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

    public void render(ModelBatch modelBatch) {
        modelBatch.render( this );
    }

    public void render(ModelBatch modelBatch, Lights light) {
        modelBatch.render( this, light );
    }

    public void move(float x, float y, float z) {
        transform.trn( x, y, z );
    }

    public void move(Vector3 move) {
        transform.trn( move );
    }

    public void rotate(float x, float y, float z) {
        if ( x !=0 )
            transform.rotate( 1, 0, 0, x );
        if ( y !=0 )
            transform.rotate( 0, 1, 0, y );
        if ( z !=0 )
            transform.rotate( 0, 0, 1, z );
    }

}
