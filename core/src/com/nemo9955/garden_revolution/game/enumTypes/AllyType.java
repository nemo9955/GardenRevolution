package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;

public enum AllyType {

	SOLDIER("Derp_potato") {

		@Override
		protected void propAllys() {

		}
	};

	public ModelInstance	model;
	public int				strenght	= 7;
	public float			speed		= 8;
	public String			name		= "Potato";
	public int				life		= 100;

	AllyType(String fileName) {
		name = name().charAt(0) + "" + name().replaceFirst(name().charAt(0) + "", "").toLowerCase();
		propAllys();

		ModelInstance tmpModI = new ModelInstance(Garden_Revolution.getModel(Assets.ALLIES));
		Array<Node> toRemove = new Array<Node>(false, 1);
		for (int i = 0; i < tmpModI.nodes.size; i++)
			if ( !tmpModI.nodes.get(i).id.equalsIgnoreCase(fileName) )
				toRemove.add(tmpModI.nodes.get(i));
		tmpModI.nodes.removeAll(toRemove, false);
		model = tmpModI;
	}

	public ModelInstance getModel( float x, float y, float z ) {
		model.transform.setToTranslation(x, y, z);
		ModelInstance modelInstance = new ModelInstance(model);
		modelInstance.transform.rotate(0, 1, 0, MathUtils.random(359));
		return modelInstance;
	}

	protected abstract void propAllys();

}
