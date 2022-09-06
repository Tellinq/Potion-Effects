package tk.tellinq.potioneffects.hud;

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

import java.util.*;

public class PotionEffects extends BasicHud {

    @Slider(
            name = "Vertical Spacing",
            min = 0,
            max = 10
    )
    public float verticalSpacing = 4f;

    @DualOption(
            name = "Vertical Sorting",
            left = "Top",
            right = "Bottom"
    )
    public boolean verticalSorting = false;

    @Dropdown(
            name = "Sorting Method",
            options = {"Vanilla", "Alphabetical", "Duration", "Amplifier"}
    )
    public int sortingMethod = 0;

    @Switch(
            name = "Show Excluded Effects in HUD Editor"
    )
    public boolean showExcludedEffects = true;

    @Info(
            text = "Not recommended to disable if all effects are excluded!",
            type = InfoType.WARNING
    )
    public String info;

    @Exclude public final int ICON_SIZE = 18;
    @Exclude private final ResourceLocation EFFECTS_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
    @Exclude public final Minecraft mc = Minecraft.getMinecraft();
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

    private float width = 10f;
    private float height = 10f;
    @Exclude private int ticks = 0;

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
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableLighting();

        List<PotionEffect> potionEffects = new ArrayList<>();
        if (mc.thePlayer != null) potionEffects.addAll(mc.thePlayer.getActivePotionEffects());

        if (potionEffects.isEmpty()) {
            if (example) {
                potionEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
                potionEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
            } else {
                this.width = 0;
                this.height = 0;
                return;
            }
        }

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

        float yOff = 0;
        float xOff = 0;
        final int yAmt = (int) ((ICON_SIZE + this.verticalSpacing) * scale);

