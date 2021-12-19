package com.ptsmods.morecommands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.ptsmods.morecommands.callbacks.ChatMessageSendCallback;
import com.ptsmods.morecommands.callbacks.ClientCommandRegistrationCallback;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.compat.client.ClientCompat;
import com.ptsmods.morecommands.gui.InfoHud;
import com.ptsmods.morecommands.miscellaneous.*;
import com.ptsmods.morecommands.mixin.client.accessor.MixinParticleManagerAccessor;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class MoreCommandsClient implements ClientModInitializer {
	public static final Logger log = LogManager.getLogger();
	public static final KeyBinding toggleInfoHudBinding = new KeyBinding("key.morecommands.toggleInfoHud", GLFW.GLFW_KEY_O, ClientCommand.DF + "MoreCommands");
	public static boolean scheduleWorldInitCommands = false;
	private static double speed = 0d;
	private static double avgSpeed = 0d;
	private static final DoubleList lastSpeeds = new DoubleArrayList();
	private static EasterEggSound easterEggSound = null;
	public static final CommandDispatcher<ClientCommandSource> clientCommandDispatcher = new CommandDispatcher<>();
	private static final Map<String, Integer> keys = new LinkedHashMap<>();
	private static final Map<Integer, String> keysReverse = new LinkedHashMap<>();
	private static DiscordUser discordUser = null;
	private static final List<String> disabledCommands = new ArrayList<>();
	private static final List<String> worldInitCommands = new ArrayList<>();
	private static final File wicFile = new File("config/MoreCommands/worldInitCommands.json");
	private static final Map<String, String> nameMCFriends = new HashMap<>();
	private static final HttpClient sslLenientHttpClient;

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
					if (keys.containsValue(keyCode)) continue; // Aliases :/
					keysReverse.put(keyCode, name);
					keys.put(keysReverse.get(keyCode), keyCode);
				}
			}
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom()
					.useTLS().loadTrustMaterial(null, (chain, authType) -> true)
					.build();
		} catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
			e.printStackTrace();
		}
		sslLenientHttpClient = sslContext == null ? null : HttpClientBuilder.create()
				.setSslcontext(sslContext)
				.setConnectionManager(new PoolingHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.INSTANCE)
						.register("https", new SSLConnectionSocketFactory(sslContext))
						.build()))
				.build();
	}

	public static String getSSLLenientHTML(String url) throws IOException {
		return MoreCommands.readInputStream(sslLenientHttpClient.execute(new HttpGet(url)).getEntity().getContent());
	}

	@Override
	public void onInitializeClient() {
		ClientOptions.read();
		MoreCommands.setFormattings(ClientOptions.Tweaks.defColour.getValue().asFormatting(), ClientOptions.Tweaks.secColour.getValue().asFormatting());

		List<ParticleTextureSheet> list = new ArrayList<>(MixinParticleManagerAccessor.getParticleTextureSheets());
		list.add(VexParticle.pts);
		MixinParticleManagerAccessor.setParticleTextureSheets(list);

		List<ItemConvertible> waterItems = Lists.newArrayList(Blocks.WATER, Blocks.BUBBLE_COLUMN);
		if (Registry.BLOCK.containsId(new Identifier("water_cauldron"))) waterItems.add(Registry.BLOCK.get(new Identifier("water_cauldron")));
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x3e76e4, waterItems.toArray(new ItemConvertible[0]));

		if (!MinecraftClient.IS_SYSTEM_MAC)
			DiscordRPC.discordInitialize("754048885755871272", new DiscordEventHandlers.Builder()
					.setReadyEventHandler(user -> {
						discordUser = user;
						log.info("Connected to Discord RPC as " + user.username + "#" + user.discriminator + " (" + user.userId + ").");
					})
					.setDisconnectedEventHandler((errorCode, message) -> log.info("Disconnected from Discord RPC with error code " + errorCode + ": " + message))
					.setErroredEventHandler((errorCode, message) -> log.info("An error occurred on the Discord RPC with error code " + errorCode + ": " + message)).build(), true);
		updatePresence();
		C2SPlayChannelEvents.REGISTER.register(((handler, sender, client, channels) -> updateTag()));

		AtomicInteger wicWarmup = new AtomicInteger();
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (scheduleWorldInitCommands) wicWarmup.set(10);
			scheduleWorldInitCommands = false;
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (wicWarmup.get() > 0 && wicWarmup.decrementAndGet() == 0) getWorldInitCommands().forEach(cmd -> server.getCommandManager().execute(server.getCommandSource(), cmd));
		});

		Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC::discordShutdown));
		KeyBindingHelper.registerKeyBinding(toggleInfoHudBinding);

		if (!wicFile.exists()) saveWorldInitCommands();
		try {
			List<String> wic = MoreCommands.readJson(wicFile);
			if (wic != null) worldInitCommands.addAll(wic);
		} catch (IOException e) {
			log.error("Could not read World Init Commands.", e);
		}

		HudRenderCallback.EVENT.register((stack, tickDelta) -> {
			if (ClientOptions.Tweaks.enableInfoHud.getValue()) InfoHud.instance.render(stack, tickDelta);
		});

		ClientTickEvents.START_WORLD_TICK.register(world -> {
			if (toggleInfoHudBinding.wasPressed()) {
				ClientOptions.Tweaks.enableInfoHud.setValue(!ClientOptions.Tweaks.enableInfoHud.getValue());
				ClientOptions.write();
			}
			ClientPlayerEntity p = MinecraftClient.getInstance().player;
			if (p != null) {
				double x = p.getX() - p.prevX;
				double y = p.getY() - p.prevY;
				double z = p.getZ() - p.prevZ;
				speed = Math.sqrt(x * x + y * y + z * z) * 20; // Apparently, Pythagoras' theorem does have some use. Who would've thunk?
				lastSpeeds.add(speed);
				if (lastSpeeds.size() > 20) lastSpeeds.removeDouble(0);
				double speedSum = 0d;
				for (double speed : lastSpeeds)
					speedSum += speed;
				avgSpeed = speedSum / lastSpeeds.size();
			}

			for (Entity entity : world.getEntities())
				if (entity instanceof PlayerEntity && MoreCommands.isCool(entity))
					for (int i = 0; i < 2; i++)
						MinecraftClient.getInstance().particleManager.addParticle(new VexParticle(entity));
		});

		List<ClientCommand> clientCommands = MoreCommands.getCommandClasses("client", ClientCommand.class).stream().map(MoreCommands::getInstance).filter(Objects::nonNull).collect(Collectors.toList());
		ClientCommandRegistrationCallback.EVENT.register(dispatcher -> clientCommands.forEach(cmd -> {
			try {
				cmd.cRegister(dispatcher);
			} catch (Exception e) {
				log.error("Could not register command " + cmd.getClass().getName() + ".", e);
			}
		}));

		ClientPlayNetworking.registerGlobalReceiver(new Identifier("morecommands:formatting_update"), (client, handler, buf, responseSender) -> {
			int id = buf.readByte();
			int index = buf.readByte();
			if (index < 0) return;
			Formatting colour = FormattingColour.values()[index].asFormatting();
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

		ClientPlayNetworking.registerGlobalReceiver(new Identifier("morecommands:disable_client_options"), (client, handler, buf, responseSender) -> {
			ClientOptions.getOptions().forEach(option -> option.setDisabled(false));
			int length = buf.readVarInt();
			for (int i = 0; i < length; i++)
				Optional.ofNullable(ClientOptions.getOption(buf.readString())).ifPresent(option -> option.setDisabled(true));
		});

		ClientPlayNetworking.registerGlobalReceiver(new Identifier("morecommands:disable_client_commands"), (client, handler, buf, responseSender) -> {
			disabledCommands.clear();
			int length = buf.readVarInt();
			for (int i = 0; i < length; i++)
				disabledCommands.add(buf.readString());
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
			if (!Screen.hasShiftDown() && ClientOptions.Tweaks.sitOnStairs.getValue() && Chair.isValid(world.getBlockState(hitResult.getBlockPos())) && ClientPlayNetworking.canSend(new Identifier("morecommands:sit_on_stairs"))) {
				ClientPlayNetworking.send(new Identifier("morecommands:sit_on_stairs"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(hitResult.getBlockPos()));
				return ActionResult.CONSUME;
			}
			return ActionResult.PASS;
		});

		MoreCommands.execute(() -> {
			try {
				@SuppressWarnings("UnstableApiUsage")
				List<Map<String, String>> friends = new Gson().fromJson(getSSLLenientHTML("https://api.namemc.com/profile/" + MinecraftClient.getInstance().getSession().getUuid() + "/friends"), new TypeToken<List<Map<String, String>>>() {}.getType());
				// SSL lenient because the certificate of api.namemc.com is not recognised on Java 8 for some reason.
				nameMCFriends.putAll(friends.stream().collect(Collectors.toMap(friend -> friend.get("uuid"), friend -> friend.get("name"))));
			} catch (IOException e) {
				log.error("Could not look up NameMC friends.", e);
			}
		});
	}

	public static String getWorldName() {
		// MinecraftClient#getServer() null check to fix https://github.com/PlanetTeamSpeakk/MoreCommands/issues/25
		return MinecraftClient.getInstance().world == null ? null : MinecraftClient.getInstance().getCurrentServerEntry() == null ?
				MinecraftClient.getInstance().getServer() == null ? null : Objects.requireNonNull(MinecraftClient.getInstance().getServer()).getSaveProperties().getLevelName() :
				MinecraftClient.getInstance().getCurrentServerEntry().address;
	}

	public static void updatePresence() {
		if (ClientOptions.RichPresence.enableRPC.getValue() && !MinecraftClient.IS_SYSTEM_MAC) {
			MinecraftClient client = MinecraftClient.getInstance();
			DiscordRichPresence.Builder builder;
			if (client.world == null) builder = new DiscordRichPresence.Builder("On the main menu").setBigImage("minecraft_logo", null);
			else {
				builder = new DiscordRichPresence.Builder(client.getCurrentServerEntry() == null ? "Singleplayer" : "Multiplayer").setBigImage("in_game", null);
				if (ClientOptions.RichPresence.showDetails.getValue() && getWorldName() != null) builder.setDetails(getWorldName());
			}
			if (ClientOptions.RichPresence.advertiseMC.getValue()) builder.setSmallImage("morecommands_logo", "Download at https://bit.ly/MoreCommands");
			DiscordRPC.discordUpdatePresence(builder.setStartTimestamps(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() / 1000L).build());
		} else DiscordRPC.discordClearPresence();
	}

	public static void updateTag() {
		if (ClientPlayNetworking.canSend(new Identifier("morecommands:discord_data")) && ClientOptions.RichPresence.shareTag.getValue() && discordUser != null) {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBoolean(ClientOptions.RichPresence.askPermission.getValue());
			buf.writeString(discordUser.userId);
			buf.writeString(discordUser.username);
			buf.writeString(discordUser.discriminator);
			buf.writeString(discordUser.avatar);
			ClientPlayNetworking.send(new Identifier("morecommands:discord_data"), buf);
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

	// Frodo on da beat
	public static void addColourPicker(Screen screen, int xOffset, int yOffset, boolean doCenter, boolean initOpened, Consumer<String> appender, Consumer<Boolean> stateListener) {
		boolean initOpened0 = initOpened || ClientOptions.Tweaks.colourPickerOpen.getValue();
		final int buttonWidth = 24;
		final int wideButtonWidth = (int) (buttonWidth / 0.75f);
		final int buttonHeight = 20;
		Formatting[] formattings = Formatting.values();
		List<ButtonWidget> btns = new ArrayList<>();
		for (int i = 0; i < formattings.length; i++) {
			int x = i; // Has to be effectively final cuz lambda
			btns.add(ClientCompat.getCompat().addButton(screen, Util.make(new ButtonWidget(xOffset + (i < 16 ? (buttonWidth+2) * (i%4) : (wideButtonWidth+3) * (i%3)), yOffset + (buttonHeight+2) * ((i < 16 ? i/4 : 4 + (i-16)/3) + 1), i < 16 ? buttonWidth : wideButtonWidth, buttonHeight,
					new LiteralText(formattings[x].toString().replace('\u00A7', '&')).setStyle(Style.EMPTY.withFormatting(formattings[x])), btn0 -> appender.accept(formattings[x].toString().replace('\u00A7', '&')),
					/*Rainbow formatting*/ i == 22 ? (button, matrices, mouseX, mouseY) -> screen.renderTooltip(matrices, new LiteralText(Formatting.RED + "Only works on servers with MoreCommands installed."), mouseX, mouseY) : ButtonWidget.EMPTY) {
				public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
					return false;
				}
			}, btn -> btn.visible = initOpened0)));
		}
		Objects.requireNonNull(ClientCompat.getCompat().addButton(screen, new ButtonWidget(xOffset + (buttonWidth+2) * 2 - 26, yOffset + (doCenter && !initOpened0 ? (buttonHeight+2) * 7 / 2 : 0), 50, 20, new LiteralText("Colours").setStyle(Command.DS), btn -> {
			boolean b = !btns.get(0).visible;
			if (doCenter) btn.y = b ? yOffset : yOffset + 22 * 7 / 2;
			btns.forEach(btn0 -> btn0.visible = b);
			if (stateListener != null) stateListener.accept(b);
		}) {
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				return false;
			}
		})).visible = ClientOptions.Tweaks.textColourPicker.getValue();
	}

	public static void clearDisabledCommands() {
		disabledCommands.clear();
	}

	public static boolean isCommandDisabled(String input) {
		if (input.startsWith("/")) input = input.substring(1);
		return disabledCommands.contains(input.split(" ")[0]);
	}

	public static List<String> getWorldInitCommands() {
		return ImmutableList.copyOf(worldInitCommands);
	}

	public static void clearWorldInitCommands() {
		worldInitCommands.clear();
		saveWorldInitCommands();
	}

	public static void setWorldInitCommands(List<String> commands) {
		worldInitCommands.clear();
		worldInitCommands.addAll(commands);
		saveWorldInitCommands();
	}

	public static void saveWorldInitCommands() {
		try {
			MoreCommands.saveJson(wicFile, worldInitCommands);
		} catch (IOException e) {
			log.error("Could not save World Init Commands.", e);
		}
	}

	public static Map<String, String> getNameMCFriends() {
		return ImmutableMap.copyOf(nameMCFriends);
	}

	public static void updateNameMCFriend(String id, String name) {
		nameMCFriends.put(id, name);
	}
}
