package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.miscellaneous.ClientOptions;
import com.ptsmods.morecommands.miscellaneous.Rainbow;
import com.ptsmods.morecommands.miscellaneous.ReflectionHelper;
import com.ptsmods.morecommands.mixin.client.accessor.MixinStyleAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;

@Mixin(Style.class)
public class MixinStyle {
	@Inject(at = @At("RETURN"), method = "getColor()Lnet/minecraft/text/TextColor;")
	public TextColor getColor(CallbackInfoReturnable<TextColor> cbi) {
		return ClientOptions.EasterEggs.rainbows.getValue() && Rainbow.getInstance() != null ? Rainbow.getInstance().RAINBOW_TC : cbi.getReturnValue();
	}
}
