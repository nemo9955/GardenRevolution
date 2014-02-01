package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.mediu.Weapon;
import com.nemo9955.garden_revolution.utility.Functions;


public class Player {

    private static final Vector3 tmp             = new Vector3();
    private PerspectiveCamera    cam;
    private World                world;
    private Tower                tower;

    private boolean              isFiringHold    = false;
    public long                  fireChargedTime = 0;

    public Player(World world) {
        this.world = world;

        cam = new PerspectiveCamera( 60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
    }


    public void update(float delta) {
        if ( isInTower() &&isFiringHold() )
            fireHoldWeapon( getCamera().getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 ) );
    }

    public boolean tap(float x, float y, int count, int button, GestureDetector gestures) {
        if ( !isInTower() ||count >=2 ) {
            Ray ray = getCamera().getPickRay( x, y );
            setPlayerTower( world.getTowerHitByRay( ray ) );
            return true;
        }
        return false;
    }

    public boolean longPress(float x, float y, GestureDetector gestures) {

        if ( isInTower() &&Math.abs( Gdx.input.getX() -x ) <20 &&Math.abs( Gdx.input.getY() -y ) <20 ) {

            // Vector3 tmp = Player.tmp;
            Functions.intersectLinePlane( getCamera().getPickRay( x, y ), tmp );

            if ( Gdx.input.isKeyPressed( Keys.F5 ) ) {
                for (int i = 0 ; i <=20 ; i ++ )
                    for (int j = 0 ; j <=20 ; j ++ ) {
                        world.addFoe( EnemyType.ROSIE, i +tmp.x -10f, tmp.y, j +tmp.z -10f );
                    }
            }
            else if ( Gdx.input.isButtonPressed( Buttons.RIGHT ) )
                world.addAlly( AllyType.SOLDIER, tmp.x, tmp.y, tmp.z );
            else if ( Gdx.input.isButtonPressed( Buttons.MIDDLE ) )
                world.addFoe( EnemyType.MORCOV, tmp.x, tmp.y, tmp.z );

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
    public void setPlayerTower(Tower tower) {
        if ( tower ==null )
            return;
        Vector3 tmp2 = Vector3.tmp2;
        Vector3 look = Vector3.tmp3;

        this.tower = tower;

        Ray ray = cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        Functions.intersectLinePlane( ray, look );

        cam.position.set( tower.place );
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

        Vector3 u = Vector3.tmp2.set( look.tmp().sub( cam.position ) );
        look.y = 0;
        Vector3 v = Vector3.tmp3.set( look.tmp().sub( cam.position ) );

        float dot = u.dot( v );
        float lenu = u.len();
        float lenv = v.len();
        float cos = dot / ( Math.abs( lenv ) *Math.abs( lenu ) );

        float angle = (float) Math.toDegrees( Math.acos( cos ) );

        moveCamera( 0, -angle );
    }

    public boolean isInTower() {
        return getTower() !=null;
    }


    public Tower getTower() {
        return tower;
    }


    public Vector3 getCameraRotAround() {
        if ( isInTower() )
            return getTower().place;
        return cam.position;

    }


    public PerspectiveCamera getCamera() {
        return cam;
    }


    public void nextTower() {
        Tower[] towers = world.towers;
        for (int i = 0 ; i <towers.length ; i ++ )
            if ( towers[i] ==tower )
                if ( i +1 ==towers.length )
                    setPlayerTower( towers[0] );
                else
                    setPlayerTower( towers[i +1] );
    }


    public void prevTower() {
        Tower[] towers = world.towers;
        for (int i = 0 ; i <towers.length ; i ++ )
            if ( towers[i] ==tower )
                if ( i ==0 )
                    setPlayerTower( towers[towers.length -1] );
                else
                    setPlayerTower( towers[i -1] );

    }


    public boolean isFiringHold() {
        return isFiringHold;
    }

    public void setFiringHold(boolean isFiring) {
        if ( isInTower() )
            this.isFiringHold = isFiring;
    }

    public void fireChargedWeapon(Ray ray, float charged) {
        if ( isInTower() )
            getTower().fireCharged( ray, charged );

    }

    public void fireHoldWeapon(Ray ray) {
        if ( isInTower() )
            getTower().fireHold( ray );

    }

    public void upgradeCurentTower(TowerType upgrade) {
        if ( isInTower() )
            if ( getTower().upgradeTower( upgrade ) ) {
                setPlayerTower( tower );
            }
    }


    public void changeCurrentWeapon(Class<? extends Weapon> weapon) {
        Tower tower = getTower();
        if ( isInTower() &&tower.type !=null )
            try {
                if ( tower.changeWeapon( weapon.getDeclaredConstructor( World.class, Vector3.class ).newInstance( world, tower.place ) ) )
                    setPlayerTower( tower );
                world.canWaveStart = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }


}
