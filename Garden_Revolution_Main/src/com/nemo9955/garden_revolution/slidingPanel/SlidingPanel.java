package com.nemo9955.garden_revolution.slidingPanel;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Buton;


public abstract class SlidingPanel implements Disposable {

    public static TweenManager       tweeger;
    public static OrthographicCamera view;
    public static boolean            exitPanel = false;

    protected float                  xpoz, ypoz;
    protected Sprite                 fundal;
    public Buton                     mufa;

    public SlidingPanel(String link, byte side, float distance) {
        final float scrw = Gdx.graphics.getWidth();
        final float scrh = Gdx.graphics.getHeight();

        mufa = new Buton( "IG" +link );
        fundal = new Sprite( (Texture) Garden_Revolution.manager.get( Garden_Revolution.ELEMENTE +String.format( "%s_fundal.png", link ) ) );
        fundal.setSize( scrw *0.85f, scrh *0.85f );
        fundal.setPosition( ( scrw /2 ) - ( fundal.getWidth() /2 ), ( scrh /2 ) - ( fundal.getHeight() /2 ) );

        switch (side) {
            case 0:
                xpoz = 0;
                ypoz = scrh *distance;
                mufa.setOrigin( 0, 0.5f );
                break;
            case 1:
                ypoz = scrh -mufa.img.getHeight();
                xpoz = scrw *distance;
                mufa.setOrigin( 0.5f, 1 );
                break;
            case 2:
                xpoz = scrw -mufa.img.getWidth();
                ypoz = scrh *distance;
                mufa.setOrigin( 1, 0.5f );
                break;
            case 3:
                ypoz = 0;
                xpoz = scrw *distance;
                mufa.setOrigin( 0.5f, 0 );
                break;
        }

        mufa.setPozi( xpoz, ypoz );
        view = new OrthographicCamera( scrw, scrh );
        view.position.set( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2, 0 );
        view.update();
    }

    public abstract void renderStatic(SpriteBatch batch, float delta);

    public abstract void renderAsCamera(SpriteBatch batch, float delta);

    public void dispose() {
        fundal.getTexture().dispose();
        mufa.dispose();
    }
}
