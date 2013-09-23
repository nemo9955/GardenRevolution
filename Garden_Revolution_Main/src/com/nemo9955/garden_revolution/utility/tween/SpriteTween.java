package com.nemo9955.garden_revolution.utility.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.TweenAccessor;

public class SpriteTween implements TweenAccessor<Sprite> {

    public static final int ALPHA  = 1;
    public static final int POZ_XY = 2;
    public static final int SIZE   = 3;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {

        switch (tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case POZ_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
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
    public void setValues(Sprite target, int tweenType, float[] newValues) {

        switch (tweenType) {
            case ALPHA:
                target.setColor(1, 1, 1, newValues[0]);
                break;
            case POZ_XY:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            case SIZE:
                target.setScale(newValues[0], newValues[1]);
                break;
            default:
                assert false;
                break;
        }

    }

}