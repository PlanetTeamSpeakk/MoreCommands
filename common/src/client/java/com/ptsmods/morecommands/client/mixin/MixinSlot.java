package com.ptsmods.morecommands.client.mixin;

import com.ptsmods.morecommands.api.addons.SlotAddon;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.client.commands.SearchItemCommand;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Mixin(Slot.class)
public abstract class MixinSlot implements SlotAddon {
    @Shadow public abstract ItemStack getItem();

    private @Unique boolean matchesCurrentSearchItemPredicate;
    private @Unique Predicate<ItemStack> lastCheckedPredicate;
    private @Unique ItemStack lastCheckedStack = ItemStack.EMPTY;

    @Override
    public boolean mc$matchesCurrentSearchItemPredicate() {
        if (SearchItemCommand.getCurrentPredicate() == null) return true;
        if (lastCheckedPredicate != SearchItemCommand.getCurrentPredicate() || !Util.stackEquals(getItem(), lastCheckedStack)) {
            lastCheckedPredicate = SearchItemCommand.getCurrentPredicate();
            lastCheckedStack = getItem().copy();
            matchesCurrentSearchItemPredicate = lastCheckedPredicate != null && lastCheckedPredicate.test(getItem());
        }

        return matchesCurrentSearchItemPredicate;
    }
}
