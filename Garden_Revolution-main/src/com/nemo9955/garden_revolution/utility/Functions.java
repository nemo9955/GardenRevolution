package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;


public class Functions {

    public static InputEvent clickedEvent = newInputEvent();


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

}
