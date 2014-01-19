package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireCharged;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireHold;


public class Tower implements Disposable {

    public BoundingBox           fundation = new BoundingBox();
    public Array<BoundingBox>    coliders  = new Array<BoundingBox>( false, 1 );

    private Array<ModelInstance> parts     = new Array<ModelInstance>( false, 1 );
    public final Vector3         place;
    public final Vector3         poz;
    public TowerType             type;


    private Weapon               weapon;

    public Tower(ModelInstance baza, World world, Vector3 poz) {
        parts.add( baza );
        place = poz.cpy().add( 0, 10, 0 );
        baza.calculateBoundingBox( this.fundation );
        this.poz = poz.cpy();
    }

    public void fireNormal(World world, Ray ray) {
        if ( hasArma() &&weapon instanceof FireHold )
            ( (FireHold) weapon ).fireHold( world, ray );
    }

    public void fireCharged(World world, Ray ray, float charged) {
        if ( hasArma() &&weapon instanceof FireCharged )
            ( (FireCharged) weapon ).fireCharged( world, ray, charged );
    }

    public boolean changeWeapon(Weapon toChange) {
        if ( hasArma() &&weapon.name ==toChange.name )
            return false;
        weapon = toChange;
        return true;
    }

    public boolean upgradeTower(TowerType upgrade) {
        if ( type !=null &&type.rank >=upgrade.rank )
            return false;
        type = upgrade;
        parts.clear();

        Array<Node> remove = new Array<Node>( false, 1 );

        ModelInstance model = new ModelInstance( upgrade.getModel(), poz );
        for (int i = 0 ; i <model.nodes.size ; i ++ ) {
            String id = model.nodes.get( i ).id;

            if ( id.startsWith( "arma" ) ) {
                place.set( model.nodes.get( i ).translation ).add( poz );
            }
            else if ( id.startsWith( "colide" ) ) {
                BoundingBox box = new BoundingBox();
                model.getNode( id ).calculateBoundingBox( box );
                box.set( box.min.add( poz ), box.max.add( poz ) );
                coliders.add( box );
                remove.add( model.nodes.get( i ) );
            }
        }
        model.nodes.removeAll( remove, false );
        parts.add( model );
        return true;
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch) {
        for (ModelInstance model : parts )
            modelBatch.render( model );
        if ( hasArma() )
            weapon.render( modelBatch );
    }

    public void render(ModelBatch modelBatch, Environment light) {
        for (ModelInstance model : parts )
            modelBatch.render( model, light );
        if ( hasArma() )
            weapon.render( modelBatch, light );
    }

    public void render(ModelBatch modelBatch, Shader shader) {
        for (ModelInstance model : parts )
            modelBatch.render( model, shader );
        if ( hasArma() )
            weapon.render( modelBatch, shader );
    }

    public Weapon getArma() {
        return weapon;
    }

    public void setArma(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean hasArma() {
        return weapon !=null;
    }

    public boolean intersectsRay(Ray ray) {
        if ( Intersector.intersectRayBoundsFast( ray, fundation ) )
            return true;
        for (BoundingBox box : coliders )
            if ( Intersector.intersectRayBoundsFast( ray, box ) )
                return true;
        return false;
    }

    @Override
    public void dispose() {
        if ( hasArma() )
            weapon.dispose();
    }

}
