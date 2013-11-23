package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;


public class CircularGroup extends Group {

    private Vector2       center;
    private int           radius;
    private int           stroke;
    private ShapeRenderer shape;

    public CircularGroup(Vector2 center, int radius, int stroke, ShapeRenderer shape) {
        super();
        this.center = center;
        this.radius = radius;
        this.stroke = stroke;
        this.shape = shape;
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor( actor );

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw( batch, parentAlpha );
        shape.setProjectionMatrix( getStage().getCamera().combined );
        shape.begin( ShapeType.Line );
        shape.circle( center.x, center.y, radius );
        shape.circle( center.x, center.y, radius +stroke );
        shape.end();

    }

}