        this.height = ((potionEffects.size() * yAmt) - this.verticalSpacing) * scale;
        this.width = 10f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(getScale(), getScale(), 1);
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
                    this.height -= yAmt * scale;
                    continue;
                }
            }
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) continue;

            float iconX = x;
            iconX /= getScale();

            GlStateManager.color(1f, 1f, 1f, excluded ? 0.5f : 1f);

            if (oComponent.icon) {
                mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectIconBlink, effect.getDuration())) {
                    mc.ingameGUI.drawTexturedModalRect(iconX, (y + yOff) / getScale(), potion.getStatusIconIndex() % 8 * 18, 198 + potion.getStatusIconIndex() / 8 * 18, 18, 18);
                }
                xOff = ICON_SIZE * getScale();
                this.width = Math.max(this.width, xOff / getScale());
            }

            RenderManager.TextType textType = RenderManager.TextType.NONE;
            switch (oFormatting.textType) {
                case 1:
                    textType = RenderManager.TextType.SHADOW;
                    break;
                case 2:
                    textType = RenderManager.TextType.FULL;
                    break;
                default:
            }

            if (oComponent.effectName) {
                if (oComponent.icon)
                    xOff = (ICON_SIZE + 4) * getScale();

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
                    if (!oAmplifier.romanNumerals) titleSb.append(amplifierNumerals(amplifier));
                    else titleSb.append(amplifier);
                }
                String builtTitle = titleSb.toString();

                int titleWidth = mc.fontRendererObj.getStringWidth(builtTitle);
                width = Math.max(width, (xOff / getScale()) + titleWidth);

                float titleX = x + xOff;
                titleX /= getScale();

                float titleY = y + yOff;
                if (!oComponent.duration)
                    titleY += mc.fontRendererObj.FONT_HEIGHT / 2f;
                titleY /= getScale();

                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectNameBlink, effect.getDuration())) {
                    RenderManager.drawScaledString(builtTitle, titleX, titleY, getColor(oColor.nameColor.getRGB(), excluded), textType, scale);
                }


            }

            if (oComponent.duration) {
                if (oComponent.icon) xOff = (ICON_SIZE + 4) * getScale();

                StringBuilder timeSb = new StringBuilder();
                if (oFormatting.boldDuration) timeSb.append(EnumChatFormatting.BOLD);
                if (oFormatting.italicDuration) timeSb.append(EnumChatFormatting.ITALIC);
                if (oFormatting.underlineDuration) timeSb.append(EnumChatFormatting.UNDERLINE);
                if (effect.getIsPotionDurationMax()) timeSb.append(oFormatting.maxDurationString);
                else timeSb.append(Potion.getDurationString(effect));
                String builtTime = timeSb.toString();

                int timeWidth = mc.fontRendererObj.getStringWidth(builtTime);
                width = Math.max(width, (xOff / getScale()) + timeWidth);

                float timeX = x + xOff;
                timeX /= getScale();

                float timeY = y + yOff + (mc.fontRendererObj.FONT_HEIGHT) + 1;
                if (!oComponent.effectName) timeY -= mc.fontRendererObj.FONT_HEIGHT / 2f;
                timeY /= getScale();
                if (showEffectDuringBlink(oBlinking, oBlinking.makeEffectDurationBlink, effect.getDuration())) {
                    RenderManager.drawScaledString(builtTime, timeX, timeY, getColor(oColor.durationColor.getRGB(), excluded), textType, scale);
                }
            }

            yOff += yAmt * getScale();
        }
        GlStateManager.popMatrix();
    }

    private boolean showEffectDuringBlink(EffectConfig effectConfig, boolean blinkType, float duration) {
        float blinkSpeed = effectConfig.blinkSpeed / 3.0f;
        if (effectConfig.blink && blinkType && duration <= effectConfig.blinkDuration * 20.0F) {
            if (this.ticks > blinkSpeed * 2) {
                this.ticks = 0;
            }
            return this.ticks <= blinkSpeed;
        }
        return true;
    }


    private String amplifierNumerals(int level) {
        if (level < 0) {
            level = 127 + Math.abs(128 + level);
        }
        int l = level;
            return String.join("", Collections.nCopies(l, "I"))
                    .replace("IIIII", "V").replace("IIII", "IV").replace("VV", "X")
                    .replace("VIV", "IX").replace("XXXXX", "L").replace("XXXX", "XL")
                    .replace("LL", "C").replace("LXL", "XC").replace("CCCCC", "D")
                    .replace("CCCC", "CD").replace("DD", "M").replace("DCD", "CM");
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
        if (effectSetting.excludePermanentEffects && effect.getIsPotionDurationMax()) {
            return true;
        }
        if (this.excludeArrayOptions(effectSetting.excludeSetDuration, effect.getDuration(), effectSetting.excludedDurationValues * 20.0F) && !effect.getIsPotionDurationMax()) return true;
        if (this.excludeArrayOptions(effectSetting.excludeSetAmplifier, effect.getAmplifier(), effectSetting.excludedAmplifierValues - 1)) return true;
        return effectSetting.exclude;
    }

    protected boolean excludeArrayOptions(int set, int realValue, float sliderValue) {
        switch (set) {
            case 1: return realValue > sliderValue;
            case 2: return realValue < sliderValue;
            case 3: return realValue == sliderValue;
            case 4: return realValue != sliderValue;
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
        return width;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return height;
    }

    public static class EffectConfig {
        public String effect;


        @Switch(
                name = "Override",
                subcategory = "Override",
                size = 2
        )
        public boolean override = false;

        @Checkbox(
                name = "Override Component",
                subcategory = "Override"
        )
        public boolean overrideComponent = false;

        @Checkbox(
                name = "Override Amplifier",
                subcategory = "Override"
        )
        public boolean overrideAmplifier = false;

        @Checkbox(
                name = "Override Blinking",
                subcategory = "Override"
        )
        public boolean overrideBlinking = false;

        @Checkbox(
                name = "Override Formatting",
                subcategory = "Override"
        )
        public boolean overrideFormatting = true;

        @Checkbox(
                name = "Override Color",
                subcategory = "Override"
        )
        public boolean overrideColor = true;

        @Checkbox(
                name = "Override Exclusion",
                subcategory = "Override"
        )
        public boolean overrideExclusion = true;

        @Checkbox(
                name = "Icon",
                subcategory = "Component"
        )
        public boolean icon = true;

        @Checkbox(
                name = "Effect Name",
                subcategory = "Component"
        )
        public boolean effectName = true;

        @Checkbox(
                name = "Duration",
                subcategory = "Component"
        )
        public boolean duration = true;

        @Switch(
                name = "Show Amplifier",
                subcategory = "Amplifier"
        )
        public boolean amplifier = true;

        @Switch(
                name = "Show Level One",
                subcategory = "Amplifier"
        )
        public boolean levelOne = false;

        @DualOption(
                name = "Amplifier Numerals",
                subcategory = "Amplifier",
                left = "Roman",
                right = "Arabic"
        )
        public boolean romanNumerals = false;

        @Switch(
                name = "Blink",
                subcategory = "Blinking"
        )
        public boolean blink = true;

        @Switch(
                name = "Make Icon Blink",
                subcategory = "Blinking"
        )
        public boolean makeEffectIconBlink = false;

        @Switch(
                name = "Make Effect Name Blink",
                subcategory = "Blinking"
        )
        public boolean makeEffectNameBlink = false;

        @Switch(
                name = "Make Duration Blink",
                subcategory = "Blinking"
        )
        public boolean makeEffectDurationBlink = true;

        @Slider(
                name = "Blink Duration",
                subcategory = "Blinking",
                min = 0,
                max = 60
        )
        public float blinkDuration = 10;

        @Slider(
                name = "Blink Speed",
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

        @Text(
                name = "Custom Name",
                subcategory = "Formatting",
                size = 2
        )
        public String customName = "";

        @Checkbox(
                name = "Bold Effect Name",
                subcategory = "Formatting"
        )
        public boolean boldEffectName = false;

        @Checkbox(
                name = "Italic Effect Name",
                subcategory = "Formatting"
        )
        public boolean italicEffectName = false;

        @Checkbox(
                name = "Underline Effect Name",
                subcategory = "Formatting"
        )
        public boolean underlineEffectName = false;

        @Text(
                name = "Max Duration String",
                subcategory = "Formatting",
                size = 2
        )
        public String maxDurationString = "**:**";

        @Checkbox(
                name = "Bold Duration",
                subcategory = "Formatting"
        )
        public boolean boldDuration = false;

        @Checkbox(
                name = "Italic Duration",
                subcategory = "Formatting"
        )
        public boolean italicDuration = false;

        @Checkbox(
                name = "Underline Duration",
                subcategory = "Formatting"
        )
        public boolean underlineDuration = false;

        @Color(
                name = "Name Color",
                subcategory = "Color"
        )
        public OneColor nameColor = new OneColor(255, 255, 255);

        @Color(
                name = "Duration Color",
                subcategory = "Color"
        )
        public OneColor durationColor = new OneColor(255, 255, 255);

        @Checkbox(
                name = "Exclude",
                subcategory = "Exclusion",
                size = 2
        )
        public boolean exclude = false;

        @Checkbox(
                name = "Permanent Effects",
                subcategory = "Exclusion",
                size = 2
        )
        public boolean excludePermanentEffects = false;

        @Dropdown(
                name = "Exclude Duration Set",
                options = {"None", "Above", "Below", "At", "Not At"},
                subcategory = "Exclusion"
        )
        public int excludeSetDuration = 0;

        @Slider(
                name = "Excluded Duration Value(s)",
                subcategory = "Exclusion",
                min = 2,
                max = 90
        )
        public float excludedDurationValues = 30f;

        @Dropdown(
                name = "Exclude Amplifier Set",
                options = {"None", "Above", "Below", "At", "Not At"},
                subcategory = "Exclusion"
        )
        public int excludeSetAmplifier = 0;

        @Slider(
                name = "Excluded Amplifier Value(s)",
                subcategory = "Exclusion",
                min = 0,
                max = 20,
                step = 1
        )
        public int excludedAmplifierValues = 10;



        public EffectConfig(String effect) {
            this.effect = effect;
        }
    }
}
