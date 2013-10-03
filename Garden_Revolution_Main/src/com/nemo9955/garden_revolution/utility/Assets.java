package com.nemo9955.garden_revolution.utility;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Model;


public enum Assets {

    FONT1(LOC.FONT, "font1.fnt"),

    SCENA(LOC.MODEL, "scena.g3db"),

    TEST(LOC.BUTON, "test.png"), //
    PLAY(LOC.BUTON, "play.png"), //
    MENIU(LOC.BUTON, "meniu.png"), //
    RESUME(LOC.BUTON, "resume.png"), //
    BACK(LOC.BUTON, "back.png"), //
    EXIT(LOC.BUTON, "exit.png"), //
    IGOPTIUNI(LOC.BUTON, "IGoptiuni.png"), //
    BUT_DOWN_1(LOC.BUTON, "buton_down.png"), //
    BUT_UP_1(LOC.BUTON, "buton_up.png"), //

    IMGTEST1(LOC.FUNDAL, "imagine_test.jpg"), //
    LOADING_BAR(LOC.FUNDAL, "loading_bar.png"), //
    LOADING_BG(LOC.FUNDAL, "loading_fundal.png"), //

    OPTIUNI_BG(LOC.ELEMENT, "optiuni_fundal.png");//


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
        BUTON("imagini/butoane/", Texture.class), //
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
