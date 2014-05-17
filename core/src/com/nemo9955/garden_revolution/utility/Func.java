package com.nemo9955.garden_revolution.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.esotericsoftware.kryo.Kryo;
import com.nemo9955.garden_revolution.net.packets.Packets;

public class Func {

    public static InputEvent clickedEvent = newInputEvent();

    public static void setSerializedClasses(Kryo kryo) {
        kryo.register( byte.class );
        kryo.register( short.class );
        kryo.register( float.class );
        kryo.register( String.class );
        kryo.register( String[].class );
        for (Class<?> cls : Packets.class.getClasses() )
            kryo.register( cls );
    }

    public static String getIpAddress() {
        URL whatismyip = null;
        BufferedReader in = null;
        try {
            whatismyip = new URL( "http://checkip.amazonaws.com" );
            in = new BufferedReader( new InputStreamReader( whatismyip.openStream() ) );
            String ip = in.readLine();
            return ip;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if ( in !=null ) {
                try {
                    in.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "Could not detect IP";
    }

    public static void click(Actor actor) {
        clickedEvent.setType( Type.touchDown );
        actor.fire( clickedEvent );
        clickedEvent.setType( Type.touchUp );
        actor.fire( clickedEvent );
    }


    private static InputEvent newInputEvent() {
        InputEvent event = new InputEvent();

        event.setBubbles( false );
        event.setButton( Buttons.LEFT );
        event.setPointer( 0 );

        return event;
    }


    public static boolean isControllerUsable() {
        return Controllers.getControllers().size >0;
    }


    public static float intersectLinePlane(Ray ray, Vector3 intersection) {

        float distance = -ray.origin.y /ray.direction.y;
        intersection.set( ray.getEndPoint( new Vector3(), distance ) );
        return distance;
    }

    public static boolean isCurrentState(Stage stage, String name) {
        for (Actor actor : stage.getActors() )
            if ( actor.isVisible() &&actor.getName() ==name )
                return true;
        return false;
    }

    public static Actor getActorInParentStage(Stage stage, String parent, String name) {
        for (Actor pare : stage.getActors() )
            if ( pare.isVisible() &&pare instanceof Group &&pare.getName() ==parent )
                for (Actor act : ( (Group) pare ).getChildren() )
                    if ( act.getName() ==name )
                        return act;
        return null;
    }

    public static Actor getActorInActiveStage(Stage stage, String name) {
        for (Actor pare : stage.getActors() )
            if ( pare.isVisible() &&pare instanceof Group )
                for (Actor act : ( (Group) pare ).getChildren() )
                    if ( act.getName() ==name )
                        return act;
        return null;
    }

    public static float getOffset(byte ofs) {
        return ofs *0.1f;
        // return 0;
    }

    public static float getRandOffset() {
        return MathUtils.random( -30, 30 ) *0.1f;
        // return 0;
    }

    public static boolean isDesktop() {
        return Gdx.app.getType() ==ApplicationType.Desktop;
    }

    public static boolean isAndroid() {
        return Gdx.app.getType() ==ApplicationType.Android;
    }

    public static final Rectangle zonBound = new Rectangle();

    public static Rectangle getScrZon() {
        zonBound.set( 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        return zonBound;
    }

    public static Rectangle getStageZon(Stage stage) {
        zonBound.set( 0, 0, stage.getWidth(), stage.getHeight() );
        return zonBound;
    }

    public static Rectangle getStageShrink(Stage stage, float rapx, float rapy) {
        zonBound.setSize( stage.getWidth() *rapx, stage.getHeight() *rapy );
        zonBound.setCenter( stage.getWidth() /2, stage.getHeight() /2 );
        return zonBound;
    }

    public static Rectangle getScrShrink(float rapx, float rapy) {
        zonBound.setSize( Gdx.graphics.getWidth() *rapx, Gdx.graphics.getHeight() *rapy );
        zonBound.setCenter( Gdx.graphics.getWidth() /2, Gdx.graphics.getHeight() /2 );
        return zonBound;
    }

    public static void makePropTouch(Group parent) {
        for (Actor act : parent.getChildren() ) {
            if ( act instanceof CheckBox ) {
                ( (CheckBox) act ).getImage().setTouchable( Touchable.disabled );
                ( (CheckBox) act ).getLabel().setTouchable( Touchable.disabled );
            }
            if ( act instanceof ImageTextButton ) {
                ( (ImageTextButton) act ).getImage().setTouchable( Touchable.disabled );
                ( (ImageTextButton) act ).getLabel().setTouchable( Touchable.disabled );
            }
            if ( act instanceof Image )
                ( (Image) act ).setTouchable( Touchable.disabled );

            if ( act instanceof Label )
                ( (Label) act ).setTouchable( Touchable.disabled );

            if ( act instanceof TextButton ) {
                ( (TextButton) act ).getLabel().setTouchable( Touchable.disabled );
            }
            if ( act instanceof Group )
                makePropTouch( (Group) act );
        }
    }
}
