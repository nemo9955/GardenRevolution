package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.game.Vietate;


public class Rosie extends Vietate {

    public Rosie(CatmullRomSpline<Vector3> drum, float x, float y, float z) {
        super( new ModelBuilder().createBox( 1f, 2f, 1f, new Material( ColorAttribute.createDiffuse( Color.BLUE ) ), Usage.Position |Usage.Normal ), drum, x, y, z );
    }

}
