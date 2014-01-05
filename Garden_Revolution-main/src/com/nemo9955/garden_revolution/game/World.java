package com.nemo9955.garden_revolution.game;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.entitati.Aliat;
import com.nemo9955.garden_revolution.game.entitati.Entitate;
import com.nemo9955.garden_revolution.game.entitati.Inamic;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.enumTypes.Armament;
import com.nemo9955.garden_revolution.game.enumTypes.Inamici;
import com.nemo9955.garden_revolution.game.enumTypes.Shots;
import com.nemo9955.garden_revolution.game.enumTypes.Turnuri;
import com.nemo9955.garden_revolution.game.mediu.Arma.FireMode;
import com.nemo9955.garden_revolution.game.mediu.Turn;
import com.nemo9955.garden_revolution.utility.IndexedObject;


public class World implements Disposable {

    private Array<Disposable>               toDispose   = new Array<Disposable>( false, 1 );

    private Array<ModelInstance>            nori        = new Array<ModelInstance>( false, 10 );
    public Array<ModelInstance>             mediu       = new Array<ModelInstance>( false, 10 );

    public Array<Inamic>                    foe         = new Array<Inamic>( false, 10 );
    public Array<Aliat>                     ally        = new Array<Aliat>( false, 10 );
    public Array<Shot>                      shot        = new Array<Shot>( false, 10 );

    public Array<BoundingBox>               colide      = new Array<BoundingBox>( false, 10 );
    public Array<CatmullRomSpline<Vector3>> paths;
    private int                             viata;

    private PerspectiveCamera               cam;
    private Vector3                         tmp         = new Vector3();
    private Vector3                         tmp2        = new Vector3();
    public final Vector3                    overview    = new Vector3();

    private Turn[]                          turnuri;
    public int                              curentTurn  = -1;
    protected boolean                       isOneToweUp = false;

    public Waves                            waves;

    public World(FileHandle location, PerspectiveCamera cam) {
        this.cam = cam;
        makeNori();
        populateWorld( location );
        readData( location );
    }

    public void update(float delta) {

        if ( isOneToweUp &&waves.finishedWaves() )
            waves.update( delta );

        for (Turn trn : turnuri ) {
            trn.update( delta );
        }

        for (Inamic fo : foe ) {
            fo.update( delta );
            if ( fo.dead ) {
                inamicPool.free( fo );
                foe.removeValue( fo, false );
            }
        }
        for (Aliat al : ally ) {
            al.update( delta );
            if ( al.dead ) {
                aliatPool.free( al );
                ally.removeValue( al, false );
            }
        }
        for (Shot sh : shot ) {
            sh.update( delta );
            if ( sh.dead ) {
                shotPool.free( sh );
                shot.removeValue( sh, false );
            }
        }

    }

    public void render(ModelBatch modelBatch, Environment light, Shader shader) {
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
        for (Turn turn : turnuri )
            turn.render( modelBatch, light );
    }

    public void renderDebug(ShapeRenderer shape) {

        shape.setProjectionMatrix( cam.combined );
        shape.begin( ShapeType.Line );

        shape.setColor( 1, 0.5f, 0, 1 );

        for (BoundingBox box : colide )
            shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        shape.setColor( 0.7f, 0.8f, 0.4f, 1 );

        for (Turn turn : turnuri )
            for (BoundingBox box : turn.coliders )
                shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        shape.setColor( 0.5f, 0, 0.5f, 1 );

        for (Turn turn : turnuri )
            shape.box( turn.baza.min.x, turn.baza.min.y, turn.baza.max.z, turn.baza.getDimensions().x, turn.baza.getDimensions().y, turn.baza.getDimensions().z );

        shape.setColor( 1, 0, 0, 1 );

        for (Entitate e : foe )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 0, 1, 1 );

