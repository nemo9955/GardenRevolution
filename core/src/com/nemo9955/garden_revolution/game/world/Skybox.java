package com.nemo9955.garden_revolution.game.world;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Assets;

public class Skybox {

	private int			size	= 30;
	private float		speed	= 1;

	private Decal[]		faces	= new Decal[6];
	private DecalBatch	decb;
	private Assets		aset;

	Vector3				temp	= new Vector3();
	Vector3				vecx	= new Vector3(1, 0, 0);
	Vector3				vecy	= new Vector3(0, 1, 0);

	public Skybox(Assets cubemap, PerspectiveCamera cam) {
		aset = cubemap;
		decb = new DecalBatch(new CameraGroupStrategy(cam));
		for (int i = 0; i < 6; i++)
			faces[i] = Decal.newDecal(getSize() * 2, getSize() * 2, new TextureRegion(GR.manager.get(aset.getAstPath() + aset.getElements()[i], Texture.class)), false);
		makeFaces();
	}

	public Skybox(DecalBatch db, Assets cubemap) {
		aset = cubemap;
		decb = db;
		for (int i = 0; i < 6; i++)
			faces[i] = Decal.newDecal(getSize() * 2, getSize() * 2, new TextureRegion(GR.manager.get(aset.getAstPath() + aset.getElements()[i], Texture.class)), false);
		makeFaces();
	}

	private void makeFaces() {

		for (int i = 0; i < 6; i++) {
			faces[i].setWidth(size * 2);
			faces[i].setHeight(size * 2);
		}
		setCorectRotation();
		setAroundCentre(Vector3.Zero);

	}

	private void setCorectRotation() {

		faces[0].setRotationY(270);
		faces[1].setRotationY(90);

		faces[2].setRotationX(90);
		faces[3].setRotationX(270);

		faces[4].setRotationY(0);
		faces[5].setRotationY(180);

	}

	public void render( float delta, Vector3 pos ) {
		rotate(delta);
		setAroundCentre(pos);

		for (Decal dec : faces)
			decb.add(dec);

		decb.flush();
	}

	private void rotate( float delta ) {
		vecx.rotate(delta * speed, 0, 0, 1);
		vecy.rotate(-delta * speed, 0, 0, 1);

		faces[0].rotateX(delta * speed);
		faces[1].rotateX(-delta * speed);

		faces[2].rotateY(delta * speed);
		faces[3].rotateY(-delta * speed);

		faces[4].rotateZ(delta * speed);
		faces[5].rotateZ(-delta * speed);
	}

	private void setAroundCentre( Vector3 poz ) {
		faces[0].setPosition(temp.set(vecx).scl(getSize(), getSize(), 0).add(poz));
		faces[1].setPosition(temp.set(vecx).scl(-getSize(), -getSize(), 0).add(poz));

		faces[2].setPosition(temp.set(vecy).scl(-getSize(), getSize(), 0).add(poz));
		faces[3].setPosition(temp.set(vecy).scl(getSize(), -getSize(), 0).add(poz));

		faces[5].setPosition(temp.set(0, 0, getSize()).add(poz));
		faces[4].setPosition(temp.set(0, 0, -getSize()).add(poz));
	}

	public int getSize() {
		return size;
	}

	public void setSize( int size ) {
		this.size = size;
		makeFaces();
	}

}
