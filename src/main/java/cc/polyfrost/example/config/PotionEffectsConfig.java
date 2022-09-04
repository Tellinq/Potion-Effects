package cc.polyfrost.example.config;

import cc.polyfrost.example.PotionEffectsMod;
import cc.polyfrost.example.hud.PotionEffects;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;

public class PotionEffectsConfig extends Config {

    @HUD(
            name = "HUD"
    )
    public static PotionEffects testHud = new PotionEffects();

    @Slider(
            name = "Vertical Spacing",
            min = 0,
            max = 10
    )
    public static float verticalSpacing = 4;

    @DualOption(
            name = "Vertical Sorting",
            left = "Top",
            right = "Bottom"
    )
    public static boolean verticalSorting = false;

    @Dropdown(
            name = "Sorting Method",
            options = {"Vanilla", "Alphabetical", "Duration", "Amplifier"}
    )
    public static int sortingMethod = 0;

    @Page(
            name = "Global Effects",
            location = PageLocation.BOTTOM
    )
    public static EffectConfig global = new EffectConfig("Global");

//    @Page(
//            name = "Speed",
//            location = PageLocation.BOTTOM
//    )
//    public static EffectConfig speed = new EffectConfig("Speed");
//
//    @Page(
//            name = "Slowness",
//            location = PageLocation.BOTTOM
//    )
//    public static EffectConfig slowness = new EffectConfig("Slowness");
//
//    @Page(
//            name = "Strength",
//            location = PageLocation.BOTTOM
//    )
//    public static EffectConfig strength = new EffectConfig("Strength");

    public PotionEffectsConfig() {
        super(new Mod(PotionEffectsMod.NAME, ModType.UTIL_QOL), PotionEffectsMod.MODID + ".json");
        initialize();
    }

    public static class EffectConfig {
        public String effect;
        public EffectConfig(String effect) {
            this.effect = effect;
        }
        
        @Checkbox(
                name = "Icon",
                subcategory = "Component"
        )
        public boolean icon = true;

        @Checkbox(
                name = "Effect Name",
                subcategory = "Component"
        )
        public static boolean effectName = true;

        @Checkbox(
                name = "Duration",
                subcategory = "Component"
        )
        public static boolean duration = true;

        @Switch(
                name = "Show Amplifier",
                subcategory = "Amplifier"
        )
        public static boolean amplifier = true;

        @Switch(
                name = "Show Level One",
                subcategory = "Amplifier"
        )
        public static boolean levelOne = false;

        @DualOption(
                name = "Amplifier Numerals",
                subcategory = "Amplifier",
                left = "Roman",
                right = "Arabic"
        )
        public static boolean romanNumerals = true;

        @Switch(
                name = "Blink",
                subcategory = "Blinking"
        )
        public static boolean blink = true;

        @Slider(
                name = "Blink Duration",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public static float blinkDuration = 10;

        @Slider(
                name = "Blink Speed",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public static float blinkSpeed = 30;

        @Color(
                name = "Name Color",
                subcategory = "Color"
        )
        public static OneColor nameColor = new OneColor(255, 255, 255);

        @Color(
                name = "Duration Color",
                subcategory = "Color"
        )
        public static OneColor durationColor = new OneColor(255, 255, 255);
        
    }
}