package com.nemo9955.garden_revolution;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.nemo9955.garden_revolution.utility.Vars;

public class MainActivity extends AndroidApplication {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.useAccelerometer = true;
		cfg.useCompass = false;

		Vars.densitate = getResources().getDisplayMetrics().density;

		initialize(new Garden_Revolution(), cfg);

	}
}