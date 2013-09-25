package com.nemo9955.garden_revolution.utility;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.GR_Start;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;


public class Buton implements Disposable {

    public static TweenManager tweeger;

    private Sprite             img;
    private static Texture     loader;
    private Camera             cam;
    private boolean            hasCamera = false;

    private final float        width, height;
    private float              x, y;
    private float              inix, iniy;

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

        loader = GR_Start.manager.get( String.format( "imagini/butoane/%s.png", link, Texture.class ) );
        loader.setFilter( TextureFilter.Linear, TextureFilter.Linear );
        img = new Sprite( loader );
        img.setOrigin( img.getWidth() /2, img.getHeight() /2 );

        x = 0;
        y = 0;

        width = img.getWidth();
        height = img.getHeight();
    }

    public void render(float delta, SpriteBatch batch) {
        if ( hasCamera ) {
            x = inix +cam.position.x -width - ( Gdx.graphics.getWidth() /2 );
            y = -iniy +cam.position.y + ( height *1.5f );// + Gdx.graphics.getHeight() ;
        }


        if ( contains( Gdx.input.getX(), Gdx.input.getY() ) ) {

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

        /*
         * if (Gdx.input.isKeyPressed( Input.Keys.F1 ) ) {
         * System.out.println( x +inix +" " + ( y +iniy ) );
         * System.out.println( inix +" " +iniy );
         * System.out.println( Gdx.input.getX() +" " +Gdx.input.getY() );
         * }
         */

        img.draw( batch );
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

    private boolean contains(float pozx, float pozy) {
        return ( pozx >x +inix &&pozx <x +inix +width &&pozy >y +iniy &&pozy <y +iniy +height );
    }

    public boolean isPressed() {
        if ( canAcc ==false &&action ==6 ) {
            canAcc = true;
            action = 0;
            return true;
        }
        return false;
    }

    public Buton setPozi(float x, float y) {
        img.setPosition( x, y );
        inix = x;
        iniy = Gdx.graphics.getHeight() -y -height;
        return this;
    }

    public Buton setOrigin(float x, float y) {
        float orX = img.getOriginX();
        float orY = img.getOriginY();

        if ( x !=-1 )
            orX = x;
        if ( y !=-1 )
            orY = y;

        img.setOrigin( orX, orY );
        return this;
    }

    public void setCamera(Camera cam) {
        this.cam = cam;
        hasCamera = true;
    }

    @Override
    public void dispose() {
        img.getTexture().dispose();
        loader.dispose();
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

}
