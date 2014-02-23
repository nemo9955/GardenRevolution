package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.utility.Functions;


public class Player {

    private PerspectiveCamera cam;
    private WorldWrapper      world;
    private Tower             tower;

    public String             name = "Player " +MathUtils.random( 99 );

    public Player(WorldWrapper world) {
        this.world = world;

        cam = new PerspectiveCamera( 60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
    }


    public void update(float delta) {
    }


    public boolean tap(float x, float y, int count, int button, GestureDetector gestures) {
        if ( count >=2 ) {
            Ray ray = getCamera().getPickRay( x, y );
            return canChangeTower( world.getWorld().getTowerHitByRay( ray ) );
        }
        return false;
    }

    private static final Vector3 tempSpawner = new Vector3();

    public boolean longPress(float x, float y, GestureDetector gestures) {

        if ( Math.abs( Gdx.input.getX() -x ) <20 &&Math.abs( Gdx.input.getY() -y ) <20 ) {

            // Vector3 tmp = Player.tmp;
            Functions.intersectLinePlane( getCamera().getPickRay( x, y ), tempSpawner );

            if ( Gdx.input.isKeyPressed( Keys.F5 ) ) {
                for (int i = 0 ; i <=20 ; i ++ )
                    for (int j = 0 ; j <=20 ; j ++ ) {
                        GR.temp2.set( i +tempSpawner.x -10f, tempSpawner.y, j +tempSpawner.z -10f );
                        world.getDef().addFoe( EnemyType.ROSIE, GR.temp2 );
                    }
            }

            else if ( Gdx.input.isButtonPressed( Buttons.MIDDLE ) )
                world.getDef().addFoe( EnemyType.MORCOV, tempSpawner );

            else if ( Gdx.input.isButtonPressed( Buttons.RIGHT ) )
                world.getDef().addAlly( tempSpawner, AllyType.SOLDIER );
            gestures.invalidateTapSquare();
            return true;
        }
        return false;
    }


    private long dirTime = 0;

    public void moveCamera(float amontX, float amontY) {

        cam.rotateAround( getCameraRotAround(), Vector3.Y, amontX );
        if ( ( amontY >0 &&cam.direction.y <0.7f ) || ( amontY <0 &&cam.direction.y >-0.9f ) ) {
            GR.temp3.set( cam.direction ).crs( cam.up ).y = 0f;
            cam.rotateAround( getCameraRotAround(), GR.temp3.nor(), amontY );
        }
        cam.direction.nor();
        if ( isInATower() ) {
            tower.setDirection( cam.direction );

            if ( GR.gameplay.mp !=null &&System.currentTimeMillis() -dirTime >100 ) {
                dirTime = System.currentTimeMillis();
                GR.gameplay.mp.sendTCP( Functions.getTDC( tower.ID, cam.direction ) );
                // System.out.println( "Sending directional info : " +tower.ID +" " +cam.direction );
            }
        }

        cam.update();
    }

    private static Vector3 look = new Vector3();

    private void resetCamera() {


        Ray ray = cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        Functions.intersectLinePlane( ray, look );

        cam.position.set( tower.place );
        look.y = cam.position.y;
        cam.lookAt( look );
        cam.up.set( Vector3.Y );

        if ( tower.type ==TowerType.FUNDATION ) {
            cam.position.add( 0, 5, 0 );
        }
        else if ( tower.type ==TowerType.VIEWPOINT ) {

        }
        else {
            GR.temp2.set( cam.direction ).scl( 4 );
            GR.temp1.set( cam.up ).crs( cam.direction ).scl( 3 );

            cam.position.sub( GR.temp2 );
            cam.position.sub( GR.temp1 );
            cam.position.add( 0, 2, 0 );
        }

        Vector3 u = GR.temp2.set( GR.temp4.set( look ).sub( cam.position ) );
        look.y = 0;
        Vector3 v = GR.temp1.set( GR.temp4.set( look ).sub( cam.position ) );

        float dot = u.dot( v );
        float lenu = u.len();
        float lenv = v.len();
        float cos = dot / ( Math.abs( lenv ) *Math.abs( lenu ) );

        float angle = (float) Math.toDegrees( Math.acos( cos ) );

        moveCamera( 0, -angle );
    }

    public boolean isInATower() {
        return tower.ID !=0;
    }


    public Tower getTower() {
        return tower;
    }


    public Vector3 getCameraRotAround() {
        if ( isInATower() )
            return tower.place;
        return cam.position;

    }


    public PerspectiveCamera getCamera() {
        return cam;
    }


    public void nextTower() {
        Tower[] towers = world.getWorld().getTowers();
        if ( tower.ID +1 ==towers.length )
            canChangeTower( towers[0] );
        else
            canChangeTower( towers[tower.ID +1] );
    }


    public void prevTower() {
        Tower[] towers = world.getWorld().getTowers();
        if ( tower.ID ==0 )
            canChangeTower( towers[towers.length -1] );
        else
            canChangeTower( towers[tower.ID -1] );
    }


    public void upgradeCurentTower(TowerType upgrade) {
        if ( world.getDef().upgradeTower( tower, upgrade ) )
            resetCamera();
    }

    public void changeCurrentWeapon(WeaponType newWeapon) {
        if ( world.getDef().changeWeapon( tower, newWeapon ) )
            resetCamera();
    }

    public boolean canChangeTower(Tower tower) {

        if ( tower !=null &&world.getDef().canChangeTowers( this.tower.ID, tower.ID, name ) ) {
            setTower( tower );
            return true;
        }
        return false;
    }


    public void setTower(Tower tower) {
        this.tower = tower;
        resetCamera();
    }
}
