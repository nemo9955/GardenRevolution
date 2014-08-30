package com.nemo9955.garden_revolution.utility.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.enumTypes.TowerType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.net.packets.Packets.msNetGR;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.MenuController;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;

public class GameStageMaker {

	private static Board			hudGroup			= new Board();
	public static ImageButton		hudPauseBut			= new ImageButton(GR.skin, "IGpause");
	public static ImageButton		hudTowerBut			= new ImageButton(GR.skin, "towerUpgrade");
	public static Label				hudViataTurn		= new Label("Life ", GR.skin);
	public static Touchpad			hudMover			= new Touchpad(1, GR.skin);
	public static Image				hudWeaponCharger	= new Image(GR.skin, "mover-knob");
	public static TextButton		hudReady			= new TextButton("Start Wave!", GR.skin, "arial");
	public static Image				hudAllyPlacer		= new Image(GR.skin, "allyPlacer");

	private static Group			tuGroup				= new Group();
	private static TextButton		tuBackBut1			= new TextButton("Bk", GR.skin);
	private static TextButton		tuBackBut2			= new TextButton("Bk", GR.skin);
	private static TextButton		tuBasicBut			= new TextButton("BASIC", GR.skin, "arial");
	private static ImageButton		tuMiniGunBut		= new ImageButton(IconType.SAGETI.getAsDrawable(GR.skin, 70, 70));
	private static ImageButton		tuCannonBut			= new ImageButton(IconType.TUN.getAsDrawable(GR.skin, 70, 70));
	public static Label				tuMoneyMeter		= new Label("Money ----", GR.skin, "clover", Color.YELLOW);
	private static CircularGroup	tuTowerCircG;

	private static Table			optTab				= new Table();
	private static ScrollPane		optScrollP			= new ScrollPane(optTab, GR.skin);
	private static TextButton		optBackBut			= new TextButton("Back", GR.skin, "demon");
	private static CheckBox			optAutUpdBut		= new CheckBox("Auto-Update Wave", GR.skin);
	private static CheckBox			optShDebugBut		= new CheckBox("Show Debug", GR.skin);
	private static CheckBox			optInvMX			= new CheckBox("Invert Drag X", GR.skin);
	private static CheckBox			optInvMY			= new CheckBox("Invert Drag Y", GR.skin);
	private static CheckBox			optInvPX			= new CheckBox("Invert TouchPad X", GR.skin);
	private static CheckBox			optInvPY			= new CheckBox("Invert TouchPad Y", GR.skin);
	private static Label			optFOVmeter			= new Label("Camera FOV 60", GR.skin);

	private static Table			pauseTab			= new Table(GR.skin);
	private static TextButton		pauseResumeBut		= new TextButton("Resume play", GR.skin, "arial");
	private static TextButton		pauseOptBut			= new TextButton("Options", GR.skin, "arial");
	private static TextButton		pauseMMenuBut		= new TextButton("Main menu", GR.skin, "arial");

	public static float				ASAtimer			= 0;
	public static boolean			showASA				= false;
	public static final Vector3		ASAonPath			= new Vector3();
	public static Decal				allySpawnArea		= Decal.newDecal(20, 20, Garden_Revolution.getPackTexture("mover-bg"), true);

	public static StageActorPointer	pointer;
	public static Label				fps					= new Label("FPS: ", GR.skin);
	private static Gameplay			gp;

	public static void makeGamePlayStage( Gameplay gpt ) {
		gp = gpt;
		// gp.stage.setViewport( new StretchViewport ( Gdx.graphics.getWidth()
		// *1.5f /Vars.densitate, Gdx.graphics.getHeight() *1.5f /Vars.densitate
		// , gp.player.getCamera()) );
		// gp.stage.setViewport();

		// gp.stage.getViewport().update();

		// clearStageRecurs( gp.stage.getRoot() );
		// gp.stage.clear();

		createHUD();

		createIGPause();

		createIGoptions();

		createTowerUpgrade();

		hudGroup.setName("HUD");
		tuGroup.setName("Tower Upgrade");
		optScrollP.setName("Optiuni");
		pauseTab.setName("Pause");

		hudPauseBut.setName("Back");
		optBackBut.setName("Back");
		tuBackBut1.setName("Back");
		pauseMMenuBut.setName("Back");
		gp.stage.addActor(hudGroup);
		gp.stage.addActor(tuGroup);
		gp.stage.addActor(optScrollP);
		gp.stage.addActor(pauseTab);
		gp.stage.addActor(fps);

	}

	public static void restartStage() {

		hudGroup.setVisible(true);
		hudWeaponCharger.setVisible(false);
		hudReady.setVisible(true);
		hudReady.setText("Start Wave!");

		tuGroup.setVisible(false);

		optScrollP.setVisible(false);

		pauseTab.setVisible(false);
	}

