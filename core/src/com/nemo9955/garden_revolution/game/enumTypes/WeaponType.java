package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.mediu.Tower;
import com.nemo9955.garden_revolution.game.world.WorldBase;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;

public enum WeaponType {

	NONE {

		{
			setPrice(0);
			name = "None";
			details = "You have no weapon equiped";
		}

		@Override
		public boolean fireProjectile( WorldWrapper world, Ray ray, float charge ) {
			return false;
		}

		@Override
		public ModelInstance getModelInstance( Vector3 poz ) {
			return null;
		}

		@Override
		public FireType getFireType() {
			return null;
		}

		@Override
		public void updateWeaponTargeting( Tower tower, boolean fromUpdate ) {}
	},
	CANNON {

		private Decal	spot;
		private float	opac	= 0.3f;
		private float	idle	= 2;
		private boolean	draw	= false;
		private boolean	aftImp	= false;
		private float	frames	= 1 / 60f;

		{
			setPrice(360);
			fireDellay = 1000;
			name = "Cannon";
			details = "Slow but powerfull.";

			spot = Decal.newDecal(Garden_Revolution.getPackTexture("mover-bg"), true);
			spot.setRotation(Vector3.Y, Vector3.Y);
			spot.setDimensions(20, 20);
			spot.setColor(1, 1, 1, opac);

		}

		@Override
		public void updateWeaponTargeting( Tower tower, boolean fromUpdate ) {
			if ( !fromUpdate ) {
				if ( tower.charge <= 0 )
					frames = Gdx.graphics.getDeltaTime();
				ShotType.GHIULEA.getInitialDir(GR.temp1.set(tower.getDirection()), tower.charge);
				GR.temp4.set(tower.place);
				do {
					ShotType.GHIULEA.makeMove(GR.temp1, GR.temp3, frames);
					GR.temp4.add(GR.temp3);
				} while ( GR.temp4.y > 0 );
				spot.setPosition(GR.temp4.x, 0f, GR.temp4.z);
				opac = 0.2f;
				idle = 2f;
				aftImp = false;
			} else {
				if ( idle >= 0 )
					idle -= Gdx.graphics.getDeltaTime();
				else
					opac -= Gdx.graphics.getDeltaTime() / 5;
				draw = opac > 0;
			}

			if ( tower.charge > 0 ) {
				draw = true;
				spot.setColor(0, 0, 0, MathUtils.clamp(tower.charge, 0.4f, 1));
			} else if ( aftImp ) {
				spot.setColor(1, 0, 0, 0.3f);
				if ( idle < 0 ) {
					opac = 0.3f;
					idle = 2;
					aftImp = false;
					tower.charge = 0;
				}
			} else
				spot.setColor(1, 1, 1, opac);
		}

		@Override
		public void render( ModelBatch modelBatch, Environment light, DecalBatch decalBatch ) {
			if ( draw )
				decalBatch.add(spot);
		}

		@Override
		public boolean fireProjectile( WorldWrapper world, Ray ray, float charge ) {
			if ( System.currentTimeMillis() - fireTime >= fireDellay ) {
				fireTime = System.currentTimeMillis();

				aftImp = true;
				idle = fireDellay / 1000f;

				world.getWorld().addShot(ShotType.GHIULEA, ray, charge);
				return true;
			}
			return false;
		}

		@Override
		public ModelInstance getModelInstance( Vector3 poz ) {
			ModelBuilder build = new ModelBuilder();
			Model sfera = build.createSphere(2, 2, 2, 12, 12, new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
			WorldBase.toDispose.add(sfera);

			return new ModelInstance(sfera, poz);

		}

		@Override
		public FireType getFireType() {
			return FireType.FIRECHARGED;
		}

	},
	MINIGUN {

		private Decal	raza1;
		private Decal	raza2;
		private float	opac		= 0f;
		private boolean	atop		= true;
		private boolean	drawRaza	= false;
		private short	rotation	= 0;

		{
			setPrice(420);
			name = "Mini Gun";
			details = "Small but vicious.";
			raza1 = Decal.newDecal(Garden_Revolution.getPackTexture("pix50"), true);
			raza1.setDimensions(200, 0.3f);
			raza1.setColor(1, 0, 0, 0);
			raza2 = Decal.newDecal(Garden_Revolution.getPackTexture("pix50"), true);
			raza2.setDimensions(200, 0.3f);
			raza2.setColor(0.5f, 0, 0, 0);
		}

		@Override
		public void updateWeaponTargeting( Tower tower, boolean fromUpdate ) {
			GR.temp1.set(tower.place).add(GR.temp2.set(tower.getDirection()).nor().scl(raza1.getWidth() / 2));

			raza1.setPosition(GR.temp1.x, GR.temp1.y, GR.temp1.z);
			raza1.setRotation(tower.getDirection(), GR.temp3.set(0, 1, 0).rotate(tower.getDirection(), rotation));
			raza1.rotateY(90);

			raza2.setPosition(GR.temp1.x, GR.temp1.y, GR.temp1.z);
			raza2.setRotation(tower.getDirection(), GR.temp3.set(0, 1, 0).rotate(tower.getDirection(), rotation + 90));
			raza2.rotateY(90);

			if ( tower.isFiringHold )
				atop = false;

			if ( fromUpdate ) {
				if ( atop ) {
					if ( !tower.isFiringHold )
						opac -= Gdx.graphics.getDeltaTime() * 2.5f;
				} else
					opac += Gdx.graphics.getDeltaTime() * 3.5f;

				drawRaza = true;
				if ( opac > 1 ) {
					opac = 1f;
					atop = true;
				} else if ( opac < 0 ) {
					drawRaza = false;
					opac = 0;
				}
			}
		}

		@Override
		public void render( ModelBatch modelBatch, Environment light, DecalBatch decalBatch ) {
			if ( drawRaza ) {
				decalBatch.add(raza1);
				decalBatch.add(raza2);
				raza1.setColor(1, 0, 0, opac);
				raza2.setColor(0.5f, 0, 0, opac);
			}
		}

		@Override
		public boolean fireProjectile( WorldWrapper world, Ray ray, float charge ) {
			if ( System.currentTimeMillis() - fireTime >= fireDellay ) {
				fireTime = System.currentTimeMillis();

				myray.set(ray);
				GR.temp2.set(MathUtils.random() - 0.5f, MathUtils.random() - 0.5f, MathUtils.random() - 0.5f).scl(2);
				myray.origin.add(GR.temp2);

				rotation += 53;
				rotation %= 360;

				world.getWorld().addShot(ShotType.STANDARD, myray, 0);
				return true;
			}
			return false;
		}

		@Override
		public ModelInstance getModelInstance( Vector3 poz2 ) {
			ModelBuilder build = new ModelBuilder();
			Model sfera = build.createSphere(2, 2, 2, 12, 12, new Material(ColorAttribute.createDiffuse(Color.WHITE)), Usage.Position | Usage.Normal | Usage.TextureCoordinates);
			WorldBase.toDispose.add(sfera);

			return new ModelInstance(sfera, poz2);

		}

		@Override
		public FireType getFireType() {
			return FireType.FIREHOLD;
		}
	};

	private static final Ray	myray		= new Ray(new Vector3(), new Vector3());

	public String				name		= "weapon name";
	public String				details		= "weapon description";
	protected int				fireDellay	= 100;
	protected long				fireTime	= 0;
	public int					cost		= 100;
	public int					value		= 75;

	public void setPrice( int cost ) {
		this.cost = cost;
		this.value = (int) (cost * 0.75f);
	}

	public void render( ModelBatch modelBatch, Environment light, DecalBatch decalBatch ) {}

	public abstract void updateWeaponTargeting( Tower tower, boolean fromUpdate );

	public abstract boolean fireProjectile( WorldWrapper world, Ray ray, float charge );

	public abstract ModelInstance getModelInstance( Vector3 poz );

	public abstract FireType getFireType();

	public static enum FireType {
		FIREHOLD, FIRECHARGED;
	}

}
