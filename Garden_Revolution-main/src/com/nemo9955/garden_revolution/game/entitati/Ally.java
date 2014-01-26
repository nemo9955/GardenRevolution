package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;


public class Ally extends LifeForm {

    public Vector3   duty;
    private AllyType type;

    public Ally(World world) {
        super( world );
        duty = new Vector3();
    }

    public Ally create(CatmullRomSpline<Vector3> path, AllyType type, float x, float y, float z) {
        this.type = type;
        super.init( x, y, z );
        duty.set( x, y, z );
        return this;
    }

    @Override
    protected void setBox(float x, float y, float z) {
        box.set( new Vector3( x -2, y -1.3f, z -2 ), new Vector3( x +2, y +6.7f, z +2 ) );
    }

    @Override
    protected ModelInstance getModel(float x, float y, float z) {
        return type.getModel( x, y, z );
    }

    @Override
    public void update(float delta) {
        super.update( delta );

    }
}
