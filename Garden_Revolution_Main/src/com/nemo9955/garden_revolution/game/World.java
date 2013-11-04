package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class World implements Disposable {

    private Array<ModelInstance> nori      = new Array<ModelInstance>( false, 10 );
    public Array<ModelInstance>  mediu     = new Array<ModelInstance>( false, 10 );

    public Array<Entitate>       foe       = new Array<Entitate>( false, 10 );
    public Array<Entitate>       ally      = new Array<Entitate>( false, 10 );
    public Array<Entitate>       shot      = new Array<Entitate>( false, 10 );

    public Array<Path<Vector3>>  paths;

    private PerspectiveCamera    cam;
    private Vector3[]            camPoz;
    public int                   curentCam = 0;

    public World(Model scena, PerspectiveCamera cam) {
        this.cam = cam;

        populateWorld( scena );
        makeNori();
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

    private Vector3 tmp = new Vector3();

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
        int cams = 0;
        int perPath[] = new int[5];
        int numPaths = 0;
        Array<Vector3[]> cp;

        String[] sect;

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            String id = scena.nodes.get( i ).id;
            ModelInstance instance = new ModelInstance( scena, id );
            Node node = instance.getNode( id );
            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();
            instance.calculateTransforms();

            // System.out.println( id );

            if ( id.startsWith( "cam" ) )
                cams ++;
            else if ( id.startsWith( "path" ) ) {
                sect = id.split( "_" );
                perPath[Integer.parseInt( sect[1] ) -1] ++;
                if ( Integer.parseInt( sect[1] ) >numPaths )
                    numPaths = Integer.parseInt( sect[1] );
            }
            else
                addMediu( instance );

        }

        paths = new Array<Path<Vector3>>();
        camPoz = new Vector3[cams];
        cp = new Array<Vector3[]>();
        for (int k = 0 ; k <numPaths ; k ++ ) {
            cp.insert( k, new Vector3[perPath[k] +2] );
            // System.out.println( ( k +1 ) +" " + ( perPath[k] +2 ) );
        }
        // System.out.println();

        // for (int k = 0 ; k <numPaths ; k ++ )
        // System.out.println( ( k +1 ) +"  " +cp.get( k ).length );

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            String id = scena.nodes.get( i ).id;

            if ( id.startsWith( "cam" ) ) {
                sect = id.split( "_" );
                camPoz[Integer.parseInt( sect[1] ) -1] = scena.nodes.get( i ).translation;
            }
            else if ( id.startsWith( "path" ) ) {
                sect = id.split( "_" );
                // System.out.println( id +" " +Integer.parseInt( sect[1] ) +" " +Integer.parseInt( sect[2] ) );
                cp.get( Integer.parseInt( sect[1] ) -1 )[Integer.parseInt( sect[2] )] = scena.nodes.get( i ).translation;
            }

        }

        for (int k = 0 ; k <numPaths ; k ++ ) {
            cp.get( k )[0] = cp.get( k )[1];
            cp.get( k )[perPath[k] +1] = cp.get( k )[perPath[k]];
        }

        // for (int k = 0 ; k <numPaths ; k ++ )
        // for (int j = 0 ; j <perPath[k] +2 ; j ++ )
        // System.out.println( k +" " +j +" " +cp.get( k )[j] );
        // System.out.println();

        for (int k = 0 ; k <numPaths ; k ++ ) {
            paths.add( new CatmullRomSpline<Vector3>( cp.get( k ), false ) );
        }
        // System.out.println();

        for (int k = 0 ; k <numPaths ; k ++ )
            for (int j = 0 ; j < ( (CatmullRomSpline<Vector3>) paths.get( k ) ).controlPoints.length ; j ++ )
                // System.out.println( ( (CatmullRomSpline<Vector3>) paths.get( k ) ).controlPoints[j] );
                setCamera( 0 );

    }

    public void setCamera(int nr) {// FIXME point at

        Ray ray = cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        float distance = -ray.origin.y /ray.direction.y;
        Vector3 look = ray.getEndPoint( new Vector3(), distance );
        Vector3 tr = cam.direction.cpy();

        cam.position.set( camPoz[nr] );

        cam.lookAt( look );

        cam.direction.y = tr.y;

        curentCam = nr;

        cam.update();
    }

    public void nextCamera() {
        curentCam ++;
        if ( curentCam >=camPoz.length )
            curentCam = 0;
        setCamera( curentCam );
    }

    public void prevCamera() {
        curentCam --;
        if ( curentCam <0 )
            curentCam = camPoz.length -1;
        setCamera( curentCam );
    }

    public Path<Vector3> closestPath(final Vector3 location) {
        Path<Vector3> closest = null;
        float dist = Float.MAX_VALUE;
        for (Path<Vector3> path : paths ) {
            tmp = ( (CatmullRomSpline<Vector3>) path ).controlPoints[0].cpy();
            if ( location.dst2( tmp ) <dist ) {
                dist = location.dst2( tmp );
                closest = path;
            }
        }
        return closest;
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
