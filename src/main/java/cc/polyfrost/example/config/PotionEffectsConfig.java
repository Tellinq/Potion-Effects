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

    @Header(
            text = "Potion Effects HUD",
            size = 2
    )

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
            location = PageLocation.TOP
    )
    public static EffectConfig global = new EffectConfig("Global");

    @Page(
            name = "Speed",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig speed = new EffectConfig("Speed");

    @Page(
            name = "Slowness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig slowness = new EffectConfig("Slowness");

    @Page(
            name = "Haste",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig haste = new EffectConfig("Haste");

    @Page(
            name = "Mining Fatigue",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig miningFatigue = new EffectConfig("Mining Fatigue");

    @Page(
            name = "Strength",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig strength = new EffectConfig("Strength");

    @Page(
            name = "Jump Boost",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig jumpBoost = new EffectConfig("Jump Boost");

    @Page(
            name = "Nausea",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig nausea = new EffectConfig("Nausea");

    @Page(
            name = "Regeneration",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig regeneration = new EffectConfig("Regeneration");

    @Page(
            name = "Resistance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig resistance = new EffectConfig("Resistance");

    @Page(
            name = "Fire Resistance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig fireResistance = new EffectConfig("Fire Resistance");

    @Page(
            name = "Water Breathing",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig waterBreathing = new EffectConfig("Water Breathing");

    @Page(
            name = "Invisibility",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig invisibility = new EffectConfig("Invisibility");

    @Page(
            name = "Blindness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig blindness = new EffectConfig("Blindness");

    @Page(
            name = "Night Vision",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig nightVision = new EffectConfig("Night Vision");

    @Page(
            name = "Hunger",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig hunger = new EffectConfig("Hunger");

    @Page(
            name = "Weakness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig weakness = new EffectConfig("Weakness");

    @Page(
            name = "Poison",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig poison = new EffectConfig("Poison");

    @Page(
            name = "Wither",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig wither = new EffectConfig("Wither");

    @Page(
            name = "Absorption",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static EffectConfig absorption = new EffectConfig("Absorption");

    @HUD(
            name = "General HUD"
    )
    public static PotionEffects testHud = new PotionEffects();

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
                name = "Override",
                subcategory = "General"
        )
        public boolean override = false;
        
        @Checkbox(
                name = "Icon",
                subcategory = "Component"
        )
        public boolean icon = true;

        @Checkbox(
                name = "Effect Name",
                subcategory = "Component"
        )
        public boolean effectName = true;

        @Checkbox(
                name = "Duration",
                subcategory = "Component"
        )
        public boolean duration = true;

        @Switch(
                name = "Show Amplifier",
                subcategory = "Amplifier"
        )
        public boolean amplifier = true;

        @Switch(
                name = "Show Level One",
                subcategory = "Amplifier"
        )
        public boolean levelOne = false;

        @DualOption(
                name = "Amplifier Numerals",
                subcategory = "Amplifier",
                left = "Roman",
                right = "Arabic"
        )
        public boolean romanNumerals = false;

        @Switch(
                name = "Blink",
                subcategory = "Blinking"
        )
        public boolean blink = true;

        @Slider(
                name = "Blink Duration",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public float blinkDuration = 10;

        @Slider(
                name = "Blink Speed",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public float blinkSpeed = 30;

        @Color(
                name = "Name Color",
                subcategory = "Color"
        )
        public OneColor nameColor = new OneColor(255, 255, 255);

        @Color(
                name = "Duration Color",
                subcategory = "Color"
        )
        public OneColor durationColor = new OneColor(255, 255, 255);
        
    }
}