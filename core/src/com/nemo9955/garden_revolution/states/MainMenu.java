package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;

public class MainMenu extends CustomAdapter implements State<MenuController> {

	private TextButton	play;

	private Table		tab;

	public MainMenu() {

		final TextButton options = new TextButton("Options", GR.skin, "earthDemon");
		final TextButton sdr = new TextButton("Shader", GR.skin, "earthDemon");
		/*-------------*/play = new TextButton("Play", GR.skin, "earthDemon");
		final TextButton exit = new TextButton("Exit", GR.skin, "earthDemon");
		final TextButton credits = new TextButton("Credits", GR.skin, "earthDemon");

		tab = new Table(GR.skin);
		tab.setFillParent(true);

		ChangeListener asc = new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				if ( play.isPressed() )
					MenuController.instance.mst.changeState(GR.selecter);

				if ( credits.isPressed() )
					MenuController.instance.mst.changeState(GR.credits);

				if ( options.isPressed() )
					MenuController.instance.mst.changeState(GR.options);
				// GR.game.setScreen(GR.options);

				if ( exit.isPressed() )
					Gdx.app.exit();

				if ( sdr.isPressed() && Func.isDesktop() )
					GR.game.setScreen(new ShaderTest());

				// if ( mode.isPressed() ) {
				// LevelSelector.internal = mode.isChecked();
				// }
			}
		};

		tab.addListener(asc);

		tab.defaults().pad(20);
		tab.add(play).bottom().colspan(2).expand();
		tab.row();
		tab.add(sdr).right();
		tab.add(options).left();
		tab.row();
		tab.add(exit).expand().align(Align.left | Align.bottom);
		tab.add(credits).expand().align(Align.right | Align.bottom);

		MenuController.instance.stage.addActor(tab);
		tab.setVisible(false);

	}

	@Override
	public void enter( MenuController entity ) {

		entity.pointer.setSelectedActor(play);
		tab.setVisible(true);

	}

	@Override
	public void update( MenuController entity ) {

	}

	@Override
	public void exit( MenuController entity ) {
		tab.setVisible(false);
	}

	@Override
	public boolean onMessage( MenuController entity, Telegram telegram ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyDown( int keycode ) {
		switch ( keycode ) {
			case Keys.ESCAPE :
			case Keys.BACK :
				Gdx.app.exit();
				break;
		}
		return false;
	}

}
