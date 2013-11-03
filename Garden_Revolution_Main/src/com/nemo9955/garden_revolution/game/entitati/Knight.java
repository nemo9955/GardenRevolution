package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.Vietate;
import com.nemo9955.garden_revolution.utility.Assets;


public class Knight extends Vietate {


    public Knight(Path<Vector3> drum, float x, float y, float z) {
        super( drum, x, y, z );
        speed = 10;
        animation.setAnimation( "Sneak", -1, null );
        model.transform.scl( 0.5f );
    }

    @Override
    protected Model getModel() {
        return Garden_Revolution.getModel( Assets.KNIGHT );
    }

    @Override
    protected void setBox(float x, float y, float z) {
        box.set( new Vector3( x -2, y -6, z -2 ), new Vector3( x +2, y +2, z +2 ) );
    }

    @Override
    public void update(float delta) {
        super.update( delta );

        // if ( Gdx.input.isKeyPressed( Keys.F5 ) ||Gdx.input.isTouched( 0 ) )
        // animation.animate( "Walk", -1, null, 0.5f );
        // if ( Gdx.input.isKeyPressed( Keys.F6 ) ||Gdx.input.isTouched( 1 ) )
        // animation.animate( "Sneak", -1, null, 1 );
        // if ( Gdx.input.isKeyPressed( Keys.F7 ) ||Gdx.input.isTouched( 2 ) )
        // animation.animate( "Damaged", -1, null, 0.5f );
        // if ( Gdx.input.isKeyPressed( Keys.F8 ) ||Gdx.input.isTouched( 3 ) )
        // animation.animate( "Idle", -1, null, 0.5f );

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
