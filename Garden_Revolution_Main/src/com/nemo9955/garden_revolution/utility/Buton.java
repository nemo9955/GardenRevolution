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


public class Buton implements Disposable {

    public static TweenManager manager;

    private Sprite             img;
    private Rectangle          zon = new Rectangle();

    public Buton(String link) {

        Texture loader = new Texture( String.format( "img/buttons/%s.png", link ) );
        loader.setFilter( TextureFilter.Linear, TextureFilter.Linear );
        img = new Sprite( loader );
        loader.dispose();

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

    public void setPozi(float x, float y) {
        zon.setPosition( x, y );
        img.setPosition( x, Gdx.graphics.getHeight() -y );
    }

    @Override
    public void dispose() {
        img.getTexture().dispose();
    }

}
