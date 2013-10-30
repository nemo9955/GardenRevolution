package com.nemo9955.garden_revolution.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.entitati.Knight;


public class World implements Disposable {

    private Array<ModelInstance>            nori  = new Array<ModelInstance>();
    public Array<ModelInstance>             mediu = new Array<ModelInstance>();

    public Array<Entitate>                  foe   = new Array<Entitate>();
    public Array<Entitate>                  ally  = new Array<Entitate>();
    public Array<Entitate>                  shot  = new Array<Entitate>();

    public Array<CatmullRomSpline<Vector3>> path;

    public World(Model scena) {
        makeNori();

        path = new Array<CatmullRomSpline<Vector3>>( 2 );

        Vector3 pct1[] = new Vector3[7];
        pct1[0] = new Vector3( 10, 1, 10 );
        pct1[1] = new Vector3( 10, 1, 10 );
        pct1[2] = new Vector3( 10, 1, -10 );
        pct1[3] = new Vector3( -8, 1, -8 );
        pct1[4] = new Vector3( 0, 1, 10 );
        pct1[5] = new Vector3( -10, 1, 10 );
        pct1[6] = new Vector3( -10, 1, 10 );


        Vector3 pct2[] = new Vector3[6];
        pct2[0] = new Vector3( -15, 0, -15 );
        pct2[1] = new Vector3( -15, 0, -15 );
        pct2[2] = new Vector3( -15, 0, 15 );
        pct2[3] = new Vector3( 15, 0, 15 );
        pct2[4] = new Vector3( 15, 0, -15 );
        pct2[5] = new Vector3( 15, 0, -15 );

        Model sfera = new ModelBuilder().createSphere( 0.2f, 0.2f, 0.2f, 5, 5, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        for (byte i = 1 ; i <pct1.length -1 ; i ++ )
            addMediu( new ModelInstance( sfera, pct1[i] ) );

        for (byte j = 1 ; j <pct2.length -1 ; j ++ )
            addMediu( new ModelInstance( sfera, pct2[j] ) );


        path.add( new CatmullRomSpline<Vector3>( pct1, false ) );
        path.add( new CatmullRomSpline<Vector3>( pct2, false ) );

        populateWorld( scena );
    }

    public void update(float delta) {

        for (Entitate fo : foe ) {
            fo.update( delta );
            if ( fo.dead )
                foe.removeValue( fo, false );
        }
        for (Entitate al : ally ) {
            al.update( delta );
            if ( al.dead )
                ally.removeValue( al, false );
        }
        for (Entitate sh : shot ) {
            sh.update( delta );
            if ( sh.dead )
                shot.removeValue( sh, false );
            for (Entitate fo : foe ) {
                if ( fo.box.intersects( sh.box ) ) {
                    fo.damage( sh );
                    sh.dead = true;
                }
            }
        }

    }

    public void render(ModelBatch modelBatch, Lights light, Shader shader) {
        for (ModelInstance nor : nori )
            modelBatch.render( nor, shader );
        for (ModelInstance e : mediu )
            modelBatch.render( e, light );

        for (Entitate e : foe )
            e.render( modelBatch );
        for (Entitate e : ally )
            e.render( modelBatch, light );
        for (Entitate e : shot )
            e.render( modelBatch );
    }

    Vector3 tmp = new Vector3();

    public void renderLines(ImmediateModeRenderer20 renderer) {
        Vector3 corn[] = new Vector3[8];

        for (Entitate e : foe ) {
            corn = e.box.getCorners();
            for (Vector3 crn : corn ) {
                renderer.color( 1, 0, 0, 1 );
                renderer.vertex( crn.x, crn.y, crn.z );
            }
            renderer.color( 1, 0, 0, 1 );
            renderer.vertex( e.box.getCenter().x, e.box.getCenter().y, e.box.getCenter().z );
        }

        for (Entitate e : ally ) {
            corn = e.box.getCorners();
            for (Vector3 crn : corn ) {
                renderer.color( 0, 0, 1, 1 );
                renderer.vertex( crn.x, crn.y, crn.z );
            }
            renderer.color( 0, 0, 1, 1 );
            renderer.vertex( e.box.getCenter().x, e.box.getCenter().y, e.box.getCenter().z );
        }

        for (Entitate e : shot ) {
            corn = e.box.getCorners();
            for (Vector3 crn : corn ) {
                renderer.color( 0, 1, 1, 1 );
                renderer.vertex( crn.x, crn.y, crn.z );
            }
            renderer.color( 0, 1, 1, 1 );
            renderer.vertex( e.box.getCenter().x, e.box.getCenter().y, e.box.getCenter().z );
        }

        float val = 0;
        while ( val <=1f ) {
            path.get( 0 ).valueAt( tmp, val );
            renderer.color( 0.5f, 0f, 0f, 1f );
            renderer.vertex( tmp.x, tmp.y, tmp.z );
            val += 1f /100f;
        }
        val = 0;
        while ( val <=1f ) {
            path.get( 1 ).valueAt( tmp, val );
            renderer.color( 0f, 0f, 0.5f, 1f );
            renderer.vertex( tmp.x, tmp.y, tmp.z );
            val += 1f /100f;
        }


    }


    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Random zar = new Random();
        Model sfera = build.createSphere( 5, 5, 5, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        ModelInstance nor;

        int norx, norz;

        for (int i = 0 ; i <20 ; i ++ ) {
            norx = zar.nextInt( 200 ) -100;
            norz = zar.nextInt( 200 ) -100;
            for (int j = 1 ; j <=5 ; j ++ ) {
                nor = new ModelInstance( sfera );
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
        }

        addFoe( new Knight( closestPath( new Vector3( -9, 1, 17 ) ), -9, 1, 17 ) );

    }

    public CatmullRomSpline<Vector3> closestPath(Vector3 loc) {
        CatmullRomSpline<Vector3> close = null;
        float dist = Float.MAX_VALUE;

        for (int i = 0 ; i <path.size ; i ++ ) {
            path.get( i ).valueAt( tmp, path.get( i ).approximate( loc ) );
            System.out.println( i +"  distanta : " +loc.dst2( tmp ) );
            if ( loc.dst2( tmp ) <dist ) {
                dist = loc.dst2( tmp );
                close = path.get( i );
            }
        }
        System.out.println();
        return close;
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

        for (Entitate e : foe )
            e.dispose();
        for (Entitate e : ally )
            e.dispose();
        for (Entitate e : shot )
            e.dispose();
        for (ModelInstance e : mediu )
            e.model.dispose();

        foe.clear();
        ally.clear();
        shot.clear();
        mediu.clear();

    }

}
