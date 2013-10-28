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

    private Array<ModelInstance>      nori  = new Array<ModelInstance>();
    public Array<ModelInstance>       mediu = new Array<ModelInstance>();

    public Array<Entitate>            foe   = new Array<Entitate>();
    public Array<Entitate>            ally  = new Array<Entitate>();
    public Array<Entitate>            shot  = new Array<Entitate>();

    public CatmullRomSpline<Vector3> path;

    public World(Model scena) {
        makeNori();

        Vector3 pct[] = new Vector3[7];
        pct[0] = new Vector3( 10, 1, 10 );
        pct[1] = new Vector3( 10, 1, 10 );
        pct[2] = new Vector3( 10, 1, -10 );
        pct[3] = new Vector3( -8, 1, -8 );
        pct[4] = new Vector3( 0, 1, 10 );
        pct[5] = new Vector3( 0, 1, 0 );
        pct[6] = new Vector3( 0, 1, 0 );

        Model sfera = new ModelBuilder().createSphere( 0.2f, 0.2f, 0.2f, 5, 5, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        for (byte i = 1 ; i <pct.length -1 ; i ++ ) {
            addMediu( new ModelInstance( sfera, pct[i] ) );
        }
        // System.out.println( dist );
        path = new CatmullRomSpline<Vector3>( pct, false );
        
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
            renderer.color( 0f, 0f, 0f, 1f );
            path.valueAt( /* out: */tmp, val );
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

        addFoe( new Knight( path, 15, 0, 9 ) );

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
