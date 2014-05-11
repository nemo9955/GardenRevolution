package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public enum Assets {

    ARIAL32(LOC.FONT, "arial_32.fnt"), //
    ARIAL64(LOC.FONT, "arial_64.fnt"), //
    DEMON32(LOC.FONT, "demonized_32.fnt"), //
    DEMON64(LOC.FONT, "demonized_64.fnt"), //

    ALLIES(LOC.MODEL, "allies.g3db"), //
    ENEMYS(LOC.MODEL, "enemies.g3db"), //
    TOWER_FUNDATION(LOC.MODEL, "tower_fundation.g3db"), //
    TOWER_BASIC(LOC.MODEL, "tower_basic.g3db"), //
    VIEW_POINT(LOC.MODEL, "view_point.g3db"), //

    ELEMENTS_PACK(LOC.ELEMENT, "MenuImages.atlas", TextureAtlas.class), //
    SKIN_JSON(LOC.ELEMENT, "MenuImages.json", Skin.class); //


    private String   path;
    private Class<?> clasa;

    Assets(LOC locatia, String name) {
        path = locatia.getLink() +name;
        clasa = locatia.getType();
    }

    Assets(LOC locatia, String name, Class<?> clasa) {
        path = locatia.getLink() +name;
        this.clasa = clasa;
    }

    public String path() {
        return path;
    }

    public Class<?> type() {
        return clasa;

    }

    public enum LOC {
        FUNDAL("imagini/fundale/", Texture.class), //
        ELEMENT("imagini/elemente/", Texture.class), //
        FONT("fonts/", BitmapFont.class), //
        MODEL("modele/", Model.class);//

        private String   link;
        private Class<?> type;

        LOC(String loc, Class<?> type) {
            link = loc;
            this.type = type;
        }

        public String getLink() {
            return link;
        }

        public Class<?> getType() {
            return type;
        }
    }
}
