package com.nemo9955.garden_revolution.states;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Buton;

public class Gameplay implements Screen, InputProcessor {


    private PerspectiveCamera    cam;
    private ModelBatch           modelBatch;
    private Array<ModelInstance> instances    = new Array<ModelInstance>();
    private Lights               lights;
    private ModelInstance        cer;
    private final int            scrw, scrh;

    private float                rotateAngle  = 360f;
    public int                   rotateButton = Buttons.LEFT;
    private float                startX, startY;
    private boolean              moveByTouch  = true;
    private boolean              moveUp       = false, moveDown = false, moveLeft = false, moveRight = false;
    private final Vector3        tmpV1        = new Vector3();
    public Vector3               target       = new Vector3( 0, 15, 0 );
    private float                deltaX       = 0;
    private float                deltaY       = 0;

    private short                toUpdate     = 0;
    private TweenManager         tweeger;
    private Stage                stage;
    private Skin                 skin;

    public Gameplay() {
        scrw = Gdx.graphics.getWidth();
        scrh = Gdx.graphics.getHeight();
        float amb = 0.4f, lum = 0.6f;
        tweeger = new TweenManager();

        modelBatch = new ModelBatch();
        lights = new Lights();
        lights.ambientLight.set( amb, amb, amb, .5f );
        lights.add( new DirectionalLight().set( lum, lum, lum, 0f, -15f, 5f ) );

        cam = new PerspectiveCamera( 67, scrw, scrh );
        cam.position.set( 0f, 15f, 0f );
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();

        makeStage();

    }

    private void makeStage() {
        stage = new Stage();
        Garden_Revolution.multiplexer.addProcessor( stage );


        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap( 1, 1, Format.RGBA8888 );
        pixmap.setColor( Color.WHITE );
        pixmap.fill();
        skin.add( "white", new Texture( pixmap ) );
        skin.add( "buton_up", new NinePatch( new Texture( "imagini/butoane/buton_up.png" ), 18, 19, 18, 19 ), NinePatch.class );
        skin.add( "buton_down", new NinePatch( new Texture( "imagini/butoane/buton_down.png" ), 18, 19, 18, 19 ), NinePatch.class );

        // Store the default libgdx font under the name "default".
        BitmapFont font = new BitmapFont();
        font.scale( 3 );
        skin.add( "default", font );

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.newDrawable( "buton_up" );
        textButtonStyle.over = skin.newDrawable( "buton_down" );
        textButtonStyle.checked = skin.newDrawable( "white", Color.BLUE );
        textButtonStyle.down = skin.newDrawable( "white", Color.LIGHT_GRAY );
        textButtonStyle.font = skin.getFont( "default" );
        skin.add( "default", textButtonStyle );

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.debug();
        table.setFillParent( true );
        stage.addActor( table );

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        final TextButton button = new TextButton( "Click me babe!", skin );
        table.add( button );

        // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
        // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
        // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
        // revert the checked state.
        button.addListener( new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                System.out.println( "Clicked! Is checked: " +button.isChecked() );
                button.setText( "Good job!" );
            }
        } );

        // Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
        table.add( new Image( skin.newDrawable( "white", Color.RED ) ) ).size( 64 );
    }

    @Override
    public void show() {

        Buton.tweeger = tweeger;
        toUpdate = 0;
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glViewport( 0, 0, scrw, scrh );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT |GL20.GL_DEPTH_BUFFER_BIT );
        tweeger.update( delta );

        if ( toUpdate ==0 )
            updateGameplay( delta );

        modelBatch.begin( cam );
        for (ModelInstance instance : instances )
            modelBatch.render( instance, lights );
        if ( cer !=null )
            modelBatch.render( cer );
        modelBatch.end();

        stage.act();
        stage.draw();
        Table.drawDebug( stage );

    }

    public void manageModels() {

        Model cuboid = Garden_Revolution.manager.get( Garden_Revolution.MODELE +"scena.g3db", Model.class );
        for (int i = 0 ; i <cuboid.nodes.size ; i ++ ) {
            String id = cuboid.nodes.get( i ).id;

            ModelInstance instance = new ModelInstance( cuboid, id );
            Node node = instance.getNode( id );

            instance.transform.set( node.globalTransform );
            node.translation.set( 0, 0, 0 );
            node.scale.set( 1, 1, 1 );
            node.rotation.idt();

            instance.calculateTransforms();
            // System.out.println(node.id);FIXME

            if ( id.equals( "cer" ) ) {
                cer = instance;
                continue;
            }

            instances.add( instance );

        }
    }

    private void updateGameplay(float delta) {
        updateCamera();
    }

    private void updateCamera() {

        final float toMove = 0.005f;

        if ( moveUp )
            moveCamera( toMove, 0 );
        if ( moveDown )
            moveCamera( -toMove, 0 );
        if ( moveLeft )
            moveCamera( 0, -toMove );
        if ( moveRight )
            moveCamera( 0, toMove );

        cam.update();
    }

    private void moveCamera(float amontX, float amontY) {
        tmpV1.set( cam.direction ).crs( cam.up ).y = 0f;
        cam.rotateAround( target, tmpV1.nor(), amontX *rotateAngle );
        cam.rotateAround( target, Vector3.Y, amontY *-rotateAngle );
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ( moveByTouch ) {
            startX = screenX;
            startY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if ( moveByTouch ) {
            deltaX = ( screenX -startX ) /scrw;
            deltaY = ( startY -screenY ) /scrh;
            startX = screenX;
            startY = screenY;
            moveCamera( deltaY, deltaX );
        }

        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.W:
                moveUp = true;
                break;
            case Keys.S:
                moveDown = true;
                break;
            case Keys.D:
                moveRight = true;
                break;
            case Keys.A:
                moveLeft = true;
                break;
            case Keys.ESCAPE:
                Garden_Revolution.game.setScreen( Garden_Revolution.meniu );
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        switch (keycode) {
            case Keys.W:
                moveUp = false;
                break;
            case Keys.S:
                moveDown = false;
                break;
            case Keys.D:
                moveRight = false;
                break;
            case Keys.A:
                moveLeft = false;
                break;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        stage.dispose();
        skin.dispose();
    }
}