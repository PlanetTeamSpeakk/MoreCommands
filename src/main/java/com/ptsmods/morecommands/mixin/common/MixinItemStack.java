package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.commands.server.unelevated.PowerToolCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.util.ReflectionHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	@Unique private int level;

	@Inject(at = @At("HEAD"), method = "addEnchantment")
	public void addEnchantment(Enchantment enchantment, int level, CallbackInfo cbi) {
		this.level = level;
	}

	@Group(name = "enchantmentLevel1171Compat", min = 1, max = 1)
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;putShort(Ljava/lang/String;S)V"), method = "addEnchantment")
	public void addEnchantment_putShort(NbtCompound nbt, String key, short value) {
		nbt.putInt(key, level);
	}

	@Group(name = "enchantmentLevel1171Compat", min = 1, max = 1)
	@ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;createNbt(Lnet/minecraft/util/Identifier;I)Lnet/minecraft/nbt/NbtCompound;"), method = "addEnchantment")
	public int addEnchantment_createNbt_lvl(int lvl) {
		return level;
	}

	@Inject(at = @At("RETURN"), method = "getTranslationKey()Ljava/lang/String;", cancellable = true)
	public void getTranslationKey(CallbackInfoReturnable<String> cbi) {
		ItemStack thiz = ReflectionHelper.cast(this);
		if (thiz.getItem() == Items.SPAWNER) cbi.setReturnValue("block.minecraft.spawner_" + Registry.ENTITY_TYPE.get(thiz.getNbt() != null && thiz.getNbt().contains("BlockEntityTag", 10) && thiz.getNbt().getCompound("BlockEntityTag").contains("SpawnData", 10) ? new Identifier(thiz.getNbt().getCompound("BlockEntityTag").getCompound("SpawnData").getString("id")) : Registry.ENTITY_TYPE.getDefaultId()).getTranslationKey());
	}

	@Inject(at = @At("TAIL"), method = "getName()Lnet/minecraft/text/Text;", cancellable = true)
	public void getName(CallbackInfoReturnable<Text> cbi) {
		ItemStack thiz = ReflectionHelper.cast(this);
		NbtCompound compoundTag = thiz.getSubNbt("display");
		if ((compoundTag == null || !compoundTag.contains("Name", 8)) && thiz.getItem() == Items.SPAWNER) cbi.setReturnValue(new TranslatableText(thiz.getTranslationKey()).setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW)));
	}

	@Inject(at = @At("RETURN"), method = "hasGlint()Z", cancellable = true)
	public void hasGlint(CallbackInfoReturnable<Boolean> cbi) {
		cbi.setReturnValue(ClientOptions.Rendering.powertoolsGlint.getValue() && PowerToolCommand.isPowerTool(ReflectionHelper.cast(this)) || cbi.getReturnValue());
	}
}
