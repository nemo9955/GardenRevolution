package com.nemo9955.garden_revolution.slidingPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nemo9955.garden_revolution.utility.Buton;


public class OptionPanel extends SlidingPanel {

    private Buton butoane[] = new Buton[1];

    public OptionPanel(byte side, float distance) {
        super( "optiuni", side, distance );

        butoane[0] = new Buton( "resume" ).setPozi( 150, 230 );// .setCamera( cam );
        butoane[0] = new Buton( "meniu" ).setPozi( 150, 300 );// .setCamera( cam );
    }

    public void render(SpriteBatch batch, float delta) {

        if ( Gdx.input.isKeyPressed( Keys.F1 ) ) {
            System.out.println( butoane[0].getZon().x +" " +butoane[0].getZon().y );
        }

        if ( butoane[0].isPressed() )
            exitPanel = true;
        if ( butoane[1].isPressed() )
            exitPanel = true;

        fundal.draw( batch );
        for (Buton buton : butoane )
            buton.render( delta, batch );

    }

    @Override
    public void dispose() {
        super.dispose();
        for (Buton buton : butoane )
            buton.dispose();
    }

}
