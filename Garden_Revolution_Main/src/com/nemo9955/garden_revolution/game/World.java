package com.nemo9955.garden_revolution.game;

import java.io.IOException;

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
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.nemo9955.garden_revolution.utility.IndexedObject;


public class World implements Disposable {

    private Array<ModelInstance> nori      = new Array<ModelInstance>( false, 10 );
    public Array<ModelInstance>  mediu     = new Array<ModelInstance>( false, 10 );

    public Array<Inamic>         foe       = new Array<Inamic>( false, 10 );
    public Array<Aliat>          ally      = new Array<Aliat>( false, 10 );
    public Array<Shot>           shot      = new Array<Shot>( false, 10 );

    public Array<Path<Vector3>>  paths;

    private PerspectiveCamera    cam;
    private Vector3[]            camPoz;
    public int                   curentCam = 0;

    public World(Model scena, PerspectiveCamera cam) {
        this.cam = cam;
        makeNori();
        populateWorld( scena );
    }

    public void update(float delta) {

        for (Inamic fo : foe ) {
            fo.update( delta );
            if ( fo.dead )
                foe.removeValue( fo, false );
        }
        for (Aliat al : ally ) {
            al.update( delta );
            if ( al.dead )
                ally.removeValue( al, false );
        }
        for (Shot sh : shot ) {
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
            e.render( modelBatch, light );
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
        int numPaths = 0;
        Array<Array<IndexedObject<Vector3>>> cp = null;
        String[] sect;

        Element map;
        try {
            map = new XmlReader().parse( Gdx.files.internal( "harti/scena.xml" ) );
            cams = map.getInt( "turnuri" );
            numPaths = map.getInt( "drumuri" );

            camPoz = new Vector3[cams];
            paths = new Array<Path<Vector3>>( numPaths );
            cp = new Array<Array<IndexedObject<Vector3>>>( 1 );

            for (int k = 0 ; k <numPaths ; k ++ )
                cp.add( new Array<IndexedObject<Vector3>>( 1 ) );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            String id = scena.nodes.get( i ).id;
            ModelInstance instance = new ModelInstance( scena, id );
            Node node = instance.getNode( id );
            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();
            instance.calculateTransforms();

            if ( id.startsWith( "cam" ) ) {
                sect = id.split( "_" );
                camPoz[Integer.parseInt( sect[1] ) -1] = scena.nodes.get( i ).translation;
            }
            else if ( id.startsWith( "path" ) ) {
                sect = id.split( "_" );
                int pat = Integer.parseInt( sect[1] ) -1;
                int pct = Integer.parseInt( sect[2] );
                cp.get( pat ).add( new IndexedObject<Vector3>( scena.nodes.get( i ).translation, pct ) );
            }
            else
                addMediu( instance );

        }

        for (int k = 0 ; k <numPaths ; k ++ ) {

            Vector3 cps[] = new Vector3[cp.get( k ).size +2];
            for (int j = 0 ; j <cp.get( k ).size ; j ++ )
                cps[j +1] = cp.get( k ).get( j ).object;
            cps[0] = cps[1];
            cps[cps.length -1] = cps[cps.length -2];

            paths.add( new CatmullRomSpline<Vector3>( cps, false ) );
        }

        setCamera( 2 );

    }

    public void setCamera(int nr) {// FIXME point at

        nr = MathUtils.clamp( nr, 0, camPoz.length -1 );

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

    public Inamic addFoe(float x, float y, float z) {
        Inamic inamicTemp = inamicPool.obtain().create( closestPath( tmp.set( x, y, z ) ), x, y, z );
        foe.add( inamicTemp );
        return inamicTemp;
    }

    public Aliat addAlly(float x, float y, float z) {
        Aliat aliatTemp = aliatPool.obtain().create( x, y, z );
        ally.add( aliatTemp );
        return aliatTemp;
    }

    public Shot addShot(Vector3 position, Vector3 direction) {
        Shot shotTemp = shotPool.obtain().create( position, direction );
        shot.add( shotTemp );
        return shotTemp;
    }

    private void addMediu(ModelInstance med) {
        mediu.add( med );
    }

    private Pool<Inamic> inamicPool = new Pool<Inamic>() {

                                        @Override
                                        protected Inamic newObject() {
                                            return new Inamic();
                                        }
                                    };

    private Pool<Aliat>  aliatPool  = new Pool<Aliat>() {

                                        @Override
                                        protected Aliat newObject() {
                                            return new Aliat();
                                        }
                                    };

    private Pool<Shot>   shotPool   = new Pool<Shot>() {

                                        @Override
                                        protected Shot newObject() {
                                            return new Shot();
                                        }
                                    };

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
