package me.tellinq.potioneffects.mixin;

import cc.polyfrost.oneconfig.events.EventManager;
import me.tellinq.potioneffects.event.UpdatePotionEffectsEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "TAIL"))
    private void inject$loadWolEnd(CallbackInfo ci) {
        EventManager.INSTANCE.post(new UpdatePotionEffectsEvent());
    }
}
