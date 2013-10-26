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
import com.nemo9955.garden_revolution.game.entitati.Knight;


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
        for (Entitate e : foe ) {
            e.update( delta );
            if ( e.dead )
                foe.removeValue( e, false );
        }
        for (Entitate e : ally ) {
            e.update( delta );
            if ( e.dead )
                ally.removeValue( e, false );
        }
        for (Entitate e : shot ) {
            e.update( delta );
            if ( e.dead )
                shot.removeValue( e, false );
        }

    }

    public void render(ModelBatch modelBatch, Lights light) {
        for (ModelInstance nor : nori )
            modelBatch.render( nor );
        for (ModelInstance e : mediu )
            modelBatch.render( e, light );

        for (Entitate e : foe )
            e.render( modelBatch, light );
        for (Entitate e : ally )
            e.render( modelBatch, light );
        for (Entitate e : shot )
            e.render( modelBatch );
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

            addMediu( instance );
            // if ( node.id.equals( "NurbsPath" ) ) {
            // for (Node nod : instance.nodes) {
            // System.out.println( nod.translation );
            // }
            // }
        }

        addFoe( new Knight( -5, 5, 5 ) );

        Model model;
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox( 1f, 1f, 1f, new Material( ColorAttribute.createDiffuse( Color.GREEN ) ), Usage.Position |Usage.Normal );
        addAlly( new Entitate( model, 8, 5, 5 ) );

    }

    public Entitate addFoe(Entitate ent) {
        foe.add( ent );
        return ent;
    }

    public Entitate addAlly(Entitate ent) {
        ally.add( ent );
        return ent;
    }

    public Entitate addShot(Entitate ent) {
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
