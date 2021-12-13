package com.ptsmods.morecommands.mixin.common;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.arguments.ServerSideArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArgumentTypes.class)
public class MixinArgumentTypes {
    @Inject(at = @At("HEAD"), method = "toPacket", cancellable = true)
    private static <T extends ArgumentType<?>> void toPacket(PacketByteBuf buf, T argumentType, CallbackInfo cbi) {
        if (MoreCommands.isServerOnly() && argumentType instanceof ServerSideArgumentType) {
            cbi.cancel();
            ArgumentTypes.toPacket(buf, ((ServerSideArgumentType) argumentType).toVanillaArgumentType());
        }
    }

    @Inject(at = @At("HEAD"), method = "toJson(Lcom/google/gson/JsonObject;Lcom/mojang/brigadier/arguments/ArgumentType;)V", cancellable = true)
    private static <T extends ArgumentType<?>> void toJson(JsonObject jsonObject, T argumentType, CallbackInfo cbi) {
        if (MoreCommands.isServerOnly() && argumentType instanceof ServerSideArgumentType) {
            cbi.cancel();
            toJson(jsonObject, ((ServerSideArgumentType) argumentType).toVanillaArgumentType());
        }
    }

    @Shadow private static <T extends ArgumentType<?>> void toJson(JsonObject jsonObject, T argumentType) {}
}
