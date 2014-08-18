package com.nemo9955.garden_revolution.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.nemo9955.garden_revolution.Garden_Revolution;

public class AndroidLauncher extends AndroidApplication {

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useCompass = false;

		initialize(new Garden_Revolution(), cfg);
	}
}
