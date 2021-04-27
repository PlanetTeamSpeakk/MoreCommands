package com.ptsmods.morecommands.commands.client;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.compat.Compat;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.*;
import java.util.function.Function;

// An unbelievable amount of code was copied from DataCommand and associated classes.
// Please don't sue me.
public class CdataCommand extends ClientCommand {

	private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.get.multiple"));
	private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((object) -> new TranslatableText("commands.data.get.unknown", object));
	private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((object) -> new TranslatableText("commands.data.get.invalid", object));
	private static final List<Function<String, ClientObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(ClientEntityDataObject.TYPE_FACTORY, ClientBlockDataObject.TYPE_FACTORY, ClientItemDataObject.TYPE_FACTORY);
	private static final List<ClientObjectType> TARGET_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map((function) -> function.apply("target")).collect(ImmutableList.toImmutableList());

	@Override
	public void cRegister(CommandDispatcher<ClientCommandSource> dispatcher) {
		LiteralArgumentBuilder<ClientCommandSource> lab = cLiteral("cdata");
		for (ClientObjectType target : TARGET_OBJECT_TYPES)
			target.addArgumentsToBuilder(lab, (argumentBuilder) -> argumentBuilder.executes(ctx -> executeGet(target.getObject(ctx))).then(cArgument("path", NbtPathArgumentType.nbtPath()).executes(ctx -> executeGet(target.getObject(ctx), ctx.getArgument("path", NbtPathArgumentType.NbtPath.class))).then(cArgument("scale", DoubleArgumentType.doubleArg()).executes(ctx -> executeGet(target.getObject(ctx), ctx.getArgument("path", NbtPathArgumentType.NbtPath.class), DoubleArgumentType.getDouble(ctx, "scale"))))));
		dispatcher.register(lab);
	}

	private static NbtElement getTag(NbtPathArgumentType.NbtPath path, ClientDataCommandObject object) throws CommandSyntaxException {
		Collection<NbtElement> collection = path.get(object.getTag());
		Iterator<NbtElement> iterator = collection.iterator();
		NbtElement tag = iterator.next();
		if (iterator.hasNext()) {
			throw GET_MULTIPLE_EXCEPTION.create();
		} else {
			return tag;
		}
	}

	private static int executeGet(ClientDataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
		NbtElement tag = getTag(path, object);
		int m;
		if (tag instanceof AbstractNbtNumber) {
			m = MathHelper.floor(((AbstractNbtNumber) tag).doubleValue());
		} else if (tag instanceof AbstractNbtList) {
			m = ((AbstractNbtList<?>) tag).size();
		} else if (tag instanceof NbtCompound) {
			m = ((NbtCompound)tag).getSize();
		} else {
			if (!(tag instanceof NbtString)) {
				throw GET_UNKNOWN_EXCEPTION.create(path.toString());
			}

			m = tag.asString().length();
		}

		sendMsg(object.feedbackQuery(tag));
		return m;
	}

	private static int executeGet(ClientDataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
		NbtElement tag = getTag(path, object);
		if (!(tag instanceof AbstractNbtNumber)) {
			throw GET_INVALID_EXCEPTION.create(path.toString());
		} else {
			int i = MathHelper.floor(((AbstractNbtNumber)tag).doubleValue() * scale);
			sendMsg(object.feedbackGet(path, scale, i));
			return i;
		}
	}

	private static int executeGet(ClientDataCommandObject object) throws CommandSyntaxException {
		sendMsg(object.feedbackQuery(object.getTag()));
		return 1;
	}

	public interface ClientObjectType {
		ClientDataCommandObject getObject(CommandContext<ClientCommandSource> context) throws CommandSyntaxException;

