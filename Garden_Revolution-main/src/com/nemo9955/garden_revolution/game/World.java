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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
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
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Entity;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.mediu.Weapon;
import com.nemo9955.garden_revolution.game.mediu.Weapon.FireHold;
import com.nemo9955.garden_revolution.utility.IndexedObject;


public class World implements Disposable {

    public static Array<Disposable>         toDispose    = new Array<Disposable>( false, 1 );

    private Array<ModelInstance>            clouds       = new Array<ModelInstance>( false, 10 );
    public Array<ModelInstance>             mediu        = new Array<ModelInstance>( false, 10 );

    public Array<Enemy>                     enemy        = new Array<Enemy>( false, 10 );
    public Array<Ally>                      ally         = new Array<Ally>( false, 10 );
    public Array<Shot>                      shot         = new Array<Shot>( false, 10 );
    public Array<BoundingBox>               colide       = new Array<BoundingBox>( false, 10 );
    public Array<CatmullRomSpline<Vector3>> paths;
    private int                             viata;

    private static Vector3                  tmp          = new Vector3();
    private static Vector3                  tmp2         = new Vector3();
    public final Vector3                    overview     = new Vector3();

    private Tower[]                         towers;
    public int                              currentTower = -1;
    protected boolean                       isOneToweUp  = false;

    public Waves                            waves;

    private PerspectiveCamera               cam;
    private Environment                     environment  = new Environment();


