package me.tellinq.potioneffects.hud;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.libs.universal.UGraphics;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.libs.universal.UMinecraft;
import cc.polyfrost.oneconfig.renderer.RenderManager;

import com.google.common.collect.ImmutableMap;

import me.tellinq.potioneffects.config.PotionEffectsConfig;
import me.tellinq.potioneffects.event.UpdatePotionMetadataEvent;
import me.tellinq.potioneffects.util.RomanNumeral;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class PotionEffects extends BasicHud {

    @Dropdown(
            name = "Horizontal Alignment",
            description = "Choose if the alignment should be automatic or manual",
            options = {"Auto", "Left", "Center", "Right"})
    public int horizontalAlignment = 0;

    @Slider(
            name = "Vertical Spacing",
            description = "Adjust the spacing between effects",
            min = 0,
            max = 10)
    public float verticalSpacing = 4f;

    @DualOption(
            name = "Vertical Sorting",
            description = "Make sorting start from the top or bottom",
            left = "Top",
            right = "Bottom")
    public boolean verticalSorting = false;

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
            })
    public int sortingMethod = 0;

    @Switch(
            name = "Show Excluded Effects in HUD Editor",
            description = "Show potion effects that are excluded in the HUD editor")
    public boolean showExcludedEffects = true;

    @Info(text = "Not recommended to disable if all effects are excluded!", type = InfoType.WARNING)
    public String info;

    /** Each effect's icon texture size is 18 pixels. */
    @Exclude public final int ICON_SIZE = 18;

    /** Gets the inventory resource location. */
    @Exclude
    private final ResourceLocation EFFECTS_RESOURCE =
            new ResourceLocation("textures/gui/container/inventory.png");

    /** Gets OneConfig's Universal Minecraft instance. */
    @Exclude public final Minecraft mc = UMinecraft.getMinecraft();

    /** Gets OneConfig's Universal Minecraft fontRenderer. */
    @Exclude public final FontRenderer fontRenderer = UMinecraft.getFontRenderer();

    /**
     * Map of all of Minecraft's effect IDs, to the mod's config potion type respectively. <br>
     * Used to get if each potion config override is enabled or not in {@link
     * #getEffectSetting(PotionEffect)}
     */
    @Exclude
    public Map<Integer, EffectConfig> effectMap =
            new ImmutableMap.Builder<Integer, EffectConfig>()
                    .put(Potion.moveSpeed.id, PotionEffectsConfig.speed)
                    .put(Potion.moveSlowdown.id, PotionEffectsConfig.slowness)
                    .put(Potion.digSpeed.id, PotionEffectsConfig.haste)
                    .put(Potion.digSlowdown.id, PotionEffectsConfig.miningFatigue)
                    .put(Potion.damageBoost.id, PotionEffectsConfig.strength)
                    .put(Potion.jump.id, PotionEffectsConfig.jumpBoost)
                    .put(Potion.confusion.id, PotionEffectsConfig.nausea)
                    .put(Potion.regeneration.id, PotionEffectsConfig.regeneration)
                    .put(Potion.resistance.id, PotionEffectsConfig.resistance)
                    .put(Potion.fireResistance.id, PotionEffectsConfig.fireResistance)
                    .put(Potion.waterBreathing.id, PotionEffectsConfig.waterBreathing)
                    .put(Potion.blindness.id, PotionEffectsConfig.blindness)
                    .put(Potion.invisibility.id, PotionEffectsConfig.invisibility)
                    .put(Potion.nightVision.id, PotionEffectsConfig.nightVision)
                    .put(Potion.hunger.id, PotionEffectsConfig.hunger)
                    .put(Potion.weakness.id, PotionEffectsConfig.weakness)
                    .put(Potion.poison.id, PotionEffectsConfig.poison)
                    .put(Potion.wither.id, PotionEffectsConfig.wither)
                    .put(Potion.healthBoost.id, PotionEffectsConfig.healthBoost)
                    .put(Potion.absorption.id, PotionEffectsConfig.absorption)
                    .put(Potion.saturation.id, PotionEffectsConfig.saturation)
                    .build();

    /** Determines the mod's dimensional width. */
    private float width = 0f;

    /** Determines the mod's dimensional height. */
    private float height = 0f;

    /**
     * Continuously counts up every tick, and resets back to 0 if the current amount is over a
     * specific threshold determined by blinkSpeed.
     */
    @Exclude private int ticks = 0;
    /**
     * Used to set the current active potion effects. <br>
     * Also determines if the mod should show if the mod is not empty, or if {@link #currentEffects}
     * should reference this list if not empty, or use {@link #dummyEffects}..
     */
    @Exclude private List<PotionEffect> activeEffects = new ArrayList<>();

    /**
     * Set by either {@link #activeEffects} or {@link #dummyEffects} depending on if {@link
     * #activeEffects} is empty. <br>
     * Used to sort (see {@link #sortEffects(List)}), help set the height of the mod, and split each
     * effect to render independently.
     */
    @Exclude private List<PotionEffect> currentEffects = new ArrayList<>();

    /**
     * Used to list example effects that will not get updated at all. <br>
     * Only called after to set {@link #currentEffects} to this list if there are no active effects.
     */
    @Exclude private final List<PotionEffect> dummyEffects = new ArrayList<>();

    /**
     * The following default options have been modified: <br>
     * Background is disabled by default <br>
     * Padding is set to 0 by default
     */
    public PotionEffects() {
        super(
                true,
                0,
                0,
                1,
                false,
                false,
                0,
                0,
                0,
                new OneColor(0, 0, 0, 120),
                false,
                2,
                new OneColor(0, 0, 0));
        EventManager.INSTANCE.register(this);
    }

    /**
     * On every tick, this tick counter will add up every tick. <br>
     * Primarily used if a component should blink.
     */
    @Subscribe
    private void onTick(TickEvent event) {
        if (event.stage == Stage.START) {
            ++this.ticks;
        }
    }

    /**
     * On Forge's initialization, add the two example/dummy effects. <br>
     * Historically, in CheatBreaker, current versions of Lunar Client, and in early versions of the
     * mod, this used to constantly be re-added every partial tick as the field used to make an
     * array list was made every partial tick as well.
     */
    @Subscribe
    private void onInitialization(InitializationEvent event) {
        this.dummyEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
        this.dummyEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
    }

    /**
     * Gets the player's active effects and sets the current effect list to either: <br>
     * 1. The actual active effect list (if not empty) <br>
     * 2. The dummy list (if there are no active effects)
     */
    @Subscribe
    private void onUpdatePotionMetadata(UpdatePotionMetadataEvent event) {
        if (this.mc.thePlayer != null) {
            this.activeEffects = new ArrayList<>(this.mc.thePlayer.getActivePotionEffects());
            this.currentEffects =
                    this.activeEffects.isEmpty() ? this.dummyEffects : this.activeEffects;
        }
    }

    /**
     * @return True if the active player effects are not empty and the basic HUD conditions are met.
     */
    @Override
    protected boolean shouldShow() {
        return !this.activeEffects.isEmpty() && super.shouldShow();
    }

    /**
     * Draws the current effects, except for any marked as excluded.
     *
     * @param matrices The UMatrixStack used for rendering in higher versions
     * @param x Top left x-coordinate of the hud
     * @param y Top left y-coordinate of the hud
     * @param scale Scale of the hud
     * @param example If the HUD is being rendered in example form
     */
    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        UGraphics.disableLighting();

        final int actualHorizontal = this.getHorizontalAlignment();

        this.sortEffects(this.currentEffects);

        float yOffset = 0;
        float tempWidth = 0;
        int yAmount = (int) (this.ICON_SIZE + this.verticalSpacing);

        this.height = (this.currentEffects.size() * yAmount) - this.verticalSpacing;

        UGraphics.GL.pushMatrix();
        UGraphics.GL.scale(scale, scale, 1);
        UGraphics.GL.translate(x / scale, y / scale, 1);
        for (PotionEffect effect : this.currentEffects) {
            EffectConfig effectSetting = getEffectSetting(effect);
            // I wish there was a more efficient way of doing this...
            EffectConfig componentConfig =
                    this.checkCategoryOverride(effectSetting, effectSetting.overrideComponent);
            EffectConfig amplifierConfig =
                    this.checkCategoryOverride(effectSetting, effectSetting.overrideAmplifier);
            EffectConfig blinkingConfig =
                    this.checkCategoryOverride(effectSetting, effectSetting.overrideBlinking);
            EffectConfig formattingConfig =
                    this.checkCategoryOverride(effectSetting, effectSetting.overrideFormatting);
            EffectConfig colorConfig =
                    this.checkCategoryOverride(effectSetting, effectSetting.overrideColor);
            EffectConfig exclusionConfig =
                    this.checkCategoryOverride(effectSetting, effectSetting.overrideExclusion);

            boolean excluded = false;
            if (this.excludePotions(exclusionConfig, effect)) {
                if (example && this.showExcludedEffects) {
                    excluded = true;
                } else {
                    this.height -= yAmount;
                    continue;
                }
            }

            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) {
                continue;
            }

            float iconPos = componentConfig.icon ? 20.0F : 0.0F;

            int titleWidth = 0;
            if (componentConfig.effectName) {
                StringBuilder titleBuilder = new StringBuilder();
                // I really hope there's a more efficient way of setting this up...
                if (formattingConfig.boldEffectName) {
                    titleBuilder.append(EnumChatFormatting.BOLD);
                }

                if (formattingConfig.italicEffectName) {
                    titleBuilder.append(EnumChatFormatting.ITALIC);
                }

                if (formattingConfig.underlineEffectName) {
                    titleBuilder.append(EnumChatFormatting.UNDERLINE);
                }

                if (formattingConfig.customName.isEmpty()) {
                    titleBuilder.append(I18n.format(potion.getName()));
                } else {
                    titleBuilder.append(formattingConfig.customName);
                }

                int amplifier = Math.max(1, effect.getAmplifier() + 1);
                if (amplifierConfig.amplifier && (amplifier != 1 || amplifierConfig.levelOne)) {
                    titleBuilder.append(" ");
                    if (!amplifierConfig.romanNumerals) {
                        titleBuilder.append(RomanNumeral.INSTANCE.getCache(amplifier));
                    } else {
                        titleBuilder.append(amplifier);
                    }
                }

                String builtTitle = titleBuilder.toString();

                titleWidth = this.fontRenderer.getStringWidth(builtTitle);

                float titleX = 0;
                float titleY = yOffset;
                if (!componentConfig.duration) {
                    titleY += this.fontRenderer.FONT_HEIGHT / 2f + 0.5f;
                }

                switch (actualHorizontal) {
                    case 0:
                        titleX = iconPos;
                        break;
                    case 1:
                        titleX =
                                this.width / 2.0f
                                        - (float) (titleWidth / 2)
                                        + iconPos
                                        - (iconPos / 2);
                        break;
                    case 2:
                        titleX = this.width - titleWidth - iconPos;
                }

                if (showDuringBlink(
                        blinkingConfig,
                        blinkingConfig.makeEffectNameBlink,
                        effect.getDuration(),
                        example)) {
                    RenderManager.drawScaledString(
                            builtTitle,
                            titleX,
                            titleY,
                            getColor(colorConfig.nameColor.getRGB(), excluded),
                            RenderManager.TextType.toType(formattingConfig.textType),
                            1);
                }

                tempWidth = Math.max(tempWidth, titleWidth + iconPos);
            }

            int timeWidth = 0;
            if (componentConfig.duration) {

                StringBuilder timeBuilder = new StringBuilder();
                // I really hope there's a more efficient way of setting this up...
                if (formattingConfig.boldDuration) {
                    timeBuilder.append(EnumChatFormatting.BOLD);
                }

                if (formattingConfig.italicDuration) {
                    timeBuilder.append(EnumChatFormatting.ITALIC);
                }

                if (formattingConfig.underlineDuration) {
                    timeBuilder.append(EnumChatFormatting.UNDERLINE);
                }

                if (effect.getIsPotionDurationMax()) {
                    timeBuilder.append(formattingConfig.maxDurationString);
                } else {
                    switch (formattingConfig.durationFormat) {
                        case 0:
                            timeBuilder.append(Potion.getDurationString(effect));
                            break;
                        case 1:
                            timeBuilder.append(effect.getDuration() / 20).append("s");
                            break;
                        case 2:
                            timeBuilder.append(
                                    RomanNumeral.INSTANCE.getCache(effect.getDuration() / 20));
                    }
                }

                String builtTime = timeBuilder.toString();

                timeWidth = this.fontRenderer.getStringWidth(builtTime);

                float timeX = 0;
                float timeY = yOffset + this.fontRenderer.FONT_HEIGHT + 1;

                if (!componentConfig.effectName) {
                    timeY -= this.fontRenderer.FONT_HEIGHT / 2f + 0.5f;
                }

                if (showDuringBlink(
                        blinkingConfig,
                        blinkingConfig.makeEffectDurationBlink,
                        effect.getDuration(),
                        example)) {
                    switch (actualHorizontal) {
                        case 0:
                            timeX = iconPos;
                            break;
                        case 1:
                            timeX =
                                    this.width / 2f
                                            - (float) (timeWidth / 2)
                                            + iconPos
                                            - (iconPos / 2);
                            break;
                        case 2:
                            timeX = this.width - timeWidth - iconPos;
                    }
                    RenderManager.drawScaledString(
                            builtTime,
                            timeX,
                            timeY,
                            getColor(colorConfig.durationColor.getRGB(), excluded),
                            RenderManager.TextType.toType(formattingConfig.textType),
                            1);
                }

                tempWidth = Math.max(tempWidth, iconPos + timeWidth);
            }

            if (componentConfig.icon) {
                UGraphics.color4f(1f, 1f, 1f, excluded ? 0.5f : 1f);
                this.mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                float iconX = 0;
                switch (actualHorizontal) {
                    case 0:
                        iconX = 0.0f;
                        break;
                    case 1:
                        iconX =
                                this.width / 2f
                                        - (float)
                                                ((componentConfig.effectName
                                                                ? titleWidth
                                                                : timeWidth)
                                                        / 2)
                                        - (iconPos / 2);
                        break;
                    case 2:
                        iconX = this.width - iconPos;
                }
                if (showDuringBlink(
                        blinkingConfig,
                        blinkingConfig.makeEffectIconBlink,
                        effect.getDuration(),
                        example)) {
                    this.mc.ingameGUI.drawTexturedModalRect(
                            iconX,
                            yOffset,
                            potion.getStatusIconIndex() % 8 * 18,
                            198 + potion.getStatusIconIndex() / 8 * 18,
                            18,
                            18);
                }

                tempWidth = Math.max(tempWidth, iconPos);
            }

            yOffset += yAmount;
        }
        this.width = tempWidth;
        UGraphics.GL.popMatrix();
    }

    /**
     * Gets the horizontal alignment based off the mod's {@link #position} anchor (if {@link
     * #horizontalAlignment} is 0 (Auto)) or the manual alignment via {@link #horizontalAlignment}.
     *
     * @return The int corresponding to the mod's alignment <br>
     *     0: Left horizontal alignment <br>
     *     1: Center horizontal alignment <br>
     *     2: Right horizontal alignment
     */
    private int getHorizontalAlignment() {
        if (this.horizontalAlignment == 0) {
            switch (position.anchor) {
                case TOP_LEFT:
                case MIDDLE_LEFT:
                case BOTTOM_LEFT:
                    return 0;
                case TOP_CENTER:
                case MIDDLE_CENTER:
                case BOTTOM_CENTER:
                    return 1;
                case TOP_RIGHT:
                case MIDDLE_RIGHT:
                case BOTTOM_RIGHT:
                    return 2;
                default:
                    return this.horizontalAlignment - 1;
            }
        }
        return this.horizontalAlignment - 1;
    }

    /**
     * Sorts all the current potion effects based off what the user set in {@link #sortingMethod}
     * <br>
     * 0: Sorts by Potion ID (Vanilla behavior) <br>
     * 1: Sorts by alphabetical name <br>
     * 2: Sorts based off duration. <br>
     * 3: Sorts based off amplifier. <br>
     * 4: Sorts prioritizing ambient (beacon) effects. <br>
     * 5: Sorts prioritizing effects showing particles. <br>
     * Optionally, the entire list can get reversed if the user enables {@link #verticalSorting}.
     *
     * @param effects {@link #currentEffects}
     */
    public void sortEffects(List<PotionEffect> effects) {
        switch (this.sortingMethod) {
            case 0:
                effects.sort(Comparator.comparingInt(PotionEffect::getPotionID));
                break;
            case 1:
                effects.sort(Comparator.comparing(effect -> I18n.format(effect.getEffectName())));
                break;
            case 2:
                effects.sort(Comparator.comparingInt(PotionEffect::getDuration));
                break;
            case 3:
                effects.sort(Comparator.comparingInt(PotionEffect::getAmplifier));
                Collections.reverse(effects);
                break;
            case 4:
                effects.sort(Comparator.comparing(PotionEffect::getIsAmbient));
                break;
            case 5:
                effects.sort(Comparator.comparing(PotionEffect::getIsShowParticles));
                break;
            case 6:
                effects.sort(
                        Comparator.comparing(
                                effect -> Potion.potionTypes[effect.getPotionID()].isBadEffect()));
        }

        if (this.verticalSorting) {
            Collections.reverse(effects);
        }
    }

    /**
     * @param config The current effect's configuration (global or effect specific)
     * @param blinkComponent If the set component should blink
     * @param duration The effect's duration
     * @param example If the HUD is being rendered in example form
     * @return False if the duration amount or tick counter is over the threshold determined by blinkSpeed.
     */
    private boolean showDuringBlink(
            EffectConfig config, boolean blinkComponent, float duration, boolean example) {
        if (config.blink && blinkComponent) {
            if (duration <= config.blinkDuration * 20.0f) {
                if (config.syncBlinking || (example && this.activeEffects.isEmpty())) {
                    float threshold = config.blinkSpeed / 3.0f;
                    if (this.ticks > threshold * 2) {
                        this.ticks = 0;
                    }
                    return this.ticks <= threshold;
                } else {
                    float threshold = 50 - config.blinkSpeed;
                    return duration % threshold <= threshold / 2.0f;
                }
            }
        }
        return true;
    }

    /**
     * @param effect The current potion effect
     * @return The specific effect config's based off the effectMap if overridden, or the global
     *     configuration if not overridden.
     */
    public EffectConfig getEffectSetting(PotionEffect effect) {
        for (Map.Entry<Integer, EffectConfig> entry : this.effectMap.entrySet()) {
            if (effect.getPotionID() == entry.getKey()) {
                if (entry.getValue().override) return entry.getValue();
            }
        }
        return PotionEffectsConfig.global;
    }

    /**
     * Checks if a certain category is overridden, or resorts to global.
     *
     * @param category The current effect setting.
     * @param override The override check based off the category's override check.
     * @return True if the category's override check is enabled.
     */
    public EffectConfig checkCategoryOverride(EffectConfig category, boolean override) {
        return override ? category : PotionEffectsConfig.global;
    }

    /**
     * Exclude the current effect depending on exclusion configuration
     *
     * @param setting The current effect's setting.
     * @param effect The current potion effect
     * @return True if one of the exclusion conditions is set to true
     */
    private boolean excludePotions(EffectConfig setting, PotionEffect effect) {
        if (this.excludeCondition(
                setting.permanentExclusionRule, effect.getIsPotionDurationMax())) {
            return true;
        }

        if (this.excludeCondition(setting.ambientExclusionRule, effect.getIsAmbient())) {
            return true;
        }

        if (this.excludeCondition(setting.particlesExclusionRule, effect.getIsShowParticles())) {
            return true;
        }

        if (this.excludeCondition(
                setting.badEffectsExclusionRule,
                Potion.potionTypes[effect.getPotionID()].isBadEffect())) {
            return true;
        }

        if (this.excludeAmount(
                        setting.excludeSetDuration,
                        effect.getDuration(),
                        setting.excludedDurationValues * 20.0F)
                && !effect.getIsPotionDurationMax()) {
            return true;
        }

        if (this.excludeAmount(
                setting.excludeSetAmplifier,
                effect.getAmplifier(),
                setting.excludedAmplifierValues - 1)) {
            return true;
        }

        return setting.exclude;
    }

    /**
     * @param rule The configuration rule
     * @param value The current effect's value
     * @return True depending on if the current value satisfies the exclusion rule <br>
     *     1: Matches the effect's value <br>
     *     2: Does not match the effect's value
     */
    protected boolean excludeCondition(int rule, boolean value) {
        switch (rule) {
            case 1:
                return value;
            case 2:
                return !value;
            default:
                return false;
        }
    }

    /**
     * @param rule The configuration rule
     * @param value The current effect's value
     * @param threshold The threshold amount
     * @return True depending on the rule number set <br>
     *     1: Current value exceeds threshold <br>
     *     2: Current value is below threshold <br>
     *     3: Current value matches threshold <br>
     *     4: Current value does not match threshold
     */
    protected boolean excludeAmount(int rule, int value, float threshold) {
        switch (rule) {
            case 1:
                return value > threshold;
            case 2:
                return value < threshold;
            case 3:
                return value == threshold;
            case 4:
                return value != threshold;
            default:
                return false;
        }
    }

    /**
     * @param color The color given
     * @param excluded If the effect is excluded
     * @return The given color or half of the color's opacity if the effect is excluded.
     */
    private int getColor(int color, boolean excluded) {
        int opacity = color >> 24 & 0xFF;
        opacity = (int) ((float) opacity * (excluded ? 0.5F : 1.0F));
        return color & 0xFFFFFF | opacity << 24;
    }

    /**
     * @param scale Scale of the hud
     * @param example If the HUD is being rendered in example form
     * @return The width of the HUD multiplied by the HUD's scale
     */
    @Override
    protected float getWidth(float scale, boolean example) {
        return this.width * scale;
    }

    /**
     * @param scale Scale of the hud
     * @param example If the HUD is being rendered in example form
     * @return The height of the HUD multiplied by the HUD's scale
     */
    @Override
    protected float getHeight(float scale, boolean example) {
        return this.height * scale;
    }

    /** All the individual config settings. */
    public static class EffectConfig {
        @Switch(
                name = "Override",
                description = "Let this specific effect override",
                subcategory = "Override",
                size = 2)
        public boolean override = false;

        @Checkbox(
                name = "Override Component",
                description = "Override the component category",
                subcategory = "Override")
        public boolean overrideComponent = false;

        @Checkbox(
                name = "Override Amplifier",
                description = "Override the amplifier category",
                subcategory = "Override")
        public boolean overrideAmplifier = false;

        @Checkbox(
                name = "Override Blinking",
                description = "Override the blinking category",
                subcategory = "Override")
        public boolean overrideBlinking = false;

        @Checkbox(
                name = "Override Formatting",
                description = "Override the formatting category",
                subcategory = "Override")
        public boolean overrideFormatting = true;

        @Checkbox(
                name = "Override Color",
                description = "Override the color category",
                subcategory = "Override")
        public boolean overrideColor = true;

        @Checkbox(
                name = "Override Exclusion",
                description = "Override the exclusion category",
                subcategory = "Override")
        public boolean overrideExclusion = true;

        @Checkbox(name = "Icon", description = "Show the effect icon", subcategory = "Component")
        public boolean icon = true;

        @Checkbox(
                name = "Effect Name",
                description = "Show the effect name",
                subcategory = "Component")
        public boolean effectName = true;

        @Checkbox(
                name = "Duration",
                description = "Show the effect duration",
                subcategory = "Component")
        public boolean duration = true;

        @Switch(
                name = "Show Amplifier",
                description = "Show the amplifier amount next to the effect name",
                subcategory = "Amplifier")
        public boolean amplifier = true;

        @Switch(
                name = "Show Level One",
                description = "Show the amplifier if the effect level is one",
                subcategory = "Amplifier")
        public boolean levelOne = false;

        @DualOption(
                name = "Amplifier Numerals",
                description = "Choose to show roman numerals or arabic amount",
                subcategory = "Amplifier",
                left = "Roman",
                right = "Arabic")
        public boolean romanNumerals = false;

        /*@Slider(
                name = "Order Priority",
                description = "Higher numbers will indicate higher priority",
                subcategory = "Sorting",
                step = 1,
                min = -15,
                max = 15
        )
        public int orderPriority = 0;*/
        // To be implemented

        @Switch(
                name = "Blink",
                description = "Make the potion effects blink when the duration is low",
                subcategory = "Blinking")
        public boolean blink = true;

        @Switch(
                name = "Sync Blinking",
                description =
                        "Make blinking synced with tick counts or make blinking go based off the"
                                + " duration time.",
                subcategory = "Blinking")
        public boolean syncBlinking = true;

        @Header(text = "Blinking components", subcategory = "Blinking", size = 2)
        public boolean blinkingIgnored = true;

        @Checkbox(name = "Icon", description = "Make the icon blink", subcategory = "Blinking")
        public boolean makeEffectIconBlink = false;

        @Checkbox(
                name = "Effect Name",
                description = "Make the effect name blink",
                subcategory = "Blinking")
        public boolean makeEffectNameBlink = false;

        @Checkbox(
                name = "Duration",
                description = "Make the duration blink",
                subcategory = "Blinking")
        public boolean makeEffectDurationBlink = true;

        @Slider(
                name = "Blink Duration",
                description = "The duration the effect should start blinking at",
                subcategory = "Blinking",
                min = 0,
                max = 60)
        public float blinkDuration = 10;

        @Slider(
                name = "Blink Speed",
                description = "The speed of which the effect should blink at",
                subcategory = "Blinking",
                min = 0,
                max = 60)
        public float blinkSpeed = 30;

        @Dropdown(
                name = "Text Type",
                subcategory = "Formatting",
                options = {"No Shadow", "Shadow", "Full Shadow"})
        public int textType = 1;

        @Header(text = "Effect Name", subcategory = "Formatting", size = 2)
        public boolean effectNameFormattingHeader;

        @Text(
                name = "Custom Name",
                description = "Override the effect name with a custom one",
                subcategory = "Formatting",
                size = 2)
        public String customName = "";

        @Checkbox(
                name = "Bold Effect Name",
                description = "Bold the effect name",
                subcategory = "Formatting")
        public boolean boldEffectName = false;

        @Checkbox(
                name = "Italic Effect Name",
                description = "Make the effect name italic",
                subcategory = "Formatting")
        public boolean italicEffectName = false;

        @Checkbox(
                name = "Underline Effect Name",
                description = "Underline the effect name",
                subcategory = "Formatting")
        public boolean underlineEffectName = false;

        @Header(text = "Duration", subcategory = "Formatting", size = 2)
        public boolean durationFormattingHeader;

        @Text(
                name = "Max Duration String",
                description = "The text that should show when you have a permanent effect",
                subcategory = "Formatting",
                size = 2)
        public String maxDurationString = "**:**";

        @Dropdown(
                name = "Duration Format",
                description = "Choose how the duration text should be formatted",
                subcategory = "Formatting",
                options = {"Standard", "Split Format", "Roman Numerals"},
                size = 2)
        public int durationFormat = 0;

        @Checkbox(
                name = "Bold Duration",
                description = "Bold the duration text",
                subcategory = "Formatting")
        public boolean boldDuration = false;

        @Checkbox(
                name = "Italic Duration",
                description = "Make the duration text italic",
                subcategory = "Formatting")
        public boolean italicDuration = false;

        @Checkbox(
                name = "Underline Duration",
                description = "Underline the duration text",
                subcategory = "Formatting")
        public boolean underlineDuration = false;

        @Color(
                name = "Name Color",
                description = "The color of the effect name",
                subcategory = "Color")
        public OneColor nameColor = new OneColor(255, 255, 255);

        @Color(
                name = "Duration Color",
                description = "The color of the duration text",
                subcategory = "Color")
        public OneColor durationColor = new OneColor(255, 255, 255);

        @Checkbox(
                name = "Exclude",
                description = "Completely exclude the effect(s)",
                subcategory = "Exclusion",
                size = 2)
        public boolean exclude = false;

        @Dropdown(
                name = "Duration Amount Rule",
                description =
                        "Exclude effects that are either above, below, at, or not at a certain"
                                + " duration threshold",
                options = {
                    "None",
                    "Exclude All Above",
                    "Exclude All Below",
                    "Exclude All At",
                    "Exclude All Not At"
                },
                subcategory = "Exclusion")
        public int excludeSetDuration = 0;

        @Dropdown(
                name = "Amplifier Amount Rule",
                description =
                        "Exclude effects that are either above, below, at, or not at a certain"
                                + " amplifier amount",
                options = {
                    "None",
                    "Exclude All Above",
                    "Exclude All Below",
                    "Exclude All At",
                    "Exclude All Not At"
                },
                subcategory = "Exclusion")
        public int excludeSetAmplifier = 0;

        @Dropdown(
                name = "Permanent Effects Rule",
                description = "Decide if permanent or temporary effects should be excluded.",
                subcategory = "Exclusion",
                options = {
                    "None",
                    "Exclude All Permanent Effects",
                    "Exclude All Temporary Effects"
                })
        public int permanentExclusionRule = 0;

        @Dropdown(
                name = "Ambient Effects Rule",
                description = "Decide if effects from or not from a beacon should be excluded.",
                subcategory = "Exclusion",
                options = {
                    "None",
                    "Exclude All Ambient Effects",
                    "Exclude All Non Ambient Effects"
                })
        public int ambientExclusionRule = 0;

        @Dropdown(
                name = "Emitting Particles Rule",
                description = "Decide if emitting/disallowing particle effects should be excluded.",
                subcategory = "Exclusion",
                options = {
                    "None",
                    "Exclude All Emitting Particles",
                    "Exclude All Disallowing Particles"
                })
        public int particlesExclusionRule = 0;

        @Dropdown(
                name = "Bad Effects Rule",
                description = "Decide if good or bad effects should be excluded.",
                subcategory = "Exclusion",
                options = {"None", "Exclude All Bad Effects", "Exclude All Good Effects"})
        public int badEffectsExclusionRule = 0;

        @Slider(
                name = "Excluded Duration Threshold",
                description = "The value(s) that will be excluded based off the duration rule",
                subcategory = "Exclusion",
                min = 2,
                max = 90,
                step = 1)
        public float excludedDurationValues = 30f;

        @Slider(
                name = "Excluded Amplifier Value(s)",
                description = "The value(s) that will be excluded based off the amplifier rule",
                subcategory = "Exclusion",
                min = 0,
                max = 20,
                step = 1)
        public int excludedAmplifierValues = 10;

        public EffectConfig() {}
    }
}
