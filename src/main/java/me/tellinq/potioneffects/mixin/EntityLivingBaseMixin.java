package me.tellinq.potioneffects.mixin;

import cc.polyfrost.oneconfig.events.EventManager;
import me.tellinq.potioneffects.event.UpdatePotionMetadataEvent;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class EntityLivingBaseMixin {
    @Inject(method = "updatePotionMetadata", at = @At(value = "TAIL"))
    private void onUpdatePotionMetadata(CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionMetadataEvent());
    }
}
