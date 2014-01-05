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
import com.nemo9955.garden_revolution.game.enumTypes.Turnuri;
import com.nemo9955.garden_revolution.game.mediu.Arma.FireCharged;
import com.nemo9955.garden_revolution.game.mediu.Arma.FireMode;
import com.nemo9955.garden_revolution.game.mediu.Arma.FireNormal;


public class Turn implements Disposable {

    public BoundingBox           baza     = new BoundingBox();
    public Array<BoundingBox>    coliders = new Array<BoundingBox>( false, 1 );

    private Array<ModelInstance> parti    = new Array<ModelInstance>( false, 1 );
    public Vector3               place;
    public Vector3               poz;
    public Turnuri               type;


    private Arma                 arma;

    public Turn(ModelInstance baza, World world, Vector3 poz) {
        parti.add( baza );
        place = poz.cpy().add( 0, 10, 0 );
        baza.calculateBoundingBox( this.baza );
        this.poz = poz.cpy();
    }

    public void fireNormal(World world, Ray ray) {
        if ( hasArma() &&arma instanceof FireNormal )
            ( (FireNormal) arma ).fireNormal( world, ray );
    }

    public void fireCharged(World world, Ray ray, float charged) {
        if ( hasArma() &&arma instanceof FireCharged )
            ( (FireCharged) arma ).fireCharged( world, ray, charged );
    }

    public boolean changeWeapon(Arma toChange) {
        if ( hasArma() &&arma.type ==toChange.type )
            return false;
        arma = toChange;
        return true;
    }

    public boolean isWeaponState(FireMode stagiu) {
        if ( !hasArma() )
            return false;
        if ( stagiu !=arma.state )
            return false;
        return true;
    }

    public boolean upgradeTower(Turnuri upgrade) {
        if ( type !=null &&type.rank >=upgrade.rank )
            return false;
        type = upgrade;
        parti.clear();

        Array<Node> remove = new Array<Node>( false, 1 );

        ModelInstance model = new ModelInstance( upgrade.getModel(), poz );
        for (int i = 0 ; i <model.nodes.size ; i ++ ) {
            String id = model.nodes.get( i ).id;

            if ( id.startsWith( "arma" ) ) {
                place = model.nodes.get( i ).translation.cpy().add( poz );
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
        parti.add( model );
        return true;
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch) {
        for (ModelInstance model : parti )
            modelBatch.render( model );
        if ( hasArma() )
            arma.render( modelBatch );
    }

    public void render(ModelBatch modelBatch, Environment light) {
        for (ModelInstance model : parti )
            modelBatch.render( model, light );
        if ( hasArma() )
            arma.render( modelBatch, light );
    }

    public void render(ModelBatch modelBatch, Shader shader) {
        for (ModelInstance model : parti )
            modelBatch.render( model, shader );
        if ( hasArma() )
            arma.render( modelBatch, shader );
    }

    public Arma getArma() {
        return arma;
    }

    public void setArma(Arma arma) {
        this.arma = arma;
    }

    public boolean hasArma() {
        return arma !=null;
    }

    public boolean weaponMoveByTouch() {
        return isWeaponState( FireMode.LOCKED_CHARGE ) ||isWeaponState( FireMode.LOCKED_TAP );
    }

    public boolean weaponFireByCharge() {
        return isWeaponState( FireMode.LOCKED_CHARGE );
    }

    public boolean intersectsRay(Ray ray) {
        if ( Intersector.intersectRayBoundsFast( ray, baza ) )
            return true;
        for (BoundingBox box : coliders )
            if ( Intersector.intersectRayBoundsFast( ray, box ) )
                return true;
        return false;
    }

    @Override
    public void dispose() {
        if ( hasArma() )
            arma.dispose();
    }

}
