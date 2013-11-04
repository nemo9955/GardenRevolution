package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;


public abstract class Vietate extends Entitate {

    public AnimationController animation;
    public float               speed = 9;
    public Path<Vector3>       drum;
    public float               percent;

    public static final float  STEP  = 1f /50f;
    public Vector3             flag;
    public Vector3             dir;
    protected Vector3          offset;

    public Vietate(Path<Vector3> drum, float x, float y, float z) {
        super( x, y, z );
        animation = new AnimationController( model );
        offset = new Vector3( MathUtils.random( -20, 20 ), 0, MathUtils.random( -20, 20 ) );
        offset.scl( 0.1f );
        flag = new Vector3();
        dir = new Vector3();
        this.drum = drum;

        percent = -STEP;

        lookAt( drum.valueAt( flag, percent +STEP ) );
        flag.add( offset );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        animation.update( delta );
        if ( Gdx.input.isKeyPressed( Keys.F12 ) )
            movement( delta );
    }


    protected void movement(float delta) {
        dir.set( flag.x -poz.x, flag.y -poz.y, flag.z -poz.z ).nor().scl( speed *delta );
        move( dir );
        if ( flag.epsilonEquals( poz, 0.5f ) ) {
            percent += STEP;
            if ( percent >=1 ) {
                // TODO cand ajunge la capatul drumului
                speed = 0;
            }
            drum.valueAt( flag, percent +STEP );
            lookAt( drum.valueAt( flag, percent +STEP ) );
            flag.add( offset );
        }
    }

    protected void lookAt(Vector3 look) {
        float ung = (float) Math.toDegrees( Math.atan2( poz.x -look.x, poz.z -look.z ) );
        if ( ung <0 )
            ung += 360;
        ung -= 180;
        rotate( 0, angle -ung, 0 );
    }
}
