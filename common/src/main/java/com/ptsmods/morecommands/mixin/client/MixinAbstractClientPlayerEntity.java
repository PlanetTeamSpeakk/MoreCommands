package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.ReflectionHelper;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayerEntity {

    @Inject(at = @At("RETURN"), method = "getCloakTextureLocation", cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<ResourceLocation> cbi) {
        if (MoreCommands.INSTANCE.isCool(ReflectionHelper.cast(this)))
            cbi.setReturnValue(new ResourceLocation("morecommands:textures/cape.png"));
    }
}
