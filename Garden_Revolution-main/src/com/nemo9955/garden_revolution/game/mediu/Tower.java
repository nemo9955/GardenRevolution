package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;


public class Tower implements Disposable {

    private World                world;
    private Array<BoundingBox>   coliders  = new Array<BoundingBox>( false, 1 );

    private Array<ModelInstance> parts     = new Array<ModelInstance>( false, 1 );
    public final Vector3         poz       = new Vector3();
    public TowerType             type;

    private Weapon               weapon;
    public final Vector3         place     = new Vector3();
    public final Vector3         direction = new Vector3();

    // private Decal pointer = Decal.newDecal( 5, 5, Garden_Revolution.getGameTexture( "pointer-2" ), true );

    public Tower(ModelInstance baza, World world, Vector3 poz) {
        this.poz.set( poz );
        this.world = world;
        parts.add( baza );
        place.set( poz ).add( 0, 10, 0 );

        this.world.addToColide( addToTowerColiders( baza.calculateBoundingBox( new BoundingBox() ) ) );
    }

    // public void fireHold(Ray ray) {
    // if ( hasArma() &&weapon instanceof FireHold )
    // ( (FireHold) weapon ).fireHold( ray );
    // }
    //
    // public void fireCharged(Ray ray, float charged) {
    // if ( hasArma() &&weapon instanceof FireCharged )
    // ( (FireCharged) weapon ).fireCharged( ray, charged );
    // }

    public void fireWeapon(World world, Ray ray, float charge) {
        weapon.fire( world, ray, charge );
    }

    public boolean changeWeapon(WeaponType toChange) {
        if ( !hasWeapon() ) {
            weapon = new Weapon( toChange, place ) {
            };
            return true;
        }
        if ( weapon.type ==toChange )
            return false;
        weapon.changeWeapon( toChange );
        return true;
    }

    public boolean upgradeTower(TowerType upgrade) {
        if ( type !=null &&type.rank >=upgrade.rank )
            return false;
        type = upgrade;
        parts.clear();

        Array<Node> remove = new Array<Node>( false, 1 );

        world.removeColiders( getTowerColiders() );
        getTowerColiders().clear();

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
                addToTowerColiders( box );
                world.addToColide( box );
                remove.add( model.nodes.get( i ) );
            }
        }
        model.nodes.removeAll( remove, false );
        parts.add( model );

        // pointer.setPosition( place.x, place.y +5f, place.z );

        return true;
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
        for (ModelInstance model : parts )
            modelBatch.render( model, light );
        if ( hasWeapon() )
            weapon.render( modelBatch, light );
        // if ( type !=null )
        // decalBatch.add( pointer );

    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setArma(Weapon weapon) {
        this.weapon = weapon;
    }

    public boolean hasWeapon() {
        return weapon !=null;
    }

    public boolean intersectsRay(Ray ray) {
        for (BoundingBox box : getTowerColiders() )
            if ( Intersector.intersectRayBoundsFast( ray, box ) )
                return true;
        return false;
    }

    @Override
    public void dispose() {
        if ( hasWeapon() )
            weapon.dispose();
    }

    public Array<BoundingBox> getTowerColiders() {
        return coliders;
    }

    public BoundingBox addToTowerColiders(BoundingBox box) {
        coliders.add( box );
        return box;
    }

    public void setTowerColiders(Array<BoundingBox> coliders) {
        this.coliders = coliders;
    }

}
