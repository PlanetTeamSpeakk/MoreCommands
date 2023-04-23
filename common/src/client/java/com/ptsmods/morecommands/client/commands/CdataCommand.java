package com.ptsmods.morecommands.client.commands;

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
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

// An unbelievable amount of code was copied from DataCommand and associated classes.
// Please don't sue me.
public class CdataCommand extends ClientCommand {
    private static final SimpleCommandExceptionType GET_MULTIPLE_EXCEPTION = new SimpleCommandExceptionType(translatableText("commands.data.get.multiple").build());
    private static final DynamicCommandExceptionType GET_UNKNOWN_EXCEPTION = new DynamicCommandExceptionType((object) -> translatableText("commands.data.get.unknown", object).build());
    private static final DynamicCommandExceptionType GET_INVALID_EXCEPTION = new DynamicCommandExceptionType((object) -> translatableText("commands.data.get.invalid", object).build());
    private static final List<Function<String, ClientObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(ClientEntityDataObject.TYPE_FACTORY, ClientBlockDataObject.TYPE_FACTORY, ClientItemDataObject.TYPE_FACTORY);
    private static final List<ClientObjectType> TARGET_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map((function) -> function.apply("target")).collect(ImmutableList.toImmutableList());

    @Override
    public void cRegister(CommandDispatcher<ClientSuggestionProvider> dispatcher) {
        LiteralArgumentBuilder<ClientSuggestionProvider> lab = cLiteral("cdata");
        for (ClientObjectType target : TARGET_OBJECT_TYPES)
            target.addArgumentsToBuilder(lab, (argumentBuilder) -> argumentBuilder
                    .executes(ctx -> executeGet(target.getObject(ctx)))
                    .then(cArgument("path", NbtPathArgument.nbtPath())
                            .executes(ctx -> executeGet(target.getObject(ctx), ctx.getArgument("path", NbtPathArgument.NbtPath.class)))
                            .then(cArgument("scale", DoubleArgumentType.doubleArg())
                                    .executes(ctx -> executeGet(target.getObject(ctx), ctx.getArgument("path", NbtPathArgument.NbtPath.class), DoubleArgumentType.getDouble(ctx, "scale"))))));
        dispatcher.register(lab);
    }

    @Override
    public String getDocsPath() {
        return "/c-data";
    }

    private static Tag getTag(NbtPathArgument.NbtPath path, ClientDataCommandObject object) throws CommandSyntaxException {
        Collection<Tag> collection = path.get(object.getTag());
        Iterator<Tag> iterator = collection.iterator();
        Tag tag = iterator.next();
        if (iterator.hasNext()) {
            throw GET_MULTIPLE_EXCEPTION.create();
        } else {
            return tag;
        }
    }

    private static int executeGet(ClientDataCommandObject object, NbtPathArgument.NbtPath path) throws CommandSyntaxException {
        Tag tag = getTag(path, object);
        int m;
        if (tag instanceof NumericTag) {
            m = Mth.floor(((NumericTag) tag).getAsDouble());
        } else if (tag instanceof CollectionTag) {
            m = ((CollectionTag<?>) tag).size();
        } else if (tag instanceof CompoundTag) {
            m = ((CompoundTag)tag).size();
        } else {
            if (!(tag instanceof StringTag)) {
                throw GET_UNKNOWN_EXCEPTION.create(path.toString());
            }

            m = tag.getAsString().length();
        }

        sendMsg(object.feedbackQuery(tag));
        return m;
    }

    private static int executeGet(ClientDataCommandObject object, NbtPathArgument.NbtPath path, double scale) throws CommandSyntaxException {
        Tag tag = getTag(path, object);
        if (!(tag instanceof NumericTag)) {
            throw GET_INVALID_EXCEPTION.create(path.toString());
        } else {
            int i = Mth.floor(((NumericTag)tag).getAsDouble() * scale);
            sendMsg(object.feedbackGet(path, scale, i));
            return i;
        }
    }

    private static int executeGet(ClientDataCommandObject object) throws CommandSyntaxException {
        sendMsg(object.feedbackQuery(object.getTag()));
        return 1;
    }

    public interface ClientObjectType {
        ClientDataCommandObject getObject(CommandContext<ClientSuggestionProvider> context) throws CommandSyntaxException;

        ArgumentBuilder<ClientSuggestionProvider, ?> addArgumentsToBuilder(ArgumentBuilder<ClientSuggestionProvider, ?> argument, Function<ArgumentBuilder<ClientSuggestionProvider, ?>, ArgumentBuilder<ClientSuggestionProvider, ?>> argumentAdder);
    }

    public interface ClientDataCommandObject {
        CompoundTag getTag() throws CommandSyntaxException;

        Component feedbackQuery(Tag tag);

        Component feedbackGet(NbtPathArgument.NbtPath nbtPath, double scale, int result);
    }

