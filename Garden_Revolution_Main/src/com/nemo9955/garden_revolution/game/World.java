package com.nemo9955.garden_revolution.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;


public class World implements Disposable {

    private Array<ModelInstance> nori  = new Array<ModelInstance>( 110 );

    public Array<Entitate>       foe   = new Array<Entitate>();
    public Array<Entitate>       ally  = new Array<Entitate>();
    public Array<Entitate>       shot  = new Array<Entitate>();
    public Array<ModelInstance>  mediu = new Array<ModelInstance>();

    public World(Model scena) {
        makeNori();
        populateWorld( scena );
    }

    public void update(float delta) {
        for (Entitate e : foe )
            e.update( delta );
        for (Entitate e : ally )
            e.update( delta );
        for (Entitate e : shot )
            e.update( delta );

        if ( Gdx.input.isKeyPressed( Keys.F5 ) ||Gdx.input.isTouched( 0 ) )
            ( (Vietate) foe.first() ).animation.animate( "Walk", -1, null, 0.5f );
        if ( Gdx.input.isKeyPressed( Keys.F6 ) ||Gdx.input.isTouched( 1 ) )
            ( (Vietate) foe.first() ).animation.animate( "Sneak", -1, null, 1 );
        if ( Gdx.input.isKeyPressed( Keys.F7 ) ||Gdx.input.isTouched( 2 ) )
            ( (Vietate) foe.first() ).animation.animate( "Damaged", -1, null, 0.5f );
        if ( Gdx.input.isKeyPressed( Keys.F8 ) ||Gdx.input.isTouched( 3 ) )
            ( (Vietate) foe.first() ).animation.animate( "Idle", -1, null, 0.5f );

    }

    public void render(ModelBatch modelBatch, Lights light) {
        for (ModelInstance nor : nori )
            modelBatch.render( nor );
        for (ModelInstance e : foe )
            modelBatch.render( e, light );
        for (ModelInstance e : ally )
            modelBatch.render( e, light );
        for (ModelInstance e : shot )
            modelBatch.render( e );
        for (ModelInstance e : mediu )
            modelBatch.render( e, light );
    }

    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Random zar = new Random();
        ModelInstance nor;

        int norx, norz;

        for (int i = 0 ; i <20 ; i ++ ) {
            norx = zar.nextInt( 200 ) -100;
            norz = zar.nextInt( 200 ) -100;
            for (int j = 1 ; j <=5 ; j ++ ) {
                nor = new ModelInstance( build.createSphere( 5, 5, 5, 12, 12, new Material( ColorAttribute.createDiffuse( Color.CYAN ) ), Usage.Position |Usage.Normal ) );
                nor.transform.translate( norx +zar.nextFloat() *7, 30, norz +zar.nextFloat() *7 );
                nori.add( nor );
            }
        }
    }

    private void populateWorld(Model scena) {

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            String id = scena.nodes.get( i ).id;
            ModelInstance instance = new ModelInstance( scena, id );
            Node node = instance.getNode( id );
            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();
            instance.calculateTransforms();

            System.out.println( node.id );

            if ( node.id.equals( "turn" ) ||node.id.equals( "pamant" ) ) {
                addMediu( instance );
                continue;
            }
        }
        Vietate knight = new Vietate( Garden_Revolution.getModel( Assets.KNIGHT ), 0, 10, -20 );
        knight.animation.setAnimation( "Sneak", -1, null );
        addFoe( knight );

    }

    private Entitate addFoe(Entitate ent) {
        foe.add( ent );
        return ent;
    }

    @SuppressWarnings("unused")
    private Entitate addAlly(Entitate ent) {
        ally.add( ent );
        return ent;
    }

    @SuppressWarnings("unused")
    private Entitate addShot(Entitate ent) {
        shot.add( ent );
        return ent;
    }

    private void addMediu(ModelInstance med) {
        mediu.add( med );
    }


    @Override
    public void dispose() {

        for (ModelInstance e : foe )
            e.model.dispose();
        for (ModelInstance e : ally )
            e.model.dispose();
        for (ModelInstance e : shot )
            e.model.dispose();
        for (ModelInstance e : mediu )
            e.model.dispose();

        foe.clear();
        ally.clear();
        shot.clear();
        mediu.clear();

    }

}
