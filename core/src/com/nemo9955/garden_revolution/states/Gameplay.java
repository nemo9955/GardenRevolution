package com.nemo9955.garden_revolution.states;

import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.ASAonPath;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.ASAtimer;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.allySpawnArea;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.fps;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.hudAllyPlacer;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.hudMover;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.hudReady;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.hudTowerBut;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.hudWeaponCharger;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.makeGamePlayStage;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.pointer;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.restartStage;
import static com.nemo9955.garden_revolution.utility.stage.GameStageMaker.showASA;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.game.Player;
import com.nemo9955.garden_revolution.game.enumTypes.AllyType;
import com.nemo9955.garden_revolution.game.enumTypes.WeaponType.FireType;
import com.nemo9955.garden_revolution.game.world.Skybox;
import com.nemo9955.garden_revolution.game.world.WorldWrapper;
import com.nemo9955.garden_revolution.net.GameClient;
import com.nemo9955.garden_revolution.net.Host;
import com.nemo9955.garden_revolution.net.packets.Packets.StartingServerInfo;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoAxis;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;
import com.nemo9955.garden_revolution.utility.stage.GameStageMaker;
import com.nemo9955.garden_revolution.utility.stage.StageActorPointer;

public class Gameplay extends CustomAdapter implements Screen {

	public GestureDetector		gestures;
	private ModelBatch			modelBatch;
	public ShapeRenderer		shape;
	public DecalBatch			decalBatch;
	private CameraGroupStrategy	camGRStr;

	public float				movex			= 0;
	public float				movey			= 0;
	public boolean				updWorld		= true;
	private Vector3				dolly			= new Vector3();
	public static Vector2		tmp1			= new Vector2();
	public float				touchPadTimmer	= 0;
	private TweenManager		tweeger;

	private Vector2				presDown		= new Vector2();
	public Stage				stage;

	public Player				player			= new Player();

	private Skybox				skybox;

	public Gameplay() {

		gestures = new GestureDetector(this);
		gestures.setLongPressSeconds(0.5f);

		tweeger = new TweenManager();
		shape = new ShapeRenderer();

		DefaultShader.Config cfg = new Config();
		cfg.defaultCullFace = 0;
		modelBatch = new ModelBatch(new DefaultShaderProvider(cfg));

		// stage = new Stage( new ScalingViewport( Scaling.stretch,
		// Gdx.graphics.getWidth() *1.5f /Vars.densitate,
		// Gdx.graphics.getHeight() *1.5f /Vars.densitate ) );

		ScreenViewport viewport = new ScreenViewport();
		viewport.setUnitsPerPixel(1.5f);
		stage = new Stage(viewport);

		makeGamePlayStage(this);
		Func.makePropTouch(stage.getRoot());
		hudAllyPlacer.setTouchable(Touchable.enabled);

		allySpawnArea.setRotation(Vector3.Y, Vector3.Y);
		pointer = new StageActorPointer(stage);

		camGRStr = new CameraGroupStrategy(player.getCamera());
		decalBatch = new DecalBatch(camGRStr);

		skybox = new Skybox(decalBatch, Assets.CLOUDS1);
		skybox.setSize(250);

	}

