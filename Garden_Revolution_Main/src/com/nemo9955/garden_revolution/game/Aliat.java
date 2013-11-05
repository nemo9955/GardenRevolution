package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public class Aliat extends Vietate {

    public Vector3 duty;

    public Aliat() {
        super();
        duty = new Vector3();
    }

    public Aliat create(float x, float y, float z) {
        super.init( x, y, z );
        duty.set( x, y, z );
        return this;
    }

    @Override
    protected void setBox(float x, float y, float z) {
        box.set( new Vector3( x -2, y -6, z -2 ), new Vector3( x +2, y +2, z +2 ) );

        model.transform.scl( 0.5f );
    }

    @Override
    protected Model getModel() {
        return Garden_Revolution.getModel( Assets.KNIGHT );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        
        
        final float rot = 65f;
        final float spd = 7f;
        if ( Gdx.input.isKeyPressed( Keys.UP ) ) {
            walk( spd *delta );
            animation.animate( "Sneak", -1, null, 0.1f );
        }
        else if ( Gdx.input.isKeyPressed( Keys.DOWN ) ) {
            walk( -spd *delta );
            animation.animate( "Sneak", -1, null, 0.1f );
        }
        else
            animation.animate( "Idle", -1, null, 0.5f );

        if ( Gdx.input.isKeyPressed( Keys.LEFT ) ) {
            rotate( 0, rot *delta, 0 );
        }
        if ( Gdx.input.isKeyPressed( Keys.RIGHT ) ) {
            rotate( 0, -rot *delta, 0 );
        }


    }


}
