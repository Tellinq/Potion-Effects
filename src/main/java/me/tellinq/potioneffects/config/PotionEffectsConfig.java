package me.tellinq.potioneffects.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;

import me.tellinq.potioneffects.PotionEffectsMod;
import me.tellinq.potioneffects.hud.PotionEffects;

import net.minecraft.potion.Potion;

import java.util.*;

public class PotionEffectsConfig extends Config {
    public static List<String> effectNames = new ArrayList<>();
    @Exclude public static PotionEffectsConfig INSTANCE;

    @Page(
            name = "Global Effects",
            description = "Change all non-overridden effects",
            location = PageLocation.TOP)
    public static PotionEffects.Effect global = new PotionEffects.Effect("Global Effects");

    @Page(
            name = "Speed",
            description = "Change the speed effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect speed =
            new PotionEffects.Effect("Speed", Potion.moveSpeed.id);

    @Page(
            name = "Slowness",
            description = "Change the slowness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect slowness =
            new PotionEffects.Effect("Slowness", Potion.moveSlowdown.id);

    @Page(
            name = "Haste",
            description = "Change the haste effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect haste =
            new PotionEffects.Effect("Haste", Potion.digSpeed.id);

    @Page(
            name = "Mining Fatigue",
            description = "Change the mining fatigue effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect miningFatigue =
            new PotionEffects.Effect("Mining Fatigue", Potion.digSlowdown.id);

    @Page(
            name = "Strength",
            description = "Change the strength effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect strength =
            new PotionEffects.Effect("Strength", Potion.damageBoost.id);

    @Page(
            name = "Jump Boost",
            description = "Change the jump boost effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect jumpBoost =
            new PotionEffects.Effect("Jump Boost", Potion.jump.id);

    @Page(
            name = "Nausea",
            description = "Change the nausea effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect nausea =
            new PotionEffects.Effect("Nausea", Potion.confusion.id);

    @Page(
            name = "Regeneration",
            description = "Change the regeneration effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect regeneration =
            new PotionEffects.Effect("Regeneration", Potion.regeneration.id);

    @Page(
            name = "Resistance",
            description = "Change the resistance effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect resistance =
            new PotionEffects.Effect("Resistance", Potion.resistance.id);

    @Page(
            name = "Fire Resistance",
            description = "Change the fire resistance effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect fireResistance =
            new PotionEffects.Effect("Fire Resistance", Potion.fireResistance.id);

    @Page(
            name = "Water Breathing",
            description = "Change the water breathing effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect waterBreathing =
            new PotionEffects.Effect("Water Breathing", Potion.waterBreathing.id);

    @Page(
            name = "Invisibility",
            description = "Change the invisibility effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect invisibility =
            new PotionEffects.Effect("Invisibility", Potion.invisibility.id);

    @Page(
            name = "Blindness",
            description = "Change the blindness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect blindness =
            new PotionEffects.Effect("Blindness", Potion.blindness.id);

    @Page(
            name = "Night Vision",
            description = "Change the night vision effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect nightVision =
            new PotionEffects.Effect("Night Vision", Potion.nightVision.id);

    @Page(
            name = "Hunger",
            description = "Change the hunger effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect hunger =
            new PotionEffects.Effect("Hunger", Potion.hunger.id);

    @Page(
            name = "Weakness",
            description = "Change the weakness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect weakness =
            new PotionEffects.Effect("Weakness", Potion.weakness.id);

    @Page(
            name = "Poison",
            description = "Change the poison effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect poison =
            new PotionEffects.Effect("Poison", Potion.poison.id);

    @Page(
            name = "Wither",
            description = "Change the wither effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect wither =
            new PotionEffects.Effect("Wither", Potion.wither.id);

    @Page(
            name = "Health Boost",
            description = "Change the health boost effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect healthBoost =
            new PotionEffects.Effect("Health Boost", Potion.healthBoost.id);

    @Page(
            name = "Absorption",
            description = "Change the absorption effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect absorption =
            new PotionEffects.Effect("Absorption", Potion.absorption.id);

    @Page(
            name = "Saturation",
            description = "Change the saturation effect's appearance",
            location = PageLocation.TOP,
            category = "Effects")
    public static PotionEffects.Effect saturation =
            new PotionEffects.Effect("Saturation", Potion.saturation.id);

    @HUD(name = "General HUD")
    public static PotionEffects testHud = new PotionEffects();

    public PotionEffectsConfig() {
        super(new Mod(PotionEffectsMod.NAME, ModType.HUD), PotionEffectsMod.MODID + ".json");
        INSTANCE = this;
        initialize();
        hideIf("Global Effects.override", () -> true);
        hideIf("Global Effects.overrideComponent", () -> true);
        hideIf("Global Effects.overrideAmplifier", () -> true);
        hideIf("Global Effects.overrideBlinking", () -> true);
        hideIf("Global Effects.overrideFormatting", () -> true);
        hideIf("Global Effects.overrideColor", () -> true);
        hideIf("Global Effects.overrideExclusion", () -> true);

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
