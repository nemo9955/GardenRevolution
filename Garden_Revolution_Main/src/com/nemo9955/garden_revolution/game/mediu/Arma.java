package com.nemo9955.garden_revolution.game.mediu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nemo9955.garden_revolution.game.World;
import com.nemo9955.garden_revolution.game.enumTypes.Armament;


public abstract class Arma implements Disposable {

    protected Array<Disposable> toDispose  = new Array<Disposable>( false, 1 );
    protected static Vector3    tmp        = new Vector3();

    private ModelInstance       model;
    public AnimationController  animation;
    public Vector3              poz;

    public FireMode             state;
    public Armament             type;

    protected int               fireDellay = 100;
    protected long              fireTime;


    public Arma(Armament type, Vector3 poz) {
        this.poz = poz;
        this.type = type;
        state = getFireState();

        ModelBuilder build = new ModelBuilder();
        Model sfera = build.createSphere( 2, 2, 2, 12, 12, new Material( ColorAttribute.createDiffuse( Color.WHITE ) ), Usage.Position |Usage.Normal |Usage.TextureCoordinates );
        toDispose.add( sfera );

        model = getModelInstance( poz );
        animation = new AnimationController( model );
    }

    protected abstract ModelInstance getModelInstance(Vector3 poz2);

    public void update(float delta) {
        animation.update( delta );
    }

    protected abstract FireMode getFireState();

    public void render(ModelBatch modelBatch) {
        modelBatch.render( model );
    }

    public void render(ModelBatch modelBatch, Environment light) {
        modelBatch.render( model, light );
    }

    public void render(ModelBatch modelBatch, Shader shader) {
        modelBatch.render( model, shader );
    }


    public int getFireDellay() {
        return fireDellay;
    }

    public void setFireDellay(int fireDellay) {
        this.fireDellay = fireDellay;
    }

    @Override
    public void dispose() {

        for (Disposable dis : toDispose )
            dis.dispose();

        toDispose.clear();
    }

    public static interface FireCharged {

        public void fireCharged(World world, Ray ray, float charged);

    }

    public static interface FireNormal {

        public void fireNormal(World world, Ray ray);

    }

    public static enum FireMode {
        CONTINUOUS, //
        TAP, //
        LOCKED_TAP, //
        LOCKED_CHARGE; //
    }
}