    public World(FileHandle location) {


        // lights.
        environment.set( ColorAttribute.createAmbient( 1, 1, 0, 1 ) );
        environment.add( new PointLight().set( Color.BLUE, new Vector3( 5, -10, 5 ), 100 ) );
        // envir.add( new DirectionalLight().set( Color.WHITE, new Vector3( 1, -1, 0 ) ) );
        environment.add( new DirectionalLight().set( Color.WHITE, new Vector3( 0, -1, 0 ) ) );

        cam = new PerspectiveCamera( 67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();


        makeNori();
        populateWorld( location );
        readData( location );
    }


    public void update(float delta) {

        if ( isOneToweUp &&waves.finishedWaves() )
            waves.update( delta );

        for (Tower trn : towers ) {
            trn.update( delta );
        }

        for (Enemy fo : enemy ) {
            fo.update( delta );
            if ( fo.dead ) {
                inamicPool.free( fo );
                enemy.removeValue( fo, false );
            }
        }
        for (Ally al : ally ) {
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
        for (ModelInstance nor : clouds )
            modelBatch.render( nor, shader );
        for (ModelInstance e : mediu )
            modelBatch.render( e, light );

        for (Entity e : enemy )
            e.render( modelBatch, light );
        for (Entity e : ally )
            e.render( modelBatch, light );
        for (Entity e : shot )
            e.render( modelBatch );
        for (Tower tower : towers )
            tower.render( modelBatch, light );
    }

    public void renderDebug(ShapeRenderer shape) {

        shape.setProjectionMatrix( cam.combined );
        shape.begin( ShapeType.Line );

        shape.setColor( 1, 0.5f, 0, 1 );

        for (BoundingBox box : colide )
            shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        shape.setColor( 0.7f, 0.8f, 0.4f, 1 );

        for (Tower tower : towers )
            for (BoundingBox box : tower.coliders )
                shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        shape.setColor( 0.5f, 0, 0.5f, 1 );

        for (Tower tower : towers )
            shape.box( tower.fundation.min.x, tower.fundation.min.y, tower.fundation.max.z, tower.fundation.getDimensions().x, tower.fundation.getDimensions().y, tower.fundation.getDimensions().z );

        shape.setColor( 1, 0, 0, 1 );

        for (Entity e : enemy )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 0, 1, 1 );

        for (Entity e : ally )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 1, 1, 1 );

        for (Entity e : shot )
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
        clouds.clear();
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
                clouds.add( nor );
            }
        }
    }

    private void populateWorld(FileHandle location) {
        Array<Array<IndexedObject<Vector3>>> cp = null;
        String[] sect;

        Element map = null;
        try {
            map = new XmlReader().parse( location );
            towers = new Tower[map.getInt( "turnuri" )];
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
                towers[Integer.parseInt( sect[1] ) -1] = new Tower( instance, this, scena.nodes.get( i ).translation );
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
                    waves.populate( numar -1, EnemyType.valueOf( monstru.getName().toUpperCase() ), monstru.getInt( "amount", 1 ) );

                }
            }
        }
        sortedWaves.clear();
    }


    public void upgradeCurentTower(TowerType upgrade) {
        if ( isInTower() )
            if ( getTower().upgradeTower( upgrade ) ) {
                setCamera( currentTower );
                isOneToweUp = true;
            }
    }

    public void changeCurrentWeapon(Class<? extends Weapon> weapon) {
        Tower tower = getTower();
        if ( isInTower() &&tower.type !=null )
            try {
                if ( tower.changeWeapon( weapon.getDeclaredConstructor( Vector3.class ).newInstance( tower.place ) ) )
                    setCamera( currentTower );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    @SuppressWarnings("deprecation")
    public CatmullRomSpline<Vector3> closestPath(final Vector3 location) {
        CatmullRomSpline<Vector3> closest = null;
        float dist = Float.MAX_VALUE;

        for (CatmullRomSpline<Vector3> path : paths ) {
            tmp = path.controlPoints[0].tmp();
            if ( location.dst2( tmp ) <dist ) {
                dist = location.dst2( tmp );
                closest = path;
            }
        }
        return closest;
    }

    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path, float x, float y, float z) {
        Enemy inamicTemp = inamicPool.obtain().create( path, type, x, y, z );
        enemy.add( inamicTemp );
        return inamicTemp;
    }

    public Enemy addFoe(EnemyType type, float x, float y, float z) {
        Enemy inamicTemp = inamicPool.obtain().create( closestPath( tmp.set( x, y, z ) ), type, x, y, z );
        enemy.add( inamicTemp );
        return inamicTemp;
    }

    public Ally addAlly(float x, float y, float z) {
        Ally aliatTemp = aliatPool.obtain().create( x, y, z );
        ally.add( aliatTemp );
        return aliatTemp;
    }

    public Shot addShot(ShotType type, Vector3 position, Vector3 direction) {
        Shot shotTemp = shotPool.obtain().create( type, position, direction );
        shot.add( shotTemp );
        return shotTemp;
    }

    public Shot addShot(ShotType type, Vector3 position, Vector3 direction, float charge) {
        Shot shotTemp = shotPool.obtain().create( type, position, direction, charge );
        shot.add( shotTemp );
        return shotTemp;
    }

    private void addMediu(ModelInstance med) {
        mediu.add( med );
    }

    private int getTowerIndex(Tower tower) {
        for (int i = 0 ; i <towers.length ; i ++ )
            if ( towers[i].equals( tower ) )
                return i;

        return -1;
    }

    private Pool<Enemy> inamicPool = new Pool<Enemy>() {

                                       @Override
                                       protected Enemy newObject() {
                                           return new Enemy( World.this );
                                       }
                                   };

    private Pool<Ally>  aliatPool  = new Pool<Ally>() {

                                       @Override
                                       protected Ally newObject() {
                                           return new Ally( World.this );
                                       }
                                   };

    private Pool<Shot>  shotPool   = new Pool<Shot>() {

                                       @Override
                                       protected Shot newObject() {
                                           return new Shot( World.this );
                                       }
                                   };

    public void isTouched(int screenX, int screenY) {

        if ( isInTower() &&getTower().getArma() instanceof FireHold &&screenX >Gdx.graphics.getWidth() /2 ) {
            getTower().fireNormal( this, cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) );
            // getTower().fireNormal( this, cam.getPickRay( screenX, screenY ) );
        }

    }

    public Tower getTowerHitByRay(Ray ray) {
        for (Tower tower : towers )
            if ( tower.intersectsRay( ray ) ) {
                return tower;
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
            // Turn turn = getTower();
            // if ( turn instanceof FireTaped )
            // turn.fireNormal( this, cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) );
        }
        return false;
    }

    public boolean longPress(float x, float y, GestureDetector gestures) {

        if ( isInTower() &&Math.abs( Gdx.input.getX() -x ) <20 &&Math.abs( Gdx.input.getY() -y ) <20 ) {
            Ray ray = cam.getPickRay( x, y );
            float distance = -ray.origin.y /ray.direction.y;
            Vector3 tmp = ray.getEndPoint( new Vector3(), distance );

            if ( Gdx.input.isKeyPressed( Keys.F5 ) ) {
                for (int i = 0 ; i <=20 ; i ++ )
                    for (int j = 0 ; j <=20 ; j ++ ) {
                        addFoe( EnemyType.ROSIE, i +tmp.x -10f, tmp.y, j +tmp.z -10f );
                    }
            }
            else if ( Gdx.input.isButtonPressed( Buttons.RIGHT ) )
                addAlly( tmp.x, tmp.y, tmp.z );
            else if ( Gdx.input.isButtonPressed( Buttons.MIDDLE ) )
                addFoe( EnemyType.MORCOV, tmp.x, tmp.y, tmp.z );

            gestures.invalidateTapSquare();
            return true;
        }
        return false;
    }

    public void moveCamera(float amontX, float amontY) {

        cam.rotateAround( getCameraRotAround(), Vector3.Y, amontX );
        if ( ( amontY >0 &&cam.direction.y <0.7f ) || ( amontY <0 &&cam.direction.y >-0.9f ) ) {
            tmp.set( cam.direction ).crs( cam.up ).y = 0f;
            cam.rotateAround( getCameraRotAround(), tmp.nor(), amontY );
        }

        cam.update();
    }


    @SuppressWarnings("deprecation")
    public void setCamera(int index) {

        if ( index !=MathUtils.clamp( index, 0, towers.length -1 ) )
            return;
        currentTower = index;

        Ray ray = cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        float distance = -ray.origin.y /ray.direction.y;
        Vector3 look = ray.getEndPoint( Vector3.tmp3, distance );

        // System.out.println( Vector3.dst( look.x, 0, look.z, cam.position.x, 0, cam.position.z ) );
        // if ( Vector3.dst( look.x, 0, look.z, cam.position.x, 0, cam.position.z ) <3 )
        // look.add( look.x -cam.position.x, 0, look.z -cam.position.z );


        cam.position.set( towers[index].place );
        look.y = cam.position.y;
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

        // TODO sa aproximez valoarea pt a folosi moveCamera(0 , valoare) sa se uite aproximativ la zona initiala

        cam.update();
    }


    public void setCamera(Tower tower) {
        if ( tower !=null )
            setCamera( getTowerIndex( tower ) );
    }


    public void nextCamera() {
        currentTower ++;
        if ( currentTower >=towers.length )
            currentTower = 0;
        setCamera( currentTower );
    }


    public void prevCamera() {
        currentTower --;
        if ( currentTower <0 )
            currentTower = towers.length -1;
        setCamera( currentTower );
    }


    public boolean isInTower() {
        return getTower() !=null;
    }


    public Tower getTower() {
        if ( towers.length ==0 ||currentTower ==-1 )
            return null;
        return towers[currentTower];
    }


    public Vector3 getCameraRotAround() {
        if ( isInTower() )
            return getTower().place;
        return cam.position;

    }


    public PerspectiveCamera getCamera() {
        return cam;
    }


    public Environment getEnvironment() {
        return environment;
    }


    public void addViata(int amount) {
        this.viata += amount;
        Garden_Revolution.gameplay.viataTurn.setText( "Life " +viata );
    }


    public int getViata() {
        return viata;
    }


    public void setViata(int viata) {
        this.viata = viata;
        Garden_Revolution.gameplay.viataTurn.setText( "Life " +viata );
    }


    @Override
    public void dispose() {

        for (Disposable dis : toDispose )
            dis.dispose();
        for (Disposable dis : towers )
            dis.dispose();

        toDispose.clear();
        enemy.clear();
        ally.clear();
        shot.clear();
        mediu.clear();
        clouds.clear();

    }

}
