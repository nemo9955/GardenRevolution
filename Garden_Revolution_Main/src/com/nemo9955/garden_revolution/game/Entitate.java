package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.utility.CustomBox;


public abstract class Entitate extends ModelInstance {

    public boolean      dead  = false;
    protected CustomBox box   = new CustomBox();
    public float        angle = 0;
    public Vector3      poz;

    public Entitate(Model model, Vector3 position) {
        this( model, position.x, position.y, position.z );
    }

    // constructorul principal
    public Entitate(Model model, float x, float y, float z) {
        super( model, x, y, z );
        poz = new Vector3( x, y, z );
        setBox( x, y, z );
    }

    protected void setBox(float x, float y, float z) {
        calculateBoundingBox( box );
        box.setPosition( x, y, z );
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render( this );
    }

    public void render(ModelBatch modelBatch, Lights light) {
        modelBatch.render( this, light );
    }

    public void render(ModelBatch modelBatch, Shader shader) {
        modelBatch.render( this, shader );
    }


    public void move(Vector3 move) {
        move( move.x, move.y, move.z );
    }

    public void walk(float dist) {
        move( (float) Math.sin( angle *MathUtils.degRad ) *dist, 0, (float) Math.cos( angle *MathUtils.degRad ) *dist );
    }

    public void move(float x, float y, float z) {
        transform.trn( x, y, z );
        box.setPosition( x, y, z );
        poz.add( x, y, z );
    }

    public void rotate(float x, float y, float z) {
        if ( x !=0 )
            transform.rotate( 1, 0, 0, x );
        if ( y !=0 ) {
            transform.rotate( 0, 1, 0, y );
            angle += y;
        }
        if ( z !=0 )
            transform.rotate( 0, 0, 1, z );


        if ( angle >=360 )
            angle -= 360;
        if ( angle <0 )
            angle += 360;
    }

    public void damage(Entitate e) {
        dead = true;
    }

}
