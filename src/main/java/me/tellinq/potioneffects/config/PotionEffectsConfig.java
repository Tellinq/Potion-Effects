package me.tellinq.potioneffects.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.InfoType;
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
            location = PageLocation.TOP
    )
    public static PotionEffects.Effect global = new PotionEffects.Effect("Global Effects");

    @Page(
            name = "Speed",
            description = "Change the speed effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect speed =
            new PotionEffects.Effect("Speed", Potion.moveSpeed.id);

    @Page(
            name = "Slowness",
            description = "Change the slowness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect slowness =
            new PotionEffects.Effect("Slowness", Potion.moveSlowdown.id);

    @Page(
            name = "Haste",
            description = "Change the haste effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect haste =
            new PotionEffects.Effect("Haste", Potion.digSpeed.id);

    @Page(
            name = "Mining Fatigue",
            description = "Change the mining fatigue effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect miningFatigue =
            new PotionEffects.Effect("Mining Fatigue", Potion.digSlowdown.id);

    @Page(
            name = "Strength",
            description = "Change the strength effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect strength =
            new PotionEffects.Effect("Strength", Potion.damageBoost.id);

    @Page(
            name = "Jump Boost",
            description = "Change the jump boost effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect jumpBoost =
            new PotionEffects.Effect("Jump Boost", Potion.jump.id);

    @Page(
            name = "Nausea",
            description = "Change the nausea effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect nausea =
            new PotionEffects.Effect("Nausea", Potion.confusion.id);

    @Page(
            name = "Regeneration",
            description = "Change the regeneration effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect regeneration =
            new PotionEffects.Effect("Regeneration", Potion.regeneration.id);

    @Page(
            name = "Resistance",
            description = "Change the resistance effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect resistance =
            new PotionEffects.Effect("Resistance", Potion.resistance.id);

    @Page(
            name = "Fire Resistance",
            description = "Change the fire resistance effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect fireResistance =
            new PotionEffects.Effect("Fire Resistance", Potion.fireResistance.id);

    @Page(
            name = "Water Breathing",
            description = "Change the water breathing effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect waterBreathing =
            new PotionEffects.Effect("Water Breathing", Potion.waterBreathing.id);

    @Page(
            name = "Invisibility",
            description = "Change the invisibility effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect invisibility =
            new PotionEffects.Effect("Invisibility", Potion.invisibility.id);

    @Page(
            name = "Blindness",
            description = "Change the blindness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect blindness =
            new PotionEffects.Effect("Blindness", Potion.blindness.id);

    @Page(
            name = "Night Vision",
            description = "Change the night vision effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect nightVision =
            new PotionEffects.Effect("Night Vision", Potion.nightVision.id);

    @Page(
            name = "Hunger",
            description = "Change the hunger effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect hunger =
            new PotionEffects.Effect("Hunger", Potion.hunger.id);

    @Page(
            name = "Weakness",
            description = "Change the weakness effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect weakness =
            new PotionEffects.Effect("Weakness", Potion.weakness.id);

    @Page(
            name = "Poison",
            description = "Change the poison effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
    public static PotionEffects.Effect poison =
            new PotionEffects.Effect("Poison", Potion.poison.id);

    @Page(
            name = "Wither",
            description = "Change the wither effect's appearance",
            location = PageLocation.TOP,
            category = "Effects"
    )
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

    @Dropdown(
            name = "Horizontal Alignment",
            description = "Choose if the alignment should be automatic or manual",
            options = {"Auto", "Left", "Center", "Right"},
            subcategory = "Dimensions"
    )
    public int horizontalAlignment = 0;

    @Slider(
            name = "Vertical Spacing",
            description = "Adjust the spacing between effects",
            min = 0,
            max = 10,
            subcategory = "Dimensions"
    )
    public float verticalSpacing = 4f;

    @Dropdown(
            name = "Sorting Method",
            description = "Choose how the potion effects should be sorted",
            options = {
                    "Potion ID (Vanilla)",
                    "Alphabetical",
                    "Duration",
                    "Amplifier",
                    "Ambient",
                    "Particles",
                    "Bad Effects"
            },
            subcategory = "Sorting"
    )
    public int sortingMethod = 0;

    @DualOption(
            name = "Vertical Sorting",
            description = "Make sorting start from the top or bottom",
            left = "Top",
            right = "Bottom",
            subcategory = "Sorting"
    )
    public boolean verticalSorting = false;

    @Switch(
            name = "Show Vanilla Inventory Potion Info",
            description = "Show the vanilla potion info",
            subcategory = "Inventory"
    )
    public boolean showPotionInfo = false;

    @Switch(
            name = "Show HUD in Foreground",
            description = "Show the HUD element in foreground",
            subcategory = "Inventory"
    )
    public boolean showHudInForeground = false;

    @Dropdown(
            name = "Show Excluded Effects in HUD Editor",
            options = {"Never", "When Not Overflowing", "Always"},
            description = "Show potion effects that are excluded in the HUD editor",
            size = 2,
            subcategory = "Exclusion"
    )
    public int showExcludedEffects = 1;

    @HUD(name = "General HUD")
    public static PotionEffects hud = new PotionEffects();

    public PotionEffectsConfig() {
        super(new Mod(PotionEffectsMod.NAME, ModType.HUD), PotionEffectsMod.MODID + ".json");
        INSTANCE = this;
        this.initialize();
        this.hideIf("Global Effects.override", () -> true);
        this.hideIf("Global Effects.overrideComponent", () -> true);
        this.hideIf("Global Effects.overrideAmplifier", () -> true);
        this.hideIf("Global Effects.overrideBlinking", () -> true);
        this.hideIf("Global Effects.overrideFormatting", () -> true);
        this.hideIf("Global Effects.overrideColor", () -> true);
        this.hideIf("Global Effects.overrideExclusion", () -> true);

        for (String effectName : effectNames) {
            this.addDependency(effectName + ".overrideComponent", effectName + ".override");
            this.addDependency(effectName + ".overrideAmplifier", effectName + ".override");
            this.addDependency(effectName + ".overrideBlinking", effectName + ".override");
            this.addDependency(effectName + ".overrideFormatting", effectName + ".override");
            this.addDependency(effectName + ".overrideColor", effectName + ".override");
            this.addDependency(effectName + ".overrideExclusion", effectName + ".override");
        }
    }
}
