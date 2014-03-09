package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class CustomAdapter implements InputProcessor, GestureListener, ControllerListener {

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {

        return false;

    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        return false;

    }

    @Override
    public boolean longPress(float x, float y) {

        return false;

    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        return false;

    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {

        return false;

    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        return false;

    }

    @Override
    public boolean zoom(float initialDistance, float distance) {

        return false;

    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {

        return false;

    }

    @Override
    public boolean keyDown(int keycode) {

        return false;

    }

    @Override
    public boolean keyUp(int keycode) {

        return false;

    }

    @Override
    public boolean keyTyped(char character) {

        return false;

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return false;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        return false;

    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        return false;

    }

    @Override
    public boolean scrolled(int amount) {

        return false;

    }


    @Override
    public void connected(Controller controller) {
    }

    @Override
    public void disconnected(Controller controller) {
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        return false;

    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {

        return false;

    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {

        return false;

    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {

        return false;

    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {

        return false;

    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {

        return false;

    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {

        return false;

    }

}
