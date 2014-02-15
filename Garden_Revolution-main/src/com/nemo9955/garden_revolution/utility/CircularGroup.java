package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
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
    private float                mid, dir, lungimea;
    private float                rotation      = 0;
    private boolean              lockInside;

    private InputListener        inputListener = new InputListener() {

                                                   private float   inAngle;
                                                   private Vector2 temp = new Vector2();
                                                   private float   initianRot;

                                                   @Override
                                                   public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                                       inAngle = temp.set( x, y ).sub( center ).angle();
                                                       initianRot = rotation;
                                                       float distCenter = temp.dst2( 0, 0 );
                                                       if ( distCenter >radius *radius &&distCenter < ( radius +stroke ) * ( radius +stroke ) )
                                                           return true;
                                                       return false;
                                                   }

                                                   @Override
                                                   public void touchDragged(InputEvent event, float x, float y, int pointer) {
                                                       temp.set( x, y ).sub( center );
                                                       setRotationMenu( formatAngle( initianRot + ( temp.angle() -inAngle ) ) );
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
    public void draw(SpriteBatch batch, float parentAlpha) {
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
    protected void childrenChanged() {
        super.childrenChanged();
        lungimea = ( getChildren().size -1 ) *interval;
    }

    @Override
    public void layout() {

        float unghi = rotation +mid;
        float direction = clockwise ? -interval : interval;
        unghi = unghi - ( ( clockwise ? -lungimea : lungimea ) /2 );
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
        if ( lockInside )
            if ( Math.min( rotation +degrees, 360 - ( rotation +degrees ) ) > ( lungimea -Math.abs( dir ) ) /2 )
                return;
        rotation += degrees;
        rotation = formatAngle( rotation );
        invalidate();
    }


    public void setRotationMenu(float degrees) {
        if ( lockInside )
            if ( Math.min( degrees, 360 -degrees ) > ( lungimea -Math.abs( dir ) ) /2 )
                return;
        rotation = degrees;
        rotation = formatAngle( rotation );
        invalidate();
    }

    private float getAlphaByDistance(float unghi) {
        float corect = formatAngle( unghi );
        if ( getDifference( corect, mid ) >Math.abs( mid +dir /2 ) )
            return 1 -getDifference( corect, mid ) /180;
        return 1;

    }

    private float formatAngle(float angle) {
        return angle >=0 ? angle %360 : angle %360 +360;
    }

    @Override
    public void act(float delta) {
        super.act( delta );
    }

    private static float getDifference(float a1, float a2) {
        return Math.min( ( a1 -a2 ) <0 ? a1 -a2 +360 : a1 -a2, ( a2 -a1 ) <0 ? a2 -a1 +360 : a2 -a1 );
    }

    public Vector2 getPositionbyAngle(Vector2 out, float angle) {
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
     * @param lockInside
     *        : if true the outer-most elements won't be allowed to go between the 2 angles
     */
    public void setActivInterval(float firstAngle, float secondAngle, boolean clockwise, float angleBetween, boolean lockInside) {
        this.minAngle = firstAngle >=0 ? firstAngle %360 : firstAngle %360 +360;
        this.maxAngle = secondAngle >=0 ? secondAngle %360 : secondAngle %360 +360;
        this.interval = angleBetween;
        this.clockwise = clockwise;
        this.lockInside = lockInside;

        float unghi;
        float direction;

        if ( !this.clockwise && ( minAngle >maxAngle ) ) {
            float dif = 360 -Math.min( maxAngle, minAngle );
            direction = Math.abs( ( minAngle +dif ) - ( maxAngle +dif ) );
            direction = 360 -direction;
            unghi = ( minAngle +maxAngle -360 ) /2;
        }
        else if ( this.clockwise && ( minAngle <maxAngle ) ) {
            float dif = 360 -Math.min( maxAngle, minAngle );
            direction = Math.abs( ( minAngle +dif ) - ( maxAngle +dif ) );
            direction = 360 -direction;
            unghi = ( minAngle +maxAngle -360 ) /2;
        }
        else {
            direction = Math.abs( minAngle -maxAngle );
            unghi = ( maxAngle +minAngle ) /2;
        }

        if ( this.clockwise )
            direction *= -1;

        mid = unghi;
        dir = direction;

        invalidate();

        // System.out.println( minAngle +" -> " +maxAngle +" " +clockwise +"  :    " +unghi +" " +direction +"    " +mid +" " +dir +" " +interval +" " +rotation );

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

    public int getRadius() {
        return radius;
    }

    public int getStroke() {
        return stroke;
    }

    /**
     * Starts at 0 and increments by {@code angle} until the point is no longer in the {@code zon}
     * 
     * @param zon
     * @param angle
     * @param padding
     *        :subtracts from the result the {@code angle*padding}
     * @return
     */
    public float minAngleInZon(final Rectangle zon, final float angle, final float padding) {
        tmp.set( zon.x +1, zon.y +1 );
        float i = 0;
        while ( zon.contains( tmp ) &&i <=180 ) {
            i += angle;
            getPositionbyAngle( tmp, i ).add( getX(), getY() );
        }
        return i -angle *padding;
    }

    /**
     * 
     * @param distance
     *        : the distance from the center
     * @param height
     *        : the height of the element
     * @return the angle for which the elements don't collide
     */

    public static float aprxOptAngl(float distance, float height) {
        tmp.set( distance, height );
        return getDifference( tmp.angle(), 0 );
    }

}
