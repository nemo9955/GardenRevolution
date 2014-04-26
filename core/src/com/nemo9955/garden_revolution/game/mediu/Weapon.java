package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public class Weapon implements Disposable {

    private ModelInstance      model;
    public AnimationController animation;
    public final Vector3       poz = new Vector3();
    public WeaponType          type;


    public Weapon(WeaponType type, Vector3 poz) {
        this.poz.set( poz );
        this.type = type;
        changeWeapon( type );
    }

    public void changeWeapon(WeaponType toChange) {
        this.type = toChange;
        model = getModelInstance( poz );
        animation = new AnimationController( model );
    }

    public boolean fire(WorldWrapper world, Ray ray, float charge) {
        return type.fireProjectile( world, ray, charge );
    }

    public ModelInstance getModelInstance(Vector3 poz) {
        return type.getModelInstance( poz );
    }

    public void update(float delta) {
        animation.update( delta );
    }

    public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch, boolean drTarg) {
        modelBatch.render( model, light );
        if ( drTarg )
            type.render( modelBatch, light, decalBatch );
    }

    @Override
    public void dispose() {
    }
}
