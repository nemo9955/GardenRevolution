package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public class Weapon implements Disposable {

    private ModelInstance      model;
    public AnimationController animation;
    public final Vector3       poz  = new Vector3();
    public WeaponType          type = WeaponType.NONE;


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
      return  type.fireProjectile( world, ray, charge );
    }

    public ModelInstance getModelInstance(Vector3 poz) {
        return type.getModelInstance( poz );
    }

    public void update(float delta) {
        animation.update( delta );
    }

    public void render(ModelBatch modelBatch) {
        modelBatch.render( model );
    }

    public void render(ModelBatch modelBatch, Environment light) {
        modelBatch.render( model, light );
    }

    @Override
    public void dispose() {

    }
}
