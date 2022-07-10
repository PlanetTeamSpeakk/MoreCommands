package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;

public class EnchantMaxCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("enchantmax")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getEntityOrException().getHandSlots().iterator().next();
                    if (stack.isEmpty()) sendError(ctx, "You're not holding an item.");
                    else {
                        stack.getEnchantmentTags().clear();
                        Registry.ENCHANTMENT.stream().filter(enchantment -> !enchantment.isCurse()).forEach(enchantment -> stack.enchant(enchantment, Short.MAX_VALUE));
                        sendMsg(ctx, "Your item has been enchanted.");
                        return stack.getEnchantmentTags().size();
                    }
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/elevated/enchant-max";
    }
}
