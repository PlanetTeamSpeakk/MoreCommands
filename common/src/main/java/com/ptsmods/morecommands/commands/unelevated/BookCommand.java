package com.ptsmods.morecommands.commands.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BookCommand extends Command {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literalReq("book")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getPlayerOrException().getMainHandItem();
                    CompoundTag tag = stack.getTag();
                    if (stack.getItem() == Items.WRITTEN_BOOK) {
                        stack = new ItemStack(Items.WRITABLE_BOOK, stack.getCount());
                        if (tag != null && tag.contains("pages")) {
                            ListTag pages = tag.getList("pages", 8);
                            ListTag pages0 = new ListTag();
                            for (Tag page : pages)
                                pages0.add(StringTag.valueOf(IMoreCommands.get().textToString(Component.Serializer.fromJson(page.getAsString()), null, true).replaceAll("\u00A7", "&")));
                            tag.put("pages", pages0);
                        }
                        stack.setTag(tag);
                        ctx.getSource().getPlayerOrException().setItemInHand(InteractionHand.MAIN_HAND, stack);
                        return 1;
                    } else sendError(ctx, "You're not holding a written book.");
                    return 0;
                }));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/book";
    }
}
