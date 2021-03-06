package com.nemo9955.garden_revolution.game.enumTypes;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Assets;

public enum EnemyType {

	ALUNA("Peanut") {

		{
			life = 10;
			speed = 10;
			value = 25;
		}
	},

	ROSIE("Tomato") {

		{
			life = 70;
			speed = 7;
			value = 65;
			strenght = 15;
		}
	},

	MORCOV("Carrot") {

		{
			life = 150;
			speed = 4;
			value = 80;
			strenght = 2;
		}
	};

	ModelInstance	model;
	public String	name		= "Plant";
	public int		life		= 50;
	public float	speed		= 8;
	public int		value		= 50;
	public int		strenght	= 5;

	EnemyType(String fileName) {
		name = name().charAt(0) + "" + name().replaceFirst(name().charAt(0) + "", "").toLowerCase();

		ModelInstance tmp = new ModelInstance(Garden_Revolution.getModel(Assets.ENEMYS));
		Array<Node> toRemove = new Array<Node>(false, 1);
		for (int i = 0; i < tmp.nodes.size; i++)
			if ( !tmp.nodes.get(i).id.equalsIgnoreCase(fileName) )
				toRemove.add(tmp.nodes.get(i));
		tmp.nodes.removeAll(toRemove, false);
		model = tmp;
	}

	public ModelInstance getModel( float x, float y, float z ) {
		model.transform.setToTranslation(x, y, z);
		return new ModelInstance(model);
	}

	public static EnemyType getRandomEnemy() {
		return EnemyType.values()[MathUtils.random(EnemyType.values().length - 1)];
	}

}
