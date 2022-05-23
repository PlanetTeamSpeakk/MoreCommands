package com.ptsmods.morecommands.commands.server.unelevated;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.callbacks.MouseEvent;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.extensions.ObjectExtensions;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.gui.PowerToolSelectionHud;
import com.ptsmods.morecommands.miscellaneous.Command;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@ExtensionMethod(ObjectExtensions.class)
public class PowerToolCommand extends Command {
    private static final SimpleCommandExceptionType TOO_MANY_COMMANDS = new SimpleCommandExceptionType(LiteralTextBuilder.literal("A powertool may only have at most 127 entries."));
    private static final KeyBinding cycleKeyBinding = new KeyBinding("key.morecommands.powerToolCycle", GLFW.GLFW_KEY_G, DF + "MoreCommands");

    @Override
    public void preinit(boolean serverOnly) {
        if (Platform.getEnv() == EnvType.CLIENT) MouseEvent.EVENT.register((button, action, mods) -> checkPowerToolClient(button, action));
        else if (serverOnly) {
            InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> checkPowerToolServer(player));
            InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> checkPowerToolServer(player).isTrue() ?
                    CompoundEventResult.interruptTrue(player.getStackInHand(hand)) : CompoundEventResult.pass());
        }

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, new Identifier("morecommands:powertool_cycle"), (buf, context) ->
                doCycleCommand(context.getPlayer(), Hand.values()[buf.readByte()], buf.readByte()));
        KeyMappingRegistry.register(cycleKeyBinding);

        AtomicBoolean pressed = new AtomicBoolean();
        ClientTickEvent.CLIENT_LEVEL_PRE.register(world -> {
            if (cycleKeyBinding.wasPressed()) {
                //noinspection StatementWithEmptyBody
                while (cycleKeyBinding.wasPressed()); // Clearing pressed counter
                if (!pressed.get()) {
                    cycleCommand();
                    pressed.set(true);
                }
            } else pressed.set(false);
        });

        ClientGuiEvent.RENDER_HUD.register(PowerToolSelectionHud::render);
    }

    @Environment(EnvType.CLIENT)
    private boolean checkPowerToolClient(int button, int action) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && action == 1 && MinecraftClient.getInstance().currentScreen == null) {
            String cmd = getCurrentPowerTool(player, button);
            if (cmd != null) {
                ClientCompat.get().sendMessageOrCommand("/" + cmd);
                player.swingHand(getPowerToolHand(player));
                return true;
            }
        }
        return false;
    }

    @Environment(EnvType.SERVER)
    private EventResult checkPowerToolServer(PlayerEntity player) {
        if (player != null) {
            String cmd = getCurrentPowerTool(player, -1);
            if (cmd != null) {
                Objects.requireNonNull(player.getServer()).getCommandManager().execute(player.getCommandSource(), cmd);
                return EventResult.interruptTrue();
            }
        }
        return EventResult.pass();
    }

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("pt", dispatcher.register(literalReq("powertool")
                .then(literal("reset")
                        .executes(ctx -> {
                            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            ItemStack stack = getPowerToolStack(player);
                            if (stack == null) sendError(ctx, "The item you're holding is not a powertool.");
                            else {
                                resetPowerTool(stack);
                                sendMsg(ctx, "The item you're holding is no longer a powertool.");
                                return 1;
                            }
                            return 0;
                        }))
                .then(literal("add")
                    .then(argument("cmd", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                                ItemStack stack = player.getMainHandStack();
                                int res = addPowerTool(stack, ctx.getArgument("cmd", String.class));
                                sendMsg(ctx, "The command has been added.");
                                return res;
                            })))
                .then(literal("remove")
                        .then(argument("index", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    int index = ctx.getArgument("index", int.class) - 1;
                                    ItemStack stack = getPowerToolStack(ctx.getSource().getPlayerOrThrow());

                                    if (stack == null) sendError(ctx, "You're not holding a powertool.");
                                    else if (getCommands(stack).size() <= index) sendError(ctx, "");
                                    else {
                                        String command = removePowerTool(stack, index);
                                        sendMsg(ctx, "Command %s%s %shas been removed.", SF, command, DF);
                                        return getCommands(stack).size();
                                    }

                                    return 0;
                                })))
                .then(literal("list")
                        .executes(ctx -> {
                            ItemStack stack = getPowerToolStack(ctx.getSource().getPlayerOrThrow());

                            if (stack == null) {
                                sendError(ctx, "You're not holding a powertool.");
                                return 0;
                            }

                            NbtCompound powertool = Objects.requireNonNull(stack.getNbt()).getCompound("PowerTool");
                            NbtList commands = powertool.getList("Commands", NbtElement.STRING_TYPE);
                            int selected = powertool.getByte("Selected");

                            sendMsg(ctx, "Powertool commands:");
                            for (int i = 0; i < commands.size(); i++) sendMsg(ctx, "  %s%d. /%s", i == selected ? SF : DF, i + 1, commands.get(i));

                            return commands.size();
                        })))));
    }

    public static String getCurrentPowerTool(PlayerEntity player, int button) {
        ItemStack stack = getPowerToolStack(player);
        reformPowerTool(stack);
        if (!isPowerTool(stack)) return null;

        NbtCompound powertool = Objects.requireNonNull(stack.getNbt()).getCompound("PowerTool");
        int pow = (int) Math.pow(2, button);

        return button == -1 || (powertool.getByte("Buttons") & pow) == pow ? powertool.getList("Commands", NbtElement.STRING_TYPE).getString(powertool.getByte("Selected")) : null;
    }

    public static int addPowerTool(ItemStack stack, @NonNull String command) throws CommandSyntaxException {
        reformPowerTool(stack);

        NbtCompound tag = stack.getOrCreateNbt();
        if (command.startsWith("/")) command = command.substring(1);
        if (!tag.contains("PowerTool", NbtElement.COMPOUND_TYPE)) tag.put("PowerTool", new NbtCompound());

        NbtList commands = tag.getCompound("PowerTool").getList("Commands", NbtElement.STRING_TYPE);
        if (commands.size() == Byte.MAX_VALUE) throw TOO_MANY_COMMANDS.create();

        commands.add(NbtString.of(command));
        return commands.size();

    }

    public static String removePowerTool(ItemStack stack, int index) {
        reformPowerTool(stack);

        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains("PowerTool", NbtElement.COMPOUND_TYPE) || getCommands(stack).size() <= index) return null;

        NbtCompound powertool = tag.getCompound("PowerTool");
        NbtList commands = powertool.getList("Commands", NbtElement.STRING_TYPE);
        NbtString s = (NbtString) commands.remove(index);
        powertool.putByte("Selected", (byte) Math.max(powertool.getByte("Selected"), commands.size() - 1));

        return s.asString();
    }

    public static void resetPowerTool(ItemStack stack) {
        reformPowerTool(stack);

        stack.getNbt().ifNonNullV(nbt -> nbt.remove("PowerTool"));
    }

    public static List<String> getCommands(ItemStack stack) {
        if (!isPowerTool(stack)) return Collections.emptyList();

        return Objects.requireNonNull(stack.getNbt()).getCompound("PowerTool").getList("Commands", NbtElement.STRING_TYPE).stream()
                .map(NbtElement::asString)
                .collect(Collectors.toList());
    }

    public static Hand getPowerToolHand(PlayerEntity player) {
        return isPowerTool(player.getMainHandStack()) ? Hand.MAIN_HAND : isPowerTool(player.getOffHandStack()) ? Hand.OFF_HAND : null;
    }

    public static ItemStack getPowerToolStack(PlayerEntity player) {
        Hand hand = getPowerToolHand(player);
        ItemStack stack = hand == null ? null : player.getStackInHand(hand);

        reformPowerTool(stack);
        return stack;
    }

    @Environment(EnvType.CLIENT)
    public static void cycleCommand() {
        PlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
        Hand hand = isPowerTool(player.getMainHandStack()) ? Hand.MAIN_HAND : isPowerTool(player.getOffHandStack()) ? Hand.OFF_HAND : null;

        if (hand == null || !NetworkManager.canServerReceive(new Identifier("morecommands:powertool_cycle"))) return;

        int index = doCycleCommand(player, hand, -1);
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeByte(hand.ordinal());
        buf.writeByte(index);

        NetworkManager.sendToServer(new Identifier("morecommands:powertool_cycle"), buf);
        PowerToolSelectionHud.currentSelection = new Pair<>(System.currentTimeMillis(), new Pair<>(index + 1, Objects.requireNonNull(player.getStackInHand(hand).getNbt())
                .getCompound("PowerTool").getList("Commands", NbtElement.STRING_TYPE).getString(index)));
    }

    public static int doCycleCommand(PlayerEntity player, Hand hand, int index) {
        ItemStack stack = player.getStackInHand(hand);
        reformPowerTool(stack);

        NbtCompound powertool = Objects.requireNonNull(stack.getNbt()).getCompound("PowerTool");
        if (index < 0) index = (powertool.getByte("Selected") + 1) % powertool.getList("Commands", NbtElement.STRING_TYPE).size();
        powertool.putByte("Selected", (byte) index);

        return index;
    }

    private static void reformPowerTool(ItemStack stack) {
        if (!isPowerTool(stack)) return;

        NbtCompound powertool = Objects.requireNonNull(stack.getNbt()).getCompound("PowerTool");
        if (powertool.contains("Command", NbtElement.STRING_TYPE)) {
            String command = powertool.getString("Command");

            NbtList commands = new NbtList();
            commands.add(NbtString.of(command));
            powertool.put("Commands", commands);

            powertool.putByte("Selected", (byte) 0);
            powertool.putByte("Buttons", (byte) (0x4 | 0x2 | 0x1));

            powertool.remove("Owner");
            powertool.remove("Command");
        }
    }

    public static boolean isPowerTool(ItemStack stack) {
        return stack != null && stack.hasNbt() && Objects.requireNonNull(stack.getNbt()).contains("PowerTool", NbtElement.COMPOUND_TYPE);
    }
}