		ArgumentBuilder<ClientCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ClientCommandSource, ?> argument, Function<ArgumentBuilder<ClientCommandSource, ?>, ArgumentBuilder<ClientCommandSource, ?>> argumentAdder);
	}

	public interface ClientDataCommandObject {
		NbtCompound getTag() throws CommandSyntaxException;

		Text feedbackQuery(NbtElement tag);

		Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result);
	}

	public static class ClientEntityDataObject implements ClientDataCommandObject {
		private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.entity.invalid"));
		public static final Function<String, ClientObjectType> TYPE_FACTORY = (string) -> new ClientObjectType() {
			public ClientDataCommandObject getObject(CommandContext<ClientCommandSource> context) throws CommandSyntaxException {
				try {
					return new ClientEntityDataObject(getEntity(context, string));
				} catch (CommandSyntaxException e) {
					throw INVALID_ENTITY_EXCEPTION.create();
				}
			}

			public ArgumentBuilder<ClientCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ClientCommandSource, ?> argument, Function<ArgumentBuilder<ClientCommandSource, ?>, ArgumentBuilder<ClientCommandSource, ?>> argumentAdder) {
				return argument.then(cLiteral("entity").then(argumentAdder.apply(cArgument(string, EntityArgumentType.entity()))));
			}
		};
		private final Entity entity;

		public ClientEntityDataObject(Entity entity) {
			this.entity = entity;
		}

		public NbtCompound getTag() {
			return NbtPredicate.entityToNbt(this.entity);
		}

		public Text feedbackQuery(NbtElement tag) {
			return new TranslatableText("commands.data.entity.query", this.entity.getDisplayName(), Compat.getCompat().toText(tag));
		}

		public Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result) {
			return new TranslatableText("commands.data.entity.get", nbtPath, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), result);
		}
	}

	public static class ClientBlockDataObject implements ClientDataCommandObject {
		private static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.data.block.invalid"));
		public static final Function<String, ClientObjectType> TYPE_FACTORY = (string) -> new ClientObjectType() {
			public ClientDataCommandObject getObject(CommandContext<ClientCommandSource> context) throws CommandSyntaxException {
				BlockPos blockPos = getLoadedBlockPos(context, string + "Pos");
				BlockEntity blockEntity = getWorld().getBlockEntity(blockPos);
				if (blockEntity == null) throw INVALID_BLOCK_EXCEPTION.create();
				else return new ClientBlockDataObject(blockEntity, blockPos);
			}

			public ArgumentBuilder<ClientCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ClientCommandSource, ?> argument, Function<ArgumentBuilder<ClientCommandSource, ?>, ArgumentBuilder<ClientCommandSource, ?>> argumentAdder) {
				return argument.then(cLiteral("block").then(argumentAdder.apply(cArgument(string + "Pos", BlockPosArgumentType.blockPos()))));
			}
		};
		private final BlockEntity blockEntity;
		private final BlockPos pos;

		public ClientBlockDataObject(BlockEntity blockEntity, BlockPos pos) {
			this.blockEntity = blockEntity;
			this.pos = pos;
		}

		public NbtCompound getTag() {
			return this.blockEntity.writeNbt(new NbtCompound());
		}

		public Text feedbackQuery(NbtElement tag) {
			return new TranslatableText("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), Compat.getCompat().toText(tag));
		}

		public Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result) {
			return new TranslatableText("commands.data.block.get", nbtPath, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", scale), result);
		}
	}

	public static class ClientItemDataObject implements ClientDataCommandObject {
		private static final SimpleCommandExceptionType NO_ITEM_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("You're not holding an item."));
		private static final SimpleCommandExceptionType NO_DATA_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("The item you're holding has no data."));
		public static final Function<String, ClientObjectType> TYPE_FACTORY = (string) -> new ClientObjectType() {
			public ClientDataCommandObject getObject(CommandContext<ClientCommandSource> context) throws CommandSyntaxException {
				ItemStack stack = getPlayer().getMainHandStack();
				if (stack.isEmpty()) throw NO_ITEM_EXCEPTION.create();
				else if (!stack.hasTag()) throw NO_DATA_EXCEPTION.create();
				else return new ClientItemDataObject(getPlayer().getMainHandStack());
			}

			public ArgumentBuilder<ClientCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ClientCommandSource, ?> argument, Function<ArgumentBuilder<ClientCommandSource, ?>, ArgumentBuilder<ClientCommandSource, ?>> argumentAdder) {
				return argument.then(argumentAdder.apply(cLiteral("item")));
			}
		};
		private final ItemStack stack;

		public ClientItemDataObject(ItemStack stack) {
			this.stack = stack;
		}

		public NbtCompound getTag() {
			return this.stack.getTag();
		}

		public Text feedbackQuery(NbtElement tag) {
			return new TranslatableText("commands.data.item.query", stack.getName(), Compat.getCompat().toText(tag));
		}

		public Text feedbackGet(NbtPathArgumentType.NbtPath nbtPath, double scale, int result) {
			return new TranslatableText("commands.data.item.get", nbtPath, stack.getName(), String.format(Locale.ROOT, "%.2f", scale), result);
		}
	}

}