    public static class ClientEntityDataObject implements ClientDataCommandObject {
        private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(translatableText("commands.data.entity.invalid").build());
        public static final Function<String, ClientObjectType> TYPE_FACTORY = (string) -> new ClientObjectType() {
            public ClientDataCommandObject getObject(CommandContext<ClientSuggestionProvider> context) throws CommandSyntaxException {
                try {
                    return new ClientEntityDataObject(getEntity(context, string));
                } catch (CommandSyntaxException e) {
                    throw INVALID_ENTITY_EXCEPTION.create();
                }
            }

            public ArgumentBuilder<ClientSuggestionProvider, ?> addArgumentsToBuilder(ArgumentBuilder<ClientSuggestionProvider, ?> argument, Function<ArgumentBuilder<ClientSuggestionProvider, ?>, ArgumentBuilder<ClientSuggestionProvider, ?>> argumentAdder) {
                return argument.then(cLiteral("entity").then(argumentAdder.apply(cArgument(string, EntityArgument.entity()))));
            }
        };
        private final Entity entity;

        public ClientEntityDataObject(Entity entity) {
            this.entity = entity;
        }

        public CompoundTag getTag() {
            return NbtPredicate.getEntityTagToCompare(this.entity);
        }

        public Component feedbackQuery(Tag tag) {
            return translatableText("commands.data.entity.query", this.entity.getDisplayName(), NbtUtils.toPrettyComponent(tag)).build();
        }

        public Component feedbackGet(NbtPathArgument.NbtPath nbtPath, double scale, int result) {
            return translatableText("commands.data.entity.get", nbtPath, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), result).build();
        }
    }

    public static class ClientBlockDataObject implements ClientDataCommandObject {
        private static final SimpleCommandExceptionType INVALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType(translatableText("commands.data.block.invalid").build());
        public static final Function<String, ClientObjectType> TYPE_FACTORY = (string) -> new ClientObjectType() {
            public ClientDataCommandObject getObject(CommandContext<ClientSuggestionProvider> context) throws CommandSyntaxException {
                BlockPos blockPos = getLoadedBlockPos(context, string + "Pos");
                BlockEntity blockEntity = getWorld().getBlockEntity(blockPos);
                if (blockEntity == null) throw INVALID_BLOCK_EXCEPTION.create();
                else return new ClientBlockDataObject(blockEntity, blockPos);
            }

            public ArgumentBuilder<ClientSuggestionProvider, ?> addArgumentsToBuilder(ArgumentBuilder<ClientSuggestionProvider, ?> argument, Function<ArgumentBuilder<ClientSuggestionProvider, ?>, ArgumentBuilder<ClientSuggestionProvider, ?>> argumentAdder) {
                return argument.then(cLiteral("block").then(argumentAdder.apply(cArgument(string + "Pos", BlockPosArgument.blockPos()))));
            }
        };
        private final BlockEntity blockEntity;
        private final BlockPos pos;

        public ClientBlockDataObject(BlockEntity blockEntity, BlockPos pos) {
            this.blockEntity = blockEntity;
            this.pos = pos;
        }

        public CompoundTag getTag() {
            return Compat.get().writeBENBT(blockEntity);
        }

        public Component feedbackQuery(Tag tag) {
            return translatableText("commands.data.block.query", this.pos.getX(), this.pos.getY(), this.pos.getZ(), NbtUtils.toPrettyComponent(tag)).build();
        }

        public Component feedbackGet(NbtPathArgument.NbtPath nbtPath, double scale, int result) {
            return translatableText("commands.data.block.get", nbtPath, this.pos.getX(), this.pos.getY(), this.pos.getZ(), String.format(Locale.ROOT, "%.2f", scale), result).build();
        }
    }

    public static class ClientItemDataObject implements ClientDataCommandObject {
        private static final SimpleCommandExceptionType NO_ITEM_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("You're not holding an item."));
        private static final SimpleCommandExceptionType NO_DATA_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("The item you're holding has no data."));
        public static final Function<String, ClientObjectType> TYPE_FACTORY = (string) -> new ClientObjectType() {
            public ClientDataCommandObject getObject(CommandContext<ClientSuggestionProvider> context) throws CommandSyntaxException {
                ItemStack stack = getPlayer().getMainHandItem();
                if (stack.isEmpty()) throw NO_ITEM_EXCEPTION.create();
                else if (!stack.hasTag()) throw NO_DATA_EXCEPTION.create();
                else return new ClientItemDataObject(getPlayer().getMainHandItem());
            }

            public ArgumentBuilder<ClientSuggestionProvider, ?> addArgumentsToBuilder(ArgumentBuilder<ClientSuggestionProvider, ?> argument, Function<ArgumentBuilder<ClientSuggestionProvider, ?>, ArgumentBuilder<ClientSuggestionProvider, ?>> argumentAdder) {
                return argument.then(argumentAdder.apply(cLiteral("item")));
            }
        };
        private final ItemStack stack;

        public ClientItemDataObject(ItemStack stack) {
            this.stack = stack;
        }

        public CompoundTag getTag() {
            return this.stack.getTag();
        }

        public Component feedbackQuery(Tag tag) {
            return translatableText("commands.data.item.query", stack.getHoverName(), NbtUtils.toPrettyComponent(tag)).build();
        }

        public Component feedbackGet(NbtPathArgument.NbtPath nbtPath, double scale, int result) {
            return translatableText("commands.data.item.get", nbtPath, stack.getHoverName(), String.format(Locale.ROOT, "%.2f", scale), result).build();
        }
    }

}
