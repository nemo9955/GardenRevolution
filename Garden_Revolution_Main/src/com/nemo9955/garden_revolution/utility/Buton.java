package com.nemo9955.garden_revolution.utility;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;


public class Buton implements Disposable {

    public static TweenManager tweeger;

    private Sprite             img;
    private Camera             cam;
    private boolean            hasCamera = false;

    private Rectangle          zon;


    private static int         scrw, scrh;

    /*
     * action :
     * 0 default
     * 1 afara
     * 2 inauntru
     * 3 + intra
     * 4 + iesi
     * 5 + apasa
     * 6 + acceseaza
     */
    private byte               action    = 0;
    private static boolean     canAcc    = true;

    public Buton(String link) {
        img = new Sprite( (Texture) Garden_Revolution.manager.get( String.format( Garden_Revolution.BUTOANE +"%s.png", link ) ) );
        img.setOrigin( img.getWidth() /2, img.getHeight() /2 );

        zon = new Rectangle();
        zon.setSize( img.getWidth(), img.getHeight() );

        scrw = Gdx.graphics.getWidth();
        scrh = Gdx.graphics.getHeight();
    }

    public void render(float delta, SpriteBatch batch) {

        if ( coordonate( Gdx.input.getX(), Gdx.input.getY() ) ) {

            if ( action ==1 )
                action = 3;
            else
                action = 2;

            if ( canAcc &&Gdx.input.isButtonPressed( Buttons.LEFT ) ) {

                canAcc = false;
                action = 5;
            }
        }
        else {
            if ( action ==2 )
                action = 4;
            else
                action = 1;
        }

        if ( action >=3 )
            doAnimation();

        img.draw( batch );
    }

    private boolean coordonate(int x, int y) {

        if ( Gdx.input.isKeyPressed( Keys.F1 ) )
            System.out.println( x +" " + ( -y +scrh ) );

        if ( hasCamera ) {
            return ( zon.contains( x +cam.position.x - ( scrw /2 ), -y + ( scrh /2 ) +cam.position.y ) );
        }
        else {
            return ( zon.contains( x, -y +scrh ) );
        }

    }

    private void doAnimation() {
        switch (action) {
            case 3:// intra
                Tween.to( img, SpriteTween.SIZE, .2f ).target( 1.15f, 1.1f ).ease( Quad.IN ).start( tweeger );
                break;
            case 4:// iese
                Tween.to( img, SpriteTween.SIZE, .35f ).target( 1f, 1f ).ease( Quad.OUT ).start( tweeger );
                break;
            case 5:// acces
                Tween.to( img, SpriteTween.ALPHA, .3f ).target( .6f ).repeatYoyo( 1, 0 ).setCallback( new TweenCallback() {

                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        action = 6;
                    }

                } ).start( tweeger );
                break;
        }
    }

    public boolean isPressed() {
        if ( canAcc ==false &&action ==6 ) {
            canAcc = true;
            action = 0;
            Tween.set( img, SpriteTween.SIZE ).target( 1f, 1f ).start( tweeger );
            return true;
        }
        return false;
    }

    public Buton setPozi(float x, float y) {
        img.setPosition( x, y );
        zon.setPosition( x, y );
        return this;
    }

    public Buton setOrigin(float x, float y) {
        float orX = img.getOriginX();
        float orY = img.getOriginY();

        if ( x !=-1 )
            orX = x;
        if ( y !=-1 )
            orY = y;

        if ( x >0 &&x <=1 )
            orX = img.getWidth() *x;
        if ( y >0 &&y <=1 )
            orY = img.getHeight() *y;

        img.setOrigin( orX, orY );
        return this;
    }

    public Buton setCamera(Camera cam) {
        this.cam = cam;
        hasCamera = true;
        return this;
    }

    @Override
    public void dispose() {
        img.getTexture().dispose();
    }

    public float getHeight() {
        return zon.getHeight();
    }

    public float getWidth() {
        return zon.getWidth();
    }

    public Rectangle getZon() {
        return zon;
    }

}
