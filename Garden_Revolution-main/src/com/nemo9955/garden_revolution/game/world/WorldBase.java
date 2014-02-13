package com.nemo9955.garden_revolution.game.world;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.UBJsonReader;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.Waves;
import com.nemo9955.garden_revolution.game.entitati.Ally;
import com.nemo9955.garden_revolution.game.entitati.Enemy;
import com.nemo9955.garden_revolution.game.entitati.Entity;
import com.nemo9955.garden_revolution.game.entitati.Shot;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.mediu.ViewPlace;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.utility.IndexedObject;
import com.nemo9955.garden_revolution.utility.Vars;


public class WorldBase {

    public static Array<Disposable>          toDispose    = new Array<Disposable>( false, 3 );

    private Array<ModelInstance>             mediu        = new Array<ModelInstance>( false, 10 );
    private Array<Enemy>                     enemy        = new Array<Enemy>( false, 10 );
    private Array<Ally>                      ally         = new Array<Ally>( false, 10 );
    private Array<Shot>                      shot         = new Array<Shot>( false, 10 );
    private Array<BoundingBox>               colide       = new Array<BoundingBox>( false, 10 );
    private Array<FightZone>                 fightZones   = new Array<FightZone>( false, 10 );
    private Array<CatmullRomSpline<Vector3>> paths;

    private static Vector3                   tmp          = new Vector3();
    private static Vector3                   tmp2         = new Vector3();
    private Vector3                          overview     = new Vector3( 20, 10, 10 );
    private int                              viata;
    private Tower[]                          towers;
    private boolean                          canWaveStart = false;
    private Waves                            waves;
    private String                           mapPath;
    private Environment                      environment  = new Environment();
    private WorldWrapper                     superior;

    { // lights.
        environment.set( ColorAttribute.createAmbient( 1f, 1f, 1f, 1f ) );
        // environment.add( new PointLight().set( Color.WHITE, new Vector3( 0, 1, 0 ), 1000 ) );
        // envir.add( new DirectionalLight().set( Color.WHITE, new Vector3( 1, -1, 0 ) ) );
        environment.add( new DirectionalLight().set( Color.WHITE, new Vector3( 0, -1, 0 ) ) );
        environment.add( new DirectionalLight().set( Color.WHITE, new Vector3( 0, 1, 0 ) ) );
    }

    public void init(FileHandle location, WorldWrapper superior) {
        reset();
        this.superior = superior;
        setMapPath( location.path() );
        populateWorld( location );
        readData( location );

    }

    public void init(StartingServerInfo info, WorldWrapper superior) {
        init( new FileHandle( info.path ), superior ); // TODO convert the relative map path to the full path specific to the platform

        for (String str : info.turnuri ) {
            String[] separ = str.split( Vars.stringSeparator );
            // System.out.println( "[C] unu din turnuri " +str );
            Tower turn = towers[Integer.parseInt( separ[0] )];
            turn.upgradeTower( TowerType.valueOf( separ[1] ) );
            if ( separ.length ==3 )
                turn.changeWeapon( WeaponType.valueOf( separ[2] ) );
        }

        for (String str : info.players ) {
            String[] separ = str.split( Vars.stringSeparator );
            towers[Integer.parseInt( separ[0] )].ocupier = separ[1];
        }
    }

    public StartingServerInfo getWorldInfo(StartingServerInfo out) {
        out.path = getMapPath();// TODO make this sent the map relative to the assets
        int size = 0, nrTrn = 0, ply = 0, nrPl = 0;
        for (Tower trn : towers ) {
            if ( trn.type !=TowerType.FUNDATION )
                size ++;
            if ( trn.ocupier !=null )
                ply ++;
        }


        String[] formater = new String[size];
        String[] players = new String[ply];
        for (int i = 0 ; i <towers.length ; i ++ ) {
            if ( towers[i].type !=TowerType.FUNDATION ) {
                formater[nrTrn] = "" +i +Vars.stringSeparator +towers[i].type.toString();
                if ( towers[i].hasWeapon() )
                    formater[nrTrn] += Vars.stringSeparator +towers[i].getWeapon().type.toString();
                // System.out.println( "[S] : " +formater[nrTrn] );
                nrTrn ++;
            }
            if ( towers[i].ocupier !=null ) {
                players[nrPl] = "" +i +Vars.stringSeparator +towers[i].ocupier;
                nrPl ++;
            }
        }
        out.turnuri = formater;
        out.players = players;

        return out;
    }


