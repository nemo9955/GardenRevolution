package com.nemo9955.garden_revolution.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.EnemyType;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.IWorldModel;
import com.nemo9955.garden_revolution.utility.Functions;


public class Player {

    private static final Vector3 tmp  = new Vector3();
    private PerspectiveCamera    cam;
    private IWorldModel          world;
    private Tower                tower;

    public String                name = "Player " +MathUtils.random( 99 );

    public Player(IWorldModel world) {
        this.world = world;

        cam = new PerspectiveCamera( 60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
    }


    public void update(float delta) {
    }

    public boolean tap(float x, float y, int count, int button, GestureDetector gestures) {
        if ( !isInTower() ||count >=2 ) {
            Ray ray = getCamera().getPickRay( x, y );
            return canChangeTower( world.getTowerHitByRay( ray ) );
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
        if ( isInTower() )
            tower.setDirection( cam.direction );

        cam.update();
    }

    @SuppressWarnings("deprecation")
    private void resetCamera() {

        Vector3 tmp2 = Vector3.tmp2;
        Vector3 look = Vector3.tmp3;

        Ray ray = cam.getPickRay( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        Functions.intersectLinePlane( ray, look );

        cam.position.set( getTower().place );
        look.y = cam.position.y;
        cam.lookAt( look );
        cam.up.set( Vector3.Y );

        if ( getTower().type ==TowerType.FUNDATION ) {
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
        Tower[] towers = world.getTowers();
        for (int i = 0 ; i <towers.length ; i ++ )
            if ( towers[i] ==getTower() )
                if ( i +1 ==towers.length )
                    canChangeTower( towers[0] );
                else
                    canChangeTower( towers[i +1] );
    }


    public void prevTower() {
        Tower[] towers = world.getTowers();
        for (int i = 0 ; i <towers.length ; i ++ )
            if ( towers[i] ==getTower() )
                if ( i ==0 )
                    canChangeTower( towers[towers.length -1] );
                else
                    canChangeTower( towers[i -1] );
    }



    public void upgradeCurentTower(TowerType upgrade) {
        if ( isInTower() &&world.upgradeTower( getTower().ID, upgrade ) )
            resetCamera();
    }

    public void changeCurrentWeapon(WeaponType newWeapon) {
        if ( isInTower() &&world.changeWeapon( getTower().ID, newWeapon ) )
            resetCamera();
    }

    public boolean canChangeTower(Tower tower) {

        if ( tower !=null &&world.canChangeTowers( isInTower() ? getTower().ID : -1, tower.ID, name ) ) {
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
