package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.Vietate;
import com.nemo9955.garden_revolution.utility.Assets;


public class Knight extends Vietate {

    private final float rot   = 2f;
    private final float spd   = 0.3f;

    public Knight(float x, float y, float z) {
        super( Garden_Revolution.getModel( Assets.KNIGHT ), x, y, z );

        animation.setAnimation( "Sneak", -1, null );
        transform.scl( 0.5f );
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

         if ( Gdx.input.isKeyPressed( Keys.UP ) ) {
         move( (float) Math.sin( angle *MathUtils.degRad ) *spd, 0, (float) Math.cos( angle *MathUtils.degRad ) *spd );
         }
         if ( Gdx.input.isKeyPressed( Keys.DOWN ) ) {
         move( -(float) Math.sin( angle *MathUtils.degRad ) *spd, 0, -(float) Math.cos( angle *MathUtils.degRad ) *spd );
         }
         if ( Gdx.input.isKeyPressed( Keys.LEFT ) ) {
         rotate( 0, rot, 0 );
         }
         if ( Gdx.input.isKeyPressed( Keys.RIGHT ) ) {
         rotate( 0, -rot, 0 );
         }
        

    }

}
