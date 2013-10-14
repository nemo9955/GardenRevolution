package com.nemo9955.garden_revolution.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;


public class World {

    public Array<Entitate>       entitati;
    private Array<ModelInstance> nori = new Array<ModelInstance>();

    public World() {
        entitati = new Array<Entitate>( false, 20 );// FIXME
        makeNori();
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch, Lights light) {
        for (ModelInstance nor : nori )
            modelBatch.render( nor );
        for (Entitate e : entitati )
            modelBatch.render( e, light );
    }

    public Entitate add(Entitate ent) {

        entitati.add( ent );

        return ent;
    }

    private void makeNori() {
        nori.clear();
        ModelBuilder build = new ModelBuilder();
        Random zar = new Random();
        ModelInstance nor;

        int norx, norz;

        for (int i = 0 ; i <200 ; i ++ ) {
            norx = zar.nextInt( 500 ) -250;
            norz = zar.nextInt( 500 ) -250;
            for (int j = 1 ; j <=15 ; j ++ ) {
                nor = new ModelInstance( build.createSphere( 5, 5, 5, 12, 12, new Material( ColorAttribute.createDiffuse( Color.CYAN ) ), Usage.Position |Usage.Normal ) );
                nor.transform.translate( norx +zar.nextFloat() *7, 30, norz +zar.nextFloat() *7 );
                nori.add( nor );
            }
        }
    }

}
