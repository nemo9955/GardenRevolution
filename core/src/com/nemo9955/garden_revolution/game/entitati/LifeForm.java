package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;


public abstract class LifeForm extends Entity {

    public short               ID;
    public AnimationController animation;
    public final Vector3       direction = new Vector3();
    public int                 life;


    public void init(Vector3 position) {
        ID = newGlobalID();
        animation = new AnimationController( model );
        direction.set( Vector3.Zero );
        super.init( position );
    }

    @Override
    public void update(float delta) {
        super.update( delta );
        animation.update( delta );
    }

    public void damage(int dmg) {
        life -= dmg;
        if ( life <=0 )
            setDead( true );
    }

    protected void lookAt(Vector3 look) {// FIXME the entities just spin
        float ang = (float) Math.toDegrees( Math.atan2( poz.x -look.x, poz.z -look.z ) );
        ang += 180;
        ang %= 360;

        model.transform.rotate( 0, 1, 0, angle -ang );
        angle += ang;
    }

    private static short globalID = -32768;

    public static short getGlobalID() {
        return globalID;
    }

    public static short newGlobalID() {
        if ( globalID <32766 )
            return ++ globalID;
        else
            return globalID = -32768;
    }
}
