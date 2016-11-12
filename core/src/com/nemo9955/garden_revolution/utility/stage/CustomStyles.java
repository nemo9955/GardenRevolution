package com.nemo9955.garden_revolution.utility.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.nemo9955.garden_revolution.GR;
import com.nemo9955.garden_revolution.Garden_Revolution;

public class CustomStyles {

	private static SplitPaneStyle vineSplit = new SplitPaneStyle(
			new TiledDrawable(Garden_Revolution.getPackTexture("vineVer")));

	private static SliderStyle sliderST;

	// private static FreeTypeFontGenerator gen;

	public static void makeStyles() {

		sliderST = new SliderStyle();
		sliderST.background = new TiledDrawable(Garden_Revolution.getPackTexture("midST"));
		sliderST.knob = new TextureRegionDrawable(Garden_Revolution.getPackTexture("knobST"));
		sliderST.knobAfter = new TextureRegionDrawable(Garden_Revolution.getPackTexture("rightST"));
		sliderST.knobBefore = new TextureRegionDrawable(Garden_Revolution.getPackTexture("leftST"));

		GR.skin.add("default-horizontal", sliderST, SliderStyle.class);
		GR.skin.add("vine", vineSplit, SplitPaneStyle.class);

		// TODO add this in the AssetManager
		// FreeTypeFontParameter fontPar = new FreeTypeFontParameter();
		// gen = new
		// FreeTypeFontGenerator(Gdx.files.internal("fonts/KBLuckyClover.ttf"));
		// fontPar.size = 50;
		// GR.skin.add("clover", gen.generateFont(fontPar), BitmapFont.class);

	}

	public static void dispose() {
		// gen.dispose();
	}

	/*
	 * default-horizontal:{ background:element1 , disabledBackground:element2 ,
	 * knob: mover-knob },
	 */
}
