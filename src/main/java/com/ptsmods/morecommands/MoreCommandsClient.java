package com.ptsmods.morecommands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.callbacks.ChatMessageSendCallback;
import com.ptsmods.morecommands.callbacks.ClientCommandRegistrationCallback;
import com.ptsmods.morecommands.gui.InfoHud;
import com.ptsmods.morecommands.miscellaneous.*;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.network.S2CPacketTypeCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Environment(EnvType.CLIENT)
public class MoreCommandsClient implements ClientModInitializer {

    public static final Logger log = LogManager.getLogger();
    public static final ClientSidePacketRegistry CPR = ClientSidePacketRegistry.INSTANCE;
    public static final KeyBinding toggleInfoHudBinding = new KeyBinding("key.morecommands.toggleInfoHud", GLFW.GLFW_KEY_O, ClientCommand.DF + "MoreCommands");;
    private static double speed = 0d;
    private static double avgSpeed = 0d;
    private static final DoubleList lastSpeeds = new DoubleArrayList();
    private static EasterEggSound easterEggSound = null;
    public static final CommandDispatcher<ClientCommandSource> clientCommandDispatcher = new CommandDispatcher<>();
    private static final Map<String, Integer> keys = new LinkedHashMap<>();
    private static final Map<Integer, String> keysReverse = new LinkedHashMap<>();
    private static DiscordUser discordUser = null;

    static {
        for (Field f : GLFW.class.getFields())
            if (f.getName().startsWith("GLFW_KEY_") || f.getName().startsWith("GLFW_MOUSE_BUTTON_")) {
                int keyCode;
                try {
                    keyCode = f.getInt(null) + (f.getName().contains("MOUSE") ? GLFW.GLFW_KEY_LAST+1 : 0);
                } catch (IllegalAccessException e) {
                    log.catching(e);
                    continue;
                }
                String name = f.getName().substring(f.getName().contains("MOUSE") ? 18 : 9);
                if (keyCode >= 0 && !name.equals("LAST")) {
                    if (keys.containsValue(keyCode)) keys.remove(keysReverse.remove(keyCode)); // Aliases :/
                    keysReverse.put(keyCode, name);
                    keys.put(keysReverse.get(keyCode), keyCode);
                }
            }
    }

