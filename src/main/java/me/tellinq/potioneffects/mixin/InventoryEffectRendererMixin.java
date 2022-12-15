package me.tellinq.potioneffects.mixin;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import me.tellinq.potioneffects.config.PotionEffectsConfig;
import me.tellinq.potioneffects.event.UpdatePotionEffectsEvent;
import me.tellinq.potioneffects.hud.PotionEffects;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryEffectRenderer.class)
public class InventoryEffectRendererMixin {
    @Shadow
    private boolean hasActivePotionEffects;

    @Inject(method = "updateActivePotionEffects", at = @At("HEAD"), cancellable = true)
    protected void injectUpdateActivePotionEffects(CallbackInfo ci) {
        for (PotionEffects potionHUD : PotionEffects.PotionHUDTracker.INSTANCE.instances) {
            if (PotionEffectsConfig.INSTANCE.overwriteIER && potionHUD.isEnabled()) {
                hasActivePotionEffects = true;
                ci.cancel();
                break;
            }
        }
    }

    @Inject(method = "drawActivePotionEffects", at = @At("HEAD"), cancellable = true)
    private void injectDrawActivePotionEffects(CallbackInfo ci) {
        boolean cancel = false;

        for (PotionEffects potionHUD : PotionEffects.PotionHUDTracker.INSTANCE.instances) {
            if (PotionEffectsConfig.INSTANCE.overwriteIER && potionHUD.isEnabled()) {
                cancel = true;

                potionHUD.drawAll(new UMatrixStack(), false);
            }
        }

        if (cancel) {
            ci.cancel();
        }
    }
}