	@Override
	public void render( float delta ) {

		Gdx.gl.glClearColor(Vars.skyColor.r, Vars.skyColor.g, Vars.skyColor.b, Vars.skyColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		tweeger.update(delta);

		if ( touchPadTimmer != Vars.tPadMinAlpha && !hudMover.isTouched() ) {
			touchPadTimmer -= delta;
			if ( touchPadTimmer < Vars.tPadMinAlpha )
				touchPadTimmer = Vars.tPadMinAlpha;
			if ( touchPadTimmer < 1 )
				hudMover.addAction(Actions.alpha(touchPadTimmer));
		}

		if ( updWorld || GR.mp != null )
			updateTheGame(delta);
		else
			gestures.cancel();

		modelBatch.begin(player.getCamera());
		shape.setProjectionMatrix(player.getCamera().combined);
		shape.begin(ShapeType.Line);

		// skybox.render(delta, player.getCamera().position);
		WorldWrapper.instance.getWorld().render(modelBatch, decalBatch);
		if ( showASA )
			decalBatch.add(allySpawnArea);
		if ( Vars.showDebug && !Gdx.input.isKeyPressed(Keys.F9) )
			WorldWrapper.instance.getWorld().renderDebug(player.getCamera(), shape);
		modelBatch.end();
		shape.end();
		decalBatch.flush();

		fps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

		stage.act();
		stage.draw();
		pointer.draw();
	}

	private void updateTheGame( float delta ) {

		player.getCamera().translate(dolly);
		if ( movex != 0 || movey != 0 || !dolly.isZero() )
			player.moveCamera(movex, movey);

		player.update(delta);

		if ( ASAtimer > 0 ) {
			hudAllyPlacer.setColor(0.4f, 0.4f, 0.4f, 1 - (ASAtimer / Vars.allySpawnInterval));
			ASAtimer -= delta;
			if ( ASAtimer <= 0 )
				hudAllyPlacer.setColor(1, 1, 1, 1);
		}

		if ( showASA && !Gdx.input.isTouched() ) {
			Func.intersectLinePlane(GR.ray1.set(player.getCamera().position, player.getCamera().direction), GR.temp4);
			WorldWrapper.instance.getWorld().getOnPath(GR.temp4, ASAonPath, 150);
			allySpawnArea.setPosition(ASAonPath.x, 0.2f, ASAonPath.z);
		}

		WorldWrapper.instance.getWorld().update(delta);
	}

	public void preInit() {

		restartStage();

		stage.draw();
		pointer.setStage(stage);
		pointer.setVisible(false);
	}

	public void postInit( WorldWrapper newWorld ) {
		updWorld = true;

		WorldWrapper.instance = newWorld;

		player.reset(WorldWrapper.instance);

		// player.getCamera().position.set( world.getWorld().getOverview() );
		player.setTower(WorldWrapper.instance.getWorld().getTowers()[0]);
		player.getCamera().lookAt(Vector3.Zero);
		player.getCamera().update();

	}

	public Gameplay initAsSinglePlayer( FileHandle nivel ) {
		Gdx.graphics.setTitle(GR.TITLU + " " + GR.VERSIUNE);
		preInit();
		postInit(WorldWrapper.instance.init(nivel, false));
		return this;
	}

	public Gameplay initAsHost( FileHandle nivel ) {
		GR.mp = new Host();

		preInit();
		postInit(WorldWrapper.instance.init(nivel, true));
		hudReady.setText("Ready!");
		Gdx.graphics.setTitle("[H] " + GR.TITLU + " " + GR.VERSIUNE);
		return this;
	}

	public Gameplay initAsClient( String ip ) {
		GR.mp = new GameClient(ip);

		preInit();
		hudReady.setText("Ready!");
		GR.mp.sendTCP(new StartingServerInfo());
		Gdx.graphics.setTitle("[C] " + GR.TITLU + " " + GR.VERSIUNE);
		return this;
	}

	@Override
	public boolean touchDown( int screenX, int screenY, int pointer, int button ) {
		presDown.set(screenX, screenY);

		if ( !updWorld ) {
		} else if ( Func.isAndroid() || button == Buttons.LEFT ) {

			if ( player.getTower().isWeaponType(FireType.FIRECHARGED) ) {
				hudWeaponCharger.setColor(Color.CLEAR);
				hudWeaponCharger.setVisible(true);
				player.getTower().charge = 0;

				tmp1.set(presDown);
				stage.screenToStageCoordinates(tmp1);
				hudWeaponCharger.setPosition(tmp1.x - (hudWeaponCharger.getWidth() / 2), tmp1.y - (hudWeaponCharger.getHeight() / 2));
			}

			if ( player.getTower().isWeaponType(FireType.FIREHOLD) ) {
				player.getTower().setFiringHold(true);
				if ( Func.isDesktop() )
					Gdx.input.setCursorCatched(true);
			}

		} else {
			if ( Func.isDesktop() && Gdx.input.isButtonPressed(Buttons.RIGHT) )
				Gdx.input.setCursorCatched(true);
		}
		return false;
	}

	@Override
	public boolean touchDragged( int screenX, int screenY, int pointer ) {

		if ( hudWeaponCharger.isVisible() ) {
			float distance = 150;
			player.getTower().charge = MathUtils.clamp(presDown.dst2(screenX, screenY), 0, distance * distance);
			player.getTower().charge /= distance * distance;
			player.getTower().getWeapon().type.updateWeaponTargeting(player.getTower(), false);
			hudWeaponCharger.setColor((player.getTower().charge != 1 ? 0 : 1), 0, 0, player.getTower().charge);
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp( int screenX, int screenY, int pointer, int button ) {

		if ( updWorld && player.getTower().isFiringHold )
			player.getTower().setFiringHold(false);

		if ( hudWeaponCharger.isVisible() ) {
			hudWeaponCharger.setVisible(false);
			if ( WorldWrapper.instance.getWorld().getTowerHitByRay(player.getCamera().getPickRay(screenX, screenY)) == null || player.getTower().charge > 0.4f ) {
				player.getTower().getWeapon().type.updateWeaponTargeting(player.getTower(), false);
				WorldWrapper.instance.getDef().fireFromTower(player.getTower());
				player.getTower().charge = 0;
				return true;
			} else {
				player.getTower().charge = 0;
				player.getTower().getWeapon().type.updateWeaponTargeting(player.getTower(), false);
			}
		}

		if ( Func.isDesktop() )
			Gdx.input.setCursorCatched(false);

		return false;
	}

	@Override
	public boolean pan( float x, float y, float deltaX, float deltaY ) {
		if ( updWorld && ((!hudWeaponCharger.isVisible() && (Gdx.input.isButtonPressed(Buttons.RIGHT) || Func.isAndroid())) || (Func.isDesktop() && Gdx.input.isCursorCatched())) ) {
			float difX = 0, difY = 0;
			difX = deltaX / 10 * Vars.modCamSpeedX;
			difY = deltaY / 7 * Vars.modCamSpeedY;
			player.moveCamera(difX * Vars.invertDragX, difY * Vars.invertDragY);
			return true;
		}
		return false;

	}

	@Override
	public boolean tap( float x, float y, int count, int button ) {

		return player.tap(x, y, count, button, gestures);

	}

	@Override
	public boolean longPress( float x, float y ) {

		return player.longPress(x, y, gestures);

	}

	@Override
	public boolean keyDown( int keycode ) {
		switch ( keycode ) {
			case Keys.R :
				WorldWrapper.instance.getDef().setMoney(1000);
				break;
			case Keys.W :
				movey = 2f;
				break;
			case Keys.S :
				movey = -2f;
				break;
			case Keys.D :
				movex = -2f;
				break;
			case Keys.A :
				movex = 2f;
				break;
			case Keys.NUMPAD_8 :
				dolly.x = 0.3f;
				break;
			case Keys.NUMPAD_2 :
				dolly.x = -0.3f;
				break;
			case Keys.NUMPAD_4 :
				dolly.z = -0.3f;
				break;
			case Keys.NUMPAD_6 :
				dolly.z = 0.3f;
				break;
			case Keys.NUMPAD_9 :
				dolly.y = 0.3f;
				break;
			case Keys.NUMPAD_3 :
				dolly.y = -0.3f;
				break;
			// case Keys.F4:
			// world.getWorld().initEnv();
			// break;
			case Keys.H :
				GR.mp.sendTCP("a random message");
				break;
			case Keys.ESCAPE :
			case Keys.BACK :
				Actor actBkBut = Func.getActorInActiveStage(stage, "Back");
				if ( actBkBut != null )
					Func.click(actBkBut);
				break;
		}

		return false;
	}

	@Override
	public boolean keyUp( int keycode ) {

		switch ( keycode ) {
			case Keys.W :
			case Keys.S :
				movey = 0;
				break;
			case Keys.D :
			case Keys.A :
				movex = 0;
				break;
			case Keys.NUMPAD_6 :
			case Keys.NUMPAD_4 :
				dolly.z = 0;
				break;
			case Keys.NUMPAD_8 :
			case Keys.NUMPAD_2 :
				dolly.x = 0;
				break;
			case Keys.NUMPAD_9 :
			case Keys.NUMPAD_3 :
				dolly.y = 0;
				break;
		}

		return false;
	}

	@Override
	public boolean povMoved( Controller controller, int povCode, PovDirection value ) {
		pointer.updFromController(controller, povCode, value);
		return false;
	}

	@Override
	public boolean buttonDown( Controller cont, int buttonCode ) {

		if ( buttonCode == CoButt.Fire.id ) {

			if ( pointer.isVisible() ) {
				pointer.fireSelected();
			} else if ( updWorld && player.getTower().isWeaponType(FireType.FIREHOLD) ) {
				player.getTower().setFiringHold(true);
			} else if ( updWorld && player.getTower().isWeaponType(FireType.FIRECHARGED) ) {
				hudWeaponCharger.setColor(Color.CLEAR);
				hudWeaponCharger.setVisible(true);
				player.getTower().charge = (-cont.getAxis(CoAxis.mvY.id) + 1) / 2;
				hudWeaponCharger.setColor((player.getTower().charge != 1 ? 0 : 1), 0, 0, player.getTower().charge);
				player.getTower().fireChargedTime = System.currentTimeMillis();

				tmp1.set(stage.screenToStageCoordinates(tmp1.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2)));
				hudWeaponCharger.setPosition(tmp1.x - (hudWeaponCharger.getWidth() / 2), tmp1.y - (hudWeaponCharger.getHeight() / 2));

				return false;
			}
		} else if ( buttonCode == CoButt.InvX.id )
			Vars.invertControllerX *= -1;

		else if ( buttonCode == CoButt.InvY.id )
			Vars.invertControllerY *= -1;

		else if ( buttonCode == CoButt.PrevT.id && Func.isCurrentState(stage, "HUD") )
			player.prevTower();

		else if ( buttonCode == CoButt.NextT.id && Func.isCurrentState(stage, "HUD") )
			player.nextTower();

		else if ( buttonCode == CoButt.TowerUpgr.id && Func.isCurrentState(stage, "HUD") )
			Func.click(hudTowerBut);

		else if ( buttonCode == CoButt.CallAlly.id && Func.isCurrentState(stage, "HUD") && ASAtimer <= 0 ) {
			showASA = true;
		}

		else if ( buttonCode == CoButt.Back.id ) {
			if ( showASA ) {
				showASA = false;
			} else {
				Actor actBkBut = Func.getActorInActiveStage(stage, "Back");
				if ( actBkBut != null )
					Func.click(actBkBut);
			}
		}

		return false;

	}

	@Override
	public boolean buttonUp( Controller controller, int buttonCode ) {
		if ( buttonCode == CoButt.Fire.id ) {
			if ( updWorld && player.getTower().isWeaponType(FireType.FIREHOLD) ) {
				player.getTower().setFiringHold(false);
			}
			if ( hudWeaponCharger.isVisible() ) {
				hudWeaponCharger.setVisible(false);
				// player.getTower().fireWeapon( world.getDef(), charge );
				WorldWrapper.instance.getDef().fireFromTower(player.getTower());
				player.getTower().fireChargedTime = 0;
				player.getTower().charge = 0;
			}
		} else if ( buttonCode == CoButt.CallAlly.id && showASA ) {
			showASA = false;
			placeAllies();
		}
		return false;

	}

	@Override
	public boolean axisMoved( Controller controller, int axisCode, float value ) {
		value = MathUtils.clamp(value, -1f, 1f);// in case of abnormal values

		if ( pointer.isVisible() )
			pointer.updFromController(controller, axisCode, value);

		else {
			if ( Math.abs(value) < Vars.deadZone ) {
				value = 0f;
				if ( Math.abs(controller.getAxis(CoAxis.mvX.id)) < Vars.deadZone )
					movex = 0;
				if ( Math.abs(controller.getAxis(CoAxis.mvY.id)) < Vars.deadZone )
					movey = 0;
			} else {
				if ( axisCode == CoAxis.mvX.id )
					movex = value * Vars.invertControllerX * Vars.multiplyControlletX;
				if ( axisCode == CoAxis.mvY.id )
					movey = value * Vars.invertControllerY * Vars.multiplyControlletY;
			}
		}

		return false;

	}

	public void placeAllies() {
		ASAtimer = Vars.allySpawnInterval;
		ASAonPath.y = 0;
		for (int i = 0; i < 3; i++) {
			GR.temp4.set(MathUtils.random(-5, 5), 0, MathUtils.random(-5, 5));
			WorldWrapper.instance.getDef().addAlly(GR.temp4.add(ASAonPath), AllyType.SOLDIER);
		}
	}

	private Dialog	dialog	= new Dialog("titlu", GR.manager.get(Assets.SKIN_JSON.getAstPath(), Skin.class)) {

								@Override
								protected void result( Object object ) {};
							};

	public void showMessage( String mesaj ) {

		System.out.println("Output : " + mesaj);

		dialog.setTitle("Mesaj");

		dialog.button(mesaj);

		dialog.pad(50);

		dialog.invalidate();

		dialog.show(stage);

	}

	@Override
	public void resize( int width, int height ) {
		stage.getViewport().update(width, height, true);
		GameStageMaker.resizeStage(width, height);

		player.getCamera().viewportHeight = height;
		player.getCamera().viewportWidth = width;
		player.getCamera().update();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {
		if ( Func.isControllerUsable() )
			Controllers.removeListener(this);
		Gdx.input.setInputProcessor(null);

		if ( GR.mp != null ) {
			GR.mp.stop();
			GR.mp = null;
		}

	}

	@Override
	public void show() {
		if ( Func.isControllerUsable() ) {
			Controllers.addListener(this);
		}
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, this, gestures));
	}

	@Override
	public void dispose() {

		if ( GR.mp != null ) {
			GR.mp.stop();
			GR.mp = null;
		}
		if ( modelBatch != null )
			modelBatch.dispose();
		if ( stage != null )
			stage.dispose();
		if ( WorldWrapper.instance != null ) {
			WorldWrapper.instance.getDef().reset();
			WorldWrapper.instance.getWorld().dispose();
		}
		if ( shape != null )
			shape.dispose();
		if ( camGRStr != null )
			camGRStr.dispose();
		if ( decalBatch != null )
			decalBatch.dispose();
	}
}
