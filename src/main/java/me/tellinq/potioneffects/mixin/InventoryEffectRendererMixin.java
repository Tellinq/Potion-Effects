package me.tellinq.potioneffects.mixin;

import me.tellinq.potioneffects.PotionEffectsMod;
import me.tellinq.potioneffects.config.PotionEffectsConfig;
import me.tellinq.potioneffects.hud.PotionEffects;
import net.minecraft.client.renderer.InventoryEffectRenderer;
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
            if (!PotionEffectsConfig.INSTANCE.showPotionInfo && potionHUD.isEnabled()) {
                hasActivePotionEffects = true;
                ci.cancel();
                break;
            }
        }
    }

    @Inject(method = "drawActivePotionEffects", at = @At("HEAD"), cancellable = true)
    private void injectDrawActivePotionEffects(CallbackInfo ci) {
        for (PotionEffects potionHUD : PotionEffects.PotionHUDTracker.INSTANCE.instances) {
            if (potionHUD.isEnabled() && PotionEffectsConfig.INSTANCE.showHudInForeground) {
                potionHUD.renderFromInventory();
            }
        }

        if (!PotionEffectsConfig.INSTANCE.showPotionInfo && PotionEffectsMod.INSTANCE.config.enabled) {
            ci.cancel();
        }
    }
}
