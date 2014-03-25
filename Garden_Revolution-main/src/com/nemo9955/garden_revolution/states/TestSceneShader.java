package com.nemo9955.garden_revolution.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.utility.CustomAdapter;
import com.nemo9955.garden_revolution.utility.Func;
import com.nemo9955.garden_revolution.utility.Vars.CoButt;

/**
 * A port of ShaderLesson4B (simplex noise) from lwjgl-basics to LibGDX:
 * https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson4
 * 
 * @author davedes
 */
public class TestSceneShader extends CustomAdapter implements Screen {

    // Minor differences:
    // LibGDX Position attribute is a vec4
    // u_projView is called u_projTrans
    // we need to set ShaderProgram.pedantic to false
    // TexCoord attribute requires "0" appended at end to denote GL_TEXTURE0
    // ShaderProgram.TEXCOORD_ATTRIBUTE+"0"
    // It's wise to use LOWP when possible in GL ES
    // In LibGDX ShaderProgram uses begin() and end()
    // we can use Texture.bind(int) to bind a texture unit
    // note that it will leave that unit active -- so we need to reset to zero after!


    // private final String FRAG2 = "#ifdef GL_ES\n" +"#define LOWP lowp\n" +"precision mediump float;\n" +"#else\n" +"#define LOWP \n" +"#endif\n" +"varying LOWP vec4 vColor;\n" +"varying vec2 vTexCoord;\n" +"uniform sampler2D u_texture;\n" +"uniform sampler2D u_texture1;\n" +"uniform sampler2D u_mask;\n" +"\n" +"\n" +"\n" +"uniform float time;\n" +"\n" +"   /////////////////////////////////////////////////////////////////////////\n" +"   /////////////////// SIMPLEX NOISE FROM WEBGL-NOISE //////////////////////\n" +"   /////////////////////////////////////////////////////////////////////////\n" +"   //            https://github.com/ashima/webgl-noise/wiki               //\n" +"   /////////////////////////////////////////////////////////////////////////\n" +"\n"
    // +"vec3 mod289(vec3 x) {\n" +"  return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +"}\n" +"\n" +"vec2 mod289(vec2 x) {\n" +"  return x - floor(x * (1.0 / 289.0)) * 289.0;\n" +"}\n" +"\n" +"vec3 permute(vec3 x) {\n" +"  return mod289(((x*34.0)+1.0)*x);\n" +"}\n" +"\n" +"float snoise(vec2 v) {\n" +"  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0\n" +"                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)\n" +"                     -0.577350269189626,  // -1.0 + 2.0 * C.x\n" +"                      0.024390243902439); // 1.0 / 41.0\n" +"// First corner\n" +"  vec2 i  = floor(v + dot(v, C.yy) );\n" +"  vec2 x0 = v -   i + dot(i, C.xx);\n" +"\n" +"// Other corners\n" +"  vec2 i1;\n"
    // +"  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0\n" +"  //i1.y = 1.0 - i1.x;\n" +"  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);\n" +"  // x0 = x0 - 0.0 + 0.0 * C.xx ;\n" +"  // x1 = x0 - i1 + 1.0 * C.xx ;\n" +"  // x2 = x0 - 1.0 + 2.0 * C.xx ;\n" +"  vec4 x12 = x0.xyxy + C.xxzz;\n" +"  x12.xy -= i1;\n" +"\n" +"// Permutations\n" +"  i = mod289(i); // Avoid truncation effects in permutation\n" +"  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))\n" +"       + i.x + vec3(0.0, i1.x, 1.0 ));\n" +"\n" +"  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);\n" +"  m = m*m ;\n" +"  m = m*m ;\n" +"\n" +"// Gradients: 41 points uniformly over a line, mapped onto a diamond.\n"
    // +"// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)\n" +"\n" +"  vec3 x = 2.0 * fract(p * C.www) - 1.0;\n" +"  vec3 h = abs(x) - 0.5;\n" +"  vec3 ox = floor(x + 0.5);\n" +"  vec3 a0 = x - ox;\n" +"\n" +"// Normalise gradients implicitly by scaling m\n" +"// Approximation of: m *= inversesqrt( a0*a0 + h*h );\n" +"  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );\n" +"\n" +"// Compute final noise value at P\n" +"  vec3 g;\n" +"  g.x  = a0.x  * x0.x  + h.x  * x0.y;\n" +"  g.yz = a0.yz * x12.xz + h.yz * x12.yw;\n" +"  return 130.0 * dot(m, g);\n" +"}\n" +"\n" +"\n" +"   /////////////////////////////////////////////////////////////////////////\n"
    // +"   ////////////////////       END SIMPLEX NOISE     ////////////////////////\n" +"   /////////////////////////////////////////////////////////////////////////\n" +"   \n" +"   \n" +"void main(void) {\n" +"   //sample the colour from the first texture\n" +"   vec4 texColor0 = texture2D(u_texture, vTexCoord);\n" +"   //sample the colour from the second texture\n" +"   vec4 texColor1 = texture2D(u_texture1, vTexCoord);\n" +"   \n" +"   //pertube texcoord by x and y\n" +"   vec2 distort = 0.2 * vec2(snoise(vTexCoord + vec2(0.0, time/3.0)),\n" +"                              snoise(vTexCoord + vec2(time/3.0, 0.0)) );\n" +"   \n" +"   //get the mask; we will only use the alpha channel\n"
    // +"   float mask = texture2D(u_mask, vTexCoord + distort).a;\n" +"\n" +"   //interpolate the colours based on the mask\n" +"   gl_FragColor = vColor * mix(texColor0, texColor1, mask);\n" +"}";

