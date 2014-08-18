package com.nemo9955.garden_revolution.game.entitati;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.game.enumTypes.ShotType;
import com.nemo9955.garden_revolution.game.world.WorldBase;

public class Shot extends Entity {

	private final Vector3	direction	= new Vector3();
	private float			life;
	public ShotType			type;

	public Shot create( ShotType type, Ray ray, float charge ) {
		this.type = type;
		super.init(ray.origin);
		life = 5f;

		this.type.getInitialDir(this.direction.set(ray.direction), charge);

		return this;
	}

	@Override
	public void reset() {
		super.reset();
		life = 0;
	}

	private static final Vector3	tempMover	= new Vector3();

	@Override
	public void update( float delta ) {
		super.update(delta);

		move(type.makeMove(direction, tempMover, delta));

		life -= delta;
		if ( life <= 0 || poz.y < 0 ) {
			setDead(true);
		}

		for (BoundingBox col : world.getWorld().getColide())
			if ( col.intersects(box) )
				setDead(true);

		type.hitActivity(this, world);

		if ( isDead() ) {
			world.getWorld().getShotPool().free(this);
			world.getWorld().getShot().removeValue(this, false);
		}
	}

	@Override
	public void render( ModelBatch modelBatch, Environment light, DecalBatch decalBatch ) {
		if ( isEntVisible(modelBatch) )
			modelBatch.render(model);
	}

	private static Model	modelTemp	= createModel();

	private static Model createModel() {
		ModelBuilder modelBuilder = new ModelBuilder();
		Model model = modelBuilder.createSphere(0.5f, 0.5f, 0.5f, 12, 12, new Material(ColorAttribute.createDiffuse(Color.RED)), Usage.Position | Usage.Normal);
		WorldBase.toDispose.add(model);
		return model;
	}

	@Override
	protected ModelInstance getModel( float x, float y, float z ) {
		return new ModelInstance(modelTemp, x, y, z);

	}

	@Override
	public void setDead( boolean dead ) {
		super.setDead(dead);

	}
}
