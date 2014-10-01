package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.fsm.StackStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.game.world.Skybox;
import com.nemo9955.garden_revolution.utility.Assets;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;
import com.nemo9955.garden_revolution.utility.stage.StageActorPointer;

public class MenuController extends CustomAdapter implements Screen {

	public static final MenuController	instance	= new MenuController();

	public StateMachine<MenuController>	mst			= new StackStateMachine<MenuController>(this);

	public StageActorPointer			pointer;
	public Stage						stage;
	public Image						img			= new Image(GR.manager.get(Assets.BACKGROUNG.getAstPath(), Texture.class));

	private ModelBatch					modelBatch;
	private PerspectiveCamera			cam;
	private ModelInstance				menu		= new ModelInstance(Garden_Revolution.getModel(Assets.MENU));
	private FirstPersonCameraController	con;
	private Environment					environment	= new Environment();
	private Skybox						sb;

	ModelInstance						axes;

	{

		/*
		 * // float width = Gdx.graphics.getWidth(); // float height = Gdx.graphics.getHeight(); // PerspectiveCamera camera = new PerspectiveCamera(67, width, height); // camera.position.add(0, 0,
		 * 1000); // camera.near = 0.1f; // camera.far = 1000f; // camera.rotate(30, 1, 0, 0); // stage.getViewport().setCamera(camera); // camera.update(); { private Vector3 intersection = new
		 * Vector3(); private Plane PLANE = new Plane(new Vector3(0, 0, 1), Vector3.Zero);
		 * @Override public Vector2 screenToStageCoordinates( Vector2 screenCoords ) { Ray pickRay = getViewport().getPickRay(screenCoords.x, screenCoords.y); System.out.println(screenCoords); if (
		 * Intersector.intersectRayPlane(pickRay, PLANE, intersection) ) { screenCoords.x = intersection.x; screenCoords.y = intersection.y; } else { screenCoords.x = Float.MAX_VALUE; screenCoords.y =
		 * Float.MAX_VALUE; } return screenCoords; } }; // stage.getViewport().getCamera().position.add(0, 0, 600); // stage.getViewport().getCamera().update(); // stage.setDebugUnderMouse(true);
		 */

		ModelBuilder mb = new ModelBuilder();
		axes = new ModelInstance(mb.createXYZCoordinates(10, new Material(), Usage.Position | Usage.Normal | Usage.Color));

		float amb = .5f;
		float intensity = 1000f;
		float height = 20f;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, amb, amb, amb, 1f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, Vars.skyColor));

		environment.add(new PointLight().set(Color.BLACK, new Vector3(0, height * 5, 0), intensity / 2f));
		environment.add(new PointLight().set(Color.BLUE, new Vector3(15.292713f, height, -29.423798f), intensity));
		environment.add(new PointLight().set(Color.RED, new Vector3(10.024132f, height, 40.54626f), intensity / 2));
		environment.add(new PointLight().set(Color.DARK_GRAY, new Vector3(-14.920635f, height, 0.18914032f), intensity));
		environment.add(new PointLight().set(Color.GREEN, new Vector3(52.817417f, height * 3, -0.45362854f), intensity));
		environment.add(new PointLight().set(Color.CYAN, new Vector3(-13.099552f, height * 2, -15.360998f), intensity));
		environment.add(new PointLight().set(Color.WHITE, new Vector3(-16.840088f, height, -17.439095f), intensity));

		// DefaultShader.Config cfg = new Config();
		// cfg.defaultCullFace = 0;
		modelBatch = new ModelBatch();

		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.near = 0.1f;
		cam.far = 50;
		cam.position.set(menu.model.getNode("camera").translation);
		// cam.position.set(0, 0, 40);
		cam.update();

		con = new FirstPersonCameraController(cam);
		// con.setVelocity(100);
		con.setDegreesPerPixel(0.3f);

		ScreenViewport viewport = new ScreenViewport();
		viewport.setUnitsPerPixel(1.3f);
		stage = new Stage(viewport);
		// stage.addActor(img);
		pointer = new StageActorPointer(stage);

		sb = new Skybox(Assets.CLOUDS1, cam);
		sb.setSize(40);

	}

	Logger								log			= new Logger("FPS", 2);

	@Override
	public void render( float delta ) {
		Gdx.gl.glClearColor(Vars.skyColor.r, Vars.skyColor.g, Vars.skyColor.b, Vars.skyColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		GR.tweeger.update(delta);
		con.update(delta);
		mst.update();
		stage.act();

		// sb.render(delta, cam.position);

		modelBatch.begin(cam);
		modelBatch.render(menu, environment);
		// modelBatch.render(axes);
		modelBatch.end();

		stage.draw();
		pointer.draw();

		log.info("FPS : " + Gdx.graphics.getFramesPerSecond());

	}

	@Override
	public void show() {

		mst.changeState(GR.menu);

		Gdx.input.setInputProcessor(new InputMultiplexer(con, stage, this));
		if ( Func.isControllerUsable() )
			Controllers.addListener(this);
	}

	@Override
	public void hide() {

		Gdx.input.setInputProcessor(null);
		if ( Func.isControllerUsable() ) {
			Controllers.removeListener(this);
		}

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void resize( int width, int height ) {

		stage.getViewport().update(width, height, true);
		// if ( img != null )
		// img.setSize(stage.getWidth(), stage.getHeight());
		cam.viewportHeight = height;
		cam.viewportWidth = width;
		cam.update();
	}

	@Override
	public void dispose() {
		stage.dispose();
		axes.model.dispose();
	}

	@Override
	public boolean povMoved( Controller controller, int povCode, PovDirection value ) {

		pointer.updFromController(controller, povCode, value);

		if ( mst.getCurrentState() instanceof ControllerListener )
			return ((ControllerListener) mst.getCurrentState()).povMoved(controller, povCode, value);
		return false;
	}

	@Override
	public boolean buttonDown( Controller controller, int buttonIndex ) {
		if ( mst.getCurrentState() instanceof ControllerListener )
			if ( ((ControllerListener) mst.getCurrentState()).buttonDown(controller, buttonIndex) )
				return false;

		if ( buttonIndex == CoButt.Fire.id )
			pointer.fireSelected();
		else if ( buttonIndex == CoButt.Back.id )
			mst.revertToPreviousState();
		else if ( buttonIndex == CoButt.InvX.id )
			Vars.invertControllerX *= -1;
		else if ( buttonIndex == CoButt.InvY.id )
			Vars.invertControllerY *= -1;

		return false;

	}

	@Override
	public boolean axisMoved( Controller controller, int axisCode, float value ) {
		value = MathUtils.clamp(value, -1f, 1f);
		pointer.updFromController(controller, axisCode, value);

		if ( mst.getCurrentState() instanceof ControllerListener )
			return ((ControllerListener) mst.getCurrentState()).axisMoved(controller, axisCode, value);

		return false;
	}

	@Override
	public boolean keyDown( int keycode ) {

		switch ( keycode ) {
			case Keys.P :
				System.out.println(cam.position);
				break;
			case Keys.ESCAPE :
			case Keys.BACK :
				mst.revertToPreviousState();
				break;
		}

		if ( mst.getCurrentState() instanceof InputProcessor )
			return ((InputProcessor) mst.getCurrentState()).keyDown(keycode);
		return false;
	}
}
