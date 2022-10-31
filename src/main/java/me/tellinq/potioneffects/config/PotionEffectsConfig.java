package me.tellinq.potioneffects.config;

import me.tellinq.potioneffects.PotionEffectsMod;
import me.tellinq.potioneffects.hud.PotionEffects;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectsConfig extends Config {

    @Page(
            name = "Global Effects",
            description = "Change all non-overridden effects",
            location = PageLocation.TOP)
    public static PotionEffects.EffectConfig global = new PotionEffects.EffectConfig();

    @Page(
            name = "Speed",
            description = "Change the speed effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig speed = new PotionEffects.EffectConfig();

    @Page(
            name = "Slowness",
            description = "Change the slowness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig slowness = new PotionEffects.EffectConfig();

    @Page(
            name = "Haste",
            description = "Change the haste effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig haste = new PotionEffects.EffectConfig();

    @Page(
            name = "Mining Fatigue",
            description = "Change the mining fatigue effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig miningFatigue = new PotionEffects.EffectConfig();

    @Page(
            name = "Strength",
            description = "Change the strength effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig strength = new PotionEffects.EffectConfig();

    @Page(
            name = "Jump Boost",
            description = "Change the jump boost effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig jumpBoost = new PotionEffects.EffectConfig();

    @Page(
            name = "Nausea",
            description = "Change the nausea effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig nausea = new PotionEffects.EffectConfig();

    @Page(
            name = "Regeneration",
            description = "Change the regeneration effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig regeneration = new PotionEffects.EffectConfig();

    @Page(
            name = "Resistance",
            description = "Change the resistance effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig resistance = new PotionEffects.EffectConfig();

    @Page(
            name = "Fire Resistance",
            description = "Change the fire resistance effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig fireResistance = new PotionEffects.EffectConfig();

    @Page(
            name = "Water Breathing",
            description = "Change the water breathing effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig waterBreathing = new PotionEffects.EffectConfig();

    @Page(
            name = "Invisibility",
            description = "Change the invisibility effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig invisibility = new PotionEffects.EffectConfig();

    @Page(
            name = "Blindness",
            description = "Change the blindness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig blindness = new PotionEffects.EffectConfig();

    @Page(
            name = "Night Vision",
            description = "Change the night vision effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig nightVision = new PotionEffects.EffectConfig();

    @Page(
            name = "Hunger",
            description = "Change the hunger effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig hunger = new PotionEffects.EffectConfig();

    @Page(
            name = "Weakness",
            description = "Change the weakness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig weakness = new PotionEffects.EffectConfig();

    @Page(
            name = "Poison",
            description = "Change the poison effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig poison = new PotionEffects.EffectConfig();

    @Page(
            name = "Wither",
            description = "Change the wither effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig wither = new PotionEffects.EffectConfig();

    @Page(
            name = "Health Boost",
            description = "Change the health boost effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig healthBoost = new PotionEffects.EffectConfig();

    @Page(
            name = "Absorption",
            description = "Change the absorption effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig absorption = new PotionEffects.EffectConfig();

    @Page(
            name = "Saturation",
            description = "Change the saturation effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.EffectConfig saturation = new PotionEffects.EffectConfig();

    @HUD(name = "General HUD")
    public static PotionEffects testHud = new PotionEffects();

    public static List<String> effectNames = new ArrayList<>();

    static {
        effectNames.add("Global Effects");
        effectNames.add("Speed");
        effectNames.add("Slowness");
        effectNames.add("Haste");
        effectNames.add("Mining Fatigue");
        effectNames.add("Strength");
        effectNames.add("Jump Boost");
        effectNames.add("Nausea");
        effectNames.add("Regeneration");
        effectNames.add("Resistance");
        effectNames.add("Fire Resistance");
        effectNames.add("Water Breathing");
        effectNames.add("Invisibility");
        effectNames.add("Blindness");
        effectNames.add("Night Vision");
        effectNames.add("Hunger");
        effectNames.add("Weakness");
        effectNames.add("Poison");
        effectNames.add("Wither");
        effectNames.add("Health Boost");
        effectNames.add("Absorption");
        effectNames.add("Saturation");
    }

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

        for (String effectName : effectNames) {
            addDependency(effectName + ".overrideComponent", effectName + ".override");
            addDependency(effectName + ".overrideAmplifier", effectName + ".override");
            addDependency(effectName + ".overrideBlinking", effectName + ".override");
            addDependency(effectName + ".overrideFormatting", effectName + ".override");
            addDependency(effectName + ".overrideColor", effectName + ".override");
            addDependency(effectName + ".overrideExclusion", effectName + ".override");
        }
    }
}
