package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public enum Assets {

	ARIAL32(LOC.FONT, "arial_32.fnt"), //
	DEMON32(LOC.FONT, "demonized_32.fnt"), //

	BACKGROUNG(LOC.FUNDAL, "gr_background.png"), //
	ELEMENTS_PACK(LOC.ELEMENT, "MenuImages.atlas", TextureAtlas.class), //
	SKIN_JSON(LOC.ELEMENT, "MenuImages.json", Skin.class), //

	MENU(LOC.MODEL, "menu.g3db"), //
	ALLIES(LOC.MODEL, "allies.g3db"), //
	ENEMYS(LOC.MODEL, "enemies.g3db"), //
	TOWER_FUNDATION(LOC.MODEL, "tower_fundation.g3db"), //
	TOWER_BASIC(LOC.MODEL, "tower_basic.g3db"), //
	VIEW_POINT(LOC.MODEL, "view_point.g3db"); //

	private String		astPath;
	private Class<?>	astClass;

	Assets(LOC locatia, String name) {
		astPath = locatia.getLink() + name;
		astClass = locatia.getType();
	}

	Assets(LOC locatia, String name, Class<?> clasa) {
		astPath = locatia.getLink() + name;
		this.astClass = clasa;
	}

	Assets(String name, Class<?> clasa) {

	}

	public String getAstPath() {
		return astPath;
	}

	public Class<?> getAstClass() {
		return astClass;

	}

	public enum LOC {
		FUNDAL("imagini/fundale/", Texture.class), //
		ELEMENT("imagini/elemente/", Texture.class), //
		FONT("fonts/", BitmapFont.class), //
		MODEL("modele/", Model.class);//

		private String		locLink;
		private Class<?>	locClass;

		LOC(String loc, Class<?> type) {
			locLink = loc;
			this.locClass = type;
		}

		public String getLink() {
			return locLink;
		}

		public Class<?> getType() {
			return locClass;
		}
	}
}
