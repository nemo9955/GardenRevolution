package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;


public class CustomBox extends BoundingBox {

    private static final long serialVersionUID = -5539529375358347324L;

    public void addPoz(Vector3 poz) {
        set( min.add( poz ), max.add( poz ) );
    }

    public void addPoz(float x, float y, float z) {
        set( min.add( x, y, z ), max.add( x, y, z ) );
    }
}
