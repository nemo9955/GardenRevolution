package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;

public class Options extends ControllerAdapter implements State<MenuController> {

	private TextButton	back;

	private TextButton	current;
	private boolean		butSelected	= false;
	private String		remName;

	private Label		opt;
	private ScrollPane	pane;

	public Options() {

		back = new TextButton("Back", GR.skin, "demon");

		final Table table = new Table(GR.skin);
		opt = new Label("Options", GR.skin, "clover", "white");

		final Slider contSensX = new Slider(1, 10, 0.1f, false, GR.skin) {

			@Override
			public float getPrefWidth() {
				return 300;
			}
		};
		contSensX.setValue(Vars.multiplyControlletX);
		contSensX.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				Vars.multiplyControlletX = contSensX.getValue();
				pane.cancel();
			}
		});

		final Slider contSensY = new Slider(1, 10, 0.1f, false, GR.skin) {

			@Override
			public float getPrefWidth() {
				return 300;
			}
		};
		contSensY.setValue(Vars.multiplyControlletY);
		contSensY.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				Vars.multiplyControlletY = contSensY.getValue();
				pane.cancel();
			}
		});

		final Slider camSensX = new Slider(0.2f, 4, 0.1f, false, GR.skin) {

			@Override
			public float getPrefWidth() {
				return 300;
			}
		};
		camSensX.setValue(Vars.modCamSpeedX);
		camSensX.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				Vars.modCamSpeedX = camSensX.getValue();
				pane.cancel();
			}
		});

		final Slider camSensY = new Slider(0.2f, 4, 0.1f, false, GR.skin) {

			@Override
			public float getPrefWidth() {
				return 300;
			}
		};
		camSensY.setValue(Vars.modCamSpeedY);
		camSensY.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				Vars.modCamSpeedY = camSensY.getValue();
				pane.cancel();
			}
		});

		final CheckBox invMX = new CheckBox("Invert Drag X", GR.skin);
		final CheckBox invMY = new CheckBox("Invert Drag Y", GR.skin);
		final CheckBox invPX = new CheckBox("Invert TouchPad X", GR.skin);
		final CheckBox invPY = new CheckBox("Invert TouchPad Y", GR.skin);
		final CheckBox invCX = new CheckBox("Invert Controller X", GR.skin);
		final CheckBox invCY = new CheckBox("Invert Controller Y", GR.skin);

		ChangeListener invButtons = new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {
				if ( invMX.isPressed() )
					Vars.invertDragX = (byte) (invMX.isChecked() ? -1 : 1);
				else if ( invMY.isPressed() )
					Vars.invertDragY = (byte) (invMY.isChecked() ? -1 : 1);
				else if ( invPX.isPressed() )
					Vars.invertPadX = (byte) (invPX.isChecked() ? -1 : 1);
				else if ( invPY.isPressed() )
					Vars.invertPadY = (byte) (invPY.isChecked() ? -1 : 1);
				else if ( invCX.isPressed() )
					Vars.invertControllerX = (byte) (invCX.isChecked() ? -1 : 1);
				else if ( invCY.isPressed() )
					Vars.invertControllerY = (byte) (invCY.isChecked() ? -1 : 1);
			}
		};

		invMX.setChecked(Vars.invertDragX < 0);
		invMX.addListener(invButtons);
		invMY.setChecked(Vars.invertDragY < 0);
		invMY.addListener(invButtons);

		invPX.setChecked(Vars.invertPadX < 0);
		invPX.addListener(invButtons);
		invPY.setChecked(Vars.invertPadY < 0);
		invPY.addListener(invButtons);

		invCX.setChecked(Vars.invertControllerX < 0);
		invCX.addListener(invButtons);
		invCY.setChecked(Vars.invertControllerY < 0);
		invCY.addListener(invButtons);

		// ----------------------------------------------------------------------------------------------------
		final byte space = 30, verSim = 10, categ = 50;
		table.defaults().padLeft(space).padBottom(space);
		table.add(opt).colspan(2).pad(40).row();

		table.add(invMX).colspan(2).row();
		table.add(invMY).colspan(2).row();
		table.add(invPX).colspan(2).row();
		table.add(invPY).colspan(2).row();
		table.add(invCX).colspan(2).row();
		table.add(invCY).padBottom(categ).colspan(2).row();

		table.add("Camera X sensivity").padBottom(verSim).colspan(2).row();
		table.add(camSensX).colspan(2).row();
		table.add("Camera Y sensivity").padBottom(verSim).colspan(2).row();
		table.add(camSensY).colspan(2).row();

		table.add("Controller X sensivity").padBottom(verSim).colspan(2).row();
		table.add(contSensX).colspan(2).row();
		table.add("Controller Y sensivity").padBottom(verSim).colspan(2).row();
		table.add(contSensY).padBottom(categ).colspan(2).row();

		table.add("Controller Input", "clover", "white").padBottom(categ).colspan(2).row();

		for (int i = 0; i < Vars.noButtons; i++) {
			TextButton button = new TextButton("Button " + CoButt.values()[i].id, GR.skin, "arial");
			button.setUserObject("Button" + Vars.stringSeparator + i + Vars.stringSeparator + "Controller");
			button.getLabel().setTouchable(Touchable.disabled);

			table.add(new Label(CoButt.values()[i].name, GR.skin));
			table.add(button);
			table.row();
		}

		for (int i = 0; i < Vars.noAxis; i++) {
			TextButton axis = new TextButton("Axis " + CoAxis.values()[i].id, GR.skin, "arial");
			axis.setUserObject("Axis" + Vars.stringSeparator + i + Vars.stringSeparator + "Controller");
			axis.getLabel().setTouchable(Touchable.disabled);

			table.add(new Label(CoAxis.values()[i].name, GR.skin));
			table.add(axis);
			table.row();
		}

		table.add(back).colspan(2);

		pane = new ScrollPane(table, GR.skin, "clear");
		pane.setFillParent(true);
		// table.setClip(false);

		pane.addListener(new ChangeListener() {

			@Override
			public void changed( ChangeEvent event, Actor actor ) {

				if ( back.equals(actor) )
					MenuController.instance.mst.revertToPreviousState();
				// GR.game.setScreen(GR.menu);
				// FIXME need to remake the conditions in the controller !!!
				if ( actor.getUserObject() != null && actor.getUserObject().toString().contains("Controller") ) {
					if ( butSelected && current != null ) {
						current.setText(remName);
						current.invalidateHierarchy();
					}

					if ( current != null && current.equals(actor) && butSelected ) {
						current.setText("None");
						current.invalidateHierarchy();
						butSelected = false;
						current = null;
					} else if ( current == null || !current.equals(actor) ) {
						current = (TextButton) actor;
						butSelected = true;
						remName = current.getText().toString();

						if ( current.getUserObject().toString().contains("Button") )
							current.setText("Press a button ...");
						else
							current.setText("Move an axis ...");
						current.invalidateHierarchy();

					}

				}

			}
		});

		pane.addListener(new InputListener() {

			@Override
			public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
				Actor actor = MenuController.instance.stage.hit(x, y, true);
				if ( !(actor.getUserObject() != null && actor.getUserObject().toString().contains("Controller")) )
					if ( butSelected ) {
						current.setText(remName);
						current.invalidateHierarchy();
						butSelected = false;
						current = null;
					}

				return false;
			}
		});
		MenuController.instance.stage.addActor(pane);
		pane.setVisible(false);
	}

	@Override
	public boolean buttonDown( Controller controller, int buttonIndex ) {

		if ( butSelected ) {
			String[] parts = current.getUserObject().toString().split(Vars.stringSeparator);

			if ( parts[0].contains("Button") ) {
				CoButt.values()[Integer.parseInt(parts[1])].id = buttonIndex;
				current.setText("Button " + buttonIndex);
				current.invalidateHierarchy();
				current = null;
				butSelected = false;
			}
			return true;
		}
		return false;

	}

	@Override
	public boolean axisMoved( Controller controller, int axisCode, float value ) {
		if ( butSelected && Math.abs(value) > 0.2f ) {
			String[] parts = current.getUserObject().toString().split(Vars.stringSeparator);

			if ( parts[0].contains("Axis") ) {
				CoAxis.values()[Integer.parseInt(parts[1])].id = axisCode;
				current.setText("Axis " + axisCode);
				current.invalidateHierarchy();
				current = null;
				butSelected = false;
			}
			return false;
		}

		return false;
	}

	@Override
	public void enter( MenuController entity ) {
		entity.pointer.setSelectedActor(opt);
		pane.setVisible(true);
		MenuController.instance.stage.setScrollFocus(pane);
	}

	@Override
	public void update( MenuController entity ) {

	}

	@Override
	public void exit( MenuController entity ) {

		pane.setVisible(false);
	}

	@Override
	public boolean onMessage( MenuController entity, Telegram telegram ) {
		return false;
	}

}
