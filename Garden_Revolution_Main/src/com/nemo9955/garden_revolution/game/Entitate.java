package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.nemo9955.garden_revolution.utility.CustomBox;


public abstract class Entitate implements Disposable, Poolable {

    protected ModelInstance model;

    public boolean          dead;
    protected CustomBox     box;
    public float            angle;
    public Vector3          poz;

    public Entitate() {
        poz = new Vector3();
        box = new CustomBox();
    }

    public void init(Vector3 position) {
        init( position.x, position.y, position.z );
    } 

    // principalul
    public void init(float x, float y, float z) {
        model = new ModelInstance( getModel(), x, y, z );
        poz.set( x, y, z );
        setBox( x, y, z );
        dead = false;
        angle = 0;
    }

    @Override
    public void reset() {
        model.model.dispose();
        model = null;
    }

    protected abstract Model getModel();

    protected void setBox(float x, float y, float z) {
        model.calculateBoundingBox( box );
        box.addPoz( x, y, z );
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render( model );
    }

    public void render(ModelBatch modelBatch, Lights light) {
        modelBatch.render( model, light );
    }

    public void render(ModelBatch modelBatch, Shader shader) {
        modelBatch.render( model, shader );
    }


    public void move(Vector3 move) {
        move( move.x, move.y, move.z );
    }

    public void walk(float dist) {
        move( (float) Math.sin( angle *MathUtils.degRad ) *dist, 0, (float) Math.cos( angle *MathUtils.degRad ) *dist );
    }

    public void move(float x, float y, float z) {
        model.transform.trn( x, y, z );
        box.addPoz( x, y, z );
        poz.add( x, y, z );
    }

    public void rotate(float x, float y, float z) {
        if ( x !=0 )
            model.transform.rotate( 1, 0, 0, x );
        if ( y !=0 ) {
            model.transform.rotate( 0, 1, 0, y );
            angle += y;
        }
        if ( z !=0 )
            model.transform.rotate( 0, 0, 1, z );


        if ( angle >=360 )
            angle -= 360;
        if ( angle <0 )
            angle += 360;
    }

    public void damage(Entitate e) {
        dead = true;
    }

    @Override
    public void dispose() {
        model.model.dispose();
    }
}
