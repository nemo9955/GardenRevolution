package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireCharged;


public class Cannon extends Weapon implements FireCharged {

    public Cannon(Vector3 poz) {
        super( poz );
        setFireDellay( 200 );
        name = "Cannon";
        details = "Slow but powerfull.";
    }

    @Override
    public void fireCharged(World world, Ray ray, float charged) {
        if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
            fireTime = System.currentTimeMillis();

            float distance = -ray.origin.y /ray.direction.y;
            tmp = ray.getEndPoint(tmp, distance );
            if ( distance >=0 )
                world.addShot( ShotType.GHIULEA, poz, tmp.sub( poz ).nor(), charged );
            else
                world.addShot( ShotType.GHIULEA, poz, ray.direction, charged );
        }
    }

    @Override
    protected ModelInstance getModelInstance(Vector3 poz2) {
        ModelBuilder build = new ModelBuilder();
        Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.DARK_GRAY ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
       World. toDispose.add( sfera );

        return new ModelInstance( sfera, poz2 );

    }
}
