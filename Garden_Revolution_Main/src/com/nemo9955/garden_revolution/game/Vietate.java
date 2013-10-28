package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;


public abstract class Vietate extends Entitate {

    public AnimationController       animation;
    public float                     speed = 9;
    public CatmullRomSpline<Vector3> drum;
    public float                     percent;

    public static final float        STEP  = 1f /50f;
    public Vector3                   flag;

    public Vietate(CatmullRomSpline<Vector3> drum, float x, float y, float z) {
        super( x, y, z );
        poz.set( x, y, z );
        animation = new AnimationController( this.model );
        this.drum = drum;

        percent = Float.MIN_VALUE;

        flag = new Vector3();
        lookAt( drum.valueAt( flag, percent +STEP ) );
        System.out.println( "directie : " +flag.sub( poz ).nor().scl( 1f /speed ) );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        animation.update( delta );
        if ( Gdx.input.isKeyPressed( Keys.F12 ) )
            movement( delta );
    }


    protected void movement(float delta) {
        // walk( speed *delta );
        move( flag.sub( poz ).nor().scl( 1f /speed ) );
        if ( flag.epsilonEquals( poz, 0.5f ) ) {
            percent += STEP;
            drum.valueAt( flag, percent +STEP );
            lookAt( drum.valueAt( flag, percent +STEP ) );
            System.out.println( flag +" adevarat " +percent );
        }
    }

    protected void lookAt(Vector3 look) {
        float ung = (float) Math.toDegrees( Math.atan2( poz.x -look.x, poz.z -look.z ) );
        if ( ung <0 )
            ung += 360;
        ung -= 180;
        rotate( 0, angle -ung, 0 );
        System.out.println( ung +"    rap: " +percent +"         " +look );
    }
}