        for (Entitate e : ally )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 1, 1, 1 );

        for (Entitate e : shot )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        int pts = paths.size;
        for (int i = 0 ; i <pts ; i ++ ) {
            float val = 0;
            paths.get( i ).valueAt( tmp, val );
            while ( val <1f ) {
                val += 1f /150f;
                paths.get( i ).valueAt( tmp2, val );
                shape.setColor( i +3 /pts, i +1 /pts, i +2 /pts, 1f );
                shape.line( tmp, tmp2 );
                tmp.set( tmp2 );
            }
        }
        shape.end();
    }

    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Model sfera = build.createSphere( 5, 5, 5, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        toDispose.add( sfera );
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

    private void populateWorld(FileHandle location) {
        Array<Array<IndexedObject<Vector3>>> cp = null;
        String[] sect;

        Element map = null;
        try {
            map = new XmlReader().parse( location );
            turnuri = new Turn[map.getInt( "turnuri" )];
            paths = new Array<CatmullRomSpline<Vector3>>( map.getInt( "drumuri" ) );
            cp = new Array<Array<IndexedObject<Vector3>>>( 1 );

            for (int k = 0 ; k <map.getInt( "drumuri" ) ; k ++ )
                cp.add( new Array<IndexedObject<Vector3>>( false, 1, IndexedObject.class ) );
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Model scena = new G3dModelLoader( new UBJsonReader() ).loadModel( location.parent().parent().child( "maps" ).child( map.get( "map" ) ) );

        toDispose.add( scena );

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            String id = scena.nodes.get( i ).id;
            ModelInstance instance = new ModelInstance( scena, id );
            // Node node = instance.getNode( id );
            // instance.transform.set( node.globalTransform );
            // node.translation.set( 0, 0, 0 );
            // node.scale.set( 1, 1, 1 );
            // node.rotation.idt();
            // instance.calculateTransforms();

            if ( id.startsWith( "turn" ) ) {
                sect = id.split( "_" );
                turnuri[Integer.parseInt( sect[1] ) -1] = new Turn( instance, this, scena.nodes.get( i ).translation );
            }
            else if ( id.startsWith( "path" ) ) {
                sect = id.split( "_" );
                int pat = Integer.parseInt( sect[1] ) -1;
                int pct = Integer.parseInt( sect[2] );
                cp.get( pat ).add( new IndexedObject<Vector3>( scena.nodes.get( i ).translation, pct ) );
            }
            else if ( id.startsWith( "colider" ) ) {
                BoundingBox box = new BoundingBox();
                instance.calculateBoundingBox( box );
                colide.add( box );
            }
            else if ( id.endsWith( "solid" ) ) {
                BoundingBox box = new BoundingBox();
                instance.calculateBoundingBox( box );
                colide.add( box );
                addMediu( instance );
            }
            else if ( id.startsWith( "camera" ) ) {
                overview.set( scena.nodes.get( i ).translation );
            }
            else {
                addMediu( instance );
            }

        }

        for (Array<IndexedObject<Vector3>> pat : cp )
            pat.sort();

        for (Array<IndexedObject<Vector3>> pat : cp ) {

            Vector3 cps[] = new Vector3[pat.size +2];
            for (int j = 0 ; j <pat.size ; j ++ )
                cps[j +1] = pat.get( j ).object;
            cps[0] = cps[1];
            cps[cps.length -1] = cps[cps.length -2];

            paths.add( new CatmullRomSpline<Vector3>( cps, false ) );
        }

        if ( overview.isZero() )
            setCamera( 0 );
        else {
            cam.position.set( overview );
            cam.lookAt( Vector3.Zero );
            cam.update();
        }


    }

    private void readData(FileHandle location) {

        Element map = null;
        try {
            map = new XmlReader().parse( location );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        waves = new Waves( this );

        setViata( map.getInt( "viata", 100 ) );

        Array<Element> tmpWaves = map.getChildrenByName( "wave" );
        tmpWaves.shrink();
        Array<IndexedObject<Element>> sortedWaves = new Array<IndexedObject<Element>>( tmpWaves.size );
        for (int i = 0 ; i <tmpWaves.size ; i ++ )
            sortedWaves.add( new IndexedObject<Element>( tmpWaves.get( i ), tmpWaves.get( i ).getInt( "index" ) -1 ) );
        sortedWaves.sort();
        tmpWaves.clear();

        for (IndexedObject<Element> wav : sortedWaves ) {
            waves.addWave( wav.object.getFloat( "delay", 5 ), wav.object.getFloat( "interval", 0.5f ) );
            Array<Element> tmpPaths = wav.object.getChildrenByName( "path" );

            for (Element pat : tmpPaths ) {

                int numar = pat.getInt( "nr" );
                for (int i = 0 ; i <pat.getChildCount() ; i ++ ) {

                    Element monstru = pat.getChild( i );
                    waves.populate( numar -1, Inamici.valueOf( monstru.getName().toUpperCase() ), monstru.getInt( "amount", 1 ) );

                }
            }
        }
        sortedWaves.clear();
    }


    public int getViata() {
        return viata;
    }


    public void setViata(int viata) {
        this.viata = viata;
        Garden_Revolution.gameplay.viataTurn.setText( "Life " +viata );
    }

    public void addViata(int amount) {
        this.viata += amount;
        Garden_Revolution.gameplay.viataTurn.setText( "Life " +viata );
    }

    public boolean isInTower() {
        return getTower() !=null;
    }

    public Turn getTower() {
        if ( turnuri.length ==0 ||curentTurn ==-1 )
            return null;
        return turnuri[curentTurn];
    }

    public void upgradeCurentTower(Turnuri upgrade) {
        if ( isInTower() )
            if ( getTower().upgradeTower( upgrade ) ) {
                setCamera( curentTurn );
                isOneToweUp = true;
            }
    }

    public void changeCurentWeapon(Armament arma) {
        Turn turn = getTower();
        if ( isInTower() &&turn.type !=null )
            if ( turn.changeWeapon( arma.getNewInstance( turn.place ) ) )
                setCamera( curentTurn );
    }

    public Vector3 getCameraRotAround() {
        if ( isInTower() )
            return getTower().place;
        return cam.position;

    }

    public void setCamera(Turn turn) {
        if ( turn !=null )
            setCamera( getTowerIndex( turn ) );
    }

    @SuppressWarnings("deprecation")
    public void setCamera(int nr) {// FIXME cand se uita prea aproape de baza turnului , camera se pune intr-o pozitie in care turnul e prea in mijlocul ecranului

        if ( nr !=MathUtils.clamp( nr, 0, turnuri.length -1 ) )
            return;
        curentTurn = nr;

        Ray ray = cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        float distance = -ray.origin.y /ray.direction.y;
        Vector3 look = ray.getEndPoint( Vector3.tmp3, distance );

        cam.position.set( turnuri[nr].place );
        cam.lookAt( look );
        cam.up.set( Vector3.Y );

        if ( getTower().type ==null ) {
            cam.position.add( 0, 3, 0 );
        }
        else {
            tmp2.set( cam.direction ).scl( 4 );
            tmp.set( cam.up ).crs( cam.direction ).scl( 3 );

            cam.position.sub( tmp2 );
            cam.position.sub( tmp );
            cam.position.add( 0, 2, 0 );
        }
        cam.update();
    }

    public void nextCamera() {
        curentTurn ++;
        if ( curentTurn >=turnuri.length )
            curentTurn = 0;
        setCamera( curentTurn );
    }

    public void prevCamera() {
        curentTurn --;
        if ( curentTurn <0 )
            curentTurn = turnuri.length -1;
        setCamera( curentTurn );
    }

    public CatmullRomSpline<Vector3> closestPath(final Vector3 location) {
        CatmullRomSpline<Vector3> closest = null;
        float dist = Float.MAX_VALUE;
        for (CatmullRomSpline<Vector3> path : paths ) {
            tmp = path.controlPoints[0].cpy();
            if ( location.dst2( tmp ) <dist ) {
                dist = location.dst2( tmp );
                closest = path;
            }
        }
        return closest;
    }

    private int getTowerIndex(Turn turn) {
        for (int i = 0 ; i <turnuri.length ; i ++ )
            if ( turnuri[i].equals( turn ) )
                return i;

        return -1;
    }

    public Inamic addFoe(Inamici type, CatmullRomSpline<Vector3> path, float x, float y, float z) {
        Inamic inamicTemp = inamicPool.obtain().create( path, type, x, y, z );
        foe.add( inamicTemp );
        return inamicTemp;
    }

    public Inamic addFoe(Inamici type, float x, float y, float z) {
        Inamic inamicTemp = inamicPool.obtain().create( closestPath( tmp.set( x, y, z ) ), type, x, y, z );
        foe.add( inamicTemp );
        return inamicTemp;
    }

    public Aliat addAlly(float x, float y, float z) {
        Aliat aliatTemp = aliatPool.obtain().create( x, y, z );
        ally.add( aliatTemp );
        return aliatTemp;
    }

    public Shot addShot(Shots type, Vector3 position, Vector3 direction) {
        Shot shotTemp = shotPool.obtain().create( type, position, direction );
        shot.add( shotTemp );
        return shotTemp;
    }

    public Shot addShot(Shots type, Vector3 position, Vector3 direction, float charge) {
        Shot shotTemp = shotPool.obtain().create( type, position, direction, charge );
        shot.add( shotTemp );
        return shotTemp;
    }

    private void addMediu(ModelInstance med) {
        mediu.add( med );
    }

    private Pool<Inamic> inamicPool = new Pool<Inamic>() {

                                        @Override
                                        protected Inamic newObject() {
                                            return new Inamic( World.this );
                                        }
                                    };

    private Pool<Aliat>  aliatPool  = new Pool<Aliat>() {

                                        @Override
                                        protected Aliat newObject() {
                                            return new Aliat( World.this );
                                        }
                                    };

    private Pool<Shot>   shotPool   = new Pool<Shot>() {

                                        @Override
                                        protected Shot newObject() {
                                            return new Shot( World.this );
                                        }
                                    };

    public void isTouched(int screenX, int screenY) {

        if ( isInTower() &&getTower().isWeaponState( FireMode.CONTINUOUS ) ) {
            getTower().fireNormal( this, cam.getPickRay( screenX, screenY ) );
        }

    }

    public Turn getTowerHitByRay(Ray ray) {
        for (Turn turn : turnuri )
            if ( turn.intersectsRay( ray ) ) {
                return turn;
            }
        return null;
    }

    public boolean tap(float x, float y, int count, int button, GestureDetector gestures) {
        if ( !isInTower() ||count >=2 ) {
            Ray ray = cam.getPickRay( x, y );
            setCamera( getTowerHitByRay( ray ) );
            return true;
        }
        else if ( isInTower() ) {
            Turn turn = getTower();

            if ( turn.isWeaponState( FireMode.TAP ) )
                turn.fireNormal( this, cam.getPickRay( x, y ) );

            if ( turn.isWeaponState( FireMode.LOCKED_TAP ) )
                turn.fireNormal( this, cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) );
        }
        return false;
    }

    public boolean longPress(float x, float y, GestureDetector gestures) {

        if ( !isInTower() &&Math.abs( Gdx.input.getX() -x ) <20 &&Math.abs( Gdx.input.getY() -y ) <20 ) {
            Ray ray = cam.getPickRay( x, y );
            float distance = -ray.origin.y /ray.direction.y;
            Vector3 tmp = ray.getEndPoint( new Vector3(), distance );

            if ( Gdx.input.isKeyPressed( Keys.F5 ) ) {
                for (int i = 0 ; i <=20 ; i ++ )
                    for (int j = 0 ; j <=20 ; j ++ ) {
                        addFoe( Inamici.ROSIE, i +tmp.x -10f, tmp.y, j +tmp.z -10f );
                    }
            }
            else if ( Gdx.input.isButtonPressed( Buttons.RIGHT ) )
                addAlly( tmp.x, tmp.y, tmp.z );
            else if ( Gdx.input.isButtonPressed( Buttons.MIDDLE ) )
                addFoe( Inamici.MORCOV, tmp.x, tmp.y, tmp.z );

            gestures.invalidateTapSquare();
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {

        for (Disposable dis : toDispose )
            dis.dispose();
        for (Disposable dis : turnuri )
            dis.dispose();

        toDispose.clear();
        foe.clear();
        ally.clear();
        shot.clear();
        mediu.clear();
        nori.clear();

    }

}
