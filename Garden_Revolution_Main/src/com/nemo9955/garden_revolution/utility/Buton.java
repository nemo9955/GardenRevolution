package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.Garden_Revolution;


public class Buton implements Disposable {

    public Sprite          img;
    private Camera         cam;
    private boolean        hasCamera = false;
    private static int     scrw, scrh;

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
    private byte           action    = 0;
    private static boolean canAcc    = true;

    public Buton(String link) {
        img = ( (TextureAtlas) Garden_Revolution.manager.get( Assets.ELEMENTS_PACK.path() ) ).createSprite( link  );
        img.setOrigin( img.getWidth() /2, img.getHeight() /2 );

        scrw = Gdx.graphics.getWidth();
        scrh = Gdx.graphics.getHeight();
    }

    public void render(float delta, SpriteBatch batch) {

        if ( coordonate( Gdx.input.getX(), Gdx.input.getY() ) ) {

            if ( action ==1 )
                action = 3;
            else
                action = 2;

            if ( canAcc &&Gdx.input.isTouched() ) {
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

        img.draw( batch );
    }

    private boolean coordonate(int x, int y) {
        if ( hasCamera )
            return ( img.getBoundingRectangle().contains( x +cam.position.x - ( scrw /2 ), -y + ( scrh /2 ) +cam.position.y ) );
        else
            return ( img.getBoundingRectangle().contains( x, -y +scrh ) );

    }


    public boolean isPressed() {
        if ( canAcc ==false &&action ==5 ) {
            canAcc = true;
            action = 0;
            return true;
        }
        return false;
    }

    public Buton setPozi(float x, float y) {
        img.setPosition( x, y );
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

}
