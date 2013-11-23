package com.nemo9955.garden_revolution.utility;

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
    private float         interval = 20;
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
        shape.setColor( 0.5f, 1, 0.7f, parentAlpha );
        shape.circle( center.x, center.y, radius );
        shape.circle( center.x, center.y, radius +stroke );
        shape.end();

    }

    @Override
    protected void childrenChanged() {
        int kids = getChildren().size;

        float unghi = ( maxAngle +minAngle ) %360 /2;
        float direction = Math.abs( minAngle -maxAngle );

        if ( kids ==interval )
            System.out.print( unghi +" " +direction +" ; " );

        if ( isClockwise( minAngle, maxAngle ) ) {
            if ( maxAngle +minAngle <0 ) {
                unghi += 180;
                direction = 360 -direction;
            }
            if ( kids ==interval )
                System.out.print( "true :  " +unghi +" " +direction +" ; " );
        }
        else {
            if ( maxAngle +minAngle <0 ) {
                unghi -= 180;
            }
            else
                direction *= -1;
            if ( kids ==interval )
                System.out.print( "true :  " +unghi +" " +direction +" ; " );
        }

        direction /= interval;
        unghi -= kids *direction /2 -direction /2;
        Vector2 tmp = new Vector2();
        if ( kids ==interval )
            System.out.print( kids +" -> " +unghi +" " +direction +"\n" );

        for (Actor actor : getChildren() ) {
            tmp = getPosition( tmp, unghi );
            actor.setPosition( tmp.x -actor.getWidth() /2, tmp.y -actor.getHeight() /2 );
            if ( rotChids ) {
                actor.setOrigin( actor.getWidth() /2, actor.getHeight() /2 );
                actor.setRotation( unghi );
            }
            unghi += direction;
        }
    }

    private boolean isClockwise(float min, float max) {
        float dist = 360 -Math.min( max, min );
        if ( getChildren().size ==interval )
            System.out.print( " size (" + ( min +dist ) %360 +" " + ( max +dist ) %360 +") " );
        if ( ( min +dist ) %360 < ( max +dist ) %360 )
            return true;
        return false;

    }

    private Vector2 getPosition(Vector2 out, float angle) {
        return out.set( center ).add( MathUtils.cosDeg( angle ) * ( radius +stroke /2 ), MathUtils.sinDeg( angle ) * ( radius +stroke /2 ) );
    }

    /**
     * Set the min and max angles in degrees in which the elements will be emphasized and how many should it accept
     * The direction in which the elements are drawn alse depend on the min and max parameters
     * 
     * @param minAngle
     * @param maxAngle
     * @param amount
     */
    public void setActivInterval(float minAngle, float maxAngle, int amount) {
        this.minAngle = minAngle >=0 ? minAngle %360 : minAngle %360 +360;
        this.maxAngle = maxAngle >=0 ? maxAngle %360 : maxAngle %360 +360;
        this.interval = amount;

        System.out.println( amount +" : " +this.minAngle +" -> " +this.maxAngle );
    }

    /**
     * Whether to rotate the children according to their position or not .
     * 
     * @param rotChildern
     */
    public void setRotateChildren(boolean rotChildern) {
        this.rotChids = rotChildern;
    }
}
