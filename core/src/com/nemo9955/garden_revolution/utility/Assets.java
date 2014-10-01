package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nemo9955.garden_revolution.GR;

public enum Assets {

	ARIAL32(LOC.FONT, "arial_32.fnt"), //
	DEMON32(LOC.FONT, "demonized_32.fnt"), //

	CLOUDS1(LOC.FUNDAL, "clouds", SPECIAL.CUBEMAP), //
	BACKGROUNG(LOC.FUNDAL, "gr_background.png"), //
	ELEMENTS_PACK(LOC.ELEMENT, "MenuImages.atlas", TextureAtlas.class), //
	SKIN_JSON(LOC.ELEMENT, "MenuImages.json", Skin.class), //

	MENU(LOC.MODEL, "menu.g3db"), //
	ALLIES(LOC.MODEL, "allies.g3db"), //
	ENEMYS(LOC.MODEL, "enemies.g3db"), //
	TOWER_FUNDATION(LOC.MODEL, "tower_fundation.g3db"), //
	TOWER_BASIC(LOC.MODEL, "tower_basic.g3db"), //
	VIEW_POINT(LOC.MODEL, "view_point.g3db"); //

	private String[]	elements;
	private SPECIAL		astSpecial	= SPECIAL.NOT;
	private String		astPath;
	private String		astName;
	private Class<?>	astClass;

	Assets(LOC locatia, String name) {
		astName = name;
		astPath = locatia.getLink();
		astClass = locatia.getType();
	}

	Assets(LOC locatia, String name, Class<?> clasa) {
		astName = name;
		astPath = locatia.getLink();
		astClass = clasa;

		if ( astClass == Texture.class ) {
			astSpecial = SPECIAL.TEXTURE;
			System.out.println("Texture : " + astPath);
		}
	}

	Assets(LOC locatia, String name, SPECIAL special) {
		astName = name;
		astPath = locatia.getLink();
		astSpecial = special;
		astClass = locatia.getType();

	}

	public String getAstPath() {
		return astPath + astName;
	}

	public String getAstName() {
		return astName;
	}

	public Class<?> getAstClass() {
		return astClass;
	}

	public SPECIAL getAstSpecial() {
		return astSpecial;
	}

	public String[] getElements() {
		return elements;
	}

	public static enum LOC {
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

	public static enum SPECIAL {
		NOT {

			@Override
			public void loadSpecial( Assets aset ) {
				GR.manager.load(aset.getAstPath(), aset.getAstClass());
			}
		},
		TEXTURE {

			TextureParameter	param	= new TextureParameter();

			@Override
			public void loadSpecial( Assets aset ) {

				param.minFilter = TextureFilter.Linear;
				param.magFilter = TextureFilter.Linear;
				GR.manager.load(aset.getAstPath(), Texture.class, param);
			}
		},
		CUBEMAP {

			@Override
			public void loadSpecial( Assets aset ) {
				String[] faces = { "posx", "negx", "posy", "negy", "posz", "negz" };
				aset.elements = new String[6];
				for (int i = 0; i < 6; i++) {
					aset.getElements()[i] = "-" + faces[i] + ".jpg";
					GR.manager.load(aset.getAstPath() + aset.getElements()[i], aset.getAstClass());
					// System.out.println(aset.getAstClass());
				}
			}
		};

		public abstract void loadSpecial( Assets aset );
	}
}
