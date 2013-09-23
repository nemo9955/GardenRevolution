package com.nemo9955.garden_revolution.utility;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.GR_Start;


public class Buton implements Disposable {

    public static TweenManager manager;

    private Sprite             img;
    private Rectangle          zon = new Rectangle();
    private static Texture            loader;

    public Buton(String link) {

        loader = GR_Start.manager.get( String.format( "imagini/butoane/%s.png", link, Texture.class ) );
        loader.setFilter( TextureFilter.Linear, TextureFilter.Linear );
        img = new Sprite( loader );

        zon.setSize( img.getWidth(), img.getHeight() );
    }

    public void render(float delta, SpriteBatch batch) {
        img.draw( batch );
    }


    public boolean isPressed() {
        if ( zon.contains( Gdx.input.getX(), Gdx.input.getX() ) ) {
            if ( Gdx.input.isButtonPressed( Buttons.LEFT ) ) {
                return true;
            }
        }
        return false;
    }

    public Buton setPozi(float x, float y) {
        zon.setPosition( x, y );
        img.setPosition( x, Gdx.graphics.getHeight() -y );
        return this;
    }

    @Override
    public void dispose() {
        img.getTexture().dispose();
        loader.dispose();
    }

}
