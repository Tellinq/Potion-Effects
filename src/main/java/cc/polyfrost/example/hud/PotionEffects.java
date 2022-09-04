package cc.polyfrost.example.hud;

import cc.polyfrost.example.PotionEffectsMod;
import cc.polyfrost.example.config.PotionEffectsConfig;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.RenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PotionEffects extends BasicHud {
    public static final int ICON_SIZE = 18;
    private final ResourceLocation EFFECTS_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
    public Minecraft mc = Minecraft.getMinecraft();
    public PotionEffectsConfig config = PotionEffectsMod.INSTANCE.config;

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
//        potionEffects = filterEffects(potionEffects);

        if (potionEffects.isEmpty()) {
            if (example) {
                potionEffects.add(new PotionEffect(Potion.moveSpeed.id, 1200, 1));
                potionEffects.add(new PotionEffect(Potion.damageBoost.id, 30, 3));
//                potionEffects = filterEffects(potionEffects);
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
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) continue;

            float iconX = x;
            iconX /= getScale();

            GlStateManager.color(1f, 1f, 1f, 1f);

            if (effectSetting.icon) {
                mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                mc.ingameGUI.drawTexturedModalRect(iconX, (y + yOff) / getScale(), potion.getStatusIconIndex() % 8 * 18, 198 + potion.getStatusIconIndex() / 8 * 18, 18, 18);
                xOff = ICON_SIZE * getScale();
                this.width = Math.max(this.width, xOff / getScale());
            }

            if (effectSetting.effectName) {
                if (effectSetting.icon)
                    xOff = (ICON_SIZE + 4) * getScale();

                StringBuilder titleSb = new StringBuilder();
//                if (titleTextBold.get()) titleSb.append(EnumChatFormatting.BOLD);
//                if (titleTextItalic.get()) titleSb.append(EnumChatFormatting.ITALIC);
//                if (titleTextUnderline.get()) titleSb.append(EnumChatFormatting.UNDERLINE);
                titleSb.append(I18n.format(potion.getName()));
                int amplifier = Math.max(1, effect.getAmplifier() + 1);
                if (effectSetting.amplifier && (amplifier != 1 || effectSetting.levelOne)) {
                    titleSb.append(" ");
                    if (!effectSetting.romanNumerals) titleSb.append(amplifierNumerals(amplifier));
                    else titleSb.append(amplifier);
                }
                String builtTitle = titleSb.toString();

                int titleWidth = mc.fontRendererObj.getStringWidth(builtTitle);
                width = Math.max(width, (xOff / getScale()) + titleWidth);

                float titleX = x + xOff;
                titleX /= getScale();

                float titleY = y + yOff;
                if (!effectSetting.duration)
                    titleY += mc.fontRendererObj.FONT_HEIGHT / 2f;
                titleY /= getScale();


                RenderManager.drawScaledString(builtTitle, titleX, titleY, effectSetting.nameColor.getRGB(), RenderManager.TextType.SHADOW, scale);

            }

            if (effectSetting.duration) {
                if (effectSetting.icon)
                    xOff = (ICON_SIZE + 4) * getScale();

                StringBuilder timeSb = new StringBuilder();
//                if (timeTextItalic.get()) timeSb.append(EnumChatFormatting.ITALIC);
//                if (timeTextUnderline.get()) timeSb.append(EnumChatFormatting.UNDERLINE);
                if (effect.getIsPotionDurationMax()) timeSb.append("**:**");
                else timeSb.append(Potion.getDurationString(effect));
                String builtTime = timeSb.toString();

                int timeWidth = mc.fontRendererObj.getStringWidth(builtTime);
                width = Math.max(width, (xOff / getScale()) + timeWidth);

                float timeX = x + xOff;
                timeX /= getScale();

                float timeY = y + yOff + (mc.fontRendererObj.FONT_HEIGHT) + 1;
                if (!effectSetting.effectName)
                    timeY -= mc.fontRendererObj.FONT_HEIGHT / 2f;
                timeY /= getScale();

                if (effect.getDuration() / 20f > effectSetting.blinkDuration || effect.getDuration() % (50 - effectSetting.blinkSpeed) <= (50 - effectSetting.blinkSpeed) / 2f) {
                    RenderManager.drawScaledString(builtTime, timeX, timeY, effectSetting.durationColor.getRGB(), RenderManager.TextType.SHADOW, scale);
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
        if (effect.getPotionID() == Potion.moveSpeed.id) {
            return PotionEffectsConfig.speed;
        } else if (effect.getPotionID() == Potion.moveSlowdown.id) {
            return PotionEffectsConfig.slowness;
        } else if (effect.getPotionID() == Potion.digSpeed.id) {
            return PotionEffectsConfig.haste;
        } else if (effect.getPotionID() == Potion.digSlowdown.id) {
            return PotionEffectsConfig.miningFatigue;
        } else if (effect.getPotionID() == Potion.damageBoost.id) {
            return PotionEffectsConfig.strength;
        } else if (effect.getPotionID() == Potion.jump.id) {
            return PotionEffectsConfig.jumpBoost;
        } else if (effect.getPotionID() == Potion.confusion.id) {
            return PotionEffectsConfig.nausea;
        } else if (effect.getPotionID() == Potion.regeneration.id) {
            return PotionEffectsConfig.regeneration;
        } else if (effect.getPotionID() == Potion.resistance.id) {
            return PotionEffectsConfig.resistance;
        } else if (effect.getPotionID() == Potion.fireResistance.id) {
            return PotionEffectsConfig.fireResistance;
        } else if (effect.getPotionID() == Potion.waterBreathing.id) {
            return PotionEffectsConfig.waterBreathing;
        } else if (effect.getPotionID() == Potion.invisibility.id) {
            return PotionEffectsConfig.invisibility;
        } else if (effect.getPotionID() == Potion.blindness.id) {
            return PotionEffectsConfig.blindness;
        } else if (effect.getPotionID() == Potion.nightVision.id) {
            return PotionEffectsConfig.nightVision;
        } else if (effect.getPotionID() == Potion.hunger.id) {
            return PotionEffectsConfig.hunger;
        } else if (effect.getPotionID() == Potion.poison.id) {
            return PotionEffectsConfig.poison;
        } else if (effect.getPotionID() == Potion.wither.id) {
            return PotionEffectsConfig.wither;
        } else if (effect.getPotionID() == Potion.absorption.id) {
            return PotionEffectsConfig.absorption;
        } else {
            return PotionEffectsConfig.global;
        }
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return width;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return height;
    }
}
