package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;


public class CircularGroup extends Group {

    private Vector2       center;
    private int           radius;
    private int           stroke;
    private ShapeRenderer shape;

    private float         minAngle = 0, maxAngle = 359;
    private float         spacing  = 10;
    private int           showMax  = 42;
    private boolean       rotChids = false;

    public CircularGroup(Vector2 center, int radius, int stroke, ShapeRenderer shape) {
        super();
        this.center = center;
        this.radius = radius;
        this.stroke = stroke;
        this.shape = shape;
        setSize( radius +stroke, radius +stroke );
        setOrigin( center.x, center.y );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw( batch, parentAlpha );
        shape.setProjectionMatrix( getStage().getCamera().combined );
        shape.begin( ShapeType.Line );
        shape.setColor( 1, 1, 1, parentAlpha );
        shape.circle( center.x, center.y, radius );
        shape.circle( center.x, center.y, radius +stroke );
        shape.end();
    }


    @Override
    protected void childrenChanged() {

        float unghi = minAngle;// TODO make it go in both directions acording to the interval
        Vector2 tmp = new Vector2();

        for (Actor actor : getChildren() ) {
            tmp = getPosition( tmp, unghi );
            actor.setPosition( tmp.x,tmp.y );
            System.out.println( tmp );
            unghi += 10;// TODO make it take in consideration everything it should
        }
    }

    private Vector2 getPosition(Vector2 out, float angle) {
        return out.set( center ).add( MathUtils.cosDeg( angle ) * ( radius +stroke /2 ), MathUtils.sinDeg( angle ) * ( radius +stroke /2 ) );
    }

    /**
     * Set the min and max angles in degrees to draw the children .
     * 
     * @param minAngle
     * @param maxAngle
     */
    public void setActivInterval(float minAngle, float maxAngle) {
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }

    /**
     * Wether to rotate the children acording to their position or not .
     * 
     * @param rotChildern
     */
    public void setRotateChildren(boolean rotChildern) {
        this.rotChids = rotChildern;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }
}
