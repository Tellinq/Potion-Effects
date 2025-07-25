package me.tellinq.potioneffects.hud;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.libs.universal.UGraphics;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.libs.universal.UMinecraft;

import cc.polyfrost.oneconfig.renderer.TextRenderer;
import com.google.common.collect.ImmutableMap;

import me.tellinq.potioneffects.config.PotionEffectsConfig;
import me.tellinq.potioneffects.event.UpdatePotionEffectsEvent;
import me.tellinq.potioneffects.mixin.GuiAccessor;
import me.tellinq.potioneffects.util.RomanNumeral;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PotionEffects extends BasicHud {

    /** Each effect's icon texture size is 18 pixels. */
    @Exclude public static final int ICON_SIZE = 18;

    @Exclude public int componentAmount = 0;
    @Exclude public boolean oneComponentActive = false;

    /** Gets the inventory resource location. */
    @Exclude
    private final ResourceLocation EFFECTS_RESOURCE =
            new ResourceLocation("textures/gui/container/inventory.png");

    /** Gets OneConfig's Universal Minecraft instance. */
    @Exclude public static final Minecraft mc = UMinecraft.getMinecraft();

    /** Gets OneConfig's Universal Minecraft fontRenderer. */
    @Exclude public static final FontRenderer fontRenderer = UMinecraft.getFontRenderer();

    /**
     * Map of all of Minecraft's effect IDs, to the mod's config potion type respectively. <br>
     * Used to check each effect config's override status ({@link #getEffectSetting(PotionEffect)})
     */
    @Exclude
    public Map<Integer, Effect> effects =
            new ImmutableMap.Builder<Integer, Effect>()
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

    @Exclude private static final List<Comparator<PotionEffect>> sortingMethods = Arrays.asList(
            Comparator.comparingInt(PotionEffect::getPotionID),
            Comparator.comparing(effect -> I18n.format(effect.getEffectName())),
            Comparator.comparingInt(PotionEffect::getDuration),
            Comparator.comparingInt(PotionEffect::getAmplifier).reversed(),
            Comparator.comparing(PotionEffect::getIsAmbient),
            Comparator.comparing(PotionEffect::getIsShowParticles),
            Comparator.comparing(effect -> Potion.potionTypes[effect.getPotionID()].isBadEffect())
    );

    @Exclude private final List<BiPredicate<Component, PotionEffect>> componentConditions = Arrays.asList(
            (c, e) -> excludeCondition(c.permanentEffectsRule, e.getIsPotionDurationMax()),
            (c, e) -> excludeCondition(c.ambientEffectsRule, e.getIsAmbient()),
            (c, e) -> excludeCondition(c.emittingParticlesRule, e.getIsShowParticles()),
            (c, e) -> excludeCondition(c.badEffectsRule, Potion.potionTypes[e.getPotionID()].isBadEffect()),
            (c, e) -> excludeAmount(c.durationAmountRule, e.getDuration(), c.excludedDurationThreshold * 20.0F) && !e.getIsPotionDurationMax(),
            (c, e) -> excludeAmount(c.amplifierAmountRule, e.getAmplifier(), c.excludedAmplifierValues - 1)
    );

    @Exclude
    private final List<BiPredicate<Effect, PotionEffect>> conditions = Arrays.asList(
            (s, e) -> excludeCondition(s.permanentEffectsRule, e.getIsPotionDurationMax()),
            (s, e) -> excludeCondition(s.ambientEffectsRule, e.getIsAmbient()),
            (s, e) -> excludeCondition(s.emittingParticlesRule, e.getIsShowParticles()),
            (s, e) -> excludeCondition(s.badEffectsRule, Potion.potionTypes[e.getPotionID()].isBadEffect()),
            (s, e) -> excludeAmount(s.excludeSetDuration, e.getDuration(), s.excludedDurationThreshold * 20.0F) && !e.getIsPotionDurationMax(),
            (s, e) -> excludeAmount(s.excludeSetAmplifier, e.getAmplifier(), s.excludedAmplifierValues - 1)
    );

    @Exclude
    private static final List<Predicate<Boolean>> conditionRules = Arrays.asList(
            value -> false,
            value -> value,
            value -> !value
    );


    @Exclude
    private static final List<BiFunction<Integer, Float, Boolean>> amountRules = Arrays.asList(
            (Integer value, Float threshold) -> false,
            (Integer value, Float threshold) -> value > threshold,
            (Integer value, Float threshold) -> value < threshold,
            (Integer value, Float threshold) -> Objects.equals(value.floatValue(), threshold),
            (Integer value, Float threshold) -> !Objects.equals(value.floatValue(), threshold)
    );

    /** Determines the mod's dimensional width. */
    private float width = 0f;

    /** Determines the mod's dimensional height. */
    private float height = 0f;

    /**
     * Continuously counts up every tick, and resets back to 0 if the current amount is over a specific threshold determined by blinkSpeed.
     */
    @Exclude private int ticks = 0;
    @Exclude private boolean fromInventory = false;

    /**
     * Used to set the current active potion effects. <br>
     * Also determines if the mod should show if the mod is not empty, or if {@link #currentEffects} should reference this list if not empty, or use {@link #dummyEffects}..
     */
    @Exclude private List<PotionEffect> activeEffects = new ArrayList<>();

    /**
     * Set by either {@link #activeEffects} or {@link #dummyEffects} depending on if {@link
     * #activeEffects} is empty. <br>
     * Used to sort (see {@link #sortEffects(List)}), help set the height of the mod, and split each effect to render independently.
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
        super(true, 2f, 1080f / 2f, 1, false, false, 0, 0, 0, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
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
     * Add example/dummy effects when Forge is initialized. <br>
     * Historically, in CheatBreaker, current versions of Lunar Client, and in early versions of the mod, this used to constantly be re-added every partial tick as the field used to make an array list was made every partial tick as well.
     */
    @Subscribe
    private void onInitialization(InitializationEvent event) {
        this.dummyEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
        this.dummyEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
        PotionHUDTracker.INSTANCE.instances.clear();
        PotionHUDTracker.INSTANCE.instances.add(this);
    }

    /**
     * Gets the player's active effects and sets the current effect list to either: <br>
     * 1. The actual active effect list (if not empty) <br>
     * 2. The dummy list (if there are no active effects)
     */
    @Subscribe
    private void onUpdatePotionEffects(UpdatePotionEffectsEvent event) {
        if (mc.thePlayer != null) {
            this.activeEffects = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
            this.currentEffects = this.activeEffects.isEmpty() ? this.dummyEffects : this.activeEffects;
            this.sortEffects(this.currentEffects);
        }
    }

    public void renderFromInventory() {
        this.fromInventory = true;
        this.drawAll(new UMatrixStack(), false);
        this.fromInventory = false;
    }

    /**
     * @return True if the active player effects are not empty and the basic HUD conditions are met.
     */
    @Override
    protected boolean shouldShow() {
        // Prevent from rendering twice when in inventory
        if (!this.fromInventory && PotionEffectsConfig.INSTANCE.showHudInForeground && mc.currentScreen instanceof InventoryEffectRenderer) {
            return false;
        }

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

        float yOffset = 0;
        float tempWidth = 0;
        float yAmount = ICON_SIZE + PotionEffectsConfig.INSTANCE.verticalSpacing;
        this.height = (this.currentEffects.size() * yAmount) - PotionEffectsConfig.INSTANCE.verticalSpacing;

        UGraphics.GL.pushMatrix();
        UGraphics.GL.scale(scale, scale, 1);
        UGraphics.GL.translate(x / scale, y / scale, 0);

        for (PotionEffect effect : this.currentEffects) {
            float tempTempWidth = 0;
            Effect[] configs = extractConfigs(effect);
            boolean excluded = isEffectExcluded(effect, configs[5], example, yAmount, scale);

            if (excluded && !shouldRenderExcluded(example)) continue;

            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) continue;

            UGraphics.GL.pushMatrix();
            boolean[] visibility = getComponentVisibility(configs[0], effect);
            float iconSpacing = ICON_SIZE + configs[0].statusIcon.spacing;

            if (visibility[0]) {
                translateForIcon(actualHorizontal, iconSpacing);
            }

            this.componentAmount = 0;
            this.oneComponentActive = !visibility[1] || !visibility[2];

            if (visibility[1]) {
                String title = getEffectTitle(potion, effect, configs[3], configs[1]);
                tempTempWidth = Math.max(tempTempWidth, this.textBuilder(title, configs[0].effectName, configs[2], configs[4].effectName, effect.getDuration(), yOffset, example, excluded));
                ++this.componentAmount;
            }

            if (visibility[2]) {
                String duration = getDurationString(effect, configs[3]);
                tempTempWidth = Math.max(tempTempWidth, this.textBuilder(duration, configs[0].duration, configs[2], configs[4].duration, effect.getDuration(), yOffset, example, excluded));
                ++this.componentAmount;
            }

            UGraphics.GL.popMatrix();

            if (visibility[0]) {
                drawPotionIcon(potion, tempTempWidth, iconSpacing, actualHorizontal, yOffset, configs[2], effect, example, excluded);
                tempTempWidth += iconSpacing;
            }

            yOffset += yAmount;
            tempWidth = Math.max(tempWidth, tempTempWidth);
        }

        this.width = tempWidth;
        UGraphics.GL.popMatrix();
    }


    public float textBuilder(String text, TextComponent component, Effect blinkingConfig, TextComponent color, float value, float yOffset, boolean example, boolean excluded) {
        StringBuilder builder = new StringBuilder();
        // I really hope there's a more efficient way of setting this up...
        if (component.boldText) {
            builder.append(EnumChatFormatting.BOLD);
        }

        if (component.italicText) {
            builder.append(EnumChatFormatting.ITALIC);
        }

        if (component.underlineText) {
            builder.append(EnumChatFormatting.UNDERLINE);
        }

        builder.append(text);

        String builtTime = builder.toString();

        int width = fontRenderer.getStringWidth(builtTime);
        float timeY = yOffset + (fontRenderer.FONT_HEIGHT + 1) * componentAmount;

        if (this.oneComponentActive) {
            timeY = yOffset + fontRenderer.FONT_HEIGHT / 2f + 0.5f;
        }

        if (showDuringBlink(blinkingConfig, component.blink, value, example)) {
            float x = 0;
            switch (this.getHorizontalAlignment()) {
                case 1:
                    x = this.width / 2f - (float) width / 2;
                    break;
                case 2:
                    x = this.width - width;
            }
            TextRenderer.drawScaledString(builtTime, x, timeY, getColor(color.color.getRGB(), excluded), TextRenderer.TextType.toType(component.textType), 1);
        }

        return width;
    }

    private Effect[] extractConfigs(PotionEffect effect) {
        Effect setting = getEffectSetting(effect);
        return new Effect[] {
                checkCategoryOverride(setting, setting.overrideComponent),
                checkCategoryOverride(setting, setting.overrideAmplifier),
                checkCategoryOverride(setting, setting.overrideBlinking),
                checkCategoryOverride(setting, setting.overrideFormatting),
                checkCategoryOverride(setting, setting.overrideColor),
                checkCategoryOverride(setting, setting.overrideExclusion)
        };
    }

    private boolean isEffectExcluded(PotionEffect effect, Effect exclusionConfig, boolean example, float yAmount, float scale) {
        if (!excludePotions(exclusionConfig, effect)) return false;

        int mode = PotionEffectsConfig.INSTANCE.showExcludedEffects;
        if (example) {
            if (mode == 2) return true;
            if (mode == 1) {
                if (mc.displayHeight < getHeight(scale, example) * new ScaledResolution(mc).getScaleFactor()) {
                    this.height -= yAmount;
                    return true;
                }
                return true;
            }
        }

        this.height -= yAmount;
        return true;
    }

    private boolean shouldRenderExcluded(boolean example) {
        return example && PotionEffectsConfig.INSTANCE.showExcludedEffects > 0;
    }

    private boolean[] getComponentVisibility(Effect componentConfig, PotionEffect effect) {
        return new boolean[] {
                showComponent(componentConfig.statusIcon, effect),
                showComponent(componentConfig.effectName, effect),
                showComponent(componentConfig.duration, effect)
        };
    }

    private void translateForIcon(int actualHorizontal, float spacing) {
        float translation = spacing;
        if (actualHorizontal == 1) translation /= 2;
        else if (actualHorizontal == 2) translation = -spacing;
        UGraphics.GL.translate(translation, 0, 0);
    }

    private String getEffectTitle(Potion potion, PotionEffect effect, Effect formatting, Effect amplifierConfig) {
        String base = formatting.effectName.customName.isEmpty()
                ? I18n.format(potion.getName())
                : formatting.effectName.customName;

        int amp = Math.max(1, effect.getAmplifier() + 1);
        if (amplifierConfig.effectName.amplifier && (amp != 1 || amplifierConfig.effectName.levelOne)) {
            base += " ";
            base += amplifierConfig.effectName.romanNumerals ? amp : RomanNumeral.INSTANCE.getCache(amp);
        }

        return base;
    }

    private String getDurationString(PotionEffect effect, Effect formatting) {
        if (effect.getIsPotionDurationMax()) return formatting.duration.maxDurationString;
        switch (formatting.duration.durationFormat) {
            case 0: return Potion.getDurationString(effect);
            case 1: return effect.getDuration() / 20 + "s";
            case 2: return RomanNumeral.INSTANCE.getCache(effect.getDuration() / 20);
            default: return "";
        }
    }

    private void drawPotionIcon(Potion potion, float tempWidth, float spacing, int alignment, float yOffset, Effect blinkingConfig, PotionEffect effect, boolean example, boolean excluded) {
        UGraphics.color4f(1f, 1f, 1f, excluded ? 0.5f : 1f);
        mc.getTextureManager().bindTexture(this.EFFECTS_RESOURCE);

        float iconX = 0;
        switch (alignment) {
            case 1:
                iconX = this.width / 2f - (tempWidth - spacing) / 2 - spacing;
                break;
            case 2:
                iconX = this.width - ICON_SIZE;
        }

        if (showDuringBlink(blinkingConfig, blinkingConfig.statusIcon.blink, effect.getDuration(), example)) {
            float zLevel = ((GuiAccessor) mc.ingameGUI).getZLevel();
            ((GuiAccessor) mc.ingameGUI).setZLevel(999);
            mc.ingameGUI.drawTexturedModalRect(iconX, yOffset,
                    potion.getStatusIconIndex() % 8 * 18,
                    198 + potion.getStatusIconIndex() / 8 * 18,
                    18, 18);
            ((GuiAccessor) mc.ingameGUI).setZLevel(zLevel);
        }
    }


    /**
     * Gets the horizontal alignment based off the mod's {@link #position} anchor.
     *
     * @return The int corresponding to the mod's alignment <br>
     *     0: Left horizontal alignment <br>
     *     1: Center horizontal alignment <br>
     *     2: Right horizontal alignment
     */
    private int getHorizontalAlignment() {
        if (PotionEffectsConfig.INSTANCE.horizontalAlignment == 0) {
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
            }
        }
        return PotionEffectsConfig.INSTANCE.horizontalAlignment - 1;
    }

    /**
     * Sorts all the current potion effects determined by the user's sorting method and order priority. <br>
     * 0: Sorts by Potion ID (Vanilla behavior) <br>
     * 1: Sorts by alphabetical name <br>
     * 2: Sorts based off duration. <br>
     * 3: Sorts based off amplifier. <br>
     * 4: Sorts prioritizing ambient (beacon) effects. <br>
     * 5: Sorts prioritizing effects showing particles. <br>
     * Optionally, the entire list will reverse if the user enables Vertical Sorting.
     *
     * @param effects {@link #currentEffects}
     */
    public void sortEffects(List<PotionEffect> effects) {
        effects.sort(sortingMethods.get(PotionEffectsConfig.INSTANCE.sortingMethod));

        effects.sort(Comparator.comparingDouble(effect -> -this.getEffectSetting(effect).orderPriority));

        if (PotionEffectsConfig.INSTANCE.verticalSorting) {
            Collections.reverse(effects);
        }
    }

    /**
     * @param config The current effect's configuration (global or effect specific)
     * @param blinkComponent If the set component should blink
     * @param duration The effect's duration
     * @param example If the HUD is being rendered in example form
     * @return False if the duration amount or tick counter is over the threshold.
     */
    private boolean showDuringBlink(Effect config, boolean blinkComponent, float duration, boolean example) {
        if (!(config.blink && blinkComponent && duration <= config.blinkDuration * 20.0F)) {
            return true;
        }

        if (config.syncBlinking || (example && this.activeEffects.isEmpty())) {
            float threshold = config.blinkSpeed / 3.0F;
            this.ticks %= (int) (threshold * 2);
            return this.ticks <= threshold;
        } else {
            float threshold = 50 - config.blinkSpeed;
            return duration % threshold <= threshold / 2.0F;
        }
    }

    /**
     * @param effect The current potion effect
     * @return The specific effect config's based off the effectMap if overridden, or the global configuration if not overridden.
     */
    public Effect getEffectSetting(PotionEffect effect) {
        return this.effects.entrySet().stream()
                .filter(entry -> effect.getPotionID() == entry.getKey() && entry.getValue().override)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(PotionEffectsConfig.global);
    }


    /**
     * Checks if a certain category is overridden, or resorts to global.
     *
     * @param category The current effect setting.
     * @param override The override check based off the category's override check.
     * @return True if the category's override check is enabled.
     */
    public Effect checkCategoryOverride(Effect category, boolean override) {
        return override ? category : PotionEffectsConfig.global;
    }

    /**
     * Exclude the current effect depending on exclusion configuration
     *
     * @param setting The current effect's setting.
     * @param effect The current potion effect
     * @return True if one of the exclusion conditions is set to true
     */
    private boolean excludePotions(Effect setting, PotionEffect effect) {
        return setting.exclude || this.conditions.stream().anyMatch(condition -> condition.test(setting, effect));
    }

    public boolean showComponent(Component component, PotionEffect effect) {
        return component.toggle && this.componentConditions.stream().noneMatch(condition -> condition.test(component, effect));
    }


    /**
     * @param rule The configuration rule
     * @param value The current effect's value
     * @return True depending on if the current value satisfies the exclusion rule
     */
    protected boolean excludeCondition(int rule, boolean value) {
        return conditionRules.get(rule).test(value);
    }

    /**
     * @param rule The configuration rule
     * @param value The current effect's value
     * @param threshold The threshold amount
     * @return True depending on the rule number set
     */
    protected boolean excludeAmount(int rule, int value, float threshold) {
        return amountRules.get(rule).apply(value, threshold);
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

    public static class PotionHUDTracker {

        public static final PotionHUDTracker INSTANCE = new PotionHUDTracker();

        public final Set<PotionEffects> instances;

        private PotionHUDTracker() {
            this.instances = new HashSet<>();
        }

    }

    /** All the individual config settings. */
    public static class Effect {
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
                subcategory = "Override"
        )
        public boolean overrideExclusion = true;

        @Page(
                name = "Status Icon",
                location = PageLocation.TOP,
                description = "Show the effect icon",
                subcategory = "Component"
        )
        public IconComponent statusIcon = new IconComponent();

        @Page(
                name = "Effect Name",
                location = PageLocation.TOP,
                description = "Show the effect name",
                subcategory = "Component"
        )
        public EffectNameComponent effectName = new EffectNameComponent();

        @Page(
                name = "Duration",
                location = PageLocation.TOP,
                description = "Show the effect duration",
                subcategory = "Component"
        )
        public DurationComponent duration = new DurationComponent();

        @Slider(
                name = "Order Priority",
                description = "Higher numbers will indicate higher priority",
                subcategory = "Sorting",
                step = 1,
                min = -15,
                max = 15
        )
        public float orderPriority = 0;

        @Switch(
                name = "Blink",
                description = "Make the potion effects blink when the duration is low",
                subcategory = "Blinking"
        )
        public boolean blink = true;

        @Switch(
                name = "Sync Blinking",
                description = "Make blinking synced with tick counts or make blinking go based off the duration time.",
                subcategory = "Blinking"
        )
        public boolean syncBlinking = true;

        @Slider(
                name = "Blink Duration",
                description = "The duration the effect should start blinking at",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public float blinkDuration = 10;

        @Slider(
                name = "Blink Speed",
                description = "The speed of which the effect should blink at",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public float blinkSpeed = 30;

        @Checkbox(
                name = "Exclude",
                description = "Completely exclude the effect(s)",
                subcategory = "Exclusion",
                size = 2
        )
        public boolean exclude = false;

        @Dropdown(
                name = "Duration Amount Rule",
                description = "Exclude effects that are either above, below, at, or not at a certain duration threshold",
                options = {
                    "None",
                    "Exclude All Above",
                    "Exclude All Below",
                    "Exclude All At",
                    "Exclude All Not At"
                },
                subcategory = "Exclusion"
        )
        public int excludeSetDuration = 0;

        @Dropdown(
                name = "Amplifier Amount Rule",
                description = "Exclude effects that are either above, below, at, or not at a certain amplifier amount",
                options = {
                    "None",
                    "Exclude All Above",
                    "Exclude All Below",
                    "Exclude All At",
                    "Exclude All Not At"
                },
                subcategory = "Exclusion"
        )
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
        public int permanentEffectsRule = 0;

        @Dropdown(
                name = "Ambient Effects Rule",
                description = "Decide if effects from or not from a beacon should be excluded.",
                subcategory = "Exclusion",
                options = {
                    "None",
                    "Exclude All Ambient Effects",
                    "Exclude All Non Ambient Effects"
                })
        public int ambientEffectsRule = 0;

        @Dropdown(
                name = "Emitting Particles Rule",
                description = "Decide if emitting/disallowing particle effects should be excluded.",
                subcategory = "Exclusion",
                options = {
                    "None",
                    "Exclude All Emitting Particles",
                    "Exclude All Disallowing Particles"
                })
        public int emittingParticlesRule = 0;

        @Dropdown(
                name = "Bad Effects Rule",
                description = "Decide if good or bad effects should be excluded.",
                subcategory = "Exclusion",
                options = {"None", "Exclude All Bad Effects", "Exclude All Good Effects"}
        )
        public int badEffectsRule = 0;

        @Slider(
                name = "Excluded Duration Threshold",
                description = "The value(s) that will be excluded based off the duration rule",
                subcategory = "Exclusion",
                min = 2,
                max = 500,
                step = 1
        )
        public float excludedDurationThreshold = 30f;

        @Slider(
                name = "Excluded Amplifier Value(s)",
                description = "The value(s) that will be excluded based off the amplifier rule",
                subcategory = "Exclusion",
                min = 0,
                max = 20,
                step = 1
        )
        public int excludedAmplifierValues = 10;

        @Exclude public int id;

        public Effect(String name) {
            PotionEffectsConfig.effectNames.add(name);
        }

        public Effect(String name, int id) {
            this(name);
            this.id = id;
        }
    }

    public static class IconComponent extends Component {

        @Slider(
                name = "Icon Spacing",
                description = "Adjust the spacing between the text and icon",
                min = 0,
                max = 10,
                subcategory = "Dimensions"
        )
        public float spacing = 2f;

        public IconComponent() {}
    }

    public static class EffectNameComponent extends TextComponent {

        @Text(
                name = "Custom Name",
                description = "Override the effect name with a custom one",
                subcategory = "Formatting",
                size = 2
        )
        public String customName = "";

        @Switch(
                name = "Show Amplifier",
                description = "Show the amplifier amount next to the effect name",
                subcategory = "Amplifier"
        )
        public boolean amplifier = true;

        @Switch(
                name = "Show Level One",
                description = "Show the amplifier if the effect level is one",
                subcategory = "Amplifier"
        )
        public boolean levelOne = false;

        @DualOption(
                name = "Amplifier Numerals",
                description = "Choose to show roman numerals or arabic amount",
                subcategory = "Amplifier",
                left = "Roman",
                right = "Arabic"
        )
        public boolean romanNumerals = false;

        public EffectNameComponent() {}
    }

    public static class DurationComponent extends TextComponent {

        @Text(
                name = "Max Duration String",
                description = "The text that should show when you have a permanent effect",
                subcategory = "Formatting",
                size = 2
        )
        public String maxDurationString = "**:**";

        @Dropdown(
                name = "Duration Format",
                description = "Choose how the duration text should be formatted",
                subcategory = "Formatting",
                options = {"Standard", "Split Format", "Roman Numerals"},
                size = 2
        )
        public int durationFormat = 0;

        public DurationComponent() {}
    }

    public static class TextComponent extends Component {

        @Checkbox(
                name = "Bold Text",
                description = "Bold the component text",
                subcategory = "Formatting"
        )
        public boolean boldText = false;

        @Checkbox(
                name = "Italic Text",
                description = "Make the component text italic",
                subcategory = "Formatting"
        )
        public boolean italicText = false;

        @Checkbox(
                name = "Underline Text",
                description = "Underline the component text",
                subcategory = "Formatting"
        )
        public boolean underlineText = false;

        @Dropdown(
                name = "Text Type",
                subcategory = "Formatting",
                options = {"No Shadow", "Shadow", "Full Shadow"}
        )
        public int textType = 1;

        @Color(
                name = "Color",
                description = "The color of the component text",
                subcategory = "Color"
        )
        public OneColor color = new OneColor(255, 255, 255);

        public TextComponent() {}
    }

    public static class Component {
        @Switch(name = "Enable Component")
        public boolean toggle = true;

        @Switch(name = "Blink")
        public boolean blink = true;


        @Dropdown(
                name = "Duration Amount Rule",
                description = "Exclude effects that are either above, below, at, or not at a certain duration threshold",
                options = {
                        "None",
                        "Exclude All Above",
                        "Exclude All Below",
                        "Exclude All At",
                        "Exclude All Not At"
                },
                subcategory = "Exclusion"
        )
        public int durationAmountRule = 0;

        @Dropdown(
                name = "Amplifier Amount Rule",
                description = "Exclude effects that are either above, below, at, or not at a certain amplifier amount",
                options = {
                        "None",
                        "Exclude All Above",
                        "Exclude All Below",
                        "Exclude All At",
                        "Exclude All Not At"
                },
                subcategory = "Exclusion"
        )
        public int amplifierAmountRule = 0;

        @Dropdown(
                name = "Permanent Effects Rule",
                description = "Decide if permanent or temporary effects should be excluded.",
                subcategory = "Exclusion",
                options = {
                        "None",
                        "Exclude All Permanent Effects",
                        "Exclude All Temporary Effects"
                })
        public int permanentEffectsRule = 0;

        @Dropdown(
                name = "Ambient Effects Rule",
                description = "Decide if effects from or not from a beacon should be excluded.",
                subcategory = "Exclusion",
                options = {
                        "None",
                        "Exclude All Ambient Effects",
                        "Exclude All Non Ambient Effects"
                })
        public int ambientEffectsRule = 0;

        @Dropdown(
                name = "Emitting Particles Rule",
                description = "Decide if emitting/disallowing particle effects should be excluded.",
                subcategory = "Exclusion",
                options = {
                        "None",
                        "Exclude All Emitting Particles",
                        "Exclude All Disallowing Particles"
                })
        public int emittingParticlesRule = 0;

        @Dropdown(
                name = "Bad Effects Rule",
                description = "Decide if good or bad effects should be excluded.",
                subcategory = "Exclusion",
                options = {"None", "Exclude All Bad Effects", "Exclude All Good Effects"}
        )
        public int badEffectsRule = 0;

        @Slider(
                name = "Excluded Duration Threshold",
                description = "The value(s) that will be excluded based off the duration rule",
                subcategory = "Exclusion",
                min = 2,
                max = 500,
                step = 1
        )
        public float excludedDurationThreshold = 30f;

        @Slider(
                name = "Excluded Amplifier Value(s)",
                description = "The value(s) that will be excluded based off the amplifier rule",
                subcategory = "Exclusion",
                min = 0,
                max = 20,
                step = 1
        )
        public int excludedAmplifierValues = 10;

        public Component() {}
    }
}
