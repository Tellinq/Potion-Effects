package me.tellinq.potioneffects.hud;

import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.universal.UGraphics;
import cc.polyfrost.oneconfig.libs.universal.UMinecraft;
import com.google.common.collect.ImmutableList;
import me.tellinq.potioneffects.config.PotionEffectsConfig;
import me.tellinq.potioneffects.util.RomanNumeral;
import net.minecraft.client.gui.FontRenderer;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.RenderManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class PotionEffects extends BasicHud {


    @Dropdown(
            name = "Horizontal Alignment",
            options = {/*"Auto", */"Left", "Center", "Right"}
    )
    public int horizontalAlignment = 0;
    @Slider(
            name = "Vertical Spacing",
            description = "Adjust the spacing between effects",
            min = 0,
            max = 10
    )
    public float verticalSpacing = 4f;

    @DualOption(
            name = "Vertical Sorting",
            description = "Make sorting start from the top or bottom",
            left = "Top",
            right = "Bottom"
    )
    public boolean verticalSorting = false;

    @Dropdown(
            name = "Sorting Method",
            description = "Vanilla: Sorts based off the default order" +
                    "\nAlphabetical: Sorts from A-Z" +
                    "\nDuration: Sorts from the effects with the lowest duration to the highest" +
                    "\nAmplifier: Sorts from the highest amplifier to the lowest",
            options = {"Vanilla", "Alphabetical", "Duration", "Amplifier"}
    )
    public int sortingMethod = 0;

    @Switch(
            name = "Show Excluded Effects in HUD Editor",
            description = "Show potion effects that are excluded in the HUD editor" +
                    "\nNot recommended to disable if all effects are excluded!"
    )
    public boolean showExcludedEffects = true;

    @Info(
            text = "Not recommended to disable if all effects are excluded!",
            type = InfoType.WARNING
    )
    public String info;

    @Exclude public final int ICON_SIZE = 18;
    @Exclude private final ResourceLocation EFFECTS_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
    @Exclude public final Minecraft mc = UMinecraft.getMinecraft();
    @Exclude public final FontRenderer fontRenderer = mc.fontRendererObj;
    @Exclude public Map<Integer, EffectConfig> effectMap =
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

    private float width = 0f;
    private float height = 0f;
    @Exclude private int ticks = 0;
    @Exclude private List<PotionEffect> activeEffects = new ArrayList<>();
    @Exclude private List<PotionEffect> currentEffects = new ArrayList<>();
    @Exclude private final List<PotionEffect> dummyEffects = new ArrayList<>();

    public PotionEffects() {
        super(true, 0, 0, 1, false, false, 0, 0, 0, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
        EventManager.INSTANCE.register(this);
    }

    /**
     * On every tick, this tick counter will add up every tick.
     * <br>
     * Primarily used if a component should blink.
     */
    @Subscribe
    private void onTick(TickEvent event) {
        if (event.stage == Stage.START) {
            ++this.ticks;
        }
    }

    /**
     * On Forge's initialization, add the two example/dummy effects.
     * <br>
     * Historically, in CheatBreaker, current versions of Lunar Client, and in early versions of the mod, this used to constantly be re-added every partial tick as the field used to make an array list was made every partial tick as well.
     */
    @Subscribe
    private void onInitialization(InitializationEvent event) {
        this.dummyEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
        this.dummyEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
    }

    /**
     * @return True if the active player effects are not empty and the basic HUD conditions are met.
     */
    @Override
    protected boolean shouldShow() {
        /*
        In my opinion, this should only be set every time the potion effect gets updated. I haven't looked into making events for OneConfig, but I will later on.
        If anyone wants to take a look at implementing this before I decide to, this is what I did for CheatBreaker: https://imgur.com/OOhV7H6 https://imgur.com/2BJAanz
         */
        if (this.mc.thePlayer != null) {
            this.activeEffects = ImmutableList.copyOf(this.mc.thePlayer.getActivePotionEffects());
        }

        this.currentEffects = this.activeEffects.isEmpty() ? this.dummyEffects : this.activeEffects;
        return !this.activeEffects.isEmpty() && super.shouldShow();
    }

    /**
     * Draws the current effects, except for any marked as excluded.
     * @param matrices The UMatrixStack used for rendering in higher versions
     * @param x        Top left x-coordinate of the hud
     * @param y        Top left y-coordinate of the hud
     * @param scale    Scale of the hud
     * @param example  If the HUD is being rendered in example form
     */
    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        UGraphics.disableLighting();

        this.softEffects(this.currentEffects);

        float yOff = 0;
        float xOff = 0;
        int yAmt = (int) (this.ICON_SIZE + this.verticalSpacing);

        this.height = (this.currentEffects.size() * yAmt) - this.verticalSpacing;
        this.width = 0f;

        UGraphics.GL.pushMatrix();
        // This REALLY should be a method inside OneConfig... CB does have it inside as a scaleAndTranslate method.
        // I would make this a method myself, but I find it pointless since it's just two lines of code and making a new one just seems silly unless I was repeatedly calling it for multiple HUD mods.
        UGraphics.GL.scale(scale, scale, 1);
        UGraphics.GL.translate(x / scale, y / scale, 1);
        for (PotionEffect effect : this.currentEffects) {
            EffectConfig effectSetting = getEffectSetting(effect);
            // I wish there was a more efficient way of doing this...
            EffectConfig oComponent = useOverride(effectSetting, effectSetting.overrideComponent);
            EffectConfig oAmplifier = useOverride(effectSetting, effectSetting.overrideAmplifier);
            EffectConfig oBlinking = useOverride(effectSetting, effectSetting.overrideBlinking);
            EffectConfig oFormatting = useOverride(effectSetting, effectSetting.overrideFormatting);
            EffectConfig oColor = useOverride(effectSetting, effectSetting.overrideColor);
            EffectConfig oExclusion = useOverride(effectSetting, effectSetting.overrideExclusion);
            boolean excluded = false;
            if (this.excludePotions(oExclusion, effect)) {
                if (example && this.showExcludedEffects) {
                    excluded = true;
                } else {
                    this.height -= yAmt;
                    continue;
                }
            }

            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) {
                continue;
            }


            if (oComponent.icon) {
                UGraphics.color4f(1f, 1f, 1f, excluded ? 0.5f : 1f);
                this.mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                float iconX = 0;
                if (this.horizontalAlignment == 2) {
                    iconX = this.width - ICON_SIZE;
                }
                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectIconBlink, effect.getDuration(), example)) {
                    this.mc.ingameGUI.drawTexturedModalRect(iconX, yOff, potion.getStatusIconIndex() % 8 * 18, 198 + potion.getStatusIconIndex() / 8 * 18, 18, 18);
                }
                xOff = this.ICON_SIZE;
                this.width = Math.max(this.width, xOff);
            }

            if (oComponent.effectName) {
                if (oComponent.icon) {
                    xOff = (this.ICON_SIZE + 4);
                }


                StringBuilder titleSb = new StringBuilder();
                // I really hope there's a more efficient way of setting this up...
                if (oFormatting.boldEffectName) {
                    titleSb.append(EnumChatFormatting.BOLD);
                }

                if (oFormatting.italicEffectName) {
                    titleSb.append(EnumChatFormatting.ITALIC);
                }

                if (oFormatting.underlineEffectName) {
                    titleSb.append(EnumChatFormatting.UNDERLINE);
                }

                if (oFormatting.customName.isEmpty()) {
                    titleSb.append(I18n.format(potion.getName()));
                } else {
                    titleSb.append(oFormatting.customName);
                }

                int amplifier = Math.max(1, effect.getAmplifier() + 1);
                if (oAmplifier.amplifier && (amplifier != 1 || oAmplifier.levelOne)) {
                    titleSb.append(" ");
                    if (!oAmplifier.romanNumerals) titleSb.append(RomanNumeral.INSTANCE.getCache(amplifier));
                    else titleSb.append(amplifier);
                }

                String builtTitle = titleSb.toString();

                int titleWidth = this.fontRenderer.getStringWidth(builtTitle);
                this.width = Math.max(this.width, xOff + titleWidth);

                float titleX = xOff;

                float titleY = yOff;
                if (!oComponent.duration) {
                    titleY += this.fontRenderer.FONT_HEIGHT / 2f + 0.5f;
                }


                switch (this.horizontalAlignment) {
                    case 0:
                        titleX = xOff;
                        break;
                    case 1:
                        titleX = this.width / 2f - xOff;
                        break;
                    case 2:
                        titleX = this.width - xOff;
                }
                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectNameBlink, effect.getDuration(), example)) {
                    RenderManager.drawScaledString(builtTitle, titleX, titleY, getColor(oColor.nameColor.getRGB(), excluded), RenderManager.TextType.toType(oFormatting.textType), 1);
                }


            }

            if (oComponent.duration) {
                if (oComponent.icon) xOff = (this.ICON_SIZE + 4);

                StringBuilder timeSb = new StringBuilder();
                // I really hope there's a more efficient way of setting this up...
                if (oFormatting.boldDuration) {
                    timeSb.append(EnumChatFormatting.BOLD);
                }

                if (oFormatting.italicDuration) {
                    timeSb.append(EnumChatFormatting.ITALIC);
                }

                if (oFormatting.underlineDuration) {
                    timeSb.append(EnumChatFormatting.UNDERLINE);
                }

                if (effect.getIsPotionDurationMax()) {
                    timeSb.append(oFormatting.maxDurationString);
                } else {
                    switch (oFormatting.durationFormat) {
                        case 0:
                            timeSb.append(Potion.getDurationString(effect));
                            break;
                        case 1:
                            timeSb.append(effect.getDuration() / 20).append("s");
                            break;
                        case 2:
                            timeSb.append(RomanNumeral.INSTANCE.getCache(effect.getDuration() / 20));
                    }

                }

                String builtTime = timeSb.toString();

                int timeWidth = this.fontRenderer.getStringWidth(builtTime);
                this.width = Math.max(width, xOff + timeWidth);

                float timeX = xOff;
                float timeY = yOff + this.fontRenderer.FONT_HEIGHT + 1;

                if (!oComponent.effectName) {
                    timeY -= this.fontRenderer.FONT_HEIGHT / 2f + 0.5f;
                }

                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectDurationBlink, effect.getDuration(), example)) {
                    switch (this.horizontalAlignment) {
                        case 0:
                            timeX = xOff;
                            break;
                        case 1:
                            timeX = this.width / 2f - xOff;
                            break;
                        case 2:
                            timeX = this.width - xOff;
                    }
                    RenderManager.drawScaledString(builtTime, timeX, timeY, getColor(oColor.durationColor.getRGB(), excluded), RenderManager.TextType.toType(oFormatting.textType), 1);
                }
            }

            yOff += yAmt;
        }
        UGraphics.GL.popMatrix();
    }

    /**
     * Sorts all the current potion effects based off what sorting method the user set
     * 1: Sorts alphabetically by name only
     * 2: Sorts alphabetically based off duration.
     * 3: Sorts alphabetically based off amplifier.
     * Optionally, the entire list can get reversed if the user enables Vertical Sorting.
     */
    public void softEffects(List<PotionEffect> potionEffects) {
        switch (this.sortingMethod) {
            case 1:
                potionEffects.sort(Comparator.comparing(effect -> I18n.format(effect.getEffectName())));
                break;
            case 2:
                potionEffects.sort(Comparator.comparingInt(PotionEffect::getDuration));
                break;
            case 3:
                potionEffects.sort(Comparator.comparingInt(PotionEffect::getAmplifier));
                Collections.reverse(potionEffects);
        }

        if (this.verticalSorting) {
            Collections.reverse(potionEffects);
        }
    }

    /**
     * @param effectConfig The current effect's configuration (global or effect specific)
     * @param makeComponentBlink If the set component should blink
     * @param duration The effect's duration
     * @param example If the user is currently in the HUD editor
     * @return False if either:
     * <br>
     * - If synced or if the example effects are running: Depending on the amount of counted ticks and the "speed" slider amount set, those two main factors determine if the element set should show while blinking.
     * <br>
     * - Depending on the effect's duration, that will determine if the effect should blink (will explain in depth later)
     */
    private boolean showEffectDuringBlink(EffectConfig effectConfig, boolean makeComponentBlink, float duration, boolean example) {
        if (effectConfig.blink && makeComponentBlink) {
            if (effectConfig.syncBlinking || (example && this.activeEffects.isEmpty())) {
                float blinkSpeed = effectConfig.blinkSpeed / 3.0f;
                if (duration <= effectConfig.blinkDuration * 20.0F) {
                    if (this.ticks > blinkSpeed * 2) this.ticks = 0;
                    return this.ticks <= blinkSpeed;
                }
            } else {
                return duration / 20f > effectConfig.blinkDuration || duration % (50 - effectConfig.blinkSpeed) <= (50 - effectConfig.blinkSpeed) / 2f;
            }
        }
        return true;
    }

    /**
     * @param effect The current potion effect
     * @return The specific effect config's based off the effectMap if overridden, or the global configuration if not overridden.
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
     * @param effectSetting The current effect setting.
     * @param overrideBoolean The override check based off the category's override check.
     * @return True if the category's override check is enabled.
     */
    public EffectConfig useOverride(EffectConfig effectSetting, boolean overrideBoolean) {
        return overrideBoolean ? effectSetting : PotionEffectsConfig.global;
    }

    /**
     * Exclude the current effect depending on exclusion configuration
     * @param effectSetting The current effect's setting.
     * @param effect The current potion effect
     * @return True if one of the exclusion conditions is set to true
     */
    private boolean excludePotions(EffectConfig effectSetting, PotionEffect effect) {
        if (effectSetting.excludePermanentEffects && effect.getIsPotionDurationMax()) return true;
        if (this.excludeBulk(effectSetting.ambientExclusionRule, effect.getIsAmbient())) return true;
        if (this.excludeBulk(effectSetting.particlesExclusionRule, effect.getIsShowParticles())) return true;
        if (this.excludeArrayOptions(effectSetting.excludeSetDuration, effect.getDuration(), effectSetting.excludedDurationValues * 20.0F) && !effect.getIsPotionDurationMax()) return true;
        if (this.excludeArrayOptions(effectSetting.excludeSetAmplifier, effect.getAmplifier(), effectSetting.excludedAmplifierValues - 1)) return true;
        return effectSetting.exclude;
    }

    /**
     * Explanation is not very well explained. Will be revised.
     * @param rule The configuration rule
     * @param currentValue The current effect's value
     * @return True depending on the config's rule
     * <br>
     * 1: Matches the effect's value
     * <br>
     * 2: Does not match the effect's value
     */
    protected boolean excludeBulk(int rule, boolean currentValue) {
        switch (rule) {
            case 1: return currentValue;
            case 2: return !currentValue;
            default: return false;
        }
    }

    /**
     *
     * @param rule The configuration rule
     * @param currentValue The current effect's value
     * @param threshold The threshold amount
     * @return True depending on the rule number set
     * <br>
     * 1: Current value exceeds threshold
     * <br>
     * 2: Current value is below threshold
     * <br>
     * 3: Current value matches threshold
     * <br>
     * 4: Current value does not match threshold
     */
    protected boolean excludeArrayOptions(int rule, int currentValue, float threshold) {
        switch (rule) {
            case 1: return currentValue > threshold;
            case 2: return currentValue < threshold;
            case 3: return currentValue == threshold;
            case 4: return currentValue != threshold;
            default: return false;
        }
    }

    /**
     * @param color The color given
     * @param excluded If the effect is excluded
     * @return The given color or half of the color's opacity if the effect is excluded.
     */
    private int getColor(int color, boolean excluded) {
        int opacity = color >> 24 & 0xFF;
        opacity = (int)((float)opacity * (excluded ? 0.5F : 1.0F));
        return color & 0xFFFFFF | opacity << 24;
    }

    /**
     * @param scale   Scale of the hud
     * @param example If the HUD is being rendered in example form
     * @return The width of the HUD multiplied by the HUD's scale
     */
    @Override
    protected float getWidth(float scale, boolean example) {
        return this.width * scale;
    }

    /**
     * @param scale   Scale of the hud
     * @param example If the HUD is being rendered in example form
     * @return The height of the HUD multiplied by the HUD's scale
     */
    @Override
    protected float getHeight(float scale, boolean example) {
        return this.height * scale;
    }

    /**
     * All the individual config settings.
     */
    public static class EffectConfig {
        @Switch(
                name = "Override",
                description = "Let this specific effect override",
                subcategory = "Override",
                size = 2
        )
        public boolean override = false;

        @Checkbox(
                name = "Override Component",
                description = "Override the component category",
                subcategory = "Override"
        )
        public boolean overrideComponent = false;

        @Checkbox(
                name = "Override Amplifier",
                description = "Override the amplifier category",
                subcategory = "Override"
        )
        public boolean overrideAmplifier = false;

        @Checkbox(
                name = "Override Blinking",
                description = "Override the blinking category",
                subcategory = "Override"
        )
        public boolean overrideBlinking = false;

        @Checkbox(
                name = "Override Formatting",
                description = "Override the formatting category",
                subcategory = "Override"
        )
        public boolean overrideFormatting = true;

        @Checkbox(
                name = "Override Color",
                description = "Override the color category",
                subcategory = "Override"
        )
        public boolean overrideColor = true;

        @Checkbox(
                name = "Override Exclusion",
                description = "Override the exclusion category",
                subcategory = "Override"
        )
        public boolean overrideExclusion = true;

        @Checkbox(
                name = "Icon",
                description = "Show the effect icon",
                subcategory = "Component"
        )
        public boolean icon = true;

        @Checkbox(
                name = "Effect Name",
                description = "Show the effect name",
                subcategory = "Component"
        )
        public boolean effectName = true;

        @Checkbox(
                name = "Duration",
                description = "Show the effect duration",
                subcategory = "Component"
        )
        public boolean duration = true;

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

        /*@Slider(
                name = "Order Priority",
                description = "Higher numbers will indicate higher priority",
                subcategory = "Sorting",
                step = 1,
                min = -15,
                max = 15
        )
        public int orderPriority = 0;*/ // To be implemented

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

        @Header(
                text = "Blinking components",
                subcategory = "Blinking",
                size = 2
        )
        public boolean blinkingIgnored = true;

        @Checkbox(
                name = "Icon",
                description = "Make the icon blink",
                subcategory = "Blinking"
        )
        public boolean makeEffectIconBlink = false;

        @Checkbox(
                name = "Effect Name",
                description = "Make the effect name blink",
                subcategory = "Blinking"
        )
        public boolean makeEffectNameBlink = false;

        @Checkbox(
                name = "Duration Text",
                description = "Make the duration blink",
                subcategory = "Blinking"
        )
        public boolean makeEffectDurationBlink = true;

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

        @Dropdown(
                name = "Text Type",
                subcategory = "Formatting",
                options = {"No Shadow", "Shadow", "Full Shadow"}
        )
        public int textType = 1;

        @Header(
                text = "Effect Name",
                subcategory = "Formatting",
                size = 2
        )
        public boolean effectNameFormattingHeader;

        @Text(
                name = "Custom Name",
                description = "Override the effect name with a custom one",
                subcategory = "Formatting",
                size = 2
        )
        public String customName = "";

        @Checkbox(
                name = "Bold Effect Name",
                description = "Bold the effect name",
                subcategory = "Formatting"
        )
        public boolean boldEffectName = false;

        @Checkbox(
                name = "Italic Effect Name",
                description = "Make the effect name italic",
                subcategory = "Formatting"
        )
        public boolean italicEffectName = false;

        @Checkbox(
                name = "Underline Effect Name",
                description = "Underline the effect name",
                subcategory = "Formatting"
        )
        public boolean underlineEffectName = false;

        @Header(
                text = "Duration",
                subcategory = "Formatting",
                size = 2
        )
        public boolean durationFormattingHeader;

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

        @Checkbox(
                name = "Bold Duration",
                description = "Bold the duration text",
                subcategory = "Formatting"
        )
        public boolean boldDuration = false;

        @Checkbox(
                name = "Italic Duration",
                description = "Make the duration text italic",
                subcategory = "Formatting"
        )
        public boolean italicDuration = false;

        @Checkbox(
                name = "Underline Duration",
                description = "Underline the duration text",
                subcategory = "Formatting"
        )
        public boolean underlineDuration = false;

        @Color(
                name = "Name Color",
                description = "The color of the effect name",
                subcategory = "Color"
        )
        public OneColor nameColor = new OneColor(255, 255, 255);

        @Color(
                name = "Duration Color",
                description = "The color of the duration text",
                subcategory = "Color"
        )
        public OneColor durationColor = new OneColor(255, 255, 255);

        @Checkbox(
                name = "Exclude",
                description = "Completely exclude the effect(s)",
                subcategory = "Exclusion",
                size = 2
        )
        public boolean exclude = false;

        @Checkbox(
                name = "Permanent Effects",
                description = "Exclude the effect(s) when the duration is permanent",
                subcategory = "Exclusion",
                size = 2
        )
        public boolean excludePermanentEffects = false;

        @Dropdown(
                name = "Exclude Duration Rule",
                description = "Exclude effects that are either above, below, at, or not at a certain duration threshold",
                options = {"None", "Above", "Below", "At", "Not At"},
                subcategory = "Exclusion"
        )
        public int excludeSetDuration = 0;

        @Dropdown(
                name = "Exclude Amplifier Rule",
                description = "Exclude effects that are either above, below, at, or not at a certain amplifier amount",
                options = {"None", "Above", "Below", "At", "Not At"},
                subcategory = "Exclusion"
        )
        public int excludeSetAmplifier = 0;

        @Dropdown(
                name = "Ambient Effects Rule",
                description = "Decide if effects from a beacon should be excluded.",
                subcategory = "Exclusion",
                options = {"None", "Exclude All Ambient", "Exclude All Non Ambient"}
        )
        public int ambientExclusionRule = 0;

        @Dropdown(
                name = "Emitting Particles Rule",
                description = "Decide if effects that allow particles should be excluded.",
                subcategory = "Exclusion",
                options = {"None", "Exclude All Emitting particles", "Exclude All Disallowing particles"}
        )
        public int particlesExclusionRule = 0;

        @Slider(
                name = "Excluded Duration Threshold",
                description = "The value(s) that will be excluded based off the duration rule",
                subcategory = "Exclusion",
                min = 2,
                max = 90
        )
        public float excludedDurationValues = 30f;

        @Slider(
                name = "Excluded Amplifier Value(s)",
                description = "The value(s) that will be excluded based off the amplifier rule",
                subcategory = "Exclusion",
                min = 0,
                max = 20,
                step = 1
        )
        public int excludedAmplifierValues = 10;

        public EffectConfig() {}
    }
}