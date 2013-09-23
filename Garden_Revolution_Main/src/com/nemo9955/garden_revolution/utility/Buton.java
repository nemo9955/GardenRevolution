package com.nemo9955.garden_revolution.utility;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.GR_Start;
import com.nemo9955.garden_revolution.utility.tween.SpriteTween;


public class Buton implements Disposable {

    public static TweenManager tweeger;

    private Sprite             img;
    private Rectangle          zon    = new Rectangle();
    private static Texture     loader;

    private static boolean     canAcc = true;
    private boolean            acces  = false;
    private byte               action = 0;

    private boolean            temp1 , temp2;

    public Buton(String link) {

        loader = GR_Start.manager.get( String.format( "imagini/butoane/%s.png", link, Texture.class ) );
        loader.setFilter( TextureFilter.Linear, TextureFilter.Linear );
        img = new Sprite( loader );
        img.setOrigin( img.getWidth() /2, img.getHeight() /2 );

        zon.setSize( img.getWidth(), img.getHeight() );
    }

    public void render(float delta, SpriteBatch batch) {

        if ( zon.contains( Gdx.input.getX(), Gdx.input.getY() ) ) {

            if ( action ==1 )
                action = 3;
            else
                action = 2;

            if ( canAcc &&Gdx.input.isButtonPressed( Buttons.LEFT ) ) {

                System.out.println( "apasa butonul" );
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

    private void doAnimation() {
        switch (action) {
            case 3:// intra
                Tween.to( img, SpriteTween.SIZE, .4f ).target( 1.2f, 1.2f ).ease( Quad.IN ).start( tweeger );
                break;
            case 4:// iese
                Tween.to( img, SpriteTween.SIZE, .4f ).target( 1f, 1f ).ease( Quad.OUT ).start( tweeger );
                break;
            case 5:// acces
                Tween.to( img, SpriteTween.ALPHA, .3f ).target( .6f ).repeatYoyo( 1, 0 ).setCallback( new TweenCallback() {

                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        System.out.println( "termina animatia" );
                        acces = true;
                    }

                } ).start( tweeger );
                break;
        }
    }

    public boolean isPressed() {
        if ( ( canAcc != temp1) || ( acces !=temp2 ) ) {
            temp1 = canAcc;
            temp2 = acces;
            System.out.println( canAcc +" " +acces );
        }
        
        if ( canAcc ==false &&acces ==true ) {
            canAcc = true;
            acces = false;
            System.out.println( "accesat" );
            return true;
        }
        return false;
    }

    public Buton setPozi(float x, float y) {
        zon.setPosition( x, Gdx.graphics.getHeight() -y -zon.getHeight() );
        img.setPosition( x, y );
        // zon.setCenter( img.getX()+(img.getWidth()/2), img.getY()+(img.getHeight()/2) );
        return this;
    }

    @Override
    public void dispose() {
        img.getTexture().dispose();
        loader.dispose();
    }

}
