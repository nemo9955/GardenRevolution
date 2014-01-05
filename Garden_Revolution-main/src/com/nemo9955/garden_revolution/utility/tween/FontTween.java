package com.nemo9955.garden_revolution.utility.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class FontTween implements TweenAccessor<BitmapFont> {


    public static final int ALPHA = 1;
    public static final int COLOR = 2;
    public static final int SIZE  = 3;

    @Override
    public int getValues(BitmapFont target, int tweenType, float[] returnValues) {

        switch (tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case COLOR:
                returnValues[0] = target.getColor().toFloatBits();
                return 1;
            case SIZE:
                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 2;
            default:
                assert false;
                return -1;
        }

    }

    @Override
    public void setValues(BitmapFont target, int tweenType, float[] newValues) {

        switch (tweenType) {
            case ALPHA:
                target.setColor( 1, 1, 1, newValues[0] );
                break;
            case COLOR:
                target.setColor( newValues[0] );// new Color((int) newValues[0]).r , new Color((int) newValues[0]).g , new Color((int) newValues[0]).b , new Color((int) newValues[0]).a );
                break;
            case SIZE:
                target.setScale( newValues[0], newValues[1] );
                break;
            default:
                assert false;
                break;
        }

    }

}
