package com.ptsmods.morecommands.commands.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.arguments.LimitedStringArgumentType;
import com.ptsmods.morecommands.clientoption.BooleanClientOption;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import net.minecraft.client.network.ClientCommandSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ToggleOptionCommand extends ClientCommand {
	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		@SuppressWarnings("UnstableApiUsage")
		Map<String, BooleanClientOption> options = ClientOptions.getMappedOptions().entrySet().stream().filter(entry -> entry.getValue() instanceof BooleanClientOption).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> (BooleanClientOption) entry.getValue()));
		dispatcher.register(cLiteral("toggleoption").then(cArgument("option", LimitedStringArgumentType.word(options.keySet())).executes(ctx -> {
			String option = ctx.getArgument("option", String.class);
			boolean b = !options.get(option).getValueRaw();
			options.get(option).setValue(b);
			sendMsg("The option " + SF + option + DF + " has been set to " + formatFromBool(b, "TRUE", "FALSE") + DF + ".");
			return 1;
		})));
	}
}
