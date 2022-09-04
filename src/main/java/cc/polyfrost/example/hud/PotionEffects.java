package cc.polyfrost.example.hud;

import cc.polyfrost.example.config.PotionEffectsConfig;
import cc.polyfrost.oneconfig.hud.BasicHud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.renderer.RenderManager;
import cc.polyfrost.oneconfig.utils.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PotionEffects extends BasicHud {
    public static final int ICON_SIZE = 18;
    private final ResourceLocation EFFECTS_RESOURCE = new ResourceLocation("textures/gui/container/inventory.png");
    public Minecraft mc = Minecraft.getMinecraft();

    private float width = 10f;
    private float height = 10f;

    public PotionEffects() {
        super(true);
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

//        if (sortingMode.get() == SortingMode.ALPHABETICAL) {
//            potionEffects.sort(Comparator.comparing(o -> I18n.format(o.getEffectName())));
//        } else if (sortingMode.get() == SortingMode.DURATION) {
//            potionEffects.sort(Comparator.comparingInt(PotionEffect::getDuration));
//            Collections.reverse(potionEffects);
//        }
//        if (verticalAlign.get() == VerticalAlignmentMode.UP) Collections.reverse(potionEffects);

        float yOff = 0;
        float xOff = 0;
        final int yAmt = (int) (ICON_SIZE + PotionEffectsConfig.verticalSpacing);

        this.height = (potionEffects.size() * yAmt) - PotionEffectsConfig.verticalSpacing;
        this.width = 10f;

        GlStateManager.pushMatrix();
//        GlStateManager.scale(getScale(), getScale(), 1);
        for (PotionEffect effect : potionEffects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (!potion.shouldRender(effect)) continue;

            float iconX = x;
            iconX /= getScale();

            GlStateManager.color(1f, 1f, 1f, 1f);

            if (PotionEffectsConfig.icon) {
                mc.getTextureManager().bindTexture(EFFECTS_RESOURCE);
                mc.ingameGUI.drawTexturedModalRect(iconX, (y + yOff) / getScale(), potion.getStatusIconIndex() % 8 * 18, 198 + potion.getStatusIconIndex() / 8 * 18, 18, 18);
                xOff = ICON_SIZE * getScale();
                this.width = Math.max(this.width, xOff / getScale());
            }

            if (PotionEffectsConfig.effectName) {
                if (PotionEffectsConfig.icon)
                    xOff = (ICON_SIZE + 4) * getScale();

                StringBuilder titleSb = new StringBuilder();
//                if (titleTextBold.get()) titleSb.append(EnumChatFormatting.BOLD);
//                if (titleTextItalic.get()) titleSb.append(EnumChatFormatting.ITALIC);
//                if (titleTextUnderline.get()) titleSb.append(EnumChatFormatting.UNDERLINE);
                titleSb.append(I18n.format(potion.getName()));
                int amplifier = Math.max(1, effect.getAmplifier() + 1);
                if (PotionEffectsConfig.amplifier && (amplifier != 1 || PotionEffectsConfig.levelOne)) {
                    titleSb.append(" ");
                    /*if (amplifierText.get() == AmplifierMode.ROMAN) */titleSb.append(amplifierNumerals(amplifier));
                    /*else titleSb.append(amplifier);*/
                }
                String builtTitle = titleSb.toString();

                int titleWidth = mc.fontRendererObj.getStringWidth(builtTitle);
                width = Math.max(width, (xOff / getScale()) + titleWidth);

                float titleX = x + xOff;
                titleX /= getScale();

                float titleY = y + yOff;
                if (!PotionEffectsConfig.duration)
                    titleY += mc.fontRendererObj.FONT_HEIGHT / 2f;
                titleY /= getScale();


                RenderManager.drawScaledString(builtTitle, titleX, titleY, -1, RenderManager.TextType.SHADOW, scale);

            }

            if (PotionEffectsConfig.duration) {
                if (PotionEffectsConfig.icon)
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
                if (!PotionEffectsConfig.effectName)
                    timeY -= mc.fontRendererObj.FONT_HEIGHT / 2f;
                timeY /= getScale();

                if (effect.getDuration() / 20f > PotionEffectsConfig.blinkDuration || effect.getDuration() % (50 - PotionEffectsConfig.blinkSpeed) <= (50 - PotionEffectsConfig.blinkSpeed) / 2f) {
                    RenderManager.drawScaledString(builtTime, timeX, timeY, PotionEffectsConfig.durationColor.getRGB(), RenderManager.TextType.SHADOW, scale);
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

    @Override
    protected float getWidth(float scale, boolean example) {
        return width;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return height;
    }
}
