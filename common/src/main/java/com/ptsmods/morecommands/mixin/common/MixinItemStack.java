package com.ptsmods.morecommands.mixin.common;

import com.ptsmods.morecommands.api.ReflectionHelper;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.unelevated.PowerToolCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    private @Unique int level;

    @Inject(at = @At("HEAD"), method = "enchant")
    public void addEnchantment(Enchantment enchantment, int level, CallbackInfo cbi) {
        this.level = level;
    }


    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;storeEnchantment(Lnet/minecraft/resources/ResourceLocation;I)Lnet/minecraft/nbt/CompoundTag;"), method = "enchant")
    public int addEnchantment_createNbt_lvl(int lvl) {
        return level;
    }

    @Inject(at = @At("RETURN"), method = "getDescriptionId", cancellable = true)
    public void getTranslationKey(CallbackInfoReturnable<String> cbi) {
        ItemStack thiz = ReflectionHelper.cast(this);
        if (thiz.getItem() != Items.SPAWNER || thiz.getTag() == null || !thiz.getTag().contains("BlockEntityTag", 10) ||
                !thiz.getTag().getCompound("BlockEntityTag").contains("SpawnData")) return;

        CompoundTag spawnData = thiz.getTag().getCompound("BlockEntityTag").getCompound("SpawnData");
        cbi.setReturnValue("block.minecraft.spawner_" +
                Registry.ENTITY_TYPE.get(thiz.getTag() != null && thiz.getTag().contains("BlockEntityTag", 10) &&
                        thiz.getTag().getCompound("BlockEntityTag").contains("SpawnData", 10) ?
                        new ResourceLocation(spawnData.contains("entity", 10) ? spawnData.getCompound("entity").getString("id") : spawnData.getString("id")) :
                        Registry.ENTITY_TYPE.getDefaultKey()).getDescriptionId());
    }

    @Inject(at = @At("TAIL"), method = "getHoverName", cancellable = true)
    public void getName(CallbackInfoReturnable<Component> cbi) {
        ItemStack thiz = ReflectionHelper.cast(this);
        CompoundTag compoundTag = thiz.getTagElement("display");
        if ((compoundTag == null || !compoundTag.contains("Name", 8)) && thiz.getItem() == Items.SPAWNER)
            cbi.setReturnValue(TranslatableTextBuilder.builder(thiz.getDescriptionId(), Style.EMPTY.applyFormat(ChatFormatting.YELLOW)).build());
    }

    @Inject(at = @At("RETURN"), method = "hasFoil", cancellable = true)
    public void hasGlint(CallbackInfoReturnable<Boolean> cbi) {
        cbi.setReturnValue(ClientOptions.Rendering.powertoolsGlint.getValue() && PowerToolCommand.isPowerTool(ReflectionHelper.cast(this)) || cbi.getReturnValue());
    }
}
