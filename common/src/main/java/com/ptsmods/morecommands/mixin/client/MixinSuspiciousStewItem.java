package com.ptsmods.morecommands.mixin.client;

import com.ptsmods.morecommands.MoreCommandsClient;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(SuspiciousStewItem.class)
public class MixinSuspiciousStewItem extends Item {
    public MixinSuspiciousStewItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!ClientOptions.Tweaks.suspiciousTooltips.getValue()) return;

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(SuspiciousStewItem.EFFECTS_KEY, NbtElement.LIST_TYPE)) return;

        NbtList effects = nbt.getList(SuspiciousStewItem.EFFECTS_KEY, NbtElement.COMPOUND_TYPE);
        ItemStack potionStack = new ItemStack(Items.POTION);

        PotionUtil.setPotion(potionStack, Potions.WATER);
        PotionUtil.setCustomPotionEffects(potionStack, effects.stream()
                .map(e -> (NbtCompound) e)
                .map(nbt0 -> {
                    StatusEffectInstance effect = new StatusEffectInstance(StatusEffect.byRawId(nbt0.getInt(SuspiciousStewItem.EFFECT_ID_KEY)),
                            nbt0.contains(SuspiciousStewItem.EFFECT_DURATION_KEY, NbtElement.INT_TYPE) ? nbt0.getInt(SuspiciousStewItem.EFFECT_DURATION_KEY) : 160);
                    MoreCommandsClient.LOG.info(effect);
                    return effect;
                })
                .collect(Collectors.toList()));
        PotionUtil.buildTooltip(potionStack, tooltip, 1.0F);
    }
}
