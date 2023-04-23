package com.ptsmods.morecommands.commands.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.util.Util;
import com.ptsmods.morecommands.miscellaneous.Command;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class UnlimitedCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReqOp("unlimited")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getEntityOrException().getHandSlots().iterator().next();
                    if (stack != null && stack.getItem() != Items.AIR) {
                        CompoundTag tag = stack.getOrCreateTag();
                        if (tag.contains("Unlimited")) tag.remove("Unlimited");
                        else tag.putByte("Unlimited", (byte) 1);
                        stack.setCount(1);
                        sendMsg(ctx, "Your itemstack will now " + Util.formatFromBool(tag.contains("Unlimited"), "never run out", "run out again") + DF + ".");
                        return tag.contains("Unlimited") ? 2 : 1;
                    }
                    sendMsg(ctx, "You must be holding an item.");
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/unlimited";
    }

    public static boolean isUnlimited(ItemStack stack) {
        return stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("Unlimited");
    }
}
