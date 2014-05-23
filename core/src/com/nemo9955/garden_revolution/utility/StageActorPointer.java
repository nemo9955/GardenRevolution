package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;


public class StageActorPointer {

    private Actor   selected;
    private Vector2 selCenter = new Vector2();
    private Stage   stage;

    public float    mvx       = 0, mvy = 0;
    private boolean visible   = true;
    private float   alfa      = 0f;
    private Sprite  img;

    public StageActorPointer(Stage stage) {
        this.stage = stage;
        img = new Sprite( GR.skin.getSprite( "mover-knob" ) );
        img.setSize( 50, 50 );
    }

    public void draw() {
        if ( visible &&alfa >0f ) {
            stage.getBatch().begin();
            img.setPosition( selCenter.x -img.getWidth() /2, selCenter.y -img.getHeight() /2 );
            img.draw( stage.getBatch(), MathUtils.clamp( alfa, 0f, 1f ) );
            stage.getBatch().end();
            alfa -= Gdx.graphics.getDeltaTime();
            if ( mvx !=0 ||mvy !=0 )
                updatePointer();
        }
    }

    public void setSelectedActor(Actor selected) {
        this.selected = selected;
        selCenter.set( selected.getWidth(), selected.getHeight() ).scl( 0.5f );
        this.selected.localToStageCoordinates( selCenter );
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

    private void updatePointer() {
        selCenter.add( GR.tmp1.set( mvx, mvy ).scl( Gdx.graphics.getDeltaTime() *400 ) );
        Actor hit = stage.hit( selCenter.x, selCenter.y, true );
        if ( hit !=null ) {
            if ( isValid( hit ) &&hit !=selected ) {
                setSelectedActor( hit );
                mvx = 0;
                mvy = 0;
            }
        }
    }


    public void fireSelected() {
        if ( selected !=null )
            Func.click( selected );
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

    public void updFromController(Controller controller, int axisCode, float value) {
        value = MathUtils.clamp( value, -1f, 1f );
        if ( Math.abs( value ) <Vars.deadZone ) {
            value = 0f;
            if ( Math.abs( controller.getAxis( CoAxis.mvX.id ) ) <Vars.deadZone )
                mvx = 0;
            if ( Math.abs( controller.getAxis( CoAxis.mvY.id ) ) <Vars.deadZone )
                mvy = 0;
        }
        else {
            if ( axisCode ==CoAxis.mvX.id )
                mvx = value *Vars.invertControlletX *-1f;
            if ( axisCode ==CoAxis.mvY.id )
                mvy = value *Vars.invertControlletY *-1f;
            alfa = 2f;
        }
    }

    public void updFromController(Controller controller, int povCode, PovDirection value) {

        alfa = 2f;

        if ( value ==PovDirection.north )
            goInDir( 0, 1 );
        else if ( value ==PovDirection.south )
            goInDir( 0, -1 );
        else if ( value ==PovDirection.east )
            goInDir( 1, 0 );
        else if ( value ==PovDirection.west )
            goInDir( -1, 0 );
        else if ( value ==PovDirection.southWest )
            goInDir( -1, -1 );
        else if ( value ==PovDirection.southEast )
            goInDir( 1, -1 );
        else if ( value ==PovDirection.northWest )
            goInDir( -1, 1 );
        else if ( value ==PovDirection.northEast )
            goInDir( 1, 1 );
    }
}
