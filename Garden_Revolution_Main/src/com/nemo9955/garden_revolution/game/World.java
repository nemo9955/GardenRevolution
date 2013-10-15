package com.nemo9955.garden_revolution.game;

import java.util.Random;

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

    private Array<ModelInstance> nori  = new Array<ModelInstance>();

    public Array<ModelInstance>  draw;

    public Array<Entitate>       foe   = new Array<Entitate>();
    public Array<Entitate>       ally  = new Array<Entitate>();
    public Array<Entitate>       shot  = new Array<Entitate>();
    public Array<Entitate>       mediu = new Array<Entitate>();

    public World(Model scena) {
        draw = new Array<ModelInstance>( false, 20 );// FIXME
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
    }

    public void render(ModelBatch modelBatch, Lights light) {
        for (ModelInstance nor : nori )
            modelBatch.render( nor );
        for (ModelInstance e : draw )
            modelBatch.render( e, light );
    }

    // public Entitate add(Entitate ent) {
    //
    // draw.add( ent );
    //
    // return ent;
    // }

    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Random zar = new Random();
        ModelInstance nor;

        int norx, norz;

        for (int i = 0 ; i <200 ; i ++ ) {
            norx = zar.nextInt( 500 ) -250;
            norz = zar.nextInt( 500 ) -250;
            for (int j = 1 ; j <=15 ; j ++ ) {
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
        Vietate knight = new Vietate( Garden_Revolution.getModel( Assets.KNIGHT ), 0, -1, -20 );

        knight.animation.setAnimation( "Sneak", -1, null );
        addFoe( knight );

    }

    private Entitate addFoe(Entitate ent) {
        draw.add( ent );
        foe.add( ent );
        return ent;
    }

    @SuppressWarnings("unused")
    private Entitate addAlly(Entitate ent) {
        draw.add( ent );
        ally.add( ent );
        return ent;
    }

    @SuppressWarnings("unused")
    private Entitate addShot(Entitate ent) {
        draw.add( ent );
        shot.add( ent );
        return ent;
    }

    private void addMediu(ModelInstance med) {
        draw.add( med );
    }


    @Override
    public void dispose() {
        for (ModelInstance e : draw )
            e.model.dispose();

        draw.clear();
        foe.clear();
        ally.clear();
        shot.clear();
        mediu.clear();

    }

}
