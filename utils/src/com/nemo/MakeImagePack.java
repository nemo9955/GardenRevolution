package com.nemo;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;


public class MakeImagePack {

    public static void main(String[] args) {
        // TexturePacker.
        Settings param = new Settings();
        param.duplicatePadding = true;
        param.pot = false;
        param.flattenPaths = true;
        param.limitMemory = false;
        param.edgePadding = true;

        TexturePacker.process( param, "/home/nemo9955/CodeStation/GR materials/forPack", "/home/nemo9955/CodeStation/GR materials", "MenuImages" );
        TexturePacker.process( param, "/home/nemo9955/CodeStation/GR materials/forPack", "../android/assets/imagini/elemente", "MenuImages" );

    }
}
