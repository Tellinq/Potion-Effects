package me.tellinq.potioneffects.mixin;

import cc.polyfrost.oneconfig.events.EventManager;

import me.tellinq.potioneffects.event.UpdatePotionEffectsEvent;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
    /** Makes UpdatePotionEffectsEvent run right before potionsNeedUpdate is set to false. */
    @Inject(method = "updatePotionEffects", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;potionsNeedUpdate:Z", shift = At.Shift.BEFORE, opcode = Opcodes.PUTFIELD))
    private void onPotionFinishNeededUpdate(CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }
}
