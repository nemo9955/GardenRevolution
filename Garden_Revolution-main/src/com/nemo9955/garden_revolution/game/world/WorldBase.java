package com.nemo9955.garden_revolution.game.world;

import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
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
import com.nemo9955.garden_revolution.Garden_Revolution;
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
import com.nemo9955.garden_revolution.game.mediu.FightZone;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.utility.IndexedObject;
import com.nemo9955.garden_revolution.utility.Vars;


public class WorldBase implements Disposable, IWorldModel {

    public static Array<Disposable>          toDispose    = new Array<Disposable>( false, 3 );

    private final Array<ModelInstance>       mediu        = new Array<ModelInstance>( false, 10 );
    private Array<Enemy>                     enemy        = new Array<Enemy>( false, 10 );
    private Array<Ally>                      ally         = new Array<Ally>( false, 10 );
    private Array<Shot>                      shot         = new Array<Shot>( false, 10 );
    private Array<BoundingBox>               colide       = new Array<BoundingBox>( false, 10 );
    private Array<FightZone>                 fightZones   = new Array<FightZone>( false, 10 );
    private Array<CatmullRomSpline<Vector3>> paths;

    private static Vector3                   tmp          = new Vector3();
    private static Vector3                   tmp2         = new Vector3();
    private final Vector3                    overview     = new Vector3( 20, 10, 10 );
    private int                              viata;
    private Tower[]                          towers;
    private boolean                          canWaveStart = false;
    private Waves                            waves;
    private String                           mapPath;
    private final Environment                environment  = new Environment();


    {
        // lights.
        environment.set( ColorAttribute.createAmbient( 1, 1, 0, 1 ) );
        environment.add( new PointLight().set( Color.BLUE, new Vector3( 5, -10, 5 ), 100 ) );
        // envir.add( new DirectionalLight().set( Color.WHITE, new Vector3( 1, -1, 0 ) ) );
        environment.add( new DirectionalLight().set( Color.WHITE, new Vector3( 0, -1, 0 ) ) );
    }

    public WorldBase(final FileHandle location) {
        // this.worldModel = worldModel;
        setMapPath( location.path() );
        populateWorld( location );
        readData( location );
    }

    public WorldBase(final StartingServerInfo info) {
        // this.worldModel = worldModel;
        // TODO convert the relative map path to the full path specific to the platform
        final FileHandle location = new FileHandle( info.path );
        populateWorld( location );
        readData( location );

        for (final String str : info.turnuri ) {
            final String[] separ = str.split( Vars.stringSeparator );
            // System.out.println( "[C] unu din turnuri " +str );
            final Tower turn = getTowers()[Integer.parseInt( separ[0] )];
            turn.upgradeTower( TowerType.valueOf( separ[1] ) );
            if ( separ.length ==3 )
                turn.changeWeapon( WeaponType.valueOf( separ[2] ) );
        }

        for (final String str : info.players ) {
            final String[] separ = str.split( Vars.stringSeparator );
            towers[Integer.parseInt( separ[0] )].ocupier = separ[1];
        }

    }

