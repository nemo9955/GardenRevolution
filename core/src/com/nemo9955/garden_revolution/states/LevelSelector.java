package com.nemo9955.garden_revolution.states;

import java.util.Arrays;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;

public class LevelSelector extends ControllerAdapter implements State<MenuController> {

	private Table			table	= new Table(GR.skin);
	private String			toAcces;
	private SplitPane		lista;
	private TextButton		start;
	private List<String>	elem;
	private TextButton		back;

	{

		table.setHeight(MenuController.instance.stage.getHeight());
		// table.clear();

		// if ( lista != null )
		// lista.clear();

		String harti[] = null;

		if ( Gdx.app.getType() == ApplicationType.Desktop ) {
			// System.out.println( Gdx.files.internal( "harti/lista nivele.txt"
			// ).readString() );
			try {
				harti = Gdx.files.classpath(GR.levelsLocation).readString().split(".xml\n");
				// for (String str : harti )
				// System.out.println( str );
			}
			catch ( Exception e ) {
				harti = Gdx.files.internal("harti/lista nivele.txt").readString().split("\n");
				// new String[] { "1aaa level", "a1 nivelul 1" };
			}
		} else if ( Gdx.app.getType() == ApplicationType.Android ) {
			FileHandle[] nivele;
			nivele = Gdx.files.internal(GR.levelsLocation).list();
			harti = new String[nivele.length];
			for (int i = 0; i < harti.length; i++)
				harti[i] = nivele[i].nameWithoutExtension();
		}

		Arrays.sort(harti);

		start = new TextButton("Start", GR.skin, "earthDemon");
		elem = new List<String>(GR.skin);
		elem.setItems(harti);
		final TextButton multy = new TextButton("Multiplayer", GR.skin, "earthDemon");

		back = new TextButton("Back", GR.skin);

		table.add("Select a LEVEL", "arial", Color.MAROON).expand().top().row();
		table.add(start).expand().row();
		table.add(multy).expand().row();
		table.add(back).bottom().expand().right();

		lista = new SplitPane(elem, table, false, GR.skin, "vine");
		lista.setFillParent(true);
		lista.setMaxSplitAmount(0.5f);
		lista.setMinSplitAmount(0.3f);
		lista.setSplitAmount(0.4f);

		elem.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				@SuppressWarnings("unchecked")
				List<String> lst = (List<String>) actor;
				toAcces = lst.getSelected();
			}
		});

		toAcces = elem.getSelected();
		table.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				if ( multy.isPressed() )
					MenuController.instance.mst.changeState(GR.multyplayer.init(toAcces));
				else if ( back.isPressed() )
					MenuController.instance.mst.revertToPreviousState();
				else if ( start.isPressed() )
					GR.game.setScreen(GR.gameplay.initAsSinglePlayer(Gdx.files.internal(GR.levelsLocation).child(toAcces + ".xml")));
			}
		});
		MenuController.instance.stage.addActor(lista);
		lista.setVisible(false);
	}

	@Override
	public void enter( MenuController entity ) {

		MenuController.instance.pointer.setSelectedActor(start);
		lista.setVisible(true);
	}

	@Override
	public void update( MenuController entity ) {}

	@Override
	public void exit( MenuController entity ) {
		lista.setVisible(false);
	}

	@Override
	public boolean onMessage( MenuController entity, Telegram telegram ) {
		return false;
	}

	@Override
	public boolean povMoved( Controller controller, int povIndex, PovDirection value ) {
		if ( value == PovDirection.south ) {
			int index = elem.getSelectedIndex() + 1;
			if ( index >= elem.getItems().size )
				index = 0;
			elem.setSelectedIndex(index);
		}
		if ( value == PovDirection.north ) {
			int index = elem.getSelectedIndex() - 1;
			if ( index < 0 )
				index = elem.getItems().size - 1;
			elem.setSelectedIndex(index);
		}
		return false;

	}

}
