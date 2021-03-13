package com.ptsmods.morecommands;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.arguments.*;
import com.ptsmods.morecommands.commands.server.elevated.*;
import com.ptsmods.morecommands.miscellaneous.*;
import com.ptsmods.morecommands.mixin.common.accessor.MixinFormattingAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinRegistryAccessor;
import com.ptsmods.morecommands.mixin.common.accessor.MixinScoreboardCriterionAccessor;
import io.netty.buffer.Unpooled;
import net.arikia.dev.drpc.DiscordUser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.TestCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static net.minecraft.block.Blocks.*;

public class MoreCommands implements ModInitializer {

	public static final Logger log = LogManager.getLogger();
	public static Formatting DF = Formatting.GOLD;
	public static Formatting SF = Formatting.YELLOW;
	public static Style DS = Style.EMPTY.withColor(DF);
	public static Style SS = Style.EMPTY.withColor(SF);
	public static CustomGameRuleCategory grc = new CustomGameRuleCategory(new Identifier("morecommands:main"), new LiteralText("MoreCommands").setStyle(Style.EMPTY.withFormatting(Formatting.GOLD).withBold(true)));
	public static GameRules.Key<EnumRule<FormattingColour>> DFrule = GameRuleRegistry.register("defaultFormatting", grc, GameRuleFactory.createEnumRule(FormattingColour.GOLD, (server, value) -> updateFormatting(server, 0, value.get())));;
	public static GameRules.Key<EnumRule<FormattingColour>> SFrule = GameRuleRegistry.register("secondaryFormatting", grc, GameRuleFactory.createEnumRule(FormattingColour.YELLOW, (server, value) -> updateFormatting(server, 1, value.get())));
	public static GameRules.Key<GameRules.BooleanRule> doMeltRule = GameRuleRegistry.register("doMelt", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.IntRule> maxHomesRule = GameRuleRegistry.register("maxHomes", grc, GameRuleFactory.createIntRule(3, -1));
	public static GameRules.Key<GameRules.BooleanRule> doSilkSpawnersRule = GameRuleRegistry.register("doSilkSpawners", grc, GameRuleFactory.createBooleanRule(false));
	public static GameRules.Key<GameRules.BooleanRule> randomOrderPlayerTickRule = GameRuleRegistry.register("randomOrderPlayerTick", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.IntRule> hopperTransferCooldownRule = GameRuleRegistry.register("hopperTransferCooldown", grc, GameRuleFactory.createIntRule(8, 0));
	public static GameRules.Key<GameRules.IntRule> hopperTransferRateRule = GameRuleRegistry.register("hopperTransferRate", grc, GameRuleFactory.createIntRule(1, 1, 64));
	public static GameRules.Key<GameRules.BooleanRule> doFarmlandTrampleRule = GameRuleRegistry.register("doFarmlandTrample", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doJoinMessageRule = GameRuleRegistry.register("doJoinMessage", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doExplosionsRule = GameRuleRegistry.register("doExplosions", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.IntRule> wildLimitRule = GameRuleRegistry.register("wildLimit", grc, GameRuleFactory.createIntRule(5000, 0, (int) WorldBorder.DEFAULT_BORDER.getSize()/2));
	public static GameRules.Key<GameRules.IntRule> tpaTimeoutRule = GameRuleRegistry.register("tpaTimeout", grc, GameRuleFactory.createIntRule(2400, 600));
	public static GameRules.Key<GameRules.BooleanRule> fluidsInfiniteRule = GameRuleRegistry.register("fluidsInfinite", grc, GameRuleFactory.createBooleanRule(false));
	public static GameRules.Key<GameRules.BooleanRule> doLiquidFlowRule = GameRuleRegistry.register("doLiquidFlow", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.IntRule> vaultRowsRule = GameRuleRegistry.register("vaultRows", grc, GameRuleFactory.createIntRule(6, 1, 6));
	public static GameRules.Key<GameRules.IntRule> vaultsRule = GameRuleRegistry.register("vaults", grc, GameRuleFactory.createIntRule(3, 0));
	public static GameRules.Key<GameRules.IntRule> nicknameLimitRule = GameRuleRegistry.register("nicknameLimit", grc, GameRuleFactory.createIntRule(16, 0));
	public static GameRules.Key<GameRules.BooleanRule> doSignColoursRule = GameRuleRegistry.register("doSignColours", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doBookColoursRule = GameRuleRegistry.register("doBookColours", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doChatColoursRule = GameRuleRegistry.register("doChatColours", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doItemColoursRule = GameRuleRegistry.register("doItemColours", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doEnchantLevelLimitRule = GameRuleRegistry.register("doEnchantLevelLimit", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doPriorWorkPenaltyRule = GameRuleRegistry.register("doPriorWorkPenalty", grc, GameRuleFactory.createBooleanRule(true));
	public static GameRules.Key<GameRules.BooleanRule> doItemsFireDamageRule = GameRuleRegistry.register("doItemsFireDamage", grc, GameRuleFactory.createBooleanRule(true));
	public static final List<Block> blockBlacklist = Lists.newArrayList(AIR, BEDROCK, LAVA, CACTUS, MAGMA_BLOCK, ACACIA_FENCE, ACACIA_FENCE_GATE, BIRCH_FENCE, BIRCH_FENCE_GATE, DARK_OAK_FENCE, DARK_OAK_FENCE_GATE, JUNGLE_FENCE, JUNGLE_FENCE_GATE, NETHER_BRICK_FENCE, OAK_FENCE, OAK_FENCE_GATE, SPRUCE_FENCE, SPRUCE_FENCE_GATE, FIRE, COBWEB, SPAWNER, END_PORTAL, END_PORTAL_FRAME, TNT, IRON_TRAPDOOR, ACACIA_TRAPDOOR, BIRCH_TRAPDOOR, CRIMSON_TRAPDOOR, DARK_OAK_TRAPDOOR, JUNGLE_TRAPDOOR, SPRUCE_TRAPDOOR, WARPED_TRAPDOOR, BREWING_STAND);
	public static final List<Block> blockWhitelist = Lists.newArrayList(AIR, DEAD_BUSH, VINE, TALL_GRASS, ACACIA_DOOR, BIRCH_DOOR, DARK_OAK_DOOR, IRON_DOOR, JUNGLE_DOOR, OAK_DOOR, SPRUCE_DOOR, POPPY, DANDELION, BROWN_MUSHROOM, RED_MUSHROOM, LILY_PAD, BEETROOTS, CARROTS, WHEAT, POTATOES, PUMPKIN_STEM, MELON_STEM, SNOW);
	public static final Block lockedChest = new Block(FabricBlockSettings.of(Material.WOOD));
	public static final Item lockedChestItem = new BlockItem(lockedChest, new Item.Settings());
	public static final Item netherPortalItem = new BlockItem(NETHER_PORTAL, new Item.Settings().fireproof()); // After all, why not? Why shouldn't a nether portal be fireproof?
	public static final ItemGroup unobtainableItems = FabricItemGroupBuilder.create(new Identifier("morecommands:unobtainable_items")).icon(() -> new ItemStack(lockedChestItem)).build();
	public static MinecraftServer serverInstance = null;
	public static final TrackedData<Boolean> MAY_FLY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Boolean> INVULNERABLE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Boolean> SUPERPICKAXE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Boolean> VANISH = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Boolean> VANISH_TOGGLED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Optional<BlockPos>> CHAIR = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS);
	public static final TrackedData<CompoundTag> VAULTS = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
	public static final TrackedData<Optional<Text>> NICKNAME = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
	public static final ScoreboardCriterion LATENCY;
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static final Map<PlayerEntity, DiscordUser> discordTags = new HashMap<>();
	public static final Set<PlayerEntity> discordTagNoPerm = new HashSet<>();
	private static final Executor executor = Executors.newCachedThreadPool();
	private static final DecimalFormat sizeFormat = new DecimalFormat("#.###");
	public static Unsafe theUnsafe = ReflectionHelper.getFieldValue(Unsafe.class, "theUnsafe", null);

	static {
		ScoreboardCriterion.CRITERIA.put("latency", LATENCY = MixinScoreboardCriterionAccessor.newInstance("latency", true, ScoreboardCriterion.RenderType.INTEGER));
	}

	@Override
	public void onInitialize() {
		MixinFormattingAccessor.setFormattingCodePattern(Pattern.compile("(?i)\u00A7[0-9A-FK-ORU]")); // Adding the 'U' for the rainbow formatting.
		File dir = new File("config/MoreCommands");
		if (!dir.exists()) dir.mkdirs();
		Registry.register(Registry.SOUND_EVENT, new Identifier("morecommands:copy"), new SoundEvent(new Identifier("morecommands:copy")));
		Registry.register(Registry.SOUND_EVENT, new Identifier("morecommands:easteregg"), new SoundEvent(new Identifier("morecommands:easteregg")));
		Registry.register(Registry.BLOCK, new Identifier("morecommands:locked_chest"), lockedChest);
		Registry.register(Registry.ITEM, new Identifier("morecommands:locked_chest"), lockedChestItem);
		Registry.register(Registry.ITEM, new Identifier("minecraft:nether_portal"), netherPortalItem);
		Registry.register(Registry.ATTRIBUTE, new Identifier("morecommands:reach"), ReachCommand.reachAttribute);
		Registry.register(Registry.ATTRIBUTE, new Identifier("morecommands:swim_speed"), SpeedType.swimSpeedAttribute);
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			for (Class<? extends Command> cmd : getCommandClasses("server", Command.class))
				try {
					Command instance = getInstance(cmd);
					if (!instance.forDedicated() || dedicated) instance.register(dispatcher);
				} catch (Exception e) {
					log.catching(e);
				}
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) TestCommand.register(dispatcher); // Cuz why not lol
		});
		S2CPlayChannelEvents.REGISTER.register((handler, sender, server, types) -> {
			if (types.contains(new Identifier("morecommands:formatting_update"))) sendFormattingUpdates(handler.player);
		});
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("morecommands:sit_on_stairs"), (server, player, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			BlockState state = player.getServerWorld().getBlockState(pos);
			if (Chair.isValid(state))
				Chair.createAndPlace(pos, player, player.getServerWorld());
		});
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("morecommands:discord_data"), (server, player, handler, buf, responseSender) -> {
			DiscordUser user = new DiscordUser();
			if (buf.readBoolean()) discordTagNoPerm.add(player);
			else discordTagNoPerm.remove(player);
			user.userId = buf.readString();
			user.username = buf.readString();
			user.discriminator = buf.readString();
			user.avatar = buf.readString();
			discordTags.put(player, user);
		});
		Set<ServerPlayerEntity> howlingPlayers = new HashSet<>();
		ServerTickEvents.START_SERVER_TICK.register(server -> {
			// This does absolutely nothing whatsoever, just pass along. :)
			for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
				if (p.isSneaking()) {
					float pitch = MathHelper.wrapDegrees(p.pitch);
					float yaw = MathHelper.wrapDegrees(p.yaw);
					double moonWidth = Math.PI / 32 * -pitch;
					long dayTime = p.getServerWorld().getTime() % 24000; // getTimeOfDay() does not return a value between 0 and 24000 when using the /time add command.
					if (!howlingPlayers.contains(p) && p.getServerWorld().getMoonPhase() == 0 && dayTime > 12000 && pitch < 0 && Math.abs(yaw) >= (90 - moonWidth) && Math.abs(yaw) <= (90 + moonWidth)) {
						double moonPitch = -90 + Math.abs(dayTime - 18000) * 0.0175;
						if (pitch >= moonPitch-3 && pitch <= moonPitch+3) {
							p.getServerWorld().playSound(null, p.getBlockPos(), SoundEvents.ENTITY_WOLF_HOWL, SoundCategory.PLAYERS, 1f, 1f);
							howlingPlayers.add(p);
						}
					}
				} else howlingPlayers.remove(p);
		});
		ArgumentTypes.register("morecommands:registry_argument", RegistryArgumentType.class, new RegistryArgumentType.Serialiser());
		ArgumentTypes.register("morecommands:limited_string", LimitedStringArgumentType.class, new LimitedStringArgumentType.Serialiser());
		ArgumentTypes.register("morecommands:enum_argument", EnumArgumentType.class, new EnumArgumentType.Serialiser());
		ArgumentTypes.register("morecommands:cramped_string", CrampedStringArgumentType.class, new CrampedStringArgumentType.Serialiser());
		ArgumentTypes.register("morecommands:time_argument", TimeArgumentType.class, new ConstantArgumentSerializer<>(TimeArgumentType::new));
		ArgumentTypes.register("morecommands:hexinteger", HexIntegerArgumentType.class, new ConstantArgumentSerializer<>(HexIntegerArgumentType::new));
		ArgumentTypes.register("morecommands:ignorant_string", IgnorantStringArgumentType.class, new IgnorantStringArgumentType.Serialiser());
	}

	static <T extends Command> List<Class<T>> getCommandClasses(String type, Class<T> clazz) {
		List<Class<T>> classes = new ArrayList<>();
		File jar = getJar();
		if (jar == null) {
			log.error("Could not find the jarfile of the mod, no commands will be registered.");
			return classes;
		}
		try {
			List<Path> classNames = new ArrayList<>();
			// It should only be a directory in the case of a debug environment, otherwise it should always be a jar file.
			if (jar.isDirectory()) classNames.addAll(java.nio.file.Files.walk(new File(jar.getAbsolutePath() + File.separator + "com" + File.separator + "ptsmods" + File.separator + "morecommands" + File.separator + "commands" + File.separator + type + File.separator).toPath()).filter(path -> java.nio.file.Files.isRegularFile(path) && !path.getFileName().toString().contains("$")).collect(Collectors.toList()));
			else {
                ZipFile zip = new ZipFile(jar);
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().startsWith("com/ptsmods/morecommands/commands/" + type + "/") && entry.getName().endsWith(".class") && !entry.getName().split("/")[entry.getName().split("/").length - 1].contains("$"))
                        classNames.add(Paths.get(entry.getName()));
                }
            }
			classNames.forEach(path -> {
				String name = "com.ptsmods.morecommands.commands." + type + ("server".equals(type) ? "." + path.toFile().getParentFile().getName() : "") + "." + path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf('.'));
				try {
					Class<?> clazz0 = Class.forName(name);
					if (clazz.isAssignableFrom(clazz0)) classes.add(ReflectionHelper.cast(clazz0));
				} catch (ClassNotFoundException e) {
					log.error("Could not find class " + name, e);
				}
			});
			log.info("Found " + classes.size() + " commands to load for type " + type + ".");
		} catch (IOException e) {
			log.error("Couldn't find commands for type " + type + " this means none of said type will be loaded.", e);
		}
		return classes;
	}

	static File getJar() {
		File jar;
		try {
			jar = new File(((ModContainer) FabricLoader.getInstance().getModContainer("morecommands").orElseThrow(NullPointerException::new)).getOriginUrl().toURI().getPath());
		} catch (URISyntaxException e) {
			log.catching(e);
			return null;
		}
		if (jar.isDirectory() && jar.getName().equals("main")) jar = new File(jar.getParentFile().getParentFile().getAbsolutePath() + File.separator + "classes" + File.separator + "java" + File.separator + "main" + File.separator);
		return jar;
	}

	public static void setFormattings(Formatting def, Formatting sec) {
		if (def != null) {
			DF = Command.DF = def;
			DS = Command.DS = DS.withColor(DF);
		}
		if (sec != null) {
			SF = Command.SF = sec;
			SS = Command.SS = SS.withColor(SF);
		}
	}

	public static boolean updateFormatting(MinecraftServer server, int id, FormattingColour value) {
		switch (id) {
			case 0:
				value = value == null ? server.getGameRules().get(DFrule).get() : value;
				if (value.toFormatting() != DF) {
					setFormattings(value.toFormatting(), null);
					sendFormattingUpdate(server, 0, DF);
					return true;
				}
				break;
			case 1:
				value = value == null ? server.getGameRules().get(SFrule).get() : value;
				if (value.toFormatting() != SF) {
					setFormattings(null, value.toFormatting());
					sendFormattingUpdate(server, 1, SF);
					return true;
				}
				break;
		}
		return false;
	}

	public static void sendFormattingUpdates(ServerPlayerEntity p) {
		sendFormattingUpdate(p, 0, DF);
		sendFormattingUpdate(p, 1, SF);
	}

	private static void sendFormattingUpdate(MinecraftServer server, int id, Formatting colour) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer().writeByte(id).writeByte(colour.getColorIndex()));
		for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList())
			sendFormattingUpdate(p, buf);
	}

	private static void sendFormattingUpdate(ServerPlayerEntity p, int id, Formatting colour) {
		sendFormattingUpdate(p, new PacketByteBuf(Unpooled.buffer().writeByte(id).writeByte(Arrays.binarySearch(FormattingColour.values(), FormattingColour.valueOf(colour.name())))));
	}

	private static void sendFormattingUpdate(ServerPlayerEntity p, PacketByteBuf buf) {
		/*if (ServerPlayNetworking.canSend(p, new Identifier("morecommands:formatting_update")))*/ ServerPlayNetworking.send(p, new Identifier("morecommands:formatting_update"), buf); // Bug still does not seem to be fixed.
	}

	static <T extends Command> T getInstance(Class<T> cmd) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<T> con = cmd.getDeclaredConstructor();
		con.setAccessible(true);
		Command instance = con.newInstance();
		instance.setActiveInstance();
		try {
			instance.preinit();
		} catch (Exception e) {
			log.error("Error invoking pre-initialisation method on class " + cmd.getName() + ".", e);
		}
		return ReflectionHelper.cast(instance);
	}

	public static String textToString(Text text, Style parentStyle) {
		return textToString(text, parentStyle, FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
	}

	public static String textToString(Text text, Style parentStyle, boolean translate) {
		if (parentStyle == null) parentStyle = Style.EMPTY;
		StringBuilder s = new StringBuilder();
		Style style = text.getStyle();
		style = (style == null ? Style.EMPTY : style).withParent(parentStyle);
		TextColor c = style.getColor();
		if (c != null) {
			Formatting f = null;
			for (Formatting form : Formatting.values())
				if (form.getColorValue() != null && form.getColorValue().equals(c.getRgb())) {
					f = form;
					break;
				}
			if (f != null) s.append(f.toString());
		}
		if (style.isBold()) s.append(Formatting.BOLD);
		if (style.isStrikethrough()) s.append(Formatting.STRIKETHROUGH);
		if (style.isUnderlined()) s.append(Formatting.UNDERLINE);
		if (style.isItalic()) s.append(Formatting.ITALIC);
		if (style.isObfuscated()) s.append(Formatting.OBFUSCATED);
		if (text instanceof TranslatableText && translate) {
			TranslatableText tt = (TranslatableText) text;
			Object[] args = new Object[tt.getArgs().length];
			for (int i = 0; i < args.length; i++)
				if (tt.getArgs()[i] instanceof Text)
					args[i] = textToString((Text) tt.getArgs()[i], style, true);
				else args[i] = tt.getArgs()[i];
			s.append(I18n.translate(tt.getKey(), args));
		} else s.append(text.asString());
		if (!text.getSiblings().isEmpty())
			for (Text t : text.getSiblings())
				s.append(textToString(t, style, translate));
		return s.toString();
	}

	public static void saveJson(File f, Object data) throws IOException {
		saveString(f, gson.toJson(data));
	}

	public static <T> T readJson(File f) throws IOException {
		return gson.fromJson(readString(f), new TypeToken<T>(){}.getType());
	}

	public static void saveString(File f, String s) throws IOException {
		if (!f.getAbsoluteFile().getParentFile().exists()) f.getAbsoluteFile().getParentFile().mkdirs();
		if (!f.exists()) f.createNewFile();
		try (PrintWriter writer = new PrintWriter(f, "UTF-8")) {
			writer.print(s);
			writer.flush();
		}
	}

	public static String readString(File f) throws IOException {
		if (!f.getAbsoluteFile().getParentFile().exists()) f.getAbsoluteFile().getParentFile().mkdirs();
		if (!f.exists()) f.createNewFile();
		return String.join("\n", Files.readAllLines(f.toPath()));
	}

	public static char getChar(Formatting f) {
		return f.toString().charAt(1);
	}

	public static double factorial(double d) {
		double d0 = d - 1;
		while (d0 > 0)
			d *= d0--;
		return d;
	}

	/**
	 * Evaluates a math equation in a String. It does addition, subtraction,
	 * multiplication, division, exponentiation (using the ^ symbol), factorial (!
	 * <b>before</b> a number), and a few basic functions like sqrt. It supports
	 * grouping using (...), and it gets the operator precedence and associativity
	 * rules correct.
	 *
	 * @param str
	 * @return The answer to the equation.
	 * @author Boann (https://stackoverflow.com/a/26227947)
	 */
	public static double eval(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = ++pos < str.length() ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ')
					nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected character: " + (char) ch);
				return x;
			}

			double parseExpression() {
				double x = parseTerm();
				for (; ; )
					if (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
			}

			double parseTerm() {
				double x = parseFactor();
				for (; ; )
					if (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if (ch >= '0' && ch <= '9' || ch == '.') { // numbers
					while (ch >= '0' && ch <= '9' || ch == '.')
						nextChar();
					x = Double.parseDouble(str.substring(startPos, pos));
				} else if (ch >= 'a' && ch <= 'z' || ch == '!') { // functions
					while (ch >= 'a' && ch <= 'z' || ch == '!')
						nextChar();
					String func = str.substring(startPos, pos);
					x = parseFactor();
					switch (func) {
						case "sqrt":
							x = Math.sqrt(x);
							break;
						case "cbrt":
							x = Math.cbrt(x);
							break;
						case "sin":
							x = Math.sin(Math.toRadians(x));
							break;
						case "cos":
							x = Math.cos(Math.toRadians(x));
							break;
						case "tan":
							x = Math.tan(Math.toRadians(x));
							break;
						case "pi":
							x = Math.PI * (x == 0D ? 1D : x);
							break;
						case "!":
							x = factorial(x);
							break;
						default:
							throw new RuntimeException("Unknown function: " + func);
					}
				} else if (ch != -1) throw new RuntimeException("Unexpected character: " + (char) ch);
				else x = 0D;

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
				return x;
			}
		}.parse();
	}

	// Blatantly copied from GameRenderer, can raytrace both entities and blocks.
	public static HitResult getRayTraceTarget(Entity entity, World world, double reach, boolean ignoreEntities, boolean ignoreLiquids) {
		HitResult crosshairTarget = null;
		if (entity != null && world != null) {
			float td = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? MinecraftClient.getInstance().getTickDelta() : 1f;
			crosshairTarget = entity.raycast(reach, td, !ignoreLiquids);
			if (!ignoreEntities) {
				Vec3d vec3d = entity.getCameraPosVec(td);
				double e = reach;
				e *= e;
				if (crosshairTarget != null)
					e = crosshairTarget.getPos().squaredDistanceTo(vec3d);
				Vec3d vec3d2 = entity.getRotationVec(td);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * reach, vec3d2.y * reach, vec3d2.z * reach);
				Box box = entity.getBoundingBox().stretch(vec3d2.multiply(reach)).expand(1.0D, 1.0D, 1.0D);
				EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.collides(), e);
				if (entityHitResult != null) crosshairTarget = entityHitResult;
			}
		}
		return crosshairTarget;
	}

	public static Entity cloneEntity(Entity entity, boolean summon) {
		CompoundTag nbt = new CompoundTag();
		entity.saveSelfToTag(nbt);
		Entity e = EntityType.loadEntityWithPassengers(nbt, entity.getEntityWorld(), e0 -> {
			e0.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
			return e0;
		});
		if (e != null) {
			e.setUuid(UUID.randomUUID());
			if (summon) entity.getEntityWorld().spawnEntity(e);
		}
		return e;
	}

	public static String readTillSpaceOrEnd(StringReader reader) {
		StringBuilder s = new StringBuilder();
		while (reader.getRemainingLength() > 0 && reader.peek() != ' ')
			s.append(reader.read());
		return s.toString();
	}

	public static void teleport(Entity target, ServerWorld world, Vec3d pos, float yaw, float pitch) {
		teleport(target, world, pos.x, pos.y, pos.z, yaw, pitch);
	}

	// Blatantly copied from TeleportCommand#teleport.
	public static void teleport(Entity target, ServerWorld world, double x, double y, double z, float yaw, float pitch) {
		if (target instanceof ServerPlayerEntity) {
			ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
			world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, target.getId());
			target.stopRiding();
			if (((ServerPlayerEntity) target).isSleeping()) ((ServerPlayerEntity) target).wakeUp(true, true);
			if (world == target.world)
				((ServerPlayerEntity) target).networkHandler.requestTeleport(x, y, z, yaw, pitch, Collections.emptySet());
			else ((ServerPlayerEntity) target).teleport(world, x, y, z, yaw, pitch);
			target.setHeadYaw(yaw);
		} else {
			float f = MathHelper.wrapDegrees(yaw);
			float g = MathHelper.wrapDegrees(pitch);
			g = MathHelper.clamp(g, -90.0F, 90.0F);
			if (world == target.world) {
				target.refreshPositionAndAngles(x, y, z, f, g);
				target.setHeadYaw(f);
			} else {
				target.detach();
				Entity entity = target;
				target = target.getType().create(world);
				if (target == null) return;
				target.copyFrom(entity);
				target.refreshPositionAndAngles(x, y, z, f, g);
				target.setHeadYaw(f);
				world.onDimensionChanged(target);
				entity.kill();
				entity.remove(Entity.RemovalReason.KILLED);
			}
		}
		if (!(target instanceof LivingEntity) || !((LivingEntity) target).isFallFlying()) {
			target.setVelocity(target.getVelocity().multiply(1.0D, 0.0D, 1.0D));
			target.setOnGround(true);
		}
		if (target instanceof PathAwareEntity) ((PathAwareEntity) target).getNavigation().stop();
	}

	public static <T> Registry<T> getRegistry(RegistryKey<T> key) {
		return ReflectionHelper.cast(getRootRegistry().get(ReflectionHelper.<RegistryKey<MutableRegistry<?>>>cast(key)));
	}

	public static <T> RegistryKey<MutableRegistry<?>> getKey(MutableRegistry<T> registry) {
		return getRootRegistry().getKey(registry).orElse(null);
	}

	private static MutableRegistry<MutableRegistry<?>> getRootRegistry() {
		return MixinRegistryAccessor.getRoot();
	}

	public static <T> void removeNode(CommandDispatcher<T> dispatcher, CommandNode<T> child) {
		removeNode(dispatcher.getRoot(), child);
	}

	public static <S> void removeNode(CommandNode<S> parent, CommandNode<S> child) {
		if (parent.getChildren().contains(child)) {
			Objects.requireNonNull(ReflectionHelper.<Map<String, CommandNode<S>>, Object>getFieldValue(CommandNode.class, "children", parent)).remove(child.getName());
			if (child instanceof LiteralCommandNode)
				Objects.requireNonNull(ReflectionHelper.<Map<String, LiteralCommandNode<S>>, Object>getFieldValue(CommandNode.class, "literals", parent)).remove(child.getName());
			else if (child instanceof ArgumentCommandNode)
				Objects.requireNonNull(ReflectionHelper.<Map<String, ArgumentCommandNode<S, ?>>, Object>getFieldValue(CommandNode.class, "arguments", parent)).remove(child.getName());
		}
	}

	public static String getHTML(String url) throws IOException {
		StringBuilder result = new StringBuilder();
		URL URL = new URL(url);
		URLConnection connection = URL.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null)
			result.append(line);
		rd.close();
		return result.toString();
	}

	public static void setServerInstance(MinecraftServer server) {
		serverInstance = server;
		Command.doInitialisations(server);
		log.info("MoreCommands data path: " + getRelativePath(server));
	}

	public static boolean isAprilFirst() {
		return Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isInteger(String s, int base) {
		try {
			Integer.parseInt(s, base);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isBoolean(String s) {
		return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
	}

	public static String translateFormattings(String s) {
		for (Formatting f : Formatting.values())
			s = s.replaceAll("&" + f.toString().charAt(1), f.toString());
		return s;
	}

	public static boolean inRange(double d, double min, double max) {
		return d >= min && d <= max;
	}

	public static String getLookDirection(float pitch, float yaw) {
		String direction = "unknown";
		if (pitch <= -30) direction = "up";
		else if (pitch >= 30) direction = "down";
		else if (inRange(yaw, 0, 22.5D) || inRange(yaw, -22.5D, 0)) direction = "south";
		else if (inRange(yaw, 22.5D, 67.5D)) direction = "south-west";
		else if (inRange(yaw, 67.5D, 112.5D)) direction = "west";
		else if (inRange(yaw, 112.5D, 157.5D)) direction = "north-west";
		else if (inRange(yaw, 157.5D, 180D) || inRange(yaw, -180D, -157.5D)) direction = "north";
		else if (inRange(yaw, -157.5D, -112.5D)) direction = "north-east";
		else if (inRange(yaw, -112.5D, -67.5D)) direction = "east";
		else if (inRange(yaw, -67.5D, -22.5D)) direction = "south-east";
		return direction;
	}

	public static String parseTime(long gameTime, boolean muricanClock) {
		long hours = gameTime / 1000 + 6;
		long minutes = gameTime % 1000 * 60 / 1000;
		String ampm = "AM";
		if (muricanClock) {
			if (hours > 12) {
				hours -= 12;
				ampm = "PM";
			}
			if (hours > 12) {
				hours -= 12;
				ampm = "AM";
			}
			if (hours == 0) hours = 12;
		} else if (hours >= 24) hours -= 24;
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2);
		return hours + ":" + mm + (muricanClock ? " " + ampm : "");
	}

	public static String formatDouble(Double dbl) {
		String dblString = dbl.toString();
		String dblString1 = dblString.split("\\.").length == 2 ? dblString.split("\\.")[1] : "";
		while (dblString1.endsWith("0"))
			dblString1 = dblString1.substring(0, dblString1.length() - 1);
		dblString1 = dblString1.length() >= 5 ? dblString1.substring(0, 5) : dblString1;
		if (dblString1.equals("")) dblString = dblString.split("\\.")[0];
		else dblString = dblString.split("\\.")[0] + "." + dblString1;
		return dblString;
	}

	public static <S> LiteralCommandNode<S> createAlias(String alias, LiteralCommandNode<S> node) {
		LiteralCommandNode<S> node0 = new LiteralCommandNode<>(alias, node.getCommand(), node.getRequirement(), node, ctx -> Collections.singletonList(ctx.getSource()), false);
		node.getChildren().forEach(node0::addChild);
		return node0;
	}

	public static <T> T allocateInstance(Class<T> clazz) throws InstantiationException {
		return ReflectionHelper.cast(theUnsafe.allocateInstance(clazz));
	}

	public static void throwWithoutDeclaration(Throwable t) {
		theUnsafe.throwException(t);
	}

	public static void execute(Runnable runnable) {
		executor.execute(runnable);
	}

	public static String formatFileSize(long bytes) {
		double size = bytes;
		int count = 0;
		while (size >= 1024 && count < 4) {
			size /= 1024F;
			count += 1;
		}
		String s = sizeFormat.format(size);
		switch (count) {
			case 0:
				return s + " bytes";
			case 1:
				return s + " kilobytes";
			case 2:
				return s + " megabytes";
			case 3:
				return s + " gigabytes";
			default:
				return s + " terabytes";
		}
	}

	// Me
	public static boolean isCool(Entity entity) {
		return entity instanceof PlayerEntity && "1aa35f31-0881-4959-bd14-21e8a72ba0c1".equals(entity.getUuidAsString());
	}

	// My best friend :3
	public static boolean isCute(Entity entity) {
		return entity instanceof PlayerEntity && "b8760dc9-19fd-4d01-a5c7-25268a677deb".equals(entity.getUuidAsString());
	}

	public static CompoundTag getDefaultTag(EntityType<?> type) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", Registry.ENTITY_TYPE.getId(type).toString());
		return tag;
	}

	public static Entity summon(CompoundTag tag, ServerWorld world, Vec3d pos) {
		return EntityType.loadEntityWithPassengers(tag, world, (entityx) -> {
			entityx.refreshPositionAndAngles(pos.x, pos.y, pos.z, entityx.yaw, entityx.pitch);
			return !world.tryLoadEntity(entityx) ? null : entityx;
		});
	}

	public static Vec3d getRotationVector(Vec2f rotation) {
		return getRotationVector(rotation.x, rotation.y);
	}

	public static Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * 0.017453292F;
		float g = -yaw * 0.017453292F;
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d(i * j, -k, h * j);
	}

	public static boolean teleportSafely(Entity entity) {
		World world = entity.getEntityWorld();
		double x = entity.getPos().x;
		double y;
		double z = entity.getPos().z;
		boolean found = false;
		boolean blockAbove = world.isSkyVisible(entity.getBlockPos());
		if (!world.isClient) while (!found && !blockAbove) {
			for (y = entity.getPos().y + 1; y < entity.getEntityWorld().getHeight(); y += 1) {
				Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
				Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
				if (!blockBlacklist.contains(block) && blockWhitelist.contains(tpblock) && (blockAbove = world.isSkyVisible(new BlockPos(x, y, z)))) {
					entity.updatePosition(x + 0.5, y, z + 0.5);
					found = true;
					break;
				}
			}
			x -= 1;
			z -= 1;
		}
		return !blockAbove;
	}

	// Copied from SpreadPlayersCommand$Pile#getY(BlockView, int)
	public static int getY(BlockView blockView, double x, double z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, blockView.getHeight(), z);
		boolean bl = blockView.getBlockState(mutable).isAir();
		mutable.move(Direction.DOWN);
		boolean bl3;
		for (boolean bl2 = blockView.getBlockState(mutable).isAir(); mutable.getY() > 0; bl2 = bl3) {
			mutable.move(Direction.DOWN);
			bl3 = blockView.getBlockState(mutable).isAir();
			if (!bl3 && bl2 && bl) return mutable.getY() + 1;
			bl = bl2;
		}
		return blockView.getHeight();
	}

	public static CompoundTag wrapTag(String key, Tag tag) {
		CompoundTag compound = new CompoundTag();
		compound.put(key, tag);
		return compound;
	}

	public static String camelCase(String s, boolean retainSpaces) {
		s = pascalCase(s, retainSpaces);
		return s.isEmpty() ? s : s.length() == 1 ? s.toLowerCase() : s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String pascalCase(String s, boolean retainSpaces) {
		String[] parts = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (part.length() == 1) sb.append(part.toUpperCase());
			else sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
			if (retainSpaces) sb.append(' ');
		}
		return sb.toString().trim();
	}

	public static VoxelShape getFluidShape(BlockState state) {
		return VoxelShapes.cuboid(0, 0, 0, 1, 1d/1.125d/8*(8-state.get(FluidBlock.LEVEL)), 1);
	}

	public static String getRelativePath() {
		return getRelativePath(serverInstance);
	}
	
	public static String getRelativePath(MinecraftServer server) {
		return server.getSavePath(WorldSavePath.ROOT).toAbsolutePath().toString() + File.separator + "MoreCommands" + File.separator;
	}

	public static void tryMove(String from, String to) {
		try {
			Files.move(Paths.get(from), Paths.get(to));
		} catch (IOException e) {
			log.catching(e);
		}
	}

	public static boolean isSingleplayer() {
		return MinecraftClient.getInstance() != null && MinecraftClient.getInstance().getCurrentServerEntry() == null && MinecraftClient.getInstance().world != null;
	}

}