    @Override
    public StartingServerInfo getWorldInfo(final StartingServerInfo out) {
        out.path = getMapPath();// TODO make this sent the map relative to the assets
        int size = 0, nrTrn = 0, ply = 0, nrPl = 0;
        for (final Tower trn : getTowers() ) {
            if ( trn.type !=TowerType.FUNDATION )
                size ++;
            if ( trn.ocupier !=null )
                ply ++;
        }


        final String[] formater = new String[size];
        final String[] players = new String[ply];
        for (int i = 0 ; i <getTowers().length ; i ++ ) {
            if ( towers[i].type !=TowerType.FUNDATION ) {
                formater[nrTrn] = "" +i +Vars.stringSeparator +getTowers()[i].type.toString();
                if ( getTowers()[i].hasWeapon() )
                    formater[nrTrn] += Vars.stringSeparator +getTowers()[i].getWeapon().type.toString();
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

    @Override
    public void update(final float delta) {

        if ( canWaveStart() &&waves.finishedWaves() )
            waves.update( delta );

        for (final FightZone fz : getFightZones() ) {
            fz.update( delta );
        }
        for (final Tower trn : getTowers() ) {
            trn.update( delta );
        }
        for (final Enemy fo : getEnemy() ) {
            fo.update( delta );
        }
        for (final Ally al : getAlly() ) {
            al.update( delta );
        }
        for (final Shot sh : getShot() ) {
            sh.update( delta );
        }

    }

    @Override
    public void render(final ModelBatch modelBatch, final Environment env, final DecalBatch decalBatch) {
        for (final ModelInstance e : mediu )
            modelBatch.render( e, env );

        for (final Enemy e : getEnemy() )
            e.render( modelBatch, env, decalBatch );
        for (final Ally e : getAlly() )
            e.render( modelBatch, env, decalBatch );
        for (final Shot e : getShot() )
            e.render( modelBatch );
        for (final Tower tower : getTowers() )
            tower.render( modelBatch, env, decalBatch );
    }

    @Override
    public void renderDebug(final PerspectiveCamera cam, final ShapeRenderer shape) {

        shape.setProjectionMatrix( cam.combined );
        shape.begin( ShapeType.Line );

        shape.setColor( 1, 0.5f, 0, 1 );
        for (final BoundingBox box : getColide() )
            shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        // shape.setColor( 0.7f, 0.8f, 0.4f, 1 );
        // for (Tower tower : towers )
        // for (BoundingBox box : tower.getTowerColiders() )
        // shape.box( box.min.x, box.min.y, box.max.z, box.getDimensions().x, box.getDimensions().y, box.getDimensions().z );

        shape.setColor( 1, 0, 0, 1 );
        for (final Entity e : getEnemy() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 0, 1, 1 );
        for (final Entity e : getAlly() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 1, 1, 1 );
        for (final Entity e : getShot() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );

        shape.setColor( 0, 0.5f, 0.5f, 1 );
        for (final FightZone e : getFightZones() )
            shape.box( e.box.min.x, e.box.min.y, e.box.max.z, e.box.getDimensions().x, e.box.getDimensions().y, e.box.getDimensions().z );


        final int pts = getPaths().size;
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

    private void populateWorld(final FileHandle location) {
        Array<Array<IndexedObject<Vector3>>> cp = null;
        String[] sect;

        Element map = null;
        try {
            map = new XmlReader().parse( location );
            setTowers( new Tower[map.getInt( "turnuri" )] );
            final int noOfPaths = map.getInt( "drumuri" );
            setPaths( new Array<CatmullRomSpline<Vector3>>( true, noOfPaths ) );
            cp = new Array<Array<IndexedObject<Vector3>>>( noOfPaths );

            for (int k = 0 ; k <noOfPaths ; k ++ )
                cp.add( new Array<IndexedObject<Vector3>>( false, 1, IndexedObject.class ) );
        }
        catch (final IOException e) {
            e.printStackTrace();
        }

        final Model scena = new G3dModelLoader( new UBJsonReader() ).loadModel( location.parent().parent().child( "maps" ).child( map.get( "map" ) ) );

        toDispose.add( scena );

        for (int i = 0 ; i <scena.nodes.size ; i ++ ) {
            final String id = scena.nodes.get( i ).id;
            final ModelInstance instance = new ModelInstance( scena, id );
            // Node node = instance.getNode( id );
            // instance.transform.set( node.globalTransform );
            // node.translation.set( 0, 0, 0 );
            // node.scale.set( 1, 1, 1 );
            // node.rotation.idt();
            // instance.calculateTransforms();

            if ( id.startsWith( "turn" ) ) {
                sect = id.split( "_" );
                final int no = Integer.parseInt( sect[1] ) -1;
                getTowers()[no] = new Tower( instance, this, scena.nodes.get( i ).translation, no );
            }
            else if ( id.startsWith( "path" ) ) {
                sect = id.split( "_" );
                final int pat = Integer.parseInt( sect[1] ) -1;// TODO get rid of the -1 so the paths can start from 0
                final int pct = Integer.parseInt( sect[2] );
                cp.get( pat ).add( new IndexedObject<Vector3>( scena.nodes.get( i ).translation, pct ) );
            }
            else if ( id.startsWith( "colider" ) ) {
                final BoundingBox box = new BoundingBox();
                instance.calculateBoundingBox( box );
                addToColide( box );
            }
            else if ( id.endsWith( "solid" ) ) {
                final BoundingBox box = new BoundingBox();
                instance.calculateBoundingBox( box );
                addToColide( box );
                addMediu( instance );
            }
            else if ( id.startsWith( "camera" ) ) {
                getOverview().set( scena.nodes.get( i ).translation );
            }
            else {
                addMediu( instance );
            }

        }

        for (final Array<IndexedObject<Vector3>> pat : cp )
            pat.sort();

        for (final Array<IndexedObject<Vector3>> pat : cp ) {

            final Vector3 cps[] = new Vector3[pat.size +2];
            for (int j = 0 ; j <pat.size ; j ++ )
                cps[j +1] = pat.get( j ).object;
            cps[0] = cps[1];
            cps[cps.length -1] = cps[cps.length -2];

            getPaths().add( new CatmullRomSpline<Vector3>( cps, false ) );
        }

    }

    private void readData(final FileHandle location) {

        Element map = null;
        try {
            map = new XmlReader().parse( location );
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        waves = new Waves( this );

        setViata( map.getInt( "viata", 100 ) );

        final Array<Element> tmpWaves = map.getChildrenByName( "wave" );
        tmpWaves.shrink();
        final Array<IndexedObject<Element>> sortedWaves = new Array<IndexedObject<Element>>( tmpWaves.size );
        for (int i = 0 ; i <tmpWaves.size ; i ++ )
            sortedWaves.add( new IndexedObject<Element>( tmpWaves.get( i ), tmpWaves.get( i ).getInt( "index" ) -1 ) );
        sortedWaves.sort();
        tmpWaves.clear();

        for (final IndexedObject<Element> wav : sortedWaves ) {
            waves.addWave( wav.object.getFloat( "delay", 5 ), wav.object.getFloat( "interval", 0.5f ) );
            final Array<Element> tmpPaths = wav.object.getChildrenByName( "path" );

            for (final Element pat : tmpPaths ) {

                final int numar = pat.getInt( "nr" );
                for (int i = 0 ; i <pat.getChildCount() ; i ++ ) {

                    final Element monstru = pat.getChild( i );
                    waves.populate( numar -1, EnemyType.valueOf( monstru.getName().toUpperCase() ), monstru.getInt( "amount", 1 ) );

                }
            }
        }
        sortedWaves.clear();
    }


    private CatmullRomSpline<Vector3> getClosestStartPath(final Vector3 location) {
        CatmullRomSpline<Vector3> closest = null;
        float dist = Float.MAX_VALUE;

        for (final CatmullRomSpline<Vector3> path : getPaths() ) {
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
    private Vector3 getPointOnClosestPath(final Vector3 location) {
        float dist = Float.MAX_VALUE;
        float tmpDist;
        final Vector3 temp = Vector3.tmp3.set( 0, 0, 0 );
        for (final CatmullRomSpline<Vector3> path : getPaths() ) {
            // path.valueAt( temp, path.locate( location ) );// gives an aproximated point on the path
            tmpDist = temp.set( path.controlPoints[path.nearest( location )] ).dst( location );// gives the nearest control ponit
            if ( dist >tmpDist ) {

                dist = tmpDist;
                tmp2.set( temp );
            }
        }
        return tmp2;
    }

    @Override
    public Enemy addFoe(final EnemyType type, final float x, final float y, final float z) {
        return addFoe( type, getClosestStartPath( tmp.set( x, y, z ) ), x, y, z );
    }

    @Override
    public Enemy addFoe(final EnemyType type, final CatmullRomSpline<Vector3> path, final float x, final float y, final float z) {
        final Enemy inamicTemp = inamicPool.obtain().create( path, type, x, y, z );
        getEnemy().add( inamicTemp );
        return inamicTemp;
    }

    @Override
    public Ally addAlly(final AllyType type, final float x, final float y, final float z) {
        return addAlly( getPointOnClosestPath( tmp.set( x, y, z ) ), type, x, y, z );
    }

    @Override
    public Ally addAlly(final Vector3 duty, final AllyType type, final float x, final float y, final float z) {
        final Ally aliatTemp = aliatPool.obtain().create( duty, type, x, y, z );
        getAlly().add( aliatTemp );

        for (final FightZone fz : getFightZones() ) {
            if ( fz.box.getCenter().dst( tmp.set( x, y, z ) ) <8 ) {
                fz.addAlly( aliatTemp );
                fz.aproximatePoz();
                return aliatTemp;
            }
        }

        addFightZone( duty ).addAlly( aliatTemp );
        return aliatTemp;
    }

    @Override
    public Shot addShot(final ShotType type, final Ray ray, final float charge) {
        final Shot shotTemp = shotPool.obtain().create( type, ray, charge );
        getShot().add( shotTemp );
        return shotTemp;
    }

    @Override
    public FightZone addFightZone(final Vector3 poz) {
        final FightZone fightZone = fzPool.obtain().create( poz );
        getFightZones().add( fightZone );
        return fightZone;
    }

    private void addMediu(final ModelInstance med) {
        mediu.add( med );
    }

    private final Pool<Enemy>     inamicPool = new Pool<Enemy>() {

                                                 @Override
                                                 protected Enemy newObject() {
                                                     return new Enemy( WorldBase.this );
                                                 }
                                             };

    private final Pool<Ally>      aliatPool  = new Pool<Ally>() {

                                                 @Override
                                                 protected Ally newObject() {
                                                     return new Ally( WorldBase.this );
                                                 }
                                             };

    private final Pool<Shot>      shotPool   = new Pool<Shot>() {

                                                 @Override
                                                 protected Shot newObject() {
                                                     return new Shot( WorldBase.this );
                                                 }
                                             };

    private final Pool<FightZone> fzPool     = new Pool<FightZone>() {

                                                 @Override
                                                 protected FightZone newObject() {
                                                     return new FightZone( WorldBase.this );
                                                 }
                                             };


    @Override
    public Pool<Enemy> getEnemyPool() {
        return inamicPool;
    }


    @Override
    public Pool<Ally> getAliatPool() {
        return aliatPool;
    }


    @Override
    public Pool<Shot> getShotPool() {
        return shotPool;
    }


    @Override
    public Pool<FightZone> getFzPool() {
        return fzPool;
    }

    @Override
    public Tower getTowerHitByRay(final Ray ray) {
        for (final Tower tower : getTowers() )
            if ( tower.intersectsRay( ray ) ) {
                return tower;
            }
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }


    @Override
    public Array<FightZone> getFightZones() {
        return fightZones;
    }


    public void setFightZones(final Array<FightZone> fightZones) {
        this.fightZones = fightZones;
    }


    @Override
    public Array<CatmullRomSpline<Vector3>> getPaths() {
        return paths;
    }


    public void setPaths(final Array<CatmullRomSpline<Vector3>> paths) {
        this.paths = paths;
    }


    @Override
    public Array<Ally> getAlly() {
        return ally;
    }


    public void setAlly(final Array<Ally> ally) {
        this.ally = ally;
    }


    @Override
    public Array<BoundingBox> getColide() {
        return colide;
    }


    @Override
    public BoundingBox addToColide(final BoundingBox box) {
        colide.add( box );
        return box;
    }

    @Override
    public void removeColiders(final Array<BoundingBox> box) {
        colide.removeAll( box, false );
    }


    public void setColide(final Array<BoundingBox> colide) {
        this.colide = colide;
    }


    @Override
    public Array<Enemy> getEnemy() {
        return enemy;
    }


    public void setEnemy(final Array<Enemy> enemy) {
        this.enemy = enemy;
    }


    @Override
    public void addViata(final int amount) {
        viata += amount;
        Garden_Revolution.gameplay.viataTurn.setText( "Life " +viata );
    }


    @Override
    public int getViata() {
        return viata;
    }


    @Override
    public void setViata(final int viata) {
        this.viata = viata;
        Garden_Revolution.gameplay.viataTurn.setText( "Life " +viata );
    }

    @Override
    public Array<Shot> getShot() {
        return shot;
    }


    public void setShot(final Array<Shot> shot) {
        this.shot = shot;
    }

    @Override
    public boolean canWaveStart() {
        return canWaveStart;
    }

    @Override
    public void setCanWaveStart(final boolean canWaveStart) {
        this.canWaveStart = canWaveStart;
    }


    @Override
    public void dispose() {

        for (final Disposable dis : toDispose )
            dis.dispose();
        for (final Disposable dis : getTowers() )
            dis.dispose();

        toDispose.clear();
        getEnemy().clear();
        getAlly().clear();
        getShot().clear();
        mediu.clear();

    }

    @Override
    public boolean upgradeTower(final byte id, final TowerType upgrade) {

        if ( getTowers()[id].upgradeTower( upgrade ) ) {
            System.out.println( "World upgraded tower" );
            return true;
        }
        return false;
    }

    @Override
    public boolean changeWeapon(final byte id, final WeaponType newWeapon) {
        if ( getTowers()[id].type !=TowerType.FUNDATION )
            if ( getTowers()[id].changeWeapon( newWeapon ) ) {
                System.out.println( "World changed weapon" );
                return true;
            }
        return false;
    }

    // public boolean canChangeTowers(Tower current, Tower next, Player player) {
    // if ( next.ocupier ==null ) {
    // if ( current !=null )
    // current.ocupier = null;
    // next.ocupier = player.name;
    // player.setTower( next );
    // return true;
    // }
    // return false;
    // } //s s s

  
    @Override
    public boolean canChangeTowers(final byte current, final byte next, final String name) {
        if ( getTowers()[next].ocupier ==null ) {
            if ( current >=0 &&current <towers.length )
                getTowers()[current].ocupier = null;
            getTowers()[next].ocupier = name;
            return true;
        }
        return false;
    }

    @Override
    public String getMapPath() {
        return mapPath;
    }

    @Override
    public void setMapPath(final String mapPath) {
        this.mapPath = mapPath;
    }

    @Override
    public Tower[] getTowers() {
        return towers;
    }

    public void setTowers(final Tower[] towers) {
        this.towers = towers;
    }

    @Override
    public Vector3 getOverview() {
        return overview;
    }


}
