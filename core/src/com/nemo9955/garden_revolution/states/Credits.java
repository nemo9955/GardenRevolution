package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.CustomAdapter;

public class Credits extends CustomAdapter implements State<MenuController> {

	private TextButton	back;

	private Table		tab	= new Table(GR.skin);
	{

		back = new TextButton("Back", GR.skin, "demon");
		back.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				MenuController.instance.mst.revertToPreviousState();
			}

		});

		tab.setFillParent(true);

		tab.defaults();

		tab.add("Main Programmer", "arial", Color.DARK_GRAY).row();
		tab.add("nemo9955", "arial", Color.BLACK).row();

		tab.add("3D Models", "arial", Color.DARK_GRAY).padTop(40).row();
		tab.add("Dante", "arial", Color.BLACK).row();

		tab.add(back).padTop(70).row();

		MenuController.instance.stage.addActor(tab);
		tab.setVisible(false);
	}

	@Override
	public void enter( MenuController entity ) {
		tab.setVisible(true);
	}

	@Override
	public void update( MenuController entity ) {}

	@Override
	public void exit( MenuController entity ) {
		tab.setVisible(false);
	}

	@Override
	public boolean onMessage( MenuController entity, Telegram telegram ) {
		return false;
	}

}
