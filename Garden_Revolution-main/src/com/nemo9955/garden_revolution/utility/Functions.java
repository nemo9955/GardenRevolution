package com.nemo9955.garden_revolution.utility;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class Functions {

    public static InputEvent clickedEvent = newInputEvent();

    public static String getIpAddress() {
        Enumeration<NetworkInterface> nis;
        try {
            nis = NetworkInterface.getNetworkInterfaces();

            NetworkInterface ni;
            while ( nis.hasMoreElements() ) {
                ni = nis.nextElement();
                if ( !ni.isLoopback()/* not loopback */&&ni.isUp()/* it works now */) {
                    for (InterfaceAddress ia : ni.getInterfaceAddresses() ) {
                        // filter for ipv4/ipv6
                        if ( ia.getAddress().getAddress().length ==4 ) { // 4 for ipv4, 16 for ipv6
                            return ia.getAddress().getHostAddress();
                        }
                    }
                }
            }
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        return "Could not detect the IP.";
    }


    public static void fire(Actor actor) {

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
        if ( Gdx.app.getType() !=ApplicationType.Desktop )
            return false;
        if ( Controllers.getControllers().size <=0 )
            return false;
        return true;
    }

    /**
     * Convenience method for {@code Intersector.intersectLinePlane }
     * 
     */
    public static float intersectLinePlane(Ray ray, Plane plane, Vector3 intersection) {
        Vector3 direction = ray.direction;
        Vector3 origin = ray.origin;
        float denom = direction.dot( plane.getNormal() );
        if ( denom !=0 ) {
            float t = - ( origin.dot( plane.getNormal() ) +plane.getD() ) /denom;
            if ( t >=0 &&t <=1 &&intersection !=null )
                intersection.set( origin ).add( direction.scl( t ) );
            return t;
        }
        else if ( plane.testPoint( origin ) ==Plane.PlaneSide.OnPlane ) {
            if ( intersection !=null )
                intersection.set( origin );
            return 0;
        }

        return -1;
    }

    public static boolean isCurrentState(Stage stage, String name) {
        for (Actor actor : stage.getActors() )
            if ( actor.isVisible() &&actor.getName() ==name )
                return true;
        return false;
    }
}
