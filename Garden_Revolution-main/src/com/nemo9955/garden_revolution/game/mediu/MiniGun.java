package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireHold;


public class MiniGun extends Weapon implements FireHold {

    public MiniGun(World world,Vector3 poz) {
        super(  world,poz );
        setFireDellay( 100 );

        name = "Mini Gun";
        details = "Small but vicious.";
    }

    @Override
    public void fireHold( Ray ray) {
        if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
            fireTime = System.currentTimeMillis();

            float distance = -ray.origin.y /ray.direction.y;
            tmp = ray.getEndPoint( tmp, distance );
            tmp.add( MathUtils.random( -2f, 2f ), MathUtils.random( -1.5f, 3f ), MathUtils.random( -2f, 2f ) );
            if ( distance >=0 )
                world.addShot( ShotType.STANDARD, poz, tmp.sub( poz ).nor() );
            else
                world.addShot( ShotType.STANDARD, poz, ray.direction );
        }
    }

    @Override
    protected ModelInstance getModelInstance(Vector3 poz2) {
        ModelBuilder build = new ModelBuilder();
        Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        World.toDispose.add( sfera );

        return new ModelInstance( sfera, poz2 );

    }

}
