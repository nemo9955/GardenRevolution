package com.nemo9955.garden_revolution.utility;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryo.Kryo;
import com.nemo9955.garden_revolution.net.packets.Packets.PlayerChangesTower;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.net.packets.Packets.TowerChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.WeaponChangedPacket;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;

public class Functions {

    public static InputEvent clickedEvent = newInputEvent();

    public static void setSerializedClasses(Kryo kryo) {
        kryo.register( byte.class );
        kryo.register( String.class );
        kryo.register( String[].class );
        kryo.register( StartingServerInfo.class );
        kryo.register( msNetGR.class );
        kryo.register( WeaponChangedPacket.class );
        kryo.register( TowerChangedPacket.class );
        kryo.register( PlayerChangesTower.class );
    }

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

    private static WeaponChangedPacket wcp = new WeaponChangedPacket();

    public static WeaponChangedPacket getWCP(byte towerID, int eOrdinal) {
        return wcp.getWCP( eOrdinal, towerID );
    }

    private static TowerChangedPacket tcp = new TowerChangedPacket();

    public static TowerChangedPacket getTCP(byte towerID, int eOrdinal) {
        return tcp.getTCP( eOrdinal, towerID );
    }

    private static PlayerChangesTower pct = new PlayerChangesTower();

    public static PlayerChangesTower getPCT(byte current, byte next, String name) {
        return pct.getPCT( current, next, name );
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
}
