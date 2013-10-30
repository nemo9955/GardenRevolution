package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.Vietate;


public class Rosie extends Vietate {

    public Rosie(CatmullRomSpline<Vector3> drum, float x, float y, float z) {
        super( drum, x, y, z );
    } 

    @Override
    protected Model getModel() {
        return new ModelBuilder().createBox( 0.5f, 3f, 0.5f, new Material( ColorAttribute.createDiffuse( Color.PINK ) ), Usage.Position |Usage.Normal );
    }

}
