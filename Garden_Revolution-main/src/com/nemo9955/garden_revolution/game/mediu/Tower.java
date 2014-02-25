package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;


public class Tower implements Disposable {

    private WorldWrapper         world;
    private Array<BoundingBox>   coliders        = new Array<BoundingBox>( false, 1 );

    private Array<ModelInstance> parts           = new Array<ModelInstance>( false, 1 );
    public final Vector3         poz             = new Vector3();
    public TowerType             type;

    private Weapon               weapon;
    public Vector3               place           = new Vector3();
    private Vector3              direction       = new Vector3();
    public Ray                   ray             = new Ray( place, direction );
    public byte                  ID;

    public String                ocupier         = null;

    public boolean               isFiringHold    = false;
    public long                  fireChargedTime = 0;
    public float                 charge          = 0;

    private Decal                pointer         = Decal.newDecal( 2, 2, Garden_Revolution.getGameTexture( "pointer-2" ), true );


    public Tower(TowerType type, WorldWrapper world, Vector3 poz, int ID) {
        this.ID = (byte) ID;
        this.poz.set( poz );
        this.world = world;

        upgradeTower( type );

        // pointer.setPosition( poz.x, poz.y +5f, poz.z );
        // place.set( poz ).add( 0, 10, 0 );
        direction.set( -1, 0, 0 );
    }

    public boolean fireWeapon() {
        if ( hasWeapon() )
            return weapon.fire( world, ray, charge );
        return false;
    }

    public boolean changeWeapon(WeaponType toChange) {
        if ( type ==TowerType.VIEWPOINT )
            return false;
        if ( !hasWeapon() ) {
            weapon = new Weapon( toChange, place );
            return true;
        }
        if ( weapon.type ==toChange )
            return false;
        weapon.changeWeapon( toChange );
        return true;
    }

    public boolean upgradeTower(TowerType upgrade) {
        if ( type !=null )
            if ( type.rank >=upgrade.rank )
                return false;
        type = upgrade;
        parts.clear();

        Array<Node> remove = new Array<Node>( false, 1 );

        world.getDef().removeColiders( getTowerColiders() );
        getTowerColiders().clear();

        ModelInstance model = new ModelInstance( upgrade.getModel(), poz );
        for (int i = 0 ; i <model.nodes.size ; i ++ ) {
            String id = model.nodes.get( i ).id;

            if ( id.startsWith( "arma" ) ) {
                place.set( model.nodes.get( i ).translation ).add( poz );
                ray.set( place, direction );
            }
            else if ( id.startsWith( "colide" ) ) {
                BoundingBox box = new BoundingBox();
                model.getNode( id ).calculateBoundingBox( box );
                box.set( box.min.add( poz ), box.max.add( poz ) );
                addToTowerColiders( box );
                remove.add( model.nodes.get( i ) );
            }
        }
        model.nodes.removeAll( remove, false );
        parts.add( model );

        pointer.setPosition( place.x, place.y +10f, place.z );

        return true;
    }

    public void update(float delta) {

        if ( ocupier !=null &&isFiringHold )
            fireWeapon();

        if ( hasWeapon() )
            weapon.type.updateWeaponTargeting( this, true );
    }

    public void render(ModelBatch modelBatch, Environment light, DecalBatch decalBatch) {
        for (ModelInstance model : parts )
            modelBatch.render( model, light );
        if ( hasWeapon() )// TODO add a showTargeting boolean
            weapon.render( modelBatch, light, decalBatch );
        if ( ocupier !=null &&world.isMultiplayer() )
            decalBatch.add( pointer );
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
        if ( hasWeapon() )// TODO get rid of this when i get proper weapon models
            weapon.dispose();
    }

    public Array<BoundingBox> getTowerColiders() {
        return coliders;
    }

    public BoundingBox addToTowerColiders(BoundingBox box) {
        coliders.add( box );
        world.getDef().addToColide( box );
        return box;
    }

    public void setFiringHold(boolean isFiring) {
        world.getDef().setTowerFireHold( this, isFiring );
    }

    public boolean isWeaponType(FireType ft) {
        if ( getWeapon() !=null )
            if ( getWeapon().type.getFireType() ==ft )
                return true;
        return false;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 dir) {
        setDirection( dir.x, dir.y, dir.z );
    }

    public void setDirection(float x, float y, float z) {
        direction.set( x, y, z );
        ray.set( place, direction );

        if ( hasWeapon() )
            weapon.type.updateWeaponTargeting( this, false );
    }

}
