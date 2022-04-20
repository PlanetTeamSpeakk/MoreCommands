package com.ptsmods.morecommands;

import com.chocohead.mm.api.ClassTinkerers;
import com.ptsmods.morecommands.asm.CopySoundDump;
import com.ptsmods.morecommands.asm.EESoundDump;
import com.ptsmods.morecommands.lib.com.chocohead.mm.EnumExtender;
import com.ptsmods.morecommands.lib.com.chocohead.mm.api.EnumAdder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EarlyRiser implements Runnable {

	@Override
	public void run() {
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

		Logger log = LogManager.getLogger("MoreCommands-EarlyRiser");
		String formattingClass = remapper.mapClassName("intermediary", "net.minecraft.class_124");
		String clickEventActionClass = remapper.mapClassName("intermediary", "net.minecraft.class_2558$class_2559");

		// Still using MM's ClassTinkerers class to do the actual tinkering, just using our own copy of their
		// EnumExtender to prevent clashing with mods that still ship an old version of MM that cannot add enums
		// on Java 15+
		ClassTinkerers.addTransformation(formattingClass, makeEnumExtender(log, new EnumAdder(formattingClass, String.class, char.class, boolean.class)
				.addEnum("RAINBOW", "RAINBOW", 'u', true)));
		ClassTinkerers.addTransformation(clickEventActionClass, makeEnumExtender(log, new EnumAdder(clickEventActionClass, String.class, boolean.class)
				.addEnum("SCROLL", "scroll", false)));

		log.info("[MoreCommands] Queued RAINBOW Formatting and SCROLL ClickEvent$Action for registration.");

		String prefix = "com.ptsmods.morecommands";

		ClassTinkerers.define(prefix + ".miscellaneous.CopySound", CopySoundDump.dump());
		ClassTinkerers.define(prefix + ".miscellaneous.EESound", EESoundDump.dump());

		log.info("[MoreCommands] Defined CopySound and EESound using dumps.");
	}

	private Consumer<ClassNode> makeEnumExtender(Logger log, EnumAdder adder) {
		Consumer<ClassNode> extender = EnumExtender.makeEnumExtender(adder);
		return node -> {
			extender.accept(node);

			log.printf(Level.INFO, "[MoreCommands] Registered value%s %s on enum %s.", adder.getAdditions().size() == 1 ? "" : "s",
					adder.getAdditions().stream()
							.map(addition -> addition.name)
							.collect(Collectors.joining(", ")),
					adder.type);
		};
	}
}