	public static void resizeStage( int width, int height ) {
		fps.setPosition(gp.stage.getWidth() / 2, gp.stage.getHeight() - fps.getHeight());
		hudAllyPlacer.setPosition(gp.stage.getWidth() * 0.25f, 0);
		hudReady.setPosition(gp.stage.getWidth() / 2, 0);
		hudTowerBut.setPosition(gp.stage.getWidth() - hudTowerBut.getWidth(), 0);
		hudViataTurn.setFontScale(0.8f);
		hudViataTurn.setPosition(10, gp.stage.getHeight() - hudViataTurn.getHeight() - 10);
		hudPauseBut.setPosition(gp.stage.getWidth() - hudPauseBut.getWidth(), gp.stage.getHeight() - hudPauseBut.getHeight());
		hudMover.setPosition(gp.stage.getWidth() * 0.02f, gp.stage.getWidth() * 0.02f);

		float freeSpace = 25;
		tuTowerCircG.setAsCircle(height / 2, 70);
		tuTowerCircG.setPosition(-tuTowerCircG.getRadius() + (freeSpace * 2.1f) + tuBackBut1.getWidth(), gp.stage.getHeight() / 2);

		final float treapta = CircularGroup.aprxOptAngl(tuTowerCircG.getRadius(), tuBasicBut.getHeight());
		float i = tuTowerCircG.minAngleInZon(Func.getStageZon(gp.stage), treapta, 2);
		tuTowerCircG.setActivInterval(i, 360 - i, true, treapta * 1.4f, false);

		tuBackBut1.setPosition(15, gp.stage.getHeight() / 2 - tuBackBut1.getHeight() / 2);
		tuBackBut2.setPosition(15, gp.stage.getHeight() / 2 - tuBackBut2.getHeight() / 2);

		optScrollP.setBounds(gp.stage.getHeight() * 0.1f, gp.stage.getHeight() * 0.1f, gp.stage.getWidth() - gp.stage.getHeight() * 0.2f, gp.stage.getHeight() * 0.8f);
		tuMoneyMeter.setPosition(5, 5);
	}

	private static void createHUD() {

		// gp.fps.setScale( Vars.densitate );

		hudAllyPlacer.setScale(1.25f);

		hudMover.addAction(Actions.alpha(Vars.tPadMinAlpha));

		hudGroup.addActor(hudReady);
		hudGroup.addActor(hudViataTurn);
		hudGroup.addActor(hudPauseBut);
		hudGroup.addActor(hudMover);
		hudGroup.addActor(hudTowerBut);
		hudGroup.addActor(hudWeaponCharger);
		hudGroup.addActor(hudAllyPlacer);

		hudGroup.addListener(hudListener);

		hudAllyPlacer.addListener(allyPlacerListener);

		hudMover.addListener(moverListener);

	}

	private static InputListener	allyPlacerListener	= new InputListener() {

															private final Vector2	tmp	= new Vector2();

															@Override
															public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
																if ( ASAtimer <= 0 ) {
																	showASA = true;
																	updatePoz(x, y);
																	return true;
																}
																return false;
															}

															@Override
															public void touchDragged( InputEvent event, float x, float y, int pointer ) {
																updatePoz(x, y);
															}

															@Override
															public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
																showASA = false;
																hudAllyPlacer.setPosition(gp.stage.getWidth() * 0.25f, 0);

																// tmp.set( x, y
																// );
																// gp.allyPlacer.localToParentCoordinates(
																// tmp );
																// if (
																// event.getStageX()
																// >0
																// &&event.getStageX()
																// <gp.stage.getWidth()
																// &&event.getStageY()
																// >0v
																// &&event.getStageY()
																// <gp.stage.getHeight()
																// ) {

