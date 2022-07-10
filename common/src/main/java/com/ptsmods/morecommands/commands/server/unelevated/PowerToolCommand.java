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
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.gui.PowerToolSelectionHud;
import com.ptsmods.morecommands.miscellaneous.ClientCommand;
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
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ExtensionMethod(ObjectExtensions.class)
public class PowerToolCommand extends Command {
    private static final SimpleCommandExceptionType TOO_MANY_COMMANDS = new SimpleCommandExceptionType(LiteralTextBuilder.literal("A powertool may only have at most 127 entries."));
    private static final KeyMapping cycleKeyBinding = new KeyMapping("key.morecommands.powerToolCycle", GLFW.GLFW_KEY_G, DF + "MoreCommands");

    @Override
    public void preinit(boolean serverOnly) {
        if (Platform.getEnv() == EnvType.CLIENT) MouseEvent.EVENT.register((button, action, mods) -> checkPowerToolClient(button, action));
        else if (serverOnly) {
            InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> checkPowerToolServer(player));
            InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> checkPowerToolServer(player).isTrue() ?
                    CompoundEventResult.interruptTrue(player.getItemInHand(hand)) : CompoundEventResult.pass());
        }

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, new ResourceLocation("morecommands:powertool_cycle"), (buf, context) ->
                doCycleCommand(context.getPlayer(), InteractionHand.values()[buf.readByte()], buf.readByte()));
        KeyMappingRegistry.register(cycleKeyBinding);

        AtomicBoolean pressed = new AtomicBoolean();
        AtomicInteger lastSelected = new AtomicInteger();
        ClientTickEvent.CLIENT_LEVEL_PRE.register(world -> {
            if (cycleKeyBinding.consumeClick()) {
                //noinspection StatementWithEmptyBody
                while (cycleKeyBinding.consumeClick()); // Clearing pressed counter
                if (!pressed.get()) {
                    cycleCommand();
                    pressed.set(true);
                }
            } else pressed.set(false);

            Inventory inventory = Objects.requireNonNull(Minecraft.getInstance().player).getInventory();
            if (inventory.selected != lastSelected.get()) {
                ItemStack stack = inventory.getItem(inventory.selected);
                if (isPowerTool(stack)) displaySelection(stack);
                lastSelected.set(inventory.selected);
            }
        });

        ClientGuiEvent.RENDER_HUD.register(PowerToolSelectionHud::render);
    }

    @Environment(EnvType.CLIENT)
    private boolean checkPowerToolClient(int button, int action) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && action == 1 && Minecraft.getInstance().screen == null) {
            String cmd = getCurrentPowerTool(player, button);
            if (cmd != null) {
                ClientCompat.get().sendMessageOrCommand("/" + cmd);
                player.swing(getPowerToolHand(player));
                return true;
            }
        }
        return false;
    }

    @Environment(EnvType.SERVER)
    private EventResult checkPowerToolServer(Player player) {
        if (player != null) {
            String cmd = getCurrentPowerTool(player, -1);
            if (cmd != null) {
                Objects.requireNonNull(player.getServer()).getCommands().performCommand(player.createCommandSourceStack(), cmd);
                return EventResult.interruptTrue();
            }
        }
        return EventResult.pass();
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.getRoot().addChild(MoreCommands.createAlias("pt", dispatcher.register(literalReq("powertool")
                .then(literal("reset")
                        .executes(ctx -> {
                            Player player = ctx.getSource().getPlayerOrException();
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
                                Player player = ctx.getSource().getPlayerOrException();
                                ItemStack stack = player.getMainHandItem();
                                int res = addPowerTool(stack, ctx.getArgument("cmd", String.class));
                                sendMsg(ctx, "The command has been added.");
                                return res;
                            })))
                .then(literal("remove")
                        .then(argument("index", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    int index = ctx.getArgument("index", int.class) - 1;
                                    ItemStack stack = getPowerToolStack(ctx.getSource().getPlayerOrException());

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
                            ItemStack stack = getPowerToolStack(ctx.getSource().getPlayerOrException());

                            if (stack == null) {
                                sendError(ctx, "You're not holding a powertool.");
                                return 0;
                            }

                            CompoundTag powertool = Objects.requireNonNull(stack.getTagElement("PowerTool"));
                            ListTag commands = powertool.getList("Commands", Tag.TAG_STRING);
                            int selected = powertool.getByte("Selected");

                            sendMsg(ctx, "Powertool commands:");
                            for (int i = 0; i < commands.size(); i++) sendMsg(ctx, "  %s%d. /%s", i == selected ? SF : DF, i + 1, commands.get(i));

                            return commands.size();
                        })))));
    }

    @Override
    public String getDocsPath() {
        return "/unelevated/power-tool";
    }

    public static String getCurrentPowerTool(Player player, int button) {
        ItemStack stack = getPowerToolStack(player);
        reformPowerTool(stack);
        if (!isPowerTool(stack)) return null;

        CompoundTag powertool = Objects.requireNonNull(stack.getTagElement("PowerTool"));
        int pow = (int) Math.pow(2, button);

        return button == -1 || (powertool.getByte("Buttons") & pow) == pow ? powertool.getList("Commands", Tag.TAG_STRING).getString(powertool.getByte("Selected")) : null;
    }

    public static int addPowerTool(ItemStack stack, @NonNull String command) throws CommandSyntaxException {
        reformPowerTool(stack);

        CompoundTag tag = stack.getOrCreateTag();
        if (command.startsWith("/")) command = command.substring(1);
        if (!tag.contains("PowerTool", Tag.TAG_COMPOUND)) tag.put("PowerTool", Util.make(new CompoundTag(), nbt -> {
            nbt.put("Commands", new ListTag());
            nbt.putByte("Selected", (byte) 0);
            nbt.putByte("Buttons", (byte) (0x4 | 0x2 | 0x1));
        }));

        ListTag commands = tag.getCompound("PowerTool").getList("Commands", Tag.TAG_STRING);
        if (commands.size() == Byte.MAX_VALUE) throw TOO_MANY_COMMANDS.create();

        commands.add(StringTag.valueOf(command));
        return commands.size();

    }

    public static String removePowerTool(ItemStack stack, int index) {
        reformPowerTool(stack);

        CompoundTag powertool = stack.getTagElement("PowerTool");
        if (powertool == null || getCommands(stack).size() <= index) return null;

        ListTag commands = powertool.getList("Commands", Tag.TAG_STRING);
        StringTag s = (StringTag) commands.remove(index);

        if (commands.size() == 0) {
            resetPowerTool(stack);
            return s.getAsString();
        }

        powertool.putByte("Selected", (byte) Math.max(powertool.getByte("Selected"), commands.size() - 1));

        return s.getAsString();
    }

    public static void resetPowerTool(ItemStack stack) {
        reformPowerTool(stack);

        stack.getTag().ifNonNullV(nbt -> nbt.remove("PowerTool"));
    }

    public static List<String> getCommands(ItemStack stack) {
        if (!isPowerTool(stack)) return Collections.emptyList();

        return Objects.requireNonNull(stack.getTagElement("PowerTool")).getList("Commands", Tag.TAG_STRING).stream()
                .map(Tag::getAsString)
                .collect(Collectors.toList());
    }

    public static InteractionHand getPowerToolHand(Player player) {
        return isPowerTool(player.getMainHandItem()) ? InteractionHand.MAIN_HAND : isPowerTool(player.getOffhandItem()) ? InteractionHand.OFF_HAND : null;
    }

    public static ItemStack getPowerToolStack(Player player) {
        InteractionHand hand = getPowerToolHand(player);
        ItemStack stack = hand == null ? null : player.getItemInHand(hand);

        reformPowerTool(stack);
        return stack;
    }

    @Environment(EnvType.CLIENT)
    public static void cycleCommand() {
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        InteractionHand hand = isPowerTool(player.getMainHandItem()) ? InteractionHand.MAIN_HAND : isPowerTool(player.getOffhandItem()) ? InteractionHand.OFF_HAND : null;

        if (hand == null || !NetworkManager.canServerReceive(new ResourceLocation("morecommands:powertool_cycle"))) return;

        int index = doCycleCommand(player, hand, -1);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(hand.ordinal());
        buf.writeByte(index);

        NetworkManager.sendToServer(new ResourceLocation("morecommands:powertool_cycle"), buf);
        displaySelection(player.getItemInHand(hand));
    }

    public static int doCycleCommand(Player player, InteractionHand hand, int index) {
        ItemStack stack = player.getItemInHand(hand);
        reformPowerTool(stack);

        CompoundTag powertool = Objects.requireNonNull(stack.getTagElement("PowerTool"));
        if (index < 0) index = (powertool.getByte("Selected") + 1) % powertool.getList("Commands", Tag.TAG_STRING).size();
        powertool.putByte("Selected", (byte) index);

        return index;
    }

    public static void displaySelection(ItemStack stack) {
        PowertoolSelectionMode mode = ClientOptions.Tweaks.powertoolSelection.getValue();
        if (mode == PowertoolSelectionMode.NONE || !isPowerTool(stack)) return;

        Font textRenderer = Minecraft.getInstance().font;
        int index = Objects.requireNonNull(stack.getTagElement("PowerTool")).getByte("Selected");
        String command = Objects.requireNonNull(stack.getTagElement("PowerTool")).getList("Commands", Tag.TAG_STRING).getString(index);
        String trimmedCommand = textRenderer.width(command) > PowerToolSelectionHud.MAX_BOX_WIDTH ?
                PowerToolSelectionHud.trimToLength(command, textRenderer) + "..." : command;

        EmptyTextBuilder text = EmptyTextBuilder.builder()
                .append(LiteralTextBuilder.builder("Selected (%d): ", DS, index + 1));

        switch (mode) {
            case HUD:
                PowerToolSelectionHud.currentSelection = new Tuple<>(System.currentTimeMillis(), new Tuple<>(index + 1, command));
                break;
            case CHAT:
                ClientCommand.sendMsg(text.append(LiteralTextBuilder.builder(command, SS)));
                break;
            case ACTION_BAR:
                ClientCommand.sendAbMsg(text.append(LiteralTextBuilder.builder(trimmedCommand, SS)));
                break;
        }
    }

    private static void reformPowerTool(ItemStack stack) {
        if (!isPowerTool(stack)) return;

        CompoundTag powertool = Objects.requireNonNull(stack.getTagElement("PowerTool"));
        if (powertool.contains("Command", Tag.TAG_STRING)) {
            String command = powertool.getString("Command");

            ListTag commands = new ListTag();
            commands.add(StringTag.valueOf(command));
            powertool.put("Commands", commands);

            powertool.putByte("Selected", (byte) 0);
            powertool.putByte("Buttons", (byte) (0x4 | 0x2 | 0x1));

            powertool.remove("Owner");
            powertool.remove("Command");
        }
    }

    public static boolean isPowerTool(ItemStack stack) {
        return stack != null && stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("PowerTool", Tag.TAG_COMPOUND);
    }

    public enum PowertoolSelectionMode {
        NONE, HUD, ACTION_BAR, CHAT
    }
}
