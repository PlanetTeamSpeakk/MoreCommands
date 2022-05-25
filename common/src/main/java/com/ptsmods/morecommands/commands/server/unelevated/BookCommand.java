package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.miscellaneous.Command;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class BookCommand extends Command {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literalReq("book")
                .executes(ctx -> {
                    ItemStack stack = ctx.getSource().getPlayerOrThrow().getMainHandStack();
                    NbtCompound tag = stack.getNbt();
                    if (stack.getItem() == Items.WRITTEN_BOOK) {
                        stack = new ItemStack(Items.WRITABLE_BOOK, stack.getCount());
                        if (tag != null && tag.contains("pages")) {
                            NbtList pages = tag.getList("pages", 8);
                            NbtList pages0 = new NbtList();
                            for (NbtElement page : pages)
                                pages0.add(NbtString.of(IMoreCommands.get().textToString(Text.Serializer.fromJson(page.asString()), null, true).replaceAll("\u00A7", "&")));
                            tag.put("pages", pages0);
                        }
                        stack.setNbt(tag);
                        ctx.getSource().getPlayerOrThrow().setStackInHand(Hand.MAIN_HAND, stack);
                        return 1;
                    } else sendError(ctx, "You're not holding a written book.");
                    return 0;
                }));
    }
}