    private Texture            tex0, tex1, mask;
    private SpriteBatch        batch;
    private OrthographicCamera cam;
    private ShaderProgram      shader;
    private float              time;

    @Override
    public void show() {
        tex0 = new Texture( Gdx.files.internal( "data/grass.png" ) );
        tex1 = new Texture( Gdx.files.internal( "data/dirt.png" ) );
        mask = new Texture( Gdx.files.internal( "data/mask.png" ) );

        // important since we aren't using some uniforms and attributes that SpriteBatch expects
        ShaderProgram.pedantic = false;
        final String FRAG = Gdx.files.internal( "data/lesson4b.frag" ).readString();

        // print it out for clarity
        // System.out.println( "Vertex Shader:\n-------------\n\n" +VERT );
        // System.out.println( "\n" );
        // System.out.println( "Fragment Shader:\n-------------\n\n" +FRAG );

        // fps = new BitmapFont();

        final String VERT = "attribute vec4 " +ShaderProgram.POSITION_ATTRIBUTE +";\n" +"attribute vec4 " +ShaderProgram.COLOR_ATTRIBUTE +";\n" +"attribute vec2 " +ShaderProgram.TEXCOORD_ATTRIBUTE +"0;\n" +"uniform mat4 u_projTrans;\n" +" \n" +"varying vec4 vColor;\n" +"varying vec2 vTexCoord;\n" +"void main() {\n" +"   vColor = " +ShaderProgram.COLOR_ATTRIBUTE +";\n" +"   vTexCoord = " +ShaderProgram.TEXCOORD_ATTRIBUTE +"0;\n" +"   gl_Position =  u_projTrans * " +ShaderProgram.POSITION_ATTRIBUTE +";\n" +"}";


        shader = new ShaderProgram( VERT, FRAG );
        if ( !shader.isCompiled() ) {
            System.err.println( shader.getLog() );
            System.exit( 0 );
        }
        if ( shader.getLog().length() !=0 )
            System.out.println( shader.getLog() );


        shader.begin();
        shader.setUniformi( "u_texture1", 1 );
        shader.setUniformi( "u_mask", 2 );
        shader.end();

        // bind mask to glActiveTexture(GL_TEXTURE2)
        mask.bind( 2 );

        // bind dirt to glActiveTexture(GL_TEXTURE1)
        tex1.bind( 1 );

        // now we need to reset glActiveTexture to zero!!!! since sprite batch does not do this for us
        Gdx.gl.glActiveTexture( GL10.GL_TEXTURE0 );

        // tex0 will be bound when we call SpriteBatch.draw

        batch = new SpriteBatch( 1000, shader );
        batch.setShader( shader );

        cam = new OrthographicCamera( Gdx.graphics.getWidth(), Gdx.graphics.getHeight() );
        cam.setToOrtho( false );
        Gdx.input.setInputProcessor( this );
        if ( Func.isControllerUsable() )
            Controllers.addListener( this );
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        if ( buttonIndex ==CoButt.Back.id )
            GR.game.setScreen( GR.menu );
        return false;

    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho( false, width, height );
        batch.setProjectionMatrix( cam.combined );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );


        batch.begin();
        shader.setUniformf( "time", time += Gdx.graphics.getDeltaTime() );
        batch.draw( tex0, 0, 0 );
        batch.end();

    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
            case Keys.BACK:
                GR.game.setScreen( GR.menu );
                break;
        }
        return false;
    }

    @Override
    public void pause() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor( null );
        if ( Func.isControllerUsable() ) {
            Controllers.removeListener( this );
        }
        dispose();
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        shader.dispose();
        tex0.dispose();
    }
}