package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;


public class CircularGroup extends WidgetGroup {

    private Vector2              center        = new Vector2();
    private int                  radius;
    private int                  stroke;
    private ShapeRenderer        shape;
    private static final Vector2 tmp           = new Vector2();

    private float                minAngle      = 0, maxAngle = 359;
    private float                interval      = 20;
    private boolean              rotKids       = false;
    private boolean              modifyAlpha   = true;

    private boolean              clockwise     = true;
    private float                mid, dir;
    private float                rotation      = 0;

    private InputListener        inputListener = new InputListener() {

                                                   private float   inAngle;
                                                   private Vector2 temp = new Vector2();

                                                   @Override
                                                   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                                       inAngle = temp.set( x, y ).sub( center ).angle();
                                                       float distCenter = temp.dst2( 0, 0 );
                                                       if ( distCenter >radius *radius &&distCenter < ( radius +stroke ) * ( radius +stroke ) )
                                                           return true;
                                                       return false;
                                                   }

                                                   @Override
                                                   public void touchDragged(InputEvent event, float x, float y, int pointer) {
                                                       temp.set( x, y ).sub( center );
                                                       setRotationMenu( ( temp.angle() -inAngle ) );// FIXME sa nu se mai restarteze unghiul cand apas
                                                   }
                                               };

    public CircularGroup(ShapeRenderer shape) {
        this.shape = shape;

    }

    public void setDraggable(boolean draggable) {
        if ( draggable &&!getCaptureListeners().contains( inputListener, true ) )
            addCaptureListener( inputListener );
        else if ( getCaptureListeners().contains( inputListener, true ) )
            removeCaptureListener( inputListener );
    }


    @Override
    public void setPosition(float x, float y) {
        this.center = new Vector2( radius +stroke, radius +stroke );
        super.setPosition( x - ( getWidth() /2 ), y - ( getHeight() /2 ) );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw( batch, parentAlpha );

        shape.setProjectionMatrix( getStage().getCamera().combined );
        shape.begin( ShapeType.Line );
        shape.setColor( 0.5f, 1, 0.7f, parentAlpha );
        shape.circle( getX() +center.x, getY() +center.y, radius );
        shape.circle( getX() +center.x, getY() +center.y, radius +stroke );
        shape.circle( getX() +center.x, getY() +center.y, 2 );
        shape.circle( getX() +center.x, getY() +center.y, 5 );
        shape.end();

    }

    @Override
    public void layout() {


        float unghi = rotation +mid;
        float direction = clockwise ? interval : -interval;
        unghi = unghi - ( ( ( getChildren().size -1 ) *direction ) /2 );

        for (Actor actor : getChildren() ) {
            tmp.set( getPositionbyAngle( tmp, unghi ) );
            actor.setPosition( tmp.x -actor.getWidth() /2, tmp.y -actor.getHeight() /2 );

            if ( modifyAlpha )
                actor.addAction( Actions.alpha( getAlphaByDistance( unghi ) ) );

            if ( rotKids ) {
                actor.setOrigin( actor.getWidth() /2, actor.getHeight() /2 );
                actor.setRotation( unghi );
            }

            unghi += direction;
        }

    }

    public void rotateMenu(float degrees) {
        rotation += degrees;
        rotation = formatAngle( rotation );
        invalidate();
    }


    public void setRotationMenu(float degrees) {
        rotation = degrees;
        rotation = formatAngle( rotation );
        invalidate();
    }

    private float getAlphaByDistance(float unghi) {
        if ( getDifference( unghi, mid ) >Math.abs( dir ) )
            return 1f -getDifference( unghi, mid ) /180;
        return 1;

    }

    private float formatAngle(float angle) {
        return angle >=0 ? angle %360 : angle %360 +360;
    }

    @Override
    public void act(float delta) {
        super.act( delta );

        if ( Gdx.input.isKeyPressed( Keys.O ) )
            rotateMenu( 2 );
        if ( Gdx.input.isKeyPressed( Keys.L ) )
            rotateMenu( -2 );

    }

    private float getDifference(float a1, float a2) {
        return Math.min( ( a1 -a2 ) <0 ? a1 -a2 +360 : a1 -a2, ( a2 -a1 ) <0 ? a2 -a1 +360 : a2 -a1 );
    }

    private Vector2 getPositionbyAngle(Vector2 out, float angle) {
        return out.set( center ).add( MathUtils.cosDeg( angle ) * ( radius +stroke /2 ), MathUtils.sinDeg( angle ) * ( radius +stroke /2 ) );
    }

    /**
     * Set the 2 angles in degrees in which the elements will be emphasized
     * 
     * @param firstAngle
     * @param secondAngle
     * @param clockwise
     *        : the direction in which to draw the elements
     * @param angleBetween
     *        : the angle between each element
     */
    public void setActivInterval(float firstAngle, float secondAngle, boolean clockwise, float angleBetween) {
        this.minAngle = firstAngle >=0 ? firstAngle %360 : firstAngle %360 +360;
        this.maxAngle = secondAngle >=0 ? secondAngle %360 : secondAngle %360 +360;
        this.interval = angleBetween;
        this.clockwise = clockwise;

        float unghi;
        float direction;

        if ( this.clockwise && ( minAngle >maxAngle ) ) {
            float dif = 360 -Math.min( maxAngle, minAngle );
            direction = Math.abs( ( minAngle +dif ) - ( maxAngle +dif ) );
            direction = 360 -direction;
            unghi = ( minAngle +maxAngle -360 ) /2;
        }
        else if ( !this.clockwise && ( minAngle <maxAngle ) ) {
            float dif = 360 -Math.min( maxAngle, minAngle );
            direction = Math.abs( ( minAngle +dif ) - ( maxAngle +dif ) );
            direction = 360 -direction;
            unghi = ( minAngle +maxAngle -360 ) /2;
        }
        else {
            direction = Math.abs( minAngle -maxAngle );
            unghi = ( maxAngle +minAngle ) /2;
        }

        if ( !this.clockwise )
            direction *= -1;

        mid = unghi;
        dir = direction;

        invalidate();

        // System.out.println( firstAngle +" -> " +secondAngle +" " +clockwise +"  :    " +unghi +" " +direction +"    " +mid +" " +dir +" " +interval +" " +rotation );

    }

    public void setAsCircle(int radius, int stroke) {
        this.radius = radius;
        this.stroke = stroke;
        setSize( ( radius +stroke ) *2, ( radius +stroke ) *2 );
    }

    /**
     * Whether to rotate the children according to their position or not .
     * 
     * @param rotChildern
     */
    public void setRotateChildren(boolean rotChildern) {
        this.rotKids = rotChildern;
    }

    public void setModifyAlpha(boolean modifyAlpha) {
        this.modifyAlpha = modifyAlpha;
    }

}
