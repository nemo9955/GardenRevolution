package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;


public class StageActorPointer {

    // private static final Vector2 tmp = new Vector2();

    private Stage         stage;
    private ShapeRenderer shape;
    private Actor         selected;
    private Vector2       selCenter = new Vector2();

    public StageActorPointer(Stage stage) {
        this.stage = stage;
        shape = new ShapeRenderer();
    }

    public StageActorPointer(Stage stage, ShapeRenderer shape) {
        this.stage = stage;
        this.shape = shape;
    }

    public void draw(float delta) {

        shape.setProjectionMatrix( stage.getCamera().combined );
        shape.begin( ShapeType.Line );

        shape.circle( selCenter.x, selCenter.y, 10 );
        shape.circle( selCenter.x, selCenter.y, 15 );
        shape.circle( selCenter.x, selCenter.y, 40 );

        shape.end();
    }

    public void setSelectedActor(Actor selected) {
        this.selected = selected;
        getSelectedCenter( selCenter );
    }

    public Vector2 getSelectedCenter(Vector2 out) {
        // out.set( selected.getWidth() /2, selected.getHeight() /2 );
        out.set( selected.getX(), selected.getY() );
      //  selected.localToStageCoordinates( out );
        stage.stageToScreenCoordinates( out );
        System.out.println( out );
        return out;
    }
}
