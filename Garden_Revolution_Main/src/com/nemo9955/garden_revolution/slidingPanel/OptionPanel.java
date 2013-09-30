package com.nemo9955.garden_revolution.slidingPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.Garden_Revolution;
import com.nemo9955.garden_revolution.utility.Buton;


public class OptionPanel extends SlidingPanel {

    private Sprite test;

    private Buton  butoane[] = new Buton[2];

    public OptionPanel(byte side, float distance) {
        super( "optiuni", side, distance );

        butoane[0] = new Buton( "resume" ).setPozi( 150, 230 ).setCamera( view );
        butoane[1] = new Buton( "meniu" ).setPozi( 150, 300 ).setCamera( view );

        test = new Sprite( (Texture) Garden_Revolution.manager.get( Garden_Revolution.FUNDALE +"imagine_test.jpg" ) );
    }

    @Override
    public void renderStatic(SpriteBatch batch, float delta) {
        if ( Gdx.input.isKeyPressed( Keys.F1 ) ) {
            System.out.println( butoane[0].getZon().x +" " +butoane[0].getZon().y );
        }

        if ( butoane[0].isPressed() )
            exitPanel = true;
        if ( butoane[1].isPressed() )
            Garden_Revolution.game.setScreen( Garden_Revolution.meniu );

        fundal.draw( batch );
    }

    @Override
    public void renderAsCamera(SpriteBatch batch, float delta) {
        test.draw( batch, 0.7f );

        for (Buton buton : butoane )
            buton.render( delta, batch );

      //  view.zoom = 1.2f;
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Buton buton : butoane )
            buton.dispose();
    }

    @Override
    public void activate() {

    }

}
