package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(SuspiciousStewItem.class)
public class MixinSuspiciousStewItem extends Item {
    public MixinSuspiciousStewItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag context) {
        if (!ClientOptions.Tweaks.suspiciousTooltips.getValue()) return;

        CompoundTag nbt = stack.getTag();
        if (nbt == null || !nbt.contains(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_LIST)) return;

        ListTag effects = nbt.getList(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_COMPOUND);
        ItemStack potionStack = new ItemStack(Items.POTION);

        PotionUtils.setPotion(potionStack, Potions.WATER);
        PotionUtils.setCustomEffects(potionStack, effects.stream()
                .map(e -> (CompoundTag) e)
                .filter( nbt0 -> MobEffect.byId(nbt0.getInt(SuspiciousStewItem.EFFECT_ID_TAG)) != null)
                .map(nbt0 -> new MobEffectInstance(Objects.requireNonNull(MobEffect.byId(nbt0.getInt(SuspiciousStewItem.EFFECT_ID_TAG))),
                        nbt0.contains(SuspiciousStewItem.EFFECT_DURATION_TAG, Tag.TAG_INT) ? nbt0.getInt(SuspiciousStewItem.EFFECT_DURATION_TAG) : 160))
                .collect(Collectors.toList()));
        PotionUtils.addPotionTooltip(potionStack, tooltip, 1.0F);
    }
}
