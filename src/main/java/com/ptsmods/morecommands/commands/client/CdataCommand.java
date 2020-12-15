package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;

public class CdataCommand extends ClientCommand {
    @Override
    public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
//        dispatcher.register(cLiteral("cdata").executes(ctx -> {
//            ItemStack holding = MinecraftClient.getInstance().player.getStackInHand(Hand.MAIN_HAND);
//            sendMsg("The data of the item stack you're holding is " + MoreCommands.textToString(holding.getOrCreateTag().toText(), SS));
//            return holding.getCount();
//        }));
        // TODO finish this
        //dispatcher.register(cLiteral("cdata").then(cLiteral("block").then(argument(""))));
    }
}
