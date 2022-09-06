package tk.tellinq.potioneffects.config;

import tk.tellinq.potioneffects.PotionEffectsMod;
import tk.tellinq.potioneffects.hud.PotionEffects;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;

public class PotionEffectsConfig extends Config {

    @Page(
            name = "Global Effects",
            location = PageLocation.TOP
    )
    public static PotionEffects.EffectConfig global = new PotionEffects.EffectConfig("Global");

    @Page(
            name = "Speed",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig speed = new PotionEffects.EffectConfig("Speed");

    @Page(
            name = "Slowness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig slowness = new PotionEffects.EffectConfig("Slowness");

    @Page(
            name = "Haste",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig haste = new PotionEffects.EffectConfig("Haste");

    @Page(
            name = "Mining Fatigue",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig miningFatigue = new PotionEffects.EffectConfig("Mining Fatigue");

    @Page(
            name = "Strength",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig strength = new PotionEffects.EffectConfig("Strength");

    @Page(
            name = "Jump Boost",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig jumpBoost = new PotionEffects.EffectConfig("Jump Boost");

    @Page(
            name = "Nausea",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig nausea = new PotionEffects.EffectConfig("Nausea");

    @Page(
            name = "Regeneration",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig regeneration = new PotionEffects.EffectConfig("Regeneration");

    @Page(
            name = "Resistance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig resistance = new PotionEffects.EffectConfig("Resistance");

    @Page(
            name = "Fire Resistance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig fireResistance = new PotionEffects.EffectConfig("Fire Resistance");

    @Page(
            name = "Water Breathing",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig waterBreathing = new PotionEffects.EffectConfig("Water Breathing");

    @Page(
            name = "Invisibility",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig invisibility = new PotionEffects.EffectConfig("Invisibility");

    @Page(
            name = "Blindness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig blindness = new PotionEffects.EffectConfig("Blindness");

    @Page(
            name = "Night Vision",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig nightVision = new PotionEffects.EffectConfig("Night Vision");

    @Page(
            name = "Hunger",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig hunger = new PotionEffects.EffectConfig("Hunger");

    @Page(
            name = "Weakness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig weakness = new PotionEffects.EffectConfig("Weakness");

    @Page(
            name = "Poison",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig poison = new PotionEffects.EffectConfig("Poison");

    @Page(
            name = "Wither",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig wither = new PotionEffects.EffectConfig("Wither");

    @Page(
            name = "Health Boost",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig healthBoost = new PotionEffects.EffectConfig("Health Boost");

    @Page(
            name = "Absorption",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig absorption = new PotionEffects.EffectConfig("Absorption");

    @Page(
            name = "Saturation",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig saturation = new PotionEffects.EffectConfig("Saturation");
    
    @HUD(
            name = "General HUD"
    )
    public static PotionEffects testHud = new PotionEffects();

    public PotionEffectsConfig() {
        super(new Mod(PotionEffectsMod.NAME, ModType.HUD), PotionEffectsMod.MODID + ".json");
        initialize();
    }
}