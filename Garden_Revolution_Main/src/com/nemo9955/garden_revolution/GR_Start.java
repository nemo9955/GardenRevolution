package com.nemo9955.garden_revolution;

import com.badlogic.gdx.Game;
import com.nemo9955.garden_revolution.states.Gameplay;
import com.nemo9955.garden_revolution.states.Meniu;
import com.nemo9955.garden_revolution.states.TestScene;


public class GR_Start extends Game {

    public static final String TITLU    = "Garden Revolution";
    public static final String VERSIUNE = "alfa 0.01";

    public Gameplay            gameplay;
    public Meniu               meniu;
    public TestScene           stats;

    @Override
    public void create() {
        gameplay = new Gameplay( this );
        meniu = new Meniu( this );
        stats = new TestScene( this );

        setScreen( meniu );
    }
}