    public void update(float delta) {

        if ( canWaveStart() &&waves.finishedWaves() )
            waves.update( delta );

        for (FightZone fz : getFightZones() ) {
            fz.update( delta );
        }
        for (Tower trn : towers ) {
            trn.update( delta );
        }
        for (Enemy fo : getEnemy() ) {
            fo.update( delta );
        }
        for (Ally al : getAlly() ) {
            al.update( delta );
        }
        for (Shot sh : getShot() ) {
            sh.update( delta );
        }

    }


    public void render(ModelBatch modelBatch, DecalBatch decalBatch) {
        for (ModelInstance e : mediu )
            modelBatch.render( e, environment );

        for (Enemy e : getEnemy() )
            e.render( modelBatch, environment, decalBatch );
        for (Ally e : getAlly() )
            e.render( modelBatch, environment, decalBatch );
        for (Shot e : getShot() )
            e.render( modelBatch );
        for (Tower tower : towers )
            tower.render( modelBatch, environment, decalBatch );
    }


    public void renderDebug(PerspectiveCamera cam, ShapeRenderer shape) {

        shape.setProjectionMatrix( cam.combined );
        shape.begin( ShapeType.Line );

        shape.setColor( 1, 0.5f, 0, 1 );
        for (BoundingBox box : getColide() )
            shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        // shape.setColor( 0.7f, 0.8f, 0.4f, 1 );
        // for (Tower tower : towers )
        // for (BoundingBox box : tower.getTowerColiders() )
        // shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        shape.setColor( 1, 0, 0, 1 );
        for (Entity e : getEnemy() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 0, 1, 1 );
        for (Entity e : getAlly() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 1, 1, 1 );
        for (Entity e : getShot() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 0.5f, 0.5f, 1 );
        for (FightZone e : getFightZones() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );


        int pts = getPaths().size;
        for (int i = 0 ; i <pts ; i ++ ) {
            float val = 0;
            getPaths().get( i ).valueAt( tmp, val );
            while ( val <1f ) {
                val += 1f /150f;
                getPaths().get( i ).valueAt( tmp2, val );
                shape.setColor( i +3 /pts, i +1 /pts, i +2 /pts, 1f );
                shape.line( tmp, tmp2 );
                tmp.set( tmp2 );
            }
        }
        shape.end();
    }

