package cc.polyfrost.example.config;

import cc.polyfrost.example.ExampleMod;
import cc.polyfrost.example.hud.PotionEffects;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

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

    @Checkbox(
            name = "Icon",
            category = "Global",
            subcategory = "Component"
    )
    public static boolean icon = true;

    @Checkbox(
            name = "Effect Name",
            category = "Global",
            subcategory = "Component"
    )
    public static boolean effectName = true;

    @Checkbox(
            name = "Duration",
            category = "Global",
            subcategory = "Component"
    )
    public static boolean duration = true;

    @Switch(
            name = "Show Amplifier",
            category = "Global",
            subcategory = "Amplifier"
    )
    public static boolean amplifier = true;

    @Switch(
            name = "Show Level One",
            category = "Global",
            subcategory = "Amplifier"
    )
    public static boolean levelOne = false;

    @Switch(
            name = "Blink",
            category = "Global",
            subcategory = "Blinking"
    )
    public static boolean blink = true;

    @Slider(
            name = "Blink Duration",
            category = "Global",
            subcategory = "Blinking",
            min = 0,
            max = 60
    )
    public static float blinkDuration = 10;

    @Slider(
            name = "Blink Speed",
            category = "Global",
            subcategory = "Blinking",
            min = 0,
            max = 60
    )
    public static float blinkSpeed = 30;

    @Color(
            name = "Name Color",
            category = "Global",
            subcategory = "Color"
    )
    public static OneColor nameColor = new OneColor(255, 255, 255);

    @Color(
            name = "Duration Color",
            category = "Global",
            subcategory = "Color"
    )
    public static OneColor durationColor = new OneColor(255, 255, 255);

    public PotionEffectsConfig() {
        super(new Mod(ExampleMod.NAME, ModType.UTIL_QOL), ExampleMod.MODID + ".json");
        initialize();
    }
}