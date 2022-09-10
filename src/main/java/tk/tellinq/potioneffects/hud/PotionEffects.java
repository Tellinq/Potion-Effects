package tk.tellinq.potioneffects.hud;

import cc.polyfrost.oneconfig.libs.universal.UGraphics;
import cc.polyfrost.oneconfig.libs.universal.UMinecraft;
import tk.tellinq.potioneffects.config.PotionEffectsConfig;
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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import tk.tellinq.potioneffects.util.RomanNumeral;

import java.util.*;

public class PotionEffects extends BasicHud {

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
    @Exclude private List<PotionEffect> potionEffects;
    @Exclude private boolean dummy = false;

    public PotionEffects() {
        super(true, 0, 0, 1, false, false, 0, 0, 0, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
        EventManager.INSTANCE.register(this);
    }

    @Subscribe
    private void onTick(TickEvent event) {
        if (event.stage == Stage.START) {
            ++this.ticks;
        }
    }

    @Override
    public void drawAll(UMatrixStack matrices, boolean example) {
        potionEffects = new ArrayList<>();
        if (mc.thePlayer != null) {
            potionEffects.addAll(mc.thePlayer.getActivePotionEffects());
        }
        this.dummy = example && potionEffects.isEmpty();
        if (this.dummy) {
            potionEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
            potionEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
        }

        int amount = 0;
        for (PotionEffect effect : potionEffects) {
            EffectConfig effectSetting = getEffectSetting(effect);
            EffectConfig oExclusion = useOverride(effectSetting, effectSetting.overrideExclusion);
            if (excludePotions(oExclusion, effect)) {
                if (!example) {
                    continue;
                }
            }
            amount++;
        }

        if (amount == 0) {
            return;
        }

        super.drawAll(matrices, example);
    }

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        GlStateManager.disableLighting();

        this.softEffects(potionEffects);

        float yOff = 0;
        float xOff = 0;
        int yAmt = (int) ((ICON_SIZE + this.verticalSpacing));

        this.height = ((potionEffects.size() * yAmt) - this.verticalSpacing);
        this.width = 0f;

        GlStateManager.pushMatrix();
        UGraphics.GL.scale(scale, scale, 1);
        UGraphics.GL.translate(x / scale - x, y / scale - y, 1);
        for (PotionEffect effect : potionEffects) {
            EffectConfig effectSetting = getEffectSetting(effect);
            EffectConfig oComponent = useOverride(effectSetting, effectSetting.overrideComponent);
            EffectConfig oAmplifier = useOverride(effectSetting, effectSetting.overrideAmplifier);
            EffectConfig oBlinking = useOverride(effectSetting, effectSetting.overrideBlinking);
            EffectConfig oFormatting = useOverride(effectSetting, effectSetting.overrideFormatting);
            EffectConfig oColor = useOverride(effectSetting, effectSetting.overrideColor);
            EffectConfig oExclusion = useOverride(effectSetting, effectSetting.overrideExclusion);
            boolean excluded = false;
            if (excludePotions(oExclusion, effect)) {
                if (example && this.showExcludedEffects) {
                    excluded = true;
                } else {
                    this.height -= yAmt;
                    continue;
                }
            }
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) continue;


            if (oComponent.icon) {
                GlStateManager.color(1f, 1f, 1f, excluded ? 0.5f : 1f);
                mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectIconBlink, effect.getDuration())) {
                    mc.ingameGUI.drawTexturedModalRect(x, (y + yOff), potion.getStatusIconIndex() % 8 * 18, 198 + potion.getStatusIconIndex() / 8 * 18, 18, 18);
                }
                xOff = ICON_SIZE;
                this.width = Math.max(this.width, xOff);
            }

            if (oComponent.effectName) {
                if (oComponent.icon) {
                    xOff = (ICON_SIZE + 4);
                }


                StringBuilder titleSb = new StringBuilder();
                if (oFormatting.boldEffectName) titleSb.append(EnumChatFormatting.BOLD);
                if (oFormatting.italicEffectName) titleSb.append(EnumChatFormatting.ITALIC);
                if (oFormatting.underlineEffectName) titleSb.append(EnumChatFormatting.UNDERLINE);
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

                int titleWidth = mc.fontRendererObj.getStringWidth(builtTitle);
                width = Math.max(width, xOff + titleWidth);

                float titleX = x + xOff;

                float titleY = y + yOff;
                if (!oComponent.duration)
                    titleY += mc.fontRendererObj.FONT_HEIGHT / 2f + 0.5f;

                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectNameBlink, effect.getDuration())) {
                    RenderManager.drawScaledString(builtTitle, titleX, titleY, getColor(oColor.nameColor.getRGB(), excluded), RenderManager.TextType.toType(oFormatting.textType), 1);
                }


            }

            if (oComponent.duration) {
                if (oComponent.icon) xOff = (ICON_SIZE + 4);

                StringBuilder timeSb = new StringBuilder();
                if (oFormatting.boldDuration) timeSb.append(EnumChatFormatting.BOLD);
                if (oFormatting.italicDuration) timeSb.append(EnumChatFormatting.ITALIC);
                if (oFormatting.underlineDuration) timeSb.append(EnumChatFormatting.UNDERLINE);
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

                int timeWidth = mc.fontRendererObj.getStringWidth(builtTime);
                width = Math.max(width, (xOff) + timeWidth);

                float timeX = x + xOff;

                float timeY = y + yOff + (mc.fontRendererObj.FONT_HEIGHT) + 1;
                if (!oComponent.effectName) timeY -= mc.fontRendererObj.FONT_HEIGHT / 2f + 0.5f;
                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectDurationBlink, effect.getDuration())) {
                    RenderManager.drawScaledString(builtTime, timeX, timeY, getColor(oColor.durationColor.getRGB(), excluded), RenderManager.TextType.toType(oFormatting.textType), 1);
                }
            }

            yOff += yAmt;
        }
        GlStateManager.popMatrix();
    }

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
        if (this.verticalSorting) Collections.reverse(potionEffects);
    }

    private boolean showEffectDuringBlink(EffectConfig effectConfig, boolean blinkType, float duration) {
        if (effectConfig.blink && blinkType) {
            if (effectConfig.syncBlinking || this.dummy) {
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

    public EffectConfig getEffectSetting(PotionEffect effect) {
        for (Map.Entry<Integer, EffectConfig> entry : effectMap.entrySet()) {
            if (effect.getPotionID() == entry.getKey()) {
                if (entry.getValue().override) return entry.getValue();
            }
        }
        return PotionEffectsConfig.global;
    }

    public EffectConfig useOverride(EffectConfig effectSetting, boolean overrideBoolean) {
        if (overrideBoolean) return effectSetting;
        else return PotionEffectsConfig.global;
    }

    private boolean excludePotions(EffectConfig effectSetting, PotionEffect effect) {
        if (effectSetting.excludePermanentEffects && effect.getIsPotionDurationMax()) return true;
        if (this.excludeBulk(effectSetting.ambientExclusionRule, effect.getIsAmbient())) return true;
        if (this.excludeBulk(effectSetting.particlesExclusionRule, effect.getIsShowParticles())) return true;
        if (this.excludeArrayOptions(effectSetting.excludeSetDuration, effect.getDuration(), effectSetting.excludedDurationValues * 20.0F) && !effect.getIsPotionDurationMax()) return true;
        if (this.excludeArrayOptions(effectSetting.excludeSetAmplifier, effect.getAmplifier(), effectSetting.excludedAmplifierValues - 1)) return true;
        return effectSetting.exclude;
    }

    protected boolean excludeBulk(int rule, boolean currentValue) {
        switch (rule) {
            case 1: return currentValue;
            case 2: return !currentValue;
            default: return false;
        }
    }

    protected boolean excludeArrayOptions(int rule, int currentValue, float threshold) {
        switch (rule) {
            case 1: return currentValue > threshold;
            case 2: return currentValue < threshold;
            case 3: return currentValue == threshold;
            case 4: return currentValue != threshold;
            default: return false;
        }
    }

    private int getColor(int color, boolean excluded) {
        int opacity = color >> 24 & 0xFF;
        opacity = (int)((float)opacity * (excluded ? 0.5F : 1.0F));
        return color & 0xFFFFFF | opacity << 24;
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return width * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return height * scale;
    }

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