    private void populateWorld(FileHandle location) {
        Array<Array<IndexedObject<Vector3>>> cp = null;
        String[] sect;

        Element map = null;
        try {
            map = new XmlReader().parse( location );
            towers = new Tower[1 +map.getInt( "turnuri" )];
            int noOfPaths = map.getInt( "drumuri" );
            paths = new Array<CatmullRomSpline<Vector3>>( true, noOfPaths );
            cp = new Array<Array<IndexedObject<Vector3>>>( noOfPaths );

            for (int k = 0 ; k <noOfPaths ; k ++ )
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
                int no = Integer.parseInt( sect[1] ) -1;
                towers[no +1] = new Tower( instance, superior, scena.nodes.get( i ).translation, no +1 );
            }
            else if ( id.startsWith( "path" ) ) {
                sect = id.split( "_" );
                int pat = Integer.parseInt( sect[1] ) -1;// TODO get rid of the -1 so the paths can start from 0
                int pct = Integer.parseInt( sect[2] );
                cp.get( pat ).add( new IndexedObject<Vector3>( scena.nodes.get( i ).translation, pct ) );
            }
            else if ( id.startsWith( "colider" ) ) {
                BoundingBox box = new BoundingBox();
                instance.calculateBoundingBox( box );
                addToColide( box );
            }
            else if ( id.endsWith( "solid" ) ) {
                BoundingBox box = new BoundingBox();
                instance.calculateBoundingBox( box );
                addToColide( box );
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

        Model temp = new ModelBuilder().createCone( 5, 5, 5, 20, new Material( ColorAttribute.createDiffuse( Color.GRAY ) ), Usage.Position |Usage.Normal );
        toDispose.add( temp );
        ModelInstance baza = new ModelInstance( temp, overview );
        baza.transform.setToTranslation( overview );
        baza.calculateTransforms();
        towers[0] = new ViewPlace( baza, superior, overview, 0 );
    }

    private void readData(FileHandle location) {

        Element map = null;
        try {
            map = new XmlReader().parse( location );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        waves = new Waves( superior );

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


    private CatmullRomSpline<Vector3> getClosestStartPath(Vector3 location) {
        CatmullRomSpline<Vector3> closest = null;
        float dist = Float.MAX_VALUE;

        for (CatmullRomSpline<Vector3> path : getPaths() ) {
            tmp2.set( path.controlPoints[0] );
            if ( location.dst2( tmp2 ) <dist ) {
                dist = location.dst2( tmp2 );
                closest = path;
            }
        }
        return closest;
    }

    @SuppressWarnings("deprecation")
    // FIXME either make it a lot more precise ( when proper locate will be added ) , or add mode control points and return a slightly altered position
    private Vector3 getPointOnClosestPath(Vector3 location) {
        float dist = Float.MAX_VALUE;
        float tmpDist;
        Vector3 temp = Vector3.tmp3.set( 0, 0, 0 );
        for (CatmullRomSpline<Vector3> path : getPaths() ) {
            // path.valueAt( temp, path.locate( location ) );// gives an aproximated point on the path
            tmpDist = temp.set( path.controlPoints[path.nearest( location )] ).dst( location );// gives the nearest control ponit
            if ( dist >tmpDist ) {

                dist = tmpDist;
                tmp2.set( temp );
            }
        }
        return tmp2;
    }


    public Enemy addFoe(EnemyType type, float x, float y, float z) {
        return addFoe( type, getClosestStartPath( tmp.set( x, y, z ) ), x, y, z );
    }


    public Enemy addFoe(EnemyType type, CatmullRomSpline<Vector3> path, float x, float y, float z) {
        Enemy inamicTemp = inamicPool.obtain().create( path, type, x, y, z );
        getEnemy().add( inamicTemp );
        return inamicTemp;
    }


    public Ally addAlly(AllyType type, float x, float y, float z) {
        return addAlly( getPointOnClosestPath( tmp.set( x, y, z ) ), type, x, y, z );
    }


    public Ally addAlly(Vector3 duty, AllyType type, float x, float y, float z) {
        Ally aliatTemp = aliatPool.obtain().create( duty, type, x, y, z );
        getAlly().add( aliatTemp );

        for (FightZone fz : getFightZones() ) {
            if ( fz.box.getCenter().dst( tmp.set( x, y, z ) ) <8 ) {
                fz.addAlly( aliatTemp );
                fz.aproximatePoz();
                return aliatTemp;
            }
        }

        addFightZone( duty ).addAlly( aliatTemp );
        return aliatTemp;
    }


    public Shot addShot(ShotType type, Ray ray, float charge) {
        Shot shotTemp = shotPool.obtain().create( type, ray, charge );
        getShot().add( shotTemp );
        return shotTemp;
    }


    public FightZone addFightZone(Vector3 poz) {
        FightZone fightZone = fzPool.obtain().create( poz );
        getFightZones().add( fightZone );
        return fightZone;
    }

    private void addMediu(ModelInstance med) {
        mediu.add( med );
    }

    private Pool<Enemy>     inamicPool = new Pool<Enemy>() {


                                           @Override
                                           protected Enemy newObject() {
                                               return new Enemy( superior );
                                           }
                                       };

    private Pool<Ally>      aliatPool  = new Pool<Ally>() {


                                           @Override
                                           protected Ally newObject() {
                                               return new Ally( superior );
                                           }
                                       };

    private Pool<Shot>      shotPool   = new Pool<Shot>() {


                                           @Override
                                           protected Shot newObject() {
                                               return new Shot( superior );
                                           }
                                       };

    private Pool<FightZone> fzPool     = new Pool<FightZone>() {


                                           @Override
                                           protected FightZone newObject() {
                                               return new FightZone( superior );
                                           }
                                       };


    public Pool<Enemy> getEnemyPool() {
        return inamicPool;
    }


    public Pool<Ally> getAliatPool() {
        return aliatPool;
    }


    public Pool<Shot> getShotPool() {
        return shotPool;
    }


    public Pool<FightZone> getFzPool() {
        return fzPool;
    }


    public Tower getTowerHitByRay(Ray ray) {
        for (Tower tower : towers )
            if ( tower.intersectsRay( ray ) ) {
                return tower;
            }
        return null;
    }


    public Environment getEnvironment1() {
        return environment;
    }


    public Array<FightZone> getFightZones() {
        return fightZones;
    }


    public Array<CatmullRomSpline<Vector3>> getPaths() {
        return paths;
    }


    public Array<Ally> getAlly() {
        return ally;
    }


    public Array<BoundingBox> getColide() {
        return colide;
    }


    public BoundingBox addToColide(BoundingBox box) {
        colide.add( box );
        return box;
    }


    public void removeColiders(Array<BoundingBox> box) {
        colide.removeAll( box, false );
    }


    public Array<Enemy> getEnemy() {
        return enemy;
    }


    public void addViata(int amount) {
        viata += amount;
        GR.gameplay.viataTurn.setText( "Life " +viata );
    }


    public int getViata() {
        return viata;
    }


    public void setViata(int viata) {
        this.viata = viata;
        GR.gameplay.viataTurn.setText( "Life " +viata );
    }


    public Array<Shot> getShot() {
        return shot;
    }


    public boolean canWaveStart() {
        return canWaveStart;
    }


    public void setCanWaveStart(boolean canWaveStart) {
        this.canWaveStart = canWaveStart;
    }


    public boolean upgradeTower(Tower tower, TowerType upgrade) {
        return tower.upgradeTower( upgrade );
    }


    public boolean changeWeapon(Tower tower, WeaponType newWeapon) {
        if ( tower.type !=TowerType.FUNDATION )
            if ( tower.changeWeapon( newWeapon ) ) {
                // System.out.println( "World changed weapon" );
                return true;
            }
        return false;
    }


    public boolean canChangeTowers(byte current, byte next, String name) {
        if ( towers[next].ocupier ==null ) {
            if ( current !=-1 )
                towers[current].ocupier = null;
            towers[next].ocupier = name;
            return true;
        }
        return false;
    }


    public String getMapPath() {
        return mapPath;
    }


    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }


    public Tower[] getTowers() {
        return towers;
    }


    public Vector3 getOverview() {
        return overview;
    }


    public boolean fireFromTower(Tower tower, float charge) {
        return tower.fireWeapon( charge );
    }


    public boolean setTowerFireHold(Tower tower, boolean hold) {
        if ( !tower.hasWeapon() )
            return false;
        if ( !tower.isWeaponType( FireType.FIREHOLD ) )
            hold = false;

        tower.isFiringHold = hold;
        Gdx.input.setCursorCatched( hold );
        return true;
    }


    public void reset() {


        for (Disposable dis : toDispose )
            dis.dispose();
        if ( towers !=null )
            for (Disposable dis : towers )
                dis.dispose();

        toDispose.clear();
        enemy.clear();
        ally.clear();
        shot.clear();
        colide.clear();
        mediu.clear();
        if ( paths !=null )
            paths.clear();

        overview.set( 0, 0, 0 );
        canWaveStart = false;
    }
}
