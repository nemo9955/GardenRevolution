package com.nemo9955.garden_revolution.slidingPanel;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.Garden_Revolution;


public abstract class SlidingPanel implements Disposable {

    public static TweenManager       tweeger;
    public static OrthographicCamera cam;
    public static boolean            exitPanel = false;

    protected float                  xpoz, ypoz;
    protected Sprite                 fundal;
    protected Sprite                 mufa;

    public SlidingPanel(String link, byte side, float distance) {
        final float scrw = Gdx.graphics.getWidth();
        final float scrh = Gdx.graphics.getHeight();

        mufa = new Sprite( (Texture) Garden_Revolution.manager.get( Garden_Revolution.ELEMENTE +String.format( "%s.png", link ) ) );
        fundal = new Sprite( (Texture) Garden_Revolution.manager.get( Garden_Revolution.ELEMENTE +String.format( "%s_fundal.png", link ) ) );
        fundal.setSize( scrw *0.85f, scrh *0.9f );
        fundal.setPosition( ( scrw /2 ) - ( fundal.getWidth() /2 ), ( scrh /2 ) - ( fundal.getHeight() /2 ) );

        switch (side) {
            case 0:
                xpoz = 0;
                ypoz = scrh *distance;
                break;
            case 1:
                ypoz = scrh -mufa.getHeight();
                xpoz = scrw *distance;
                break;
            case 2:
                xpoz = scrw -mufa.getWidth();
                ypoz = scrh *distance;
                break;
            case 3:
                ypoz = 0;
                xpoz = scrw *distance;
                break;
        }

        mufa.setPosition( xpoz, ypoz );
        cam = new OrthographicCamera( scrw, scrh );
    }

    public abstract void render(SpriteBatch batch, float delta);

    public boolean isActivated(float x, float y) {
        return ( mufa.getBoundingRectangle().contains( x, -y +Gdx.graphics.getHeight() ) );
    }

    public void activate() {

    }

    public void dispose() {
        fundal.getTexture().dispose();
        mufa.getTexture().dispose();
    }

    public Sprite getMufa() {
        return mufa;
    }
}
