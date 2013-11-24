package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class CircularGroup extends Group {

    private Vector2       center;
    private int           radius;
    private int           stroke;
    private ShapeRenderer shape;

    private float         minAngle  = 0, maxAngle = 359;
    private float         interval  = 20;
    private boolean       rotKids   = false;

    private boolean       clockwise = true;
    private float         mid, dir;
    private float         rotation  = 90;

    private ClickListener clickListener;

    public CircularGroup(Vector2 center, int radius, int stroke, ShapeRenderer shape) {
        super();
        this.center = center;
        this.radius = radius;
        this.stroke = stroke;
        this.shape = shape;
        setSize( 0, 0 );


        addCaptureListener( clickListener = new ClickListener() {

            private float stx, sty;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                System.out.println( "apasat" );
                stx = x;
                sty = y;

                return false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                rotateMenu( (float) Math.toDegrees( Math.atan2( stx -x, sty -y ) ) );
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        } );

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

    public void rotateMenu(float degrees) {
        System.out.println(degrees);
        rotation += degrees;
        childrenChanged();
    }

    @Override
    protected void childrenChanged() {


        float unghi = rotation +mid;
        float direction = dir / ( interval -1 );
        unghi = unghi - ( ( ( getChildren().size -1 ) *direction ) /2 );

        Vector2 tmp = new Vector2();
        for (Actor actor : getChildren() ) {
            tmp = getPosition( tmp, unghi );
            actor.setPosition( tmp.x -actor.getWidth() /2, tmp.y -actor.getHeight() /2 );

            if ( Math.abs( unghi ) >mid +Math.abs( dir /2 ) )
                actor.addAction( Actions.alpha( Math.abs( 1 -unghi / ( Math.abs( dir /2 ) *3 ) ) ) );
            else
                actor.addAction( Actions.alpha( 1 ) );

            if ( rotKids ) {
                actor.setOrigin( actor.getWidth() /2, actor.getHeight() /2 );
                actor.setRotation( unghi );
            }

            unghi += direction;
        }
        act( Gdx.graphics.getDeltaTime() );
    }

    private Vector2 getPosition(Vector2 out, float angle) {
        return out.set( center ).add( MathUtils.cosDeg( angle ) * ( radius +stroke /2 ), MathUtils.sinDeg( angle ) * ( radius +stroke /2 ) );
    }

    /**
     * Set the 2 angles in degrees in which the elements will be emphasized and how many should it accept
     * 
     * @param firstAngle
     * @param secondAngle
     * @param clockwise
     *        : the direction in which to draw the elements
     * @param amount
     */
    public void setActivInterval(float firstAngle, float secondAngle, boolean clockwise, int amount) {
        this.minAngle = firstAngle >=0 ? firstAngle %360 : firstAngle %360 +360;
        this.maxAngle = secondAngle >=0 ? secondAngle %360 : secondAngle %360 +360;
        this.interval = amount +1;
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

    }

    /**
     * Whether to rotate the children according to their position or not .
     * 
     * @param rotChildern
     */
    public void setRotateChildren(boolean rotChildern) {
        this.rotKids = rotChildern;
    }
}
