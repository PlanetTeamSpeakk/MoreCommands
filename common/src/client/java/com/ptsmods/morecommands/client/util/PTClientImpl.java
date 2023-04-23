package com.ptsmods.morecommands.client.util;

import com.google.common.base.Suppliers;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.PTClient;
import com.ptsmods.morecommands.api.callbacks.MouseEvent;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.client.gui.PowerToolSelectionHud;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.unelevated.PowerToolCommand;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class PTClientImpl implements PTClient {
    public static final PTClientImpl INSTANCE = new PTClientImpl();
    public static final Supplier<KeyMapping> CYCLE_KEY_BINDING = Suppliers.memoize(() -> new KeyMapping("key.morecommands.powerToolCycle", GLFW.GLFW_KEY_G,
            MoreCommands.DF + "MoreCommands"));

    @Override
    public void preinit() {
        MouseEvent.EVENT.register((button, action, mods) -> checkPowerToolClient(button, action));
        KeyMapping cycleKey = CYCLE_KEY_BINDING.get();
        if (!Platform.isForge()) KeyMappingRegistry.register(cycleKey);

        AtomicBoolean pressed = new AtomicBoolean();
        AtomicInteger lastSelected = new AtomicInteger();
        ClientTickEvent.CLIENT_LEVEL_PRE.register(world -> {
            if (cycleKey.consumeClick()) {
                //noinspection StatementWithEmptyBody
                while (cycleKey.consumeClick()); // Clearing pressed counter
                if (!pressed.get()) {
                    cycleCommand();
                    pressed.set(true);
                }
            } else pressed.set(false);

            Inventory inventory = Objects.requireNonNull(Minecraft.getInstance().player).getInventory();
            if (inventory.selected != lastSelected.get()) {
                ItemStack stack = inventory.getItem(inventory.selected);
                if (PowerToolCommand.isPowerTool(stack)) displaySelection(stack);
                lastSelected.set(inventory.selected);
            }
        });

        ClientGuiEvent.RENDER_HUD.register(PowerToolSelectionHud::render);
    }

    private boolean checkPowerToolClient(int button, int action) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && action == 1 && Minecraft.getInstance().screen == null) {
            String cmd = PowerToolCommand.getCurrentPowerTool(player, button);
            if (cmd != null) {
                ClientCompat.get().sendMessageOrCommand("/" + cmd);
                player.swing(Objects.requireNonNull(PowerToolCommand.getPowerToolHand(player)));
                return true;
            }
        }
        return false;
    }

    private void cycleCommand() {
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        InteractionHand hand = PowerToolCommand.isPowerTool(player.getMainHandItem()) ? InteractionHand.MAIN_HAND :
                PowerToolCommand.isPowerTool(player.getOffhandItem()) ? InteractionHand.OFF_HAND : null;

        if (hand == null || !NetworkManager.canServerReceive(new ResourceLocation("morecommands:powertool_cycle"))) return;

        int index = PowerToolCommand.doCycleCommand(player, hand, -1);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeByte(hand.ordinal());
        buf.writeByte(index);

        NetworkManager.sendToServer(new ResourceLocation("morecommands:powertool_cycle"), buf);
        displaySelection(player.getItemInHand(hand));
    }

    private void displaySelection(ItemStack stack) {
        PowerToolCommand.PowertoolSelectionMode mode = ClientOptions.Tweaks.powertoolSelection.getValue();
        if (mode == PowerToolCommand.PowertoolSelectionMode.NONE || !PowerToolCommand.isPowerTool(stack)) return;

        Font textRenderer = Minecraft.getInstance().font;
        int index = Objects.requireNonNull(stack.getTagElement("PowerTool")).getByte("Selected");
        String command = Objects.requireNonNull(stack.getTagElement("PowerTool")).getList("Commands", Tag.TAG_STRING).getString(index);
        String trimmedCommand = textRenderer.width(command) > PowerToolSelectionHud.MAX_BOX_WIDTH ?
                PowerToolSelectionHud.trimToLength(command, textRenderer) + "..." : command;

        EmptyTextBuilder text = EmptyTextBuilder.builder()
                .append(LiteralTextBuilder.builder("Selected (%d): ", MoreCommands.DS, index + 1));

        switch (mode) {
            case HUD -> PowerToolSelectionHud.currentSelection = new Tuple<>(System.currentTimeMillis(), new Tuple<>(index + 1, command));
            case CHAT -> ClientCommand.sendMsg(text.append(LiteralTextBuilder.builder(command, MoreCommands.SS)));
            case ACTION_BAR -> ClientCommand.sendAbMsg(text.append(LiteralTextBuilder.builder(trimmedCommand, MoreCommands.SS)));
        }
    }
}
