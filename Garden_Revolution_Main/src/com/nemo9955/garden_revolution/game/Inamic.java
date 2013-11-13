package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.entitati.Inamici;


public class Inamic extends Vietate {


    public Path<Vector3>      drum;
    public float              percent;
    public static final float STEP = 1f /50f;
    public Vector3            flag;
    private Vector3           offset;
    private Inamici           type;

    public Inamic() {
        super();
        offset = new Vector3();
        flag = new Vector3();
    }

    public Inamic create(CatmullRomSpline<Vector3> drum, Inamici type, float x, float y, float z) {
        this.type = type;

        super.init( x, y, z );

        offset.set( MathUtils.random( -20, 20 ), 0, MathUtils.random( -20, 20 ) );
        offset.scl( 0.1f );
        this.drum = drum;
        percent = -STEP;

        lookAt( drum.valueAt( flag, percent +STEP ) );
        flag.add( offset );


        return this;
    }

    @Override
    public void reset() {
        super.reset();
        drum = null;
    }

    @Override
    protected Model getModel() {
        return type.getModel();
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        animation.update( delta );
        if ( Gdx.input.isKeyPressed( Keys.F12 ) )
            movement( delta );
    }

    protected void movement(float delta) {
        super.movement( delta );
        dir.set( flag ).sub( poz ).nor().scl( speed *delta );
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


}