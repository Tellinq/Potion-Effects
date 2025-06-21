package me.tellinq.potioneffects.mixin;

import cc.polyfrost.oneconfig.events.EventManager;

import me.tellinq.potioneffects.event.UpdatePotionEffectsEvent;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
    @Inject(method = "readEntityFromNBT", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;activePotionsMap:Ljava/util/Map;", shift = At.Shift.AFTER))
    private void inject$readEntityFromNBT(CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }

    @Inject(method = "addPotionEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;onNewPotionEffect(Lnet/minecraft/potion/PotionEffect;)V", shift = At.Shift.AFTER))
    private void inject$addPotionEffect(CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }

    @Inject(method = "addPotionEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;onChangedPotionEffect(Lnet/minecraft/potion/PotionEffect;Z)V", shift = At.Shift.AFTER))
    private void inject$addPotionEffect2(CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }

    @Inject(method = "removePotionEffect", at = @At(value = "TAIL"))
    public void inject$removePotionEffect(int potionId, CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }

    @Inject(method = "removePotionEffectClient", at = @At(value = "TAIL"))
    public void inject$removePotionEffectClient(int potionId, CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }
}
