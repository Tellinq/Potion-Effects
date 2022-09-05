package cc.polyfrost.example.hud;

import cc.polyfrost.example.PotionEffectsMod;
import cc.polyfrost.example.config.PotionEffectsConfig;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.BasicHud;
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
    public static final int ICON_SIZE = 18;
    private final ResourceLocation EFFECTS_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
    public Minecraft mc = Minecraft.getMinecraft();
    public PotionEffectsConfig config = PotionEffectsMod.INSTANCE.config;
    public Map<Integer, PotionEffectsConfig.EffectConfig> effectMap =
            new ImmutableMap.Builder<Integer, PotionEffectsConfig.EffectConfig>()
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
                    .put(Potion.invisibility.id, PotionEffectsConfig.invisibility)
                    .put(Potion.nightVision.id, PotionEffectsConfig.nightVision)
                    .put(Potion.hunger.id, PotionEffectsConfig.hunger)
                    .put(Potion.poison.id, PotionEffectsConfig.poison)
                    .put(Potion.wither.id, PotionEffectsConfig.wither)
                    .put(Potion.absorption.id, PotionEffectsConfig.absorption)
                    .build();

    private float width = 10f;
    private float height = 10f;

    public PotionEffects() {
        super(true, 0, 0, 1, false, false, 0, 0, 0, new OneColor(0, 0, 0, 120), false, 2, new OneColor(0, 0, 0));
    }

    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        // we would be rendering it twice
//        if (origin == RenderOrigin.HUD && overwriteIER.get() && mc.currentScreen instanceof InventoryEffectRenderer)
//            return;

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableLighting();

        List<PotionEffect> potionEffects = new ArrayList<>();
        if (mc.thePlayer != null) potionEffects.addAll(mc.thePlayer.getActivePotionEffects());

        if (potionEffects.isEmpty()) {
            if (example) {
                potionEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
                potionEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
            } else {
                return;
            }
        }

        switch (PotionEffectsConfig.sortingMethod) {
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
        if (PotionEffectsConfig.verticalSorting) Collections.reverse(potionEffects);

        float yOff = 0;
        float xOff = 0;
        final int yAmt = (int) (ICON_SIZE + PotionEffectsConfig.verticalSpacing);

        this.height = (potionEffects.size() * yAmt) - PotionEffectsConfig.verticalSpacing;
        this.width = 10f;

        GlStateManager.pushMatrix();
//        GlStateManager.scale(getScale(), getScale(), 1);
        for (PotionEffect effect : potionEffects) {
            PotionEffectsConfig.EffectConfig effectSetting = getEffectSetting(effect);
            PotionEffectsConfig.EffectConfig oComponent = useOverride(effectSetting, effectSetting.overrideComponent);
            PotionEffectsConfig.EffectConfig oAmplifier = useOverride(effectSetting, effectSetting.overrideAmplifier);
            PotionEffectsConfig.EffectConfig oBlinking = useOverride(effectSetting, effectSetting.overrideBlinking);
            PotionEffectsConfig.EffectConfig oFormatting = useOverride(effectSetting, effectSetting.overrideFormatting);
            PotionEffectsConfig.EffectConfig oColor = useOverride(effectSetting, effectSetting.overrideColor);
            PotionEffectsConfig.EffectConfig oExclusion = useOverride(effectSetting, effectSetting.overrideExclusion);
            if (excludePotions(oExclusion, effect)) continue;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) continue;

            float iconX = x;
            iconX /= getScale();

            GlStateManager.color(1f, 1f, 1f, 1f);

            if (oComponent.icon) {
                mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                mc.ingameGUI.drawTexturedModalRect(iconX, (y + yOff) / getScale(), potion.getStatusIconIndex() % 8 * 18, 198 + potion.getStatusIconIndex() / 8 * 18, 18, 18);
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


                RenderManager.drawScaledString(builtTitle, titleX, titleY, oColor.nameColor.getRGB(), textType, scale);

            }

            if (oComponent.duration) {
                if (oComponent.icon)
                    xOff = (ICON_SIZE + 4) * getScale();

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
                if (!oComponent.effectName)
                    timeY -= mc.fontRendererObj.FONT_HEIGHT / 2f;
                timeY /= getScale();

                if (effect.getDuration() / 20f > oBlinking.blinkDuration || effect.getDuration() % (50 - oBlinking.blinkSpeed) <= (50 - oBlinking.blinkSpeed) / 2f) {
                    RenderManager.drawScaledString(builtTime, timeX, timeY, oColor.durationColor.getRGB(), textType, scale);
                }
            }

            yOff += yAmt * getScale();
        }
        GlStateManager.popMatrix();
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

    public PotionEffectsConfig.EffectConfig getEffectSetting(PotionEffect effect) {
        for (Map.Entry<Integer, PotionEffectsConfig.EffectConfig> entry : effectMap.entrySet()) {
            if (effect.getPotionID() == entry.getKey()) {
                if (entry.getValue().override) return entry.getValue();
            }
        }
        return PotionEffectsConfig.global;
    }

    public PotionEffectsConfig.EffectConfig useOverride(PotionEffectsConfig.EffectConfig effectSetting, boolean overrideBoolean) {
        if (overrideBoolean) return effectSetting;
        else return PotionEffectsConfig.global;
    }

    private boolean excludePotions(PotionEffectsConfig.EffectConfig effectSetting, PotionEffect effect) {
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

//    private int getPotionColor(int color, PotionEffect effect) {
//        int time = effect.getDuration();
//        int value;
//        switch ((String) option) {
//            case "Effect":
//                Potion var9 = Potion.potionTypes[effect.getPotionID()];
//                switch (this.colorType.getStringValue()) {
//                    case "Default":
//                        value = potionColorDefault.get(effect.getPotionID());
//                        break;
//                    case "Potion Colors":
//                        value = potionColorPotionColors.get(effect.getPotionID());
//                        break;
//                    case "Color Codes":
//                        value = potionColorColorCodes.get(effect.getPotionID());
//                        break;
//                    case "Vibrant":
//                        value = potionColorsVibrant.get(effect.getPotionID());
//                        break;
//                    default:
//                        if (!var9.isBadEffect()) {
//                            value = -15691760;
//                        } else {
//                            value = -7335920;
//                        }
//                }
//                break;
//            case "Duration":
//                if (time >= 1200) {
//                    value = -16733696;
//                } else if (time >= 600) {
//                    value = -11141291;
//                } else if (time >= 200) {
//                    value = -171;
//                } else {
//                    value = time >= 100 ? -43691 : -5636096;
//                }
//                break;
//            default:
//                value = staticColor;
//        }
//        int opacity = value >> 24 & 0xFF;
//        opacity = (int)((float)opacity * (excludePotions(effect) ? 0.5F : 1.0F));
//        return value & 0xFFFFFF | opacity << 24;
//    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return width;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return height;
    }
}
