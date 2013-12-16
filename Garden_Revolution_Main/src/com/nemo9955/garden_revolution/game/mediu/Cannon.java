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
import com.nemo9955.garden_revolution.game.enumTypes.Armament;
import com.nemo9955.garden_revolution.game.enumTypes.Shots;


public class Cannon extends Arma {

    public Cannon(Armament type, Vector3 poz) {
        super( type, poz );
        setFireDellay( 2500 );
    }

    @Override
    public void fireMain(World world, Ray ray) {
        if ( System.currentTimeMillis() -fireTime >=fireDellay ) {
            fireTime = System.currentTimeMillis();

            float distance = -ray.origin.y /ray.direction.y;
            tmp = ray.getEndPoint( new Vector3(), distance );
            if ( distance >=0 )
                world.addShot( Shots.STANDARD, poz, tmp.sub( poz ).nor() );
            else
                world.addShot( Shots.STANDARD, poz, ray.direction );

        }
    }

    @Override
    protected FireState getFireState() {
        return FireState.LOCKED_CHARGE;
    }

    @Override
    protected ModelInstance getModelInstance(Vector3 poz2) {
        ModelBuilder build = new ModelBuilder();
        Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.DARK_GRAY ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        toDispose.add( sfera );

        return new ModelInstance( sfera, poz2 );

    }
}
