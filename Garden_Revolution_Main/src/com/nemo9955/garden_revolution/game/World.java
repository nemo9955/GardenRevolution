package com.nemo9955.garden_revolution.game;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.entitati.Knight;


public class World implements Disposable {

    private Array<ModelInstance> nori  = new Array<ModelInstance>( false, 10 );
    public Array<ModelInstance>  mediu = new Array<ModelInstance>( false, 10 );

    public Array<Entitate>       foe   = new Array<Entitate>( false, 10 );
    public Array<Entitate>       ally  = new Array<Entitate>( false, 10 );
    public Array<Entitate>       shot  = new Array<Entitate>( false, 10 );

    public ModelInstance         point1;
    public ModelInstance         point2;

    public Array<Path<Vector3>>  paths;

    public World(Model scena) {
        makeNori();

        point1 = new ModelInstance( new ModelBuilder().createBox( 1, 4, 1, new Material( ColorAttribute.createDiffuse( Color.RED ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates ), tmp );
        point2 = new ModelInstance( new ModelBuilder().createBox( 1, 4, 1, new Material( ColorAttribute.createDiffuse( Color.BLUE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates ), tmp );


        paths = new Array<Path<Vector3>>( 2 );

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

        paths.add( new CatmullRomSpline<Vector3>( pct1, false ) );
        paths.add( new CatmullRomSpline<Vector3>( pct2, false ) );

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
        modelBatch.render( point1 );
        modelBatch.render( point2 );
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
            paths.get( 0 ).valueAt( tmp, val );
            renderer.color( 0.5f, 0f, 0f, 1f );
            renderer.vertex( tmp.x, tmp.y, tmp.z );
            val += 1f /100f;
        }
        val = 0;
        while ( val <=1f ) {
            paths.get( 1 ).valueAt( tmp, val );
            renderer.color( 0f, 0f, 0.5f, 1f );
            renderer.vertex( tmp.x, tmp.y, tmp.z );
            val += 1f /100f;
        }


    }


    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Model sfera = build.createSphere( 5, 5, 5, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        ModelInstance nor;

        int norx, norz;

        for (int i = 0 ; i <20 ; i ++ ) {
            norx = MathUtils.random( -100, 100 );
            norz = MathUtils.random( -100, 100 );
            for (int j = 1 ; j <=5 ; j ++ ) {
                nor = new ModelInstance( sfera );
                nor.transform.translate( norx +MathUtils.random( 0f, 7f ), 50, norz +MathUtils.random( 0f, 7f ) );
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

    public Path<Vector3> closestPath(final Vector3 location) {// FIXME
        Path<Vector3> closest = null;
        float dist = Float.MAX_VALUE;
        float rap = 0;

        for (Path<Vector3> path : paths ) {
            rap = path.locate( location );
            path.valueAt( tmp, rap );

            // used to visualise the point in the world
            // if ( ( (CatmullRomSpline<Vector3>) path ).spanCount ==4 )
            // point1.transform.setToTranslation( tmp );
            // if ( ( (CatmullRomSpline<Vector3>) path ).spanCount ==3 )
            // point2.transform.setToTranslation( tmp );

            if ( location.dst2( tmp ) <dist ) {
                dist = location.dst2( tmp );
                closest = path;
            }

            // to see the specific numbers
            System.out.println( "value on path: " +rap +"  location: " +location +"  distance:" +dist +" point on path: " +tmp );

        }
        System.out.println();
        return closest;
    }

    @SuppressWarnings("unused")
    private static float xzDistance2(Vector3 loc1, Vector3 loc2) {
        float a = loc1.x -loc2.x;
        float b = loc1.z -loc2.z;

        return ( a *a +b *b );
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
        for (ModelInstance e : nori )
            e.model.dispose();

        foe.clear();
        ally.clear();
        shot.clear();
        mediu.clear();
        nori.clear();

    }

}
