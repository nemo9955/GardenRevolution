package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.World;


public abstract class Vietate extends Entitate {

    public AnimationController animation;
    public float               viteza;
    public Vector3             dir;

    public int                 viata;

    public Vietate(World world) {
        super( world );
        dir = new Vector3();
    }

    public void init(float x, float y, float z) {
        super.init( x, y, z );
        animation = new AnimationController( model );
        dir = Vector3.Zero;
        viteza = 4;
        update( Gdx.graphics.getDeltaTime() );
    }

    @Override
    public void reset() {
        super.reset();
        animation = null;
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        animation.update( delta );
    }


    protected void movement(float delta) {

    }

    @Override
    public void damage(Entitate e) {
        if ( e instanceof Shot )
            viata -= ( (Shot) e ).damage;

        if ( viata <=0 )
            super.damage( e );

    }

    protected void lookAt(Vector3 look) {// FIXME doar se invarte
        float ung = (float) Math.toDegrees( Math.atan2( poz.x -look.x, poz.z -look.z ) );
        if ( ung <0 )
            ung += 360;
        ung -= 180;
        rotate( 0, angle -ung, 0 );
    }
}
