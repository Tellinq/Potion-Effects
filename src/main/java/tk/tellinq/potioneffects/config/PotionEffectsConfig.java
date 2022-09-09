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
    public static PotionEffects.EffectConfig global = new PotionEffects.EffectConfig();

    @Page(
            name = "Speed",
            description = "Change the speed effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig speed = new PotionEffects.EffectConfig();

    @Page(
            name = "Slowness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig slowness = new PotionEffects.EffectConfig();

    @Page(
            name = "Haste",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig haste = new PotionEffects.EffectConfig();

    @Page(
            name = "Mining Fatigue",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig miningFatigue = new PotionEffects.EffectConfig();

    @Page(
            name = "Strength",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig strength = new PotionEffects.EffectConfig();

    @Page(
            name = "Jump Boost",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig jumpBoost = new PotionEffects.EffectConfig();

    @Page(
            name = "Nausea",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig nausea = new PotionEffects.EffectConfig();

    @Page(
            name = "Regeneration",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig regeneration = new PotionEffects.EffectConfig();

    @Page(
            name = "Resistance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig resistance = new PotionEffects.EffectConfig();

    @Page(
            name = "Fire Resistance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig fireResistance = new PotionEffects.EffectConfig();

    @Page(
            name = "Water Breathing",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig waterBreathing = new PotionEffects.EffectConfig();

    @Page(
            name = "Invisibility",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig invisibility = new PotionEffects.EffectConfig();

    @Page(
            name = "Blindness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig blindness = new PotionEffects.EffectConfig();

    @Page(
            name = "Night Vision",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig nightVision = new PotionEffects.EffectConfig();

    @Page(
            name = "Hunger",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig hunger = new PotionEffects.EffectConfig();

    @Page(
            name = "Weakness",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig weakness = new PotionEffects.EffectConfig();

    @Page(
            name = "Poison",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig poison = new PotionEffects.EffectConfig();

    @Page(
            name = "Wither",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig wither = new PotionEffects.EffectConfig();

    @Page(
            name = "Health Boost",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig healthBoost = new PotionEffects.EffectConfig();

    @Page(
            name = "Absorption",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig absorption = new PotionEffects.EffectConfig();

    @Page(
            name = "Saturation",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.EffectConfig saturation = new PotionEffects.EffectConfig();
    
    @HUD(
            name = "General HUD"
    )
    public static PotionEffects testHud = new PotionEffects();

    public PotionEffectsConfig() {
        super(new Mod(PotionEffectsMod.NAME, ModType.HUD), PotionEffectsMod.MODID + ".json");
        initialize();
        hideIf("Global Effects.override", true);
        hideIf("Global Effects.overrideComponent", true);
        hideIf("Global Effects.overrideAmplifier", true);
        hideIf("Global Effects.overrideBlinking", true);
        hideIf("Global Effects.overrideFormatting", true);
        hideIf("Global Effects.overrideColor", true);
        hideIf("Global Effects.overrideExclusion", true);
    }
}