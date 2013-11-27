package com.nemo9955.garden_revolution.utility;


public enum Icons {

    VANATA("aubergin"), //
    MAR("shiny-apple"), //
    GHINDA("acorn"), //
    TINTA("targeting"), //
    CIUPERCA("spotted-mushroom"), //
    SAGETI("radial-balance"), //
    ATINTIT("profit"), //
    ARC_FULGER("lighting-bow"), //
    CROSBOW("cross-bow"), //
    TUN("cannon"), //
    RAZA("beam-wake"), //
    TINTA_ARC("archery-target");//

    String nume;

    Icons(String nume) {
        this.nume = nume;
    }

    public String getName() {
        return nume;
    }


}
