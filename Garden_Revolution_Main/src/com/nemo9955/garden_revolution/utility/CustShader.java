package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * See: http://blog.xoppa.com/creating-a-shader-with-libgdx
 * 
 * @author Xoppa
 */
public class CustShader implements Shader {

    private ShaderProgram program;
    @SuppressWarnings("unused")
    private Camera        camera;
    @SuppressWarnings("unused")
    private RenderContext context;
    private int           u_projTrans;
    private int           u_worldTrans;

    private String        shade = "test";

    @Override
    public void init() {
        String vert = Gdx.files.internal( "shaders/" +shade +".vertex.glsl" ).readString();
        String frag = Gdx.files.internal( "shaders/" +shade +".fragment.glsl" ).readString();
        program = new ShaderProgram( vert, frag );
        if ( !program.isCompiled() )
            throw new GdxRuntimeException( program.getLog() );
        u_projTrans = program.getUniformLocation( "u_projTrans" );
        u_worldTrans = program.getUniformLocation( "u_worldTrans" );
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        program.begin();
        program.setUniformMatrix( u_projTrans, camera.combined );
        context.setDepthTest( GL20.GL_LEQUAL );
        context.setCullFace( GL20.GL_BACK );
    }

    @Override
    public void render(Renderable renderable) {
        program.setUniformMatrix( u_worldTrans, renderable.worldTransform );
        renderable.mesh.render( program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize );
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