																if ( Func.getStageShrink(gp.stage, 0.9f, 0.9f).contains(event.getStageX(), event.getStageY()) ) {
																	gp.placeAllies();
																}
																// System.out.println(
																// tmp.x +" "
																// +tmp.y );
															}

															private void updatePoz( float x, float y ) {
																tmp.set(x, y);
																hudAllyPlacer.localToParentCoordinates(tmp);
																tmp.sub(hudAllyPlacer.getWidth() / 2, hudAllyPlacer.getHeight() / 2);
																hudAllyPlacer.setPosition(tmp.x, tmp.y);

																tmp.add(hudAllyPlacer.getWidth() / 2, hudAllyPlacer.getHeight() / 2);
																gp.stage.stageToScreenCoordinates(tmp);
																Func.intersectLinePlane(gp.player.getCamera().getPickRay(tmp.x, tmp.y), GR.temp4);
																WorldWrapper.instance.getWorld().getOnPath(GR.temp4, ASAonPath, 150);
																allySpawnArea.setPosition(ASAonPath.x, 0.2f, ASAonPath.z);
															}
														};
	private static ChangeListener	moverListener		= new ChangeListener() {

															@Override
															public void changed( final ChangeEvent event, final Actor actor ) {
																gp.movex = Vars.invertPadX * hudMover.getKnobPercentX() * Vars.modCamSpeedX;
																gp.movey = Vars.invertPadY * hudMover.getKnobPercentY() * Vars.modCamSpeedY;
																gp.touchPadTimmer = Vars.tPadAlphaDellay;
																hudMover.addAction(Actions.alpha(1));
															}
														};
	private static ChangeListener	hudListener			= new ChangeListener() {

															@Override
															public void changed( final ChangeEvent event, final Actor actor ) {
																if ( hudPauseBut.isPressed() ) {
																	hudGroup.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false)));
																	pauseTab.addAction(Actions.sequence(Actions.alpha(0), Actions.visible(true), Actions.delay(0.2f), Actions.alpha(1, 0.5f)));
																	gp.updWorld = false;
																	pointer.setVisible(true);
																	pointer.setSelectedActor(pauseResumeBut);
																} else if ( hudTowerBut.isPressed() ) {
																	hudGroup.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false)));
																	tuGroup.addAction(Actions.sequence(Actions.alpha(0), Actions.visible(true), Actions.delay(0.2f), Actions.alpha(1, 0.5f)));
																	gp.updWorld = false;
																	pointer.setVisible(true);
																	pointer.setSelectedActor(tuBasicBut);
																} else if ( hudReady.isPressed() ) {
																	// singleplayer
																	// part
																	if ( GR.mp == null ) {
																		hudReady.setVisible(false);
																		WorldWrapper.instance.getSgPl().setCanWaveStart(true);
																	}
																	// multyplayer
																	// part
																	else {
																		hudReady.setText("Waiting for others ...");
																		hudReady.setTouchable(Touchable.disabled);

																		GR.mp.sendTCP(msNetGR.IAmReady);

																	}
																	hudReady.pack();
																}

															}
														};

	private static void createTowerUpgrade() {

		tuTowerCircG = new CircularGroup(gp.shape);
		tuTowerCircG.setDraggable(true);

		tuTowerCircG.addActor(tuBasicBut);
		tuTowerCircG.addActor(tuMiniGunBut);
		tuTowerCircG.addActor(tuCannonBut);

		tuMoneyMeter.setFontScale(0.7f);

		tuGroup.addActor(tuMoneyMeter);
		tuGroup.addActor(tuBackBut1);
		tuGroup.addActor(tuTowerCircG);
		tuGroup.addActor(tuBackBut2);

		tuGroup.addListener(towerUpListener);
	}

	private static ChangeListener	towerUpListener	= new ChangeListener() {

														@Override
														public void changed( final ChangeEvent event, final Actor actor ) {
															if ( tuBackBut1.isPressed() || tuBackBut2.isPressed() ) {
																tuGroup.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false)));
																hudGroup.addAction(Actions.sequence(Actions.alpha(0), Actions.visible(true), Actions.delay(0.2f), Actions.alpha(1, 0.5f)));
																gp.updWorld = true;
																pointer.setVisible(false);
															} else if ( tuBasicBut.isPressed() )
																gp.player.upgradeCurentTower(TowerType.BASIC);
															else if ( tuMiniGunBut.isPressed() )
																gp.player.changeCurrentWeapon(WeaponType.MINIGUN);
															else if ( tuCannonBut.isPressed() )
																gp.player.changeCurrentWeapon(WeaponType.CANNON);

														}
													};

	private static void createIGoptions() {

		optAutUpdBut.setChecked(Vars.updateUave);
		optShDebugBut.setChecked(Vars.showDebug);

		optInvMX.setChecked(Vars.invertDragX < 0);
		optInvMX.addListener(invButtons);
		optInvMY.setChecked(Vars.invertDragY < 0);
		optInvMY.addListener(invButtons);

		optInvPX.setChecked(Vars.invertPadX < 0);
		optInvPX.addListener(invButtons);
		optInvPY.setChecked(Vars.invertPadY < 0);
		optInvPY.addListener(invButtons);

		optCamFOV.setValue(60);
		optCamFOV.addListener(valueListener);

		optTab.defaults().pad(20);
		optTab.add(optAutUpdBut).row();
		optTab.add(optShDebugBut).row();
		optTab.add(optInvMX).row();
		optTab.add(optInvMY).row();
		optTab.add(optInvPX).row();
		optTab.add(optInvPY).row();
		optTab.add(optFOVmeter).padBottom(3).row();
		optTab.add(optCamFOV).padTop(0).row();
		optTab.add(optBackBut).row();

		optScrollP.setWidget(optTab);

		optScrollP.addListener(optScrollListener);
	}

	private static Slider			optCamFOV			= new Slider(20, 140, 1, false, GR.skin) {

															@Override
															public float getPrefWidth() {
																return 300;
															}
														};

	private static ChangeListener	valueListener		= new ChangeListener() {

															@Override
															public void changed( ChangeEvent event, Actor actor ) {
																gp.player.getCamera().fieldOfView = optCamFOV.getValue();
																gp.player.getCamera().update();
																optFOVmeter.setText("Camera FOV " + (int) optCamFOV.getValue());
																optScrollP.cancel();
															}
														};

	private static ChangeListener	optScrollListener	= new ChangeListener() {

															@Override
															public void changed( final ChangeEvent event, final Actor actor ) {
																if ( optBackBut.isPressed() ) {
																	optScrollP.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false)));
																	pauseTab.addAction(Actions.sequence(Actions.alpha(0), Actions.visible(true), Actions.delay(0.2f), Actions.alpha(1, 0.5f)));
																} else if ( optAutUpdBut.isPressed() ) {
																	Vars.updateUave = optAutUpdBut.isChecked();
																} else if ( optShDebugBut.isPressed() ) {
																	Vars.showDebug = optShDebugBut.isChecked();
																}
															}
														};

	private static ChangeListener	invButtons			= new ChangeListener() {

															@Override
															public void changed( ChangeEvent event, Actor actor ) {
																if ( optInvMX.isPressed() )
																	Vars.invertDragX = (byte) (optInvMX.isChecked() ? -1 : 1);
																else if ( optInvMY.isPressed() )
																	Vars.invertDragY = (byte) (optInvMY.isChecked() ? -1 : 1);
																else if ( optInvPX.isPressed() )
																	Vars.invertPadX = (byte) (optInvPX.isChecked() ? -1 : 1);
																else if ( optInvPY.isPressed() )
																	Vars.invertPadY = (byte) (optInvPY.isChecked() ? -1 : 1);
															}
														};

	private static void createIGPause() {

		pauseTab.setFillParent(true);
		pauseTab.setBackground("pix30");
		pauseTab.add("PAUSE", "clover", Color.GREEN).padBottom(gp.stage.getHeight() * 0.1f);
		pauseTab.row();
		pauseTab.add(pauseResumeBut).padBottom(gp.stage.getHeight() * 0.07f);
		pauseTab.row();
		pauseTab.add(pauseOptBut).padBottom(gp.stage.getHeight() * 0.07f);
		pauseTab.row();
		pauseTab.add(pauseMMenuBut);

		pauseTab.addListener(pauseListener);

	}

	private static ChangeListener	pauseListener	= new ChangeListener() {

														@Override
														public void changed( final ChangeEvent event, final Actor actor ) {
															if ( pauseOptBut.isPressed() ) {
																pauseTab.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false)));
																optScrollP.addAction(Actions.sequence(Actions.alpha(0), Actions.visible(true), Actions.delay(0.2f), Actions.alpha(1, 0.5f)));
																gp.updWorld = false;
																pointer.setVisible(true);
																pointer.setSelectedActor(optBackBut);
															} else if ( pauseResumeBut.isPressed() ) {
																pauseTab.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false)));
																hudGroup.addAction(Actions.sequence(Actions.alpha(0), Actions.visible(true), Actions.delay(0.2f), Actions.alpha(1, 0.5f)));
																gp.updWorld = true;
																pointer.setVisible(false);
															} else if ( pauseMMenuBut.isPressed() ) {
																pointer.setVisible(false);
																hudGroup.setVisible(true);
																hudGroup.addAction(Actions.alpha(1));
																pauseTab.addAction(Actions.sequence(Actions.alpha(0, 0.5f), Actions.visible(false), Actions.run(new Runnable() {

																	@Override
																	public void run() {
																		GR.game.setScreen(MenuController.instance);
																	}
																})));
															}
														}
													};

	/**
	 * https://bitbucket.org/dermetfan/somelibgdxtests/src/28080f f7dd7bd6d000ec8ba7f9514e177bb03e17 /SomeLibgdxTests/src/net/dermetfan/someLibgdxTests /screens/TabsLeftTest.java?at=default
	 * 
	 * @author dermetfan
	 * 
	 */
	public static class Board extends Group {

		public void pack() {
			float width = Float.NEGATIVE_INFINITY, height = Float.NEGATIVE_INFINITY, childXandWidth, childYandHeight;
			for (final Actor child : getChildren()) {
				if ( (childXandWidth = child.getX() + child.getWidth()) > width )
					width = childXandWidth;

				if ( (childYandHeight = child.getY() + child.getHeight()) > height )
					height = childYandHeight;
			}

			setSize(width, height);
		}

	}
}
