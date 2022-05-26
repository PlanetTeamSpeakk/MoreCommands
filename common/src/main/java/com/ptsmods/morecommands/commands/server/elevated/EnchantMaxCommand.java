package com.ptsmods.morecommands.commands.server.elevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.Registry;

public class EnchantMaxCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) throws Exception {
        dispatcher.register(literalReqOp("enchantmax")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getEntityOrThrow().getItemsHand().iterator().next();
                    if (stack.isEmpty()) sendError(ctx, "You're not holding an item.");
                    else {
                        stack.getEnchantments().clear();
                        Registry.ENCHANTMENT.stream().filter(enchantment -> !enchantment.isCursed()).forEach(enchantment -> stack.addEnchantment(enchantment, Short.MAX_VALUE));
                        sendMsg(ctx, "Your item has been enchanted.");
                        return stack.getEnchantments().size();
                    }
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/server/elevated/enchant-max";
    }
}