    @Override
    public void onInitializeClient() {
        ClientOptions.read();
        DiscordRPC.discordInitialize("754048885755871272", new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> {
                    discordUser = user;
                    log.info("Connected to Discord RPC as " + user.username + "#" + user.discriminator + " (" + user.userId + ").");
                })
                .setDisconnectedEventHandler((errorCode, message) -> log.info("Disconnected from Discord RPC with error code " + errorCode + ": " + message))
                .setErroredEventHandler((errorCode, message) -> log.info("An error occurred on the Discord RPC with error code " + errorCode + ": " + message)).build(), true);
        updatePresence();
        //C2SPlayChannelEvents.REGISTER.register(((handler, sender, client, channels) -> updateTag()));
        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
        Language.setInstance(Language.getInstance()); // Wrap the current instance so it can translate all enchant levels and spawner names. :3 (Look at MixinLanguage)
        KeyBindingHelper.registerKeyBinding(toggleInfoHudBinding);
        HudRenderCallback.EVENT.register((stack, tickDelta) -> {
            if (ClientOptions.Tweaks.enableInfoHud) InfoHud.instance.render(stack, tickDelta);
        });
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (toggleInfoHudBinding.wasPressed()) {
                ClientOptions.Tweaks.enableInfoHud = !ClientOptions.Tweaks.enableInfoHud;
                ClientOptions.write();
            }
            ClientPlayerEntity p = MinecraftClient.getInstance().player;
            if (p != null) {
                double x = p.getX() - p.prevX;
                double y = p.getY() - p.prevY;
                double z = p.getZ() - p.prevZ;
                speed = MathHelper.sqrt(x * x + y * y + z * z) * 20; // Apparently, Pythagoras' theorem does have some use. Who would've thunk?
                lastSpeeds.add(speed);
                if (lastSpeeds.size() > 20) lastSpeeds.removeDouble(0);
                double speedSum = 0d;
                for (double speed : lastSpeeds)
                    speedSum += speed;
                avgSpeed = speedSum / lastSpeeds.size();
            }
        });
        ClientCommandRegistrationCallback.EVENT.register(dispatcher -> {
            for (Class<? extends ClientCommand> cmd : MoreCommands.getCommandClasses("client", ClientCommand.class))
                try {
                     MoreCommands.getInstance(cmd).cRegister(dispatcher);
                } catch (Exception e) {
                    log.catching(e);
                }
        });
        CPR.register(new Identifier("morecommands:formatting_update"), (ctx, buf) -> {
            int id = buf.readByte();
            int index = buf.readByte();
            if (index < 0) return;
            Formatting colour = FormattingColour.values()[index].toFormatting();
            switch (id) {
                case 0:
                    MoreCommands.DF = Command.DF = colour;
                    MoreCommands.DS = Command.DS = MoreCommands.DS.withColor(MoreCommands.DF);
                    break;
                case 1:
                    MoreCommands.SF = Command.SF = colour;
                    MoreCommands.SS = Command.SS = MoreCommands.SS.withColor(MoreCommands.SF);
                    break;
            }
        });
        ChatMessageSendCallback.EVENT.register(message -> {
            if (message.startsWith("/easteregg")) {
                if (easterEggSound == null) MinecraftClient.getInstance().getSoundManager().play(easterEggSound = new EasterEggSound());
                else {
                    MinecraftClient.getInstance().getSoundManager().stop(easterEggSound);
                    easterEggSound = null;
                }
                return null;
            }
            return message;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!Screen.hasShiftDown() && ClientOptions.Tweaks.sitOnStairs && Chair.isValid(world.getBlockState(hitResult.getBlockPos())) && CPR.canServerReceive(new Identifier("morecommands:sit_on_stairs"))) {
                CPR.sendToServer(CPR.toPacket(new Identifier("morecommands:sit_on_stairs"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(hitResult.getBlockPos())));
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        });
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            for (Entity entity : world.getEntities())
                if (entity instanceof PlayerEntity && MoreCommands.isCool(entity))
                    for (int i = 0; i < 2; i++)
                        MinecraftClient.getInstance().particleManager.addParticle(new VexParticle(entity));
        });
    }

    public static String getWorldName() {
        return MinecraftClient.getInstance().world == null ? null : MinecraftClient.getInstance().getCurrentServerEntry() == null ? Objects.requireNonNull(MinecraftClient.getInstance().getServer()).getSaveProperties().getLevelName() : MinecraftClient.getInstance().getCurrentServerEntry().address;
    }

    public static void updatePresence() {
        if (ClientOptions.RichPresence.enableRPC) {
            MinecraftClient client = MinecraftClient.getInstance();
            DiscordRichPresence.Builder builder;
            if (client.world == null) builder = new DiscordRichPresence.Builder("On the main menu").setBigImage("minecraft_logo", null);
            else {
                builder = new DiscordRichPresence.Builder(client.getCurrentServerEntry() == null ? "Singleplayer" : "Multiplayer").setBigImage("in_game", null);
                if (ClientOptions.RichPresence.showDetails) builder.setDetails(getWorldName());
            }
            if (ClientOptions.RichPresence.advertiseMC) builder.setSmallImage("morecommands_logo", "Download at https://bit.ly/MoreCommands");
            DiscordRPC.discordUpdatePresence(builder.setStartTimestamps(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000L).build());
        } else DiscordRPC.discordClearPresence();
    }

    public static void updateTag() {
        if (CPR.canServerReceive(new Identifier("morecommands:discord_data")) && ClientOptions.RichPresence.shareTag && discordUser != null) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(ClientOptions.RichPresence.askPermission);
            buf.writeString(discordUser.userId);
            buf.writeString(discordUser.username);
            buf.writeString(discordUser.discriminator);
            buf.writeString(discordUser.avatar);
            CPR.sendToServer(new Identifier("morecommands:discord_data"), buf);
        }
    }

    public static double getSpeed() {
        return speed;
    }

    public static double getAvgSpeed() {
        return avgSpeed;
    }

    public static int getKeyCodeForKey(String key) {
        return keys.get(key);
    }

    public static String getKeyForKeyCode(int keyCode) {
        return keysReverse.get(keyCode);
    }

    public static List<String> getKeys() {
        return ImmutableList.copyOf(keys.keySet());
    }

}
