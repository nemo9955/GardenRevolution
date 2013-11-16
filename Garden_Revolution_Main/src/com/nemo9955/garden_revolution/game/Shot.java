package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;


public class Shot extends Entitate {

    private Vector3 direction;

    private float   life;

    public Shot() {
        super();
    }

    public Shot create(Vector3 position, Vector3 direction) {
        super.init( position );
        this.direction = direction.cpy();
        life = 3f;
        return this;
    }

    @Override
    public void reset() {
        super.reset();
        direction = null;
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        move( direction );
        life -= delta;
        if ( life <=0 ) {
            dead = true;
        }
    }

    @Override
    protected Model getModel() {

        return new ModelBuilder().createSphere( 0.5f, 0.5f, 0.5f, 12, 12, new Material( ColorAttribute.createDiffuse( Color.RED ) ), Usage.Position |Usage.Normal );

    }

}
