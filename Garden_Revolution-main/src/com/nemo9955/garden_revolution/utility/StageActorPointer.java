package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.nemo9955.garden_revolution.GR;


public class StageActorPointer {

    // private static final Vector2 tmp = new Vector2();

    private ShapeRenderer shape;
    private Actor         selected;
    private Vector2       selCenter = new Vector2();
    private Stage         stage;

    public float          mvx       = 0, mvy = 0;
    private boolean       visible = true;

    public StageActorPointer(Stage stage) {
        this.stage = stage;
        shape = new ShapeRenderer();
    }

    public StageActorPointer(Stage stage, ShapeRenderer shape) {
        this.stage = stage;
        this.shape = shape;
    }


    public void draw() {
        if ( visible ) {
            shape.setProjectionMatrix( stage.getCamera().combined );
            shape.begin( ShapeType.Line );

            shape.setColor( 1, 1, 1, 0.1f );

            shape.circle( selCenter.x, selCenter.y, 4 );
            shape.circle( selCenter.x, selCenter.y, 40 );

            shape.end();

            if ( mvx !=0 ||mvy !=0 )
                updatePointer();
        }
    }

    public void setSelectedActor(Actor selected) {
        this.selected = selected;
        selCenter.set( selected.getWidth(), selected.getHeight() ).scl( 0.5f );
        this.selected.localToStageCoordinates( selCenter );
    }

    public void fireSelected() {
        if ( selected !=null )
            Func.click( selected );
    }


    private void updatePointer() {
        movePoint( mvx, mvy );

        Actor hit = stage.hit( selCenter.x, selCenter.y, true );
        if ( hit !=null ) {
            // System.out.println( hit );
            if ( isValid( hit ) &&hit !=selected ) {
                setSelectedActor( hit );
                mvx = 0;
                mvy = 0;
            }
        }
    }

    private void movePoint(float movex, float movey) {
        selCenter.add( GR.tmp1.set( movex, movey ).scl( Gdx.graphics.getDeltaTime() *400 ) );

        if ( !Func.getStageZon( stage ).contains( selCenter ) )
            selCenter.sub( GR.tmp1.set( movex, movey ).scl( Gdx.graphics.getDeltaTime() *400 ) );
    }

    private boolean isValid(Actor hit) {
        if ( hit instanceof ImageButton )
            return true;
        if ( hit instanceof Image )
            return true;
        if ( hit instanceof TextButton )
            return true;
        if ( hit instanceof ImageTextButton )
            return true;
        if ( hit instanceof Button )
            return true;
        return false;
    }

    public Actor getSelected() {
        return selected;
    }

    public Vector2 getPoint() {
        return selCenter;
    }

    public void goInDir(float dirX, float dirY) {
        Vector2 down = GR.tmp2.set( selCenter );
        while ( Func.getStageZon( stage ).contains( down ) ) {
            down.add( dirX, dirY );

            Actor hit = stage.hit( down.x, down.y, true );
            if ( hit !=null &&isValid( hit ) &&hit !=selected ) {
                setSelectedActor( hit );
                selCenter.set( down );
                break;
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
