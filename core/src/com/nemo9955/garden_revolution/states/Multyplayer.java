package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;

public class Multyplayer implements State<MenuController> {

	private TextButton	back;

	private FileHandle	level;

	private Table		table;
	public TextField	ipInput;
	public TextField	portInput;

	private Dialog		dialog;
	private TextButton	start;
	private CheckBox	isHost;
	private CheckBox	isPublic;
	private Label		theIP;

	{

		table = new Table(GR.skin);
		start = new TextButton("Start", GR.skin, "demon");
		back = new TextButton("Back", GR.skin, "demon");
		ipInput = new TextField("", GR.skin) {

			@Override
			public float getPrefWidth() {
				return 250;
			}

		};
		portInput = new TextField("", GR.skin) {

			@Override
			public float getPrefWidth() {
				return 100;
			};
		};
		isHost = new CheckBox("Is host", GR.skin);
		isPublic = new CheckBox("Is public", GR.skin);
		theIP = new Label(Func.getIpAddress(), GR.skin);

		portInput.setText("29955");

		makeLayout();

		table.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {

				if ( back.isPressed() )
					MenuController.instance.mst.revertToPreviousState();
				else if ( start.isPressed() ) {
					try {
						Vars.TCPport = Integer.parseInt(portInput.getText());
						Vars.UDPport = Vars.TCPport + 10000;

						if ( isHost.isChecked() )
							startAsHost();
						else
							startAsClient(ipInput.getText());
					}
					catch ( Exception e ) {
						showMessage(e.getMessage());
					}

				}
				if ( isHost.isChecked() ) {
					makeLayout();
				} else {
					makeLayout();
				}

			}

		});
		MenuController.instance.stage.addActor(table);
		table.setVisible(false);

		dialog = new Dialog("You cannot connect !", GR.skin, "error") {

			{
				pad(50);
				button("OK");
			}

			@Override
			protected void result( Object object ) {};
		};

	}

	private void makeLayout() {

		table.clearChildren();

		table.setFillParent(true);
		table.defaults().space(50);
		table.add(start).colspan(2).row();
		table.add(isHost);
		if ( isHost.isChecked() )
			table.add(theIP).size(250, theIP.getHeight());
		else
			table.add(ipInput);
		table.row();
		table.add("Port :");
		table.add(portInput).row();
		table.add(isPublic).colspan(2).row();
		table.add(back).colspan(2).row();

		table.invalidateHierarchy();
	}

	public Multyplayer init( String level ) {
		this.level = Gdx.files.internal(GR.levelsLocation).child(level + ".xml");
		return this;

	}

	private void startAsClient( String textIP ) {
		GR.gameplay.initAsClient(textIP);
	}

	private void startAsHost() {
		GR.gameplay.initAsHost(level);
	}

	public void showMessage( String message ) {
		dialog.setTitle(message);
		dialog.show(MenuController.instance.stage);
	}

	@Override
	public void enter( MenuController entity ) {
		ipInput.setText("188.173.17.234");
		table.setVisible(true);
	}

	@Override
	public void update( MenuController entity ) {}

	@Override
	public void exit( MenuController entity ) {
		table.setVisible(false);
	}

	@Override
	public boolean onMessage( MenuController entity, Telegram telegram ) {
		return false;
	}

}
