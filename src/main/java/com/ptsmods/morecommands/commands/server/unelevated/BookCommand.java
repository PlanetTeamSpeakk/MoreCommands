package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

public class BookCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("book").executes(ctx -> {
            ItemStack stack = ctx.getSource().getPlayer().getMainHandStack();
            CompoundTag tag = stack.getTag();
            if (stack.getItem() == Items.WRITTEN_BOOK) {
                stack = new ItemStack(Items.WRITABLE_BOOK, stack.getCount());
                if (tag != null && tag.contains("pages")) {
                    ListTag pages = tag.getList("pages", 8);
                    ListTag pages0 = new ListTag();
                    for (Tag page : pages)
                        pages0.add(StringTag.of(MoreCommands.textToString(Text.Serializer.fromJson(((StringTag) page).asString()), null).replaceAll("\u00A7", "&")));
                    tag.put("pages", pages0);
                }
                stack.setTag(tag);
                ctx.getSource().getPlayer().setStackInHand(Hand.MAIN_HAND, stack);
                return 1;
            } else sendMsg(ctx, Formatting.RED + "You're not holding a written book.");
            return 0;
        }));
    }
}
