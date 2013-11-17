package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;


public class Turn {

    private BoundingBox          baza     = new BoundingBox();
    private Array<BoundingBox>   coliders = new Array<BoundingBox>( false, 1 );

    private Array<ModelInstance> parti    = new Array<ModelInstance>( false, 1 );
    public Vector3               place;
    public Vector3               poz;

    public Turn(ModelInstance baza, Vector3 poz) {
        parti.add( baza );
        place = poz.cpy().add( 0, 5, 0 );
        baza.calculateBoundingBox( this.baza );
        this.poz = poz.cpy();
    }

    private void manageInfo(ModelInstance model) {
        for (Node nod : model.nodes ) {
            if ( nod.id.startsWith( "arma" ) ) {
                place = nod.translation.cpy();
                place.add( poz );
            }
            else if ( nod.id.startsWith( "colide" ) ) {
                coliders.add( nod.calculateBoundingBox( new BoundingBox(), true ) );
            }
        }
    }

    public void update(float delta) {

    }

    public void render(ModelBatch modelBatch) {
        for (ModelInstance model : parti )
            modelBatch.render( model );
    }

    public void render(ModelBatch modelBatch, Environment light) {
        for (ModelInstance model : parti )
            modelBatch.render( model, light );
    }

    public void render(ModelBatch modelBatch, Shader shader) {
        for (ModelInstance model : parti )
            modelBatch.render( model, shader );
    }


}
