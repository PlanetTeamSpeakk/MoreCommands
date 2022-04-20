package com.ptsmods.morecommands.commands.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ptsmods.morecommands.clientoption.BooleanClientOption;
import com.ptsmods.morecommands.clientoption.ClientOption;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.client.network.ClientCommandSource;

public class ToggleOptionCommand extends ClientCommand {
	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		LiteralArgumentBuilder<ClientCommandSource> toggleoption = cLiteral("toggleoption");

		ClientOption.getUnmappedOptions().entrySet().stream()
				.filter(entry -> entry.getValue() instanceof BooleanClientOption)
				.forEach(entry -> toggleoption.then(cLiteral(entry.getKey()).executes(ctx -> {
					BooleanClientOption option = (BooleanClientOption) entry.getValue();
					boolean b = !option.getValueRaw();
					option.setValue(b);
					sendMsg("Option %s has been set to %s.", coloured(entry.getKey()), formatFromBool(b, "TRUE", "FALSE") + DF);
					return 1;
				})));

		dispatcher.register(toggleoption);
	}
}
