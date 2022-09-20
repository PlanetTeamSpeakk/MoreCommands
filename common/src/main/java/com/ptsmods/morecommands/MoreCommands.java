package com.ptsmods.morecommands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ptsmods.morecommands.api.*;
import com.ptsmods.morecommands.api.callbacks.PostInitEvent;
import com.ptsmods.morecommands.api.miscellaneous.FormattingColour;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.EmptyTextBuilder;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.api.util.text.TextBuilder;
import com.ptsmods.morecommands.api.util.text.TranslatableTextBuilder;
import com.ptsmods.morecommands.arguments.*;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.commands.server.elevated.ReachCommand;
import com.ptsmods.morecommands.compat.*;
import com.ptsmods.morecommands.compat.client.*;
import com.ptsmods.morecommands.miscellaneous.Chair;
import com.ptsmods.morecommands.miscellaneous.Command;
import com.ptsmods.morecommands.miscellaneous.MoreGameRules;
import com.ptsmods.morecommands.mixin.common.accessor.*;
import com.ptsmods.morecommands.util.DataTrackerHelper;
import com.ptsmods.morecommands.util.MixinAccessWidenerImpl;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.fabricmc.api.EnvType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum MoreCommands implements IMoreCommands {
    INSTANCE;

    public static final String MOD_ID = "morecommands";
    public static ChatFormatting DF = ClientOptions.Tweaks.defColour.getValue().asFormatting();
    public static ChatFormatting SF = ClientOptions.Tweaks.secColour.getValue().asFormatting();
    public static Style DS = Style.EMPTY.withColor(DF);
    public static Style SS = Style.EMPTY.withColor(SF);
    public static final boolean SERVER_ONLY;
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> argumentTypeRegistry;
    private static final DeferredRegister<SoundEvent> soundEventRegistry = DeferredRegister.create(MOD_ID, Registry.SOUND_EVENT_REGISTRY);
    private static final DeferredRegister<Block> blockRegistry = DeferredRegister.create(MOD_ID, Registry.BLOCK_REGISTRY);
    private static final DeferredRegister<Item> itemRegistry = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    private static final DeferredRegister<Attribute> attributeRegistry = DeferredRegister.create(MOD_ID, Registry.ATTRIBUTE_REGISTRY);
    public static final Set<Block> blockBlacklist = new HashSet<>();
    public static final Set<Block> blockWhitelist = new HashSet<>();
    public static final CreativeModeTab unobtainableItems = CreativeTabRegistry.create(new ResourceLocation("morecommands:unobtainable_items"),
        () -> new ItemStack(Registry.ITEM.get(new ResourceLocation("morecommands:locked_chest"))));
    public static MinecraftServer serverInstance = null;
    public static final ObjectiveCriteria LATENCY;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final Map<Player, String> discordTags = new WeakHashMap<>();
    public static final Set<Player> discordTagNoPerm = Collections.newSetFromMap(new WeakHashMap<>());
    public static final Map<String, DamageSource> DAMAGE_SOURCES = new HashMap<>();
    public static boolean creatingWorld = false;
    private static final Executor executor = Executors.newCachedThreadPool();
    private static final DecimalFormat sizeFormat = new DecimalFormat("#.###");
    private static final Map<String, Boolean> permissions = new LinkedHashMap<>();
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final Map<Command, Collection<CommandNode<CommandSourceStack>>> nodes = new LinkedHashMap<>();
    private static final Map<Player, Integer> targetedEntities = new HashMap<>();
    private static boolean registeredStuff = false;
    private static RegistrySupplier<SoundEvent> copySound, eeSound;
    private static RegistrySupplier<Attribute> reachAttribute, swimSpeedAttribute;

    static {
        Path serverOnlyFile = MoreCommandsArch.getConfigDirectory().resolve("SERVERONLY.txt");
        boolean serverOnly = false;
        if (Files.exists(serverOnlyFile)) {
            Properties props = new Properties();
            try {
                props.load(Files.newBufferedReader(serverOnlyFile));
                serverOnly = Boolean.parseBoolean(props.getProperty("serverOnly", "false"));
            } catch (IOException e) {
                LOG.error("Could not read server-only state.", e);
            }
        } else {
            Properties props = new Properties();
            props.setProperty("serverOnly", "false");

            try {
                Files.createDirectories(serverOnlyFile.getParent());
                PrintWriter writer = new PrintWriter(Files.newOutputStream(serverOnlyFile));
                props.store(writer, "https://morecommands.ptsmods.com/server-only");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                LOG.error("Could not save SERVERONLY.txt", e);
            }
        }
        SERVER_ONLY = serverOnly;

        if (Platform.getEnv() == EnvType.SERVER && SERVER_ONLY) {
            LOG.info("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            LOG.info("-=-RUNNING IN SERVER-ONLY MODE-=-");
            LOG.info("-=CLIENTS WILL NOT NEED THE MOD=-");
            LOG.info("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        }

        argumentTypeRegistry = Version.getCurrent().isNewerThanOrEqual(Version.V1_19) ? DeferredRegister.create(MOD_ID, Registry.COMMAND_ARGUMENT_TYPE_REGISTRY) : null;

        Holder.setMixinAccessWidener(new MixinAccessWidenerImpl());
        MixinScoreboardCriterionAccessor.getCriteria().put("latency", LATENCY = MixinScoreboardCriterionAccessor.newInstance("latency", true, ObjectiveCriteria.RenderType.INTEGER));
    }

    MoreCommands() {
        Holder.setMoreCommands(this);

        Holder.setCompat((Compat) determineCurrentCompat(false));
        if (Platform.getEnv() == EnvType.CLIENT) Holder.setClientCompat((ClientCompat) determineCurrentCompat(true));
    }

    public static void init() {
        INSTANCE.doInit();
    }

    @SneakyThrows
    private void doInit() {
        MixinFormattingAccessor.setStripFormattingPattern(Pattern.compile("(?i)\u00A7[0-9A-FK-ORU]")); // Adding the 'U' for the rainbow formatting.
        MoreGameRules.init();
        DataTrackerHelper.init();

        File dir = new File("config/MoreCommands");
        if (!dir.exists() && !dir.mkdirs()) throw new RuntimeException("Could not make config dir.");

        doCommandRegistration();

        PostInitEvent.EVENT.register(() -> {
            blockBlacklist.clear();
            blockWhitelist.clear();
            Registry.BLOCK.forEach(block -> {
                if (block instanceof LiquidBlock || !((MixinAbstractBlockAccessor) block).isHasCollision()) blockBlacklist.add(block);
                try {
                    VoxelShape shape = block.defaultBlockState().getCollisionShape(null, null);
                    if (shape.max(Direction.Axis.Y) > 16) blockBlacklist.add(block); // Will fall through if teleported on. (E.g. fences)
                    if (shape.min(Direction.Axis.Z) > 6 / 16d || shape.max(Direction.Axis.Z) < 10 / 16d || // Can't stand on this. (E.g. doors)
                            shape.min(Direction.Axis.X) > 6 / 16d || shape.max(Direction.Axis.X) < 10 / 16d) blockBlacklist.add(block);
                    if (shape.min(Direction.Axis.Y) == shape.max(Direction.Axis.Y)) blockBlacklist.add(block); // Will fall straight through.

                    if (!block.defaultBlockState().getMaterial().blocksMotion() || !Block.isShapeFullBlock(shape)) blockWhitelist.add(block); // Block does not cause suffocation.
                } catch (Exception ignored) {} // Getting collision shape probably requires a world and position which we don't have.

                try {
                    Method onEntityCollision = ReflectionHelper.getMappedMethod(block.getClass(), "onEntityCollision", "method_9548", "m_7892_",
                            BlockState.class, Level.class, BlockPos.class, Entity.class);
                    if (onEntityCollision != null && onEntityCollision.getDeclaringClass() != BlockBehaviour.class) blockBlacklist.add(block);

                    onEntityCollision = ReflectionHelper.getMappedMethod(block.defaultBlockState().getClass(), "onEntityCollision", "method_26178", "m_60682_",
                            Level.class, BlockPos.class, Entity.class);
                    if (onEntityCollision != null && onEntityCollision.getDeclaringClass() != BlockBehaviour.BlockStateBase.class) blockBlacklist.add(block);
                    // onEntityCollision method was overridden, block does something to entities on collision, assume it's malicious.
                } catch (Throwable ignored) {} // For some reason, this can throw NoClassDefFoundErrors.
            });
        });

        if (!isServerOnly()) {
            registerStuff();

            PlayerEvent.PLAYER_JOIN.register(player -> {
                if (NetworkManager.canPlayerReceive(player, new ResourceLocation("morecommands:formatting_update"))) sendFormattingUpdates(player);
            });

            PlayerEvent.PLAYER_QUIT.register(targetedEntities::remove);

            registerPacketReceivers();

            PostInitEvent.EVENT.register(() -> {
                NonNullList<ItemStack> defaultedList = NonNullList.create();
                for (Item item : Registry.ITEM) item.fillItemCategory(CreativeModeTab.TAB_SEARCH, defaultedList);

                for (Block block : Registry.BLOCK) {
                    ResourceLocation id = Registry.BLOCK.getKey(block);
                    if (!Compat.get().registryContainsId(Registry.ITEM, id)) Registry.register(Registry.ITEM,
                            new ResourceLocation(id.getNamespace(), "mcsynthetic_" + id.getPath()), new BlockItem(block, new Item.Properties()));
                }

                for (Item item : Registry.ITEM)
                    if (item.getItemCategory() == null) ((MixinItemAccessor) item).setCategory(unobtainableItems);

                defaultedList = NonNullList.create();
                for (Item item : Registry.ITEM) item.fillItemCategory(CreativeModeTab.TAB_SEARCH, defaultedList);
            });
        } else {
            InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
                Level world = player.level;
                BlockState state = world.getBlockState(pos);
                if (!player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && MoreGameRules.get().checkBooleanWithPerm(world.getGameRules(), MoreGameRules.get().doChairsRule(), player) &&
                        Compat.get().tagContains(new ResourceLocation("minecraft:stairs"), state.getBlock()) && Chair.isValid(state)) {
                    Chair.createAndPlace(pos, player, world);
                    return EventResult.interruptTrue();
                }
                return EventResult.pass();
            });
        }

        Set<ServerPlayer> howlingPlayers = new HashSet<>();
        TickEvent.SERVER_PRE.register(server -> {
            // This does absolutely nothing whatsoever, just pass along. :)
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                if (!p.isShiftKeyDown()) {
                    howlingPlayers.remove(p);
                    continue;
                }

                float pitch = Mth.wrapDegrees(((MixinEntityAccessor) p).getXRot_());
                float yaw = Mth.wrapDegrees(((MixinEntityAccessor) p).getYRot_());
                double moonWidth = Math.PI / 32 * -pitch;
                long dayTime = p.getLevel().getGameTime() % 24000; // getTimeOfDay() does not return a value between 0 and 24000 when using the /time add command.
                if (howlingPlayers.contains(p) || getMoonPhase(p.getLevel().dayTime()) != 0 || dayTime <= 12000 || !(pitch < 0) ||
                        !(Math.abs(yaw) >= (90 - moonWidth)) || !(Math.abs(yaw) <= (90 + moonWidth)))
                    continue;

                double moonPitch = -90 + Math.abs(dayTime - 18000) * 0.0175;
                if (pitch >= moonPitch-3 && pitch <= moonPitch+3) {
                    p.getLevel().playSound(null, p.blockPosition(), SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 1f, 1f);
                    howlingPlayers.add(p);
                }
            }
        });

        if (Version.getCurrent().isNewerThanOrEqual(Version.V1_19_2))
            ChatEvent.DECORATE.register((player, component) -> component.set(Component.literal(translateFormattings(INSTANCE.textToString(component.get())))));

//        if (MoreCommandsArch.isFabricModLoaded("placeholder-api")) {
//            // Example: %morecommands:reach/1%
//            PlaceholderAPI.register(new Identifier("morecommands:reach"), ctx -> ctx.getPlayerOrThrow() == null ? PlaceholderResult.invalid("A player must be passed.") :
//                    PlaceholderResult.value(new DecimalFormat("0" + ("0".equals(ctx.getArgument()) ? "" :
//                            "." + multiplyString("#", isInteger(ctx.getArgument()) ? Integer.parseInt(ctx.getArgument()) : 2))).format(ReachCommand.getReach(ctx.getPlayerOrThrow(), false))));
//
//            // Example: %morecommands:gradient/#FFAA00-#FF0000;This text will be a gradient from orange to red.%, %morecommands:gradient/6-c;This text too will be a gradient from orange to red.%
//            PlaceholderAPI.register(new Identifier("morecommands:gradient"), MoreCommands::gradientPlaceholder);
//        }
    }

    public static void registerStuff() {
        if (INSTANCE.isServerOnly() || registeredStuff) return;

        ResourceLocation lockedChestLocation = new ResourceLocation("morecommands:locked_chest");
        copySound = soundEventRegistry.register(new ResourceLocation("morecommands:copy"),
                () -> new SoundEvent(new ResourceLocation("morecommands:copy")));
        eeSound = soundEventRegistry.register(new ResourceLocation("morecommands:ee"),
                () -> new SoundEvent(new ResourceLocation("morecommands:ee")));
        RegistrySupplier<Block> lockedChest = blockRegistry.register(lockedChestLocation,
                () -> new Block(BlockBehaviour.Properties.of(Material.WOOD)));
        itemRegistry.register(lockedChestLocation, () -> new BlockItem(lockedChest.get(), new Item.Properties()));
        itemRegistry.register(new ResourceLocation("minecraft:nether_portal"), () -> new BlockItem(Blocks.NETHER_PORTAL, new Item.Properties().fireResistant()));
        reachAttribute = attributeRegistry.register(new ResourceLocation("morecommands:reach"), () ->
                new RangedAttribute("attribute.morecommands.reach", 4.5d, 1d, 160d).setSyncable(true));
        swimSpeedAttribute = attributeRegistry.register(new ResourceLocation("morecommands:swim_speed"), () ->
                new RangedAttribute("attribute.morecommands.swim_speed", 1f, 0f, Float.MAX_VALUE).setSyncable(true));

        soundEventRegistry.register();
        blockRegistry.register();
        itemRegistry.register();
        attributeRegistry.register();

        Compat compat = Compat.get();

        compat.registerArgumentType(argumentTypeRegistry, "morecommands:enum_argument", EnumArgumentType.class, EnumArgumentType.SERIALISER);
        compat.registerArgumentType(argumentTypeRegistry, "morecommands:cramped_string", CrampedStringArgumentType.class, CrampedStringArgumentType.SERIALISER);
        compat.registerArgumentType(argumentTypeRegistry, "morecommands:time_argument", TimeArgumentType.class, TimeArgumentType.SERIALISER);
        compat.registerArgumentType(argumentTypeRegistry, "morecommands:hexinteger", HexIntegerArgumentType.class, HexIntegerArgumentType.SERIALISER);
        compat.registerArgumentType(argumentTypeRegistry, "morecommands:ignorant_string", IgnorantStringArgumentType.class, IgnorantStringArgumentType.SERIALISER);
        compat.registerArgumentType(argumentTypeRegistry, "morecommands:painting_variant", PaintingVariantArgumentType.class, PaintingVariantArgumentType.SERIALISER);
        compat.registerArgumentType(argumentTypeRegistry, "morecommands:potion", PotionArgumentType.class, PotionArgumentType.SERIALISER);

        if (argumentTypeRegistry != null) argumentTypeRegistry.register();
        registeredStuff = true;
    }

    static <T> List<Class<? extends T>> getCommandClasses(String type, Class<T> clazz) {
        long start = System.currentTimeMillis();
        List<Class<? extends T>> classes = new ArrayList<>();
        Path jar = MoreCommandsArch.getJar();
        if (jar == null) {
            LOG.error("Could not find the jarfile of the mod, no commands will be registered.");
            return classes;
        }

        try {
            classes = ReflectionHelper.getClasses(clazz, "com.ptsmods.morecommands.commands." + type);
            LOG.info("Found " + classes.size() + " commands to load for type " + type + ". Took " + (System.currentTimeMillis() - start));
        } catch (IOException e) {
            LOG.error("Couldn't find commands for type " + type + ". This means none of said type will be loaded.", e);
        }

        return classes;
    }

     private static void doCommandRegistration() {
         List<Command> serverCommands = getCommandClasses("server", Command.class).stream()
                .map(MoreCommands::getInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

         Map<Command, Collection<CommandNode<CommandSourceStack>>> nodes = new LinkedHashMap<>();

         // Lomboks @Helper straight up doesn't work.
         // Complaining about it only being legal on method-local classes even though this is one.
         class CommandRegisterer {
             void registerCommand(Command cmd, boolean dedicated, CommandDispatcher<CommandSourceStack> dispatcher, boolean dryRun) {
                 Collection<String> registeredNodes = cmd.getRegisteredNodes();

                 // Some commands cannot yet be registered at this stage
                 // E.g. commands using CommandRegistryAccess
                 if (dryRun && registeredNodes != null) {
                     nodes.put(cmd, registeredNodes.stream()
                             .map(node -> Command.literal(node).build())
                             .collect(ImmutableList.toImmutableList()));
                     return;
                 }

                 try {
                     CommandDispatcher<CommandSourceStack> tempDispatcher = new CommandDispatcher<>();
                     cmd.register(tempDispatcher, dedicated);

                     for (CommandNode<CommandSourceStack> child : tempDispatcher.getRoot().getChildren()) dispatcher.getRoot().addChild(child);
                     nodes.put(cmd, ImmutableList.copyOf(tempDispatcher.getRoot().getChildren()));
                     permissions.putAll(cmd.getExtraPermissions());
                 } catch (Exception e) {
                     LOG.error("Could not register command " + cmd.getClass().getName() + ".", e);
                 }
             }
         }

         CommandRegisterer registerer = new CommandRegisterer();
         CommandDispatcher<CommandSourceStack> nilDispatcher = new CommandDispatcher<>();

         serverCommands.stream()
                 .filter(cmd -> !cmd.doLateInit())
                 .forEach(cmd -> registerer.registerCommand(cmd, Platform.getEnv() == EnvType.SERVER, nilDispatcher, true));

         // Only registering them in CommandRegistrationEvent is too late for the docs command.
         MoreCommands.nodes.putAll(nodes);

         Compat.get().registerCommandRegistrationEventListener((dispatcher, environment) -> {
             boolean dedicated = environment == Commands.CommandSelection.DEDICATED;
             serverCommands.stream()
                     .filter(cmd -> (!cmd.isDedicatedOnly() || dedicated) && !cmd.doLateInit())
                     .forEach(cmd -> registerer.registerCommand(cmd, dedicated, dispatcher, false));

             serverCommands.stream()
                     .filter(cmd -> (!cmd.isDedicatedOnly() || dedicated) && cmd.doLateInit())
                     .forEach(cmd -> registerer.registerCommand(cmd, dedicated, dispatcher, false));
         });
    }

    private static void registerPacketReceivers() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, new ResourceLocation("morecommands:sit_on_stairs"), (buf, context) -> {
            BlockPos pos = buf.readBlockPos();
            Player player = context.getPlayer();

            if (player.position().distanceToSqr(new Vec3(pos.getX(), pos.getY(), pos.getZ())) > ReachCommand.getReach(player, true))
                return; // Out of reach

            BlockState state = player.getLevel().getBlockState(pos);
            if (MoreGameRules.get().checkBooleanWithPerm(player.getLevel().getGameRules(), MoreGameRules.get().doChairsRule(), player) && Chair.isValid(state))
                Chair.createAndPlace(pos, player, player.getLevel());
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, new ResourceLocation("morecommands:discord_data"), (buf, context) -> {
            Player player = context.getPlayer();
            if (buf.readBoolean()) discordTagNoPerm.add(player);
            else discordTagNoPerm.remove(player);
            String tag = buf.readUtf();

            if (tag.contains("#") && tag.lastIndexOf('#') == tag.length() - 5 && isInteger(tag.substring(tag.lastIndexOf('#') + 1)))
                discordTags.put(player, tag);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, new ResourceLocation("morecommands:entity_target_update"), (buf, context) -> {
            int targetEntity = buf.readVarInt();
            targetedEntities.put(context.getPlayer(), targetEntity);
        });
    }

    @Override
    public ChatFormatting getDefaultFormatting() {
        return DF;
    }

    @Override
    public ChatFormatting getSecondaryFormatting() {
        return SF;
    }

    public static void setFormattings(ChatFormatting def, ChatFormatting sec) {
        if (def != null) {
            DF = Command.DF = def;
            DS = Command.DS = DS.withColor(DF);
        }

        if (sec != null) {
            SF = Command.SF = sec;
            SS = Command.SS = SS.withColor(SF);
        }
    }

    public static void updateFormatting(MinecraftServer server, int id, FormattingColour value) {
        if (IMoreCommands.get().isServerOnly()) return;

        switch (id) {
            case 0:
                value = value == null ? server.getGameRules().getRule(MoreGameRules.get().DFrule()).get() : value;
                if (value.asFormatting() != DF) {
                    setFormattings(value.asFormatting(), null);
                    sendFormattingUpdate(server, 0, DF);
                }
                break;
            case 1:
                value = value == null ? server.getGameRules().getRule(MoreGameRules.get().SFrule()).get() : value;
                if (value.asFormatting() != SF) {
                    setFormattings(null, value.asFormatting());
                    sendFormattingUpdate(server, 1, SF);
                }
                break;
        }
    }

    public static void sendFormattingUpdates(ServerPlayer p) {
        if (IMoreCommands.get().isServerOnly()) return;
        sendFormattingUpdate(p, 0, DF);
        sendFormattingUpdate(p, 1, SF);
    }

    private static void sendFormattingUpdate(MinecraftServer server, int id, ChatFormatting colour) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer().writeByte(id).writeByte(colour.getId()));
        for (ServerPlayer p : server.getPlayerList().getPlayers())
            sendFormattingUpdate(p, buf);
    }

    private static void sendFormattingUpdate(ServerPlayer p, int id, ChatFormatting colour) {
        sendFormattingUpdate(p, new FriendlyByteBuf(Unpooled.buffer().writeByte(id).writeByte(Arrays.binarySearch(FormattingColour.values(), FormattingColour.valueOf(colour.name())))));
    }

    private static void sendFormattingUpdate(ServerPlayer p, FriendlyByteBuf buf) {
        if (NetworkManager.canPlayerReceive(p, new ResourceLocation("morecommands:formatting_update"))) NetworkManager.sendToPlayer(p, new ResourceLocation("morecommands:formatting_update"), buf);
    }

    static <T extends Command> T getInstance(Class<T> cmd) {
        T instance;
        try {
            Constructor<T> con = cmd.getDeclaredConstructor();
            con.setAccessible(true);
            instance = con.newInstance();
        } catch (Exception e) {
            LOG.error("Could not instantiate command class " + cmd.getName() + ".", e);
            return null;
        }
        instance.setActiveInstance();
        try {
            instance.preinit(IMoreCommands.get().isServerOnly());
        } catch (Exception e) {
            LOG.error("Error invoking pre-initialisation method on class " + cmd.getName() + ".", e);
        }
        return ReflectionHelper.cast(instance);
    }

    public String textToString(Component text, Style parentStyle, boolean includeFormattings) {
        return textToString(text, parentStyle, Platform.getEnv() == EnvType.CLIENT, includeFormattings);
    }

    public String textToString(Component text, Style parentStyle, boolean translate, boolean includeFormattings) {
        if (parentStyle == null) parentStyle = Style.EMPTY;

        TextBuilder<?> builder = Compat.get().builderFromText(text);
        StringBuilder s = new StringBuilder();
        Style style = text.getStyle();
        style = (style == null ? Style.EMPTY : style).applyTo(parentStyle);

        if (includeFormattings) {
            TextColor c = style.getColor();

            if (c != null) {
                int rgb = ((MixinTextColorAccessor) (Object) c).getValue_();
                ChatFormatting f = null;

                for (ChatFormatting form : ChatFormatting.values())
                    if (form.getColor() != null && form.getColor().equals(rgb)) {
                        f = form;
                        break;
                    }

                if (f != null) s.append(f);
                else {
                    Color colour = new Color(rgb);
                    s.append("\u00A7").append(String.format("#%02x%02x%02x", colour.getRed(), colour.getGreen(), colour.getBlue()));
                }
            }

            if (style.isBold()) s.append(ChatFormatting.BOLD);
            if (style.isStrikethrough()) s.append(ChatFormatting.STRIKETHROUGH);
            if (style.isUnderlined()) s.append(ChatFormatting.UNDERLINE);
            if (style.isItalic()) s.append(ChatFormatting.ITALIC);
            if (style.isObfuscated()) s.append(ChatFormatting.OBFUSCATED);
        }

        if (builder instanceof TranslatableTextBuilder && translate) {
            TranslatableTextBuilder tt = (TranslatableTextBuilder) builder;
            Object[] args = new Object[tt.getArgs().length];

            for (int i = 0; i < args.length; i++) {
                Object arg = tt.getArgs()[i];
                if (arg instanceof Component || arg instanceof TextBuilder)
                    args[i] = textToString(arg instanceof Component ? (Component) arg : ((TextBuilder<?>) arg).build(), style, true, includeFormattings);
                else args[i] = arg;
            }

            s.append(I18n.get(tt.getKey(), Arrays.stream(args)
                    .map(o -> o instanceof TextBuilder ? ((TextBuilder<?>) o).build() : o)
                    .collect(Collectors.toList())));
        } else s.append(builder instanceof EmptyTextBuilder ? "" :
                builder instanceof LiteralTextBuilder ? ((LiteralTextBuilder) builder).getLiteral() :
                builder instanceof TranslatableTextBuilder ? ((TranslatableTextBuilder) builder).getKey() :
                        text);

        if (!text.getSiblings().isEmpty())
            for (Component t : text.getSiblings())
                s.append(textToString(t, style, translate, includeFormattings));

        return s.toString();
    }

    public static String stripFormattings(String s) {
        return Objects.requireNonNull(ChatFormatting.stripFormatting(s)).replaceAll("\u00A7#[0-9A-Fa-f]{6}", "");
    }

    public static void saveJson(File f, Object data) throws IOException {
        saveString(f, gson.toJson(data));
    }

    public static void saveJson(Path p, Object data) throws IOException {
        saveString(p, gson.toJson(data));
    }

    public static <T> T readJson(File f) throws IOException {
        return gson.fromJson(readString(f), new TypeToken<T>(){}.getType());
    }

    public static <T> T readJson(Path p) throws IOException {
        return gson.fromJson(readString(p), new TypeToken<T>(){}.getType());
    }

    public static void saveString(File f, String s) throws IOException {
        saveString(f.toPath(), s);
    }

    public static void saveString(Path p, String s) throws IOException {
        createFileAndDirectories(p);
        try (PrintWriter writer = new PrintWriter(p.toString(), "UTF-8")) {
            writer.print(s);
            writer.flush();
        }
    }

    public static String readString(File f) throws IOException {
        return readString(f.toPath());
    }

    public static String readString(Path p) throws IOException {
        createFileAndDirectories(p);
        return String.join("\n", Files.readAllLines(p));
    }

    private static void createFileAndDirectories(Path p) throws IOException {
        p = p.toAbsolutePath();
        if (!Files.exists(p.getParent())) Files.createDirectories(p.getParent());
        if (!Files.exists(p)) Files.createFile(p);
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
     * @param str The equation to solve.
     * @return The answer to the equation.
     * @author Boann (<a href="https://stackoverflow.com/a/26227947">https://stackoverflow.com/a/26227947</a>)
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
    public static HitResult getRayTraceTarget(Entity entity, double reach, boolean ignoreEntities, boolean ignoreLiquids) {
        if (entity == null) return null;

        float td = Platform.getEnv() == EnvType.CLIENT ? Minecraft.getInstance().getFrameTime() : 1f;
        HitResult crosshairTarget = entity.pick(reach, td, !ignoreLiquids);
        if (ignoreEntities) return crosshairTarget;

        EntityHitResult entityHit = getEntityRayTraceTarget(entity, reach);
        if (entityHit != null) crosshairTarget = entityHit;
        return crosshairTarget;
    }

    public static EntityHitResult getEntityRayTraceTarget(Entity entity, double reach) {
        float td = Platform.getEnv() == EnvType.CLIENT ? Minecraft.getInstance().getFrameTime() : 1f;
        Vec3 vec3d = entity.getEyePosition(td);
        double e = reach;
        e *= e;
        Vec3 vec3d2 = entity.getViewVector(td);
        Vec3 vec3d3 = vec3d.add(vec3d2.x * reach, vec3d2.y * reach, vec3d2.z * reach);
        AABB box = entity.getBoundingBox().expandTowards(vec3d2.scale(reach)).inflate(1.0D, 1.0D, 1.0D);
        return ProjectileUtil.getEntityHitResult(entity, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.isPickable(), e);
    }

    public static Entity cloneEntity(Entity entity, boolean summon) {
        CompoundTag nbt = new CompoundTag();
        entity.saveAsPassenger(nbt);
        Entity e = EntityType.loadEntityRecursive(nbt, entity.getCommandSenderWorld(), e0 -> {
            e0.moveTo(entity.getX(), entity.getY(), entity.getZ(), ((MixinEntityAccessor) entity).getYRot_(), ((MixinEntityAccessor) entity).getXRot_());
            return e0;
        });
        if (e != null) {
            e.setUUID(UUID.randomUUID());
            if (summon) entity.getCommandSenderWorld().addFreshEntity(e);
        }
        return e;
    }

    public static String readTillSpaceOrEnd(StringReader reader) {
        StringBuilder s = new StringBuilder();
        while (reader.getRemainingLength() > 0 && reader.peek() != ' ')
            s.append(reader.read());
        return s.toString();
    }

    public static void teleport(Entity target, ServerLevel world, Vec3 pos, float yaw, float pitch) {
        teleport(target, world, pos.x, pos.y, pos.z, yaw, pitch);
    }

    // Blatantly copied from TeleportCommand#teleport.
    public static void teleport(Entity target, ServerLevel world, double x, double y, double z, float yaw, float pitch) {
        if (target instanceof ServerPlayer) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 1, target.getId());
            target.stopRiding();
            if (((ServerPlayer) target).isSleeping()) ((ServerPlayer) target).stopSleepInBed(true, true);
            if (world == target.level)
                ((ServerPlayer) target).connection.teleport(x, y, z, yaw, pitch, Collections.emptySet());
            else ((ServerPlayer) target).teleportTo(world, x, y, z, yaw, pitch);
            target.setYHeadRot(yaw);
        } else {
            float f = Mth.wrapDegrees(yaw);
            float g = Mth.wrapDegrees(pitch);
            g = Mth.clamp(g, -90.0F, 90.0F);
            if (world == target.level) {
                target.moveTo(x, y, z, f, g);
                target.setYHeadRot(f);
            } else {
                target.unRide();
                Entity entity = target;
                target = target.getType().create(world);
                if (target == null) return;
                target.restoreFrom(entity);
                target.moveTo(x, y, z, f, g);
                target.setYHeadRot(f);
                world.addDuringTeleport(target);
                Compat.get().setRemoved(entity, 4); // CHANGED_DIMENSION
            }
        }
        if (!(target instanceof LivingEntity) || !((LivingEntity) target).isFallFlying()) {
            target.setDeltaMovement(target.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            target.setOnGround(true);
        }
        if (target instanceof PathfinderMob) ((PathfinderMob) target).getNavigation().stop();
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
        URL url0 = new URL(url);
        URLConnection connection = url0.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36 OPR/81.0.4196.61");
        return readInputStream(connection.getInputStream());
    }

    public static String readInputStream(InputStream stream) throws IOException {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(stream))) {
            return rd.lines().collect(Collectors.joining("\n"));
        }
    }

    @SneakyThrows // Very unlikely connecting to an SQLite database results in an error.
    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
        Command.doInitialisations(server);

        File file = MoreCommandsArch.getConfigDirectory().resolve("SERVERONLY.txt").toFile();

        try {
            if (server.isDedicatedServer() && (!file.exists() || Files.readAllLines(file.toPath()).isEmpty())) {
                Properties props = new Properties();
                props.setProperty("serverOnly", "" + file.exists());

                props.store(new PrintWriter(file), "Set the below value to true to enable server-only mode for MoreCommands.\n" +
                        "Clients will not need the mod to join the server when enabled.");
            }
        } catch (IOException e) {
            LOG.catching(e);
        }

        MoreGameRules.get().checkPerms(server);
        LOG.info("MoreCommands data path: " + getRelativePath(server));
    }

    public static boolean isAprilFirst() {
        return Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s == null) return false;
        if (s.startsWith("-") || s.startsWith("+")) s = s.substring(1);
        if (s.isEmpty()) return false;

        for (char c : s.toCharArray()) if (Character.digit(c, radix) < 0) return false;
        return true;
    }

    public static String translateFormattings(String s) {
        for (ChatFormatting f : ChatFormatting.values())
            s = s.replaceAll("&" + f.toString().charAt(1), f.toString());
        return s;
    }

    public static boolean inRange(double d, double min, double max) {
        return d >= min && d <= max;
    }

    public static String getLookDirection(float yaw, float pitch) {
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

    public static String formatDouble(double dbl, int decimals) {
        return new DecimalFormat("0." + multiplyString("#", decimals), new DecimalFormatSymbols(Locale.ROOT)).format(dbl);
    }

    public static <S> LiteralCommandNode<S> createAlias(String alias, LiteralCommandNode<S> node) {
        LiteralCommandNode<S> node0 = new LiteralCommandNode<>(alias, node.getCommand(), node.getRequirement(), node, ctx -> Collections.singletonList(ctx.getSource()), false);
        node.getChildren().forEach(node0::addChild);
        return node0;
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
        return entity instanceof Player && ("1aa35f31-0881-4959-bd14-21e8a72ba0c1".equals(entity.getStringUUID()) ||
                Platform.isDevelopmentEnvironment()) && (Minecraft.getInstance().player == null ||
                entity.getUUID().equals(Minecraft.getInstance().player.getUUID()));
    }

    // My best friend :3
    public static boolean isCute(Entity entity) {
        return entity instanceof Player && "b8760dc9-19fd-4d01-a5c7-25268a677deb".equals(entity.getStringUUID());
    }

    public static CompoundTag getDefaultTag(EntityType<?> type) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", Registry.ENTITY_TYPE.getKey(type).toString());
        return tag;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Entity summon(CompoundTag tag, ServerLevel world, Vec3 pos) {
        return EntityType.loadEntityRecursive(tag, world, (entityx) -> {
            entityx.moveTo(pos.x, pos.y, pos.z, ((MixinEntityAccessor) entityx).getYRot_(), ((MixinEntityAccessor) entityx).getXRot_());
            return !world.addWithUUID(entityx) ? null : entityx;
        });
    }

    public static Vec3 getRotationVector(Vec2 rotation) {
        return getRotationVector(rotation.x, rotation.y);
    }

    public static Vec3 getRotationVector(float pitch, float yaw) {
        float pitchRad = pitch * 0.017453292F;
        float yawRad = -yaw * 0.017453292F;
        float h = Mth.cos(yawRad);
        float i = Mth.sin(yawRad);
        float j = Mth.cos(pitchRad);
        float k = Mth.sin(pitchRad);
        return new Vec3(i * j, -k, h * j);
    }

    public static boolean teleportSafely(Entity entity) {
        Level world = entity.getCommandSenderWorld();
        double x = entity.position().x;
        double y;
        double z = entity.position().z;
        boolean found = false;
        boolean blockAbove = world.canSeeSky(entity.blockPosition());
        if (!world.isClientSide) while (!found && !blockAbove) {
            for (y = entity.position().y + 1; y < entity.getCommandSenderWorld().getHeight(); y += 1) {
                Block block = world.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
                Block tpblock = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!blockBlacklist.contains(block) && blockWhitelist.contains(tpblock) && (blockAbove = world.canSeeSky(new BlockPos(x, y, z)))) {
                    entity.absMoveTo(x + 0.5, y, z + 0.5);
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
    public static int getY(BlockGetter blockView, double x, double z) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, Compat.get().getWorldHeight(blockView), z);
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
        return Shapes.box(0, 0, 0, 1, 1d/1.125d/8 * (8-state.getValue(LiquidBlock.LEVEL)), 1);
    }

    public static Path getRelativePath() {
        return getRelativePath(serverInstance);
    }

    public static Path getRelativePath(MinecraftServer server) {
        return Paths.get(server.getWorldPath(LevelResource.ROOT).toAbsolutePath() + "MoreCommands");
    }

    public static void tryMove(String from, String to) {
        try {
            Files.move(Paths.get(from), Paths.get(to));
        } catch (IOException e) {
            LOG.catching(e);
        }
    }

    public static boolean isSingleplayer() {
        return Platform.getEnv() != EnvType.SERVER && Minecraft.getInstance() != null &&
                Minecraft.getInstance().getCurrentServer() == null && Minecraft.getInstance().level != null;
    }

    public static String formatSeconds(long seconds, ChatFormatting mainColour, ChatFormatting commaColour) {
        long days = seconds / 86400;
        long hours = seconds / 3600 - days * 24;
        long minutes = seconds / 60 - hours * 60 - days * 1440;
        seconds = seconds % 60;
        StringBuilder sb = new StringBuilder(mainColour.toString());
        if (days > 0) sb.append(days).append(" day").append(days == 1 ? "" : "s");
        if (hours > 0) sb.append(sb.length() == 2 ? "" : commaColour + ", " + mainColour).append(hours).append(" hour").append(hours == 1 ? "" : "s");
        if (minutes > 0) sb.append(sb.length() == 2 ? "" : commaColour + ", " + mainColour).append(minutes).append(" minute").append(minutes == 1 ? "" : "s");
        if (seconds > 0) sb.append(sb.length() == 2 ? "" : commaColour + ", " + mainColour).append(seconds).append(" seconds").append(seconds == 1 ? "" : "s");
        String s = sb.toString();
        if (s.contains(",")) s = s.substring(0, s.lastIndexOf(',')) + " and" + s.substring(s.lastIndexOf(',') + 1);
        return s;
    }

    public boolean isServerOnly() {
        return Platform.getEnv() == EnvType.SERVER && SERVER_ONLY;
    }

    @Override
    public MinecraftServer getServer() {
        return serverInstance;
    }

    @Override
    public Path getConfigDirectory() {
        return MoreCommandsArch.getConfigDirectory();
    }

    @Override
    public boolean isCreatingWorld() {
        return creatingWorld;
    }

    @Override
    public void setCreatingWorld(boolean creatingWorld) {
        MoreCommands.creatingWorld = creatingWorld;
    }

    // Following 2 methods are from Apache Commons Codec.
    public static String encodeHex(byte[] data) {
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_DIGITS[0x0F & data[i]];
        }

        return new String(out);
    }

    @Override
    public Path getJar() {
        return MoreCommandsArch.getJar();
    }

    @Override
    public RegistrySupplier<SoundEvent> getCopySound() {
        return copySound;
    }

    @Override
    public RegistrySupplier<SoundEvent> getEESound() {
        return eeSound;
    }

    @Override
    public RegistrySupplier<Attribute> getReachAttribute() {
        return reachAttribute;
    }

    @Override
    public RegistrySupplier<Attribute> getSwimSpeedAttribute() {
        return swimSpeedAttribute;
    }

    public static byte[] decodeHex(String data) {
        char[] dataChars = data.toCharArray();
        byte[] out = new byte[dataChars.length >> 1];

        for (int i = 0, j = 0; j < dataChars.length; i++) {
            int f = Character.digit(dataChars[j], 16) << 4;
            j++;
            f = f | Character.digit(dataChars[j], 16);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    public static String multiplyString(String s, int x) {
        if (s == null || s.isEmpty()) return s;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < x; i++)
            result.append(s);

        return result.toString();
    }

    public static List<Color> createGradient(int steps, Color from, Color to) {
        if (steps == 0) return Lists.newArrayList();
        BufferedImage img = new BufferedImage(steps, 1, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = img.createGraphics();
        g.setPaint(new GradientPaint(0, 0, from, steps, 1, to));
        g.fillRect(0, 0, steps, 1);
        g.dispose();

        List<Color> gradient = new ArrayList<>();
        for (int i = 0; i < steps; i++)
            gradient.add(new Color(img.getRGB(i, 0)));
        return Collections.unmodifiableList(gradient);
    }

    public static Component getNickname(Player player) {
        return player.getEntityData().get(IDataTrackerHelper.get().nickname()).orElse(null);
    }

//    public static PlaceholderResult gradientPlaceholder(PlaceholderContext ctx) {
//        String arg = ctx.getArgument();
//        String[] parts = arg.split(";", 2);
//
//        if (arg.isEmpty() || parts.length != 2 || !parts[0].contains("-")) return PlaceholderResult.value("Invalid format. Valid example: %morecommands:gradient/#FFAA00-#FF0000;Text here%");
//
//        String fromS = parts[0].substring(0, parts[0].indexOf('-'));
//        String toS = parts[0].substring(parts[0].indexOf('-') + 1, parts[0].contains(",") ? parts[0].indexOf(',') : parts[0].length());
//        String formatting = parts[0].contains(",") ? parts[0].substring(parts[0].indexOf(',') + 1) : "";
//
//        char[] chs = formatting.toCharArray();
//        Character[] chsI = new Character[chs.length];
//
//        for (int i = 0; i < chs.length; i++)
//            chsI[i] = chs[i];
//
//        // Turns any formatting, like lm or lo, into their actual string representation.
//        formatting = Arrays.stream(chsI)
//                .map(c -> "" + Character.toLowerCase(c))
//                .filter("klmno"::contains)
//                .map(c -> "\u00A7" + c)
//                .collect(Collectors.joining());
//
//        Function<String, Boolean> checkColour = c -> c.length() == 1 ? Character.isDigit(c.charAt(0)) || Character.toLowerCase(c.charAt(0)) >= 'a' && Character.toLowerCase(c.charAt(0)) <= 'f' :
//                c.length() == 7 && c.charAt(0) == '#' && isInteger(c.substring(1), 16);
//
//        // Checking if the colours are actual colours.
//        if (!checkColour.apply(fromS))
//            return PlaceholderResult.value("First colour must be between 0-9 or a-f or a hex colour.");
//        if (!checkColour.apply(toS))
//            return PlaceholderResult.value("Second colour must be between 0-9 or a-f or a hex colour.");
//
//        // Parse colours and create gradient.
//        Color from = new Color(fromS.length() == 1 ? Objects.requireNonNull(TextColor.fromFormatting(Formatting.byCode(fromS.charAt(0)))).getRgb() : Integer.parseInt(fromS.substring(1), 16));
//        Color to = new Color(toS.length() == 1 ? Objects.requireNonNull(TextColor.fromFormatting(Formatting.byCode(toS.charAt(0)))).getRgb() : Integer.parseInt(toS.substring(1), 16));
//        String content = parts[1];
//        List<Color> gradient = createGradient(content.length(), from, to);
//
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < content.length(); i++) {
//            Color c = gradient.get(i);
//
//            result.append("\u00a7#")
//                    .append(String.format("%02x", c.getRed()))
//                    .append(String.format("%02x", c.getGreen()))
//                    .append(String.format("%02x", c.getBlue()))
//                    .append(formatting)
//                    .append(content.charAt(i));
//        }
//
//        return PlaceholderResult.value(result.toString());
//    }

    public static int getMoonPhase(long time) {
        return (int)(time / 24000L % 8L + 8L) % 8;
    }

    public static Map<String, Boolean> getPermissions() {
        return ImmutableMap.copyOf(permissions);
    }

    public static void registerPermission(String permission, boolean defaultValue) {
        permissions.put(permission.toLowerCase(Locale.ROOT), defaultValue);
    }

    private static Object determineCurrentCompat(boolean client) {
        Object compat = determineCurrentCompat0(client);
        LOG.info("Determined " + (client ? "client " : "") + "compat: " + compat.getClass().getSimpleName());

        return compat;
    }

    private static Object determineCurrentCompat0(boolean client) {
        Version v = Version.getCurrent();
        int minor = v.minor, rev = v.revision == null ? -1 : v.revision;

        if (!client) switch (minor) {
            case 16:
                return new Compat16();
            case 17:
                return new Compat17();
            case 18:
                return rev >= 2 ? new Compat182() : new Compat18();
            case 19:
            default:
                return rev >= 2 ? new Compat192() : rev == 1 ? new Compat191() : new Compat19();
        }
        else switch (minor) {
            case 16:
                return new ClientCompat16();
            case 17:
            case 18:
                return new ClientCompat17();
            case 19:
            default:
                return rev >= 2 ? new ClientCompat192() : rev == 1 ? new ClientCompat191() : rev == 0 ? new ClientCompat190() : new ClientCompat19();
        }
    }

    public static Map<Command, Collection<CommandNode<CommandSourceStack>>> getNodes() {
        return ImmutableMap.copyOf(nodes);
    }

    public static Entity getTargetedEntity(Player entity) {
        int target = targetedEntities.getOrDefault(entity, -1);
        return entity.getLevel().getEntity(target);
    }

    public void registerAttributes(boolean addToSupplier) {
        if (isServerOnly()) return;

        Attribute reach = reachAttribute.get();
        Attribute swimSpeed = swimSpeedAttribute.get();
        if (!addToSupplier) return;

        MixinAttributeSupplierAccessor supplierAccessor = (MixinAttributeSupplierAccessor) DefaultAttributes.getSupplier(EntityType.PLAYER);
        Map<Attribute, AttributeInstance> instances = new LinkedHashMap<>(supplierAccessor.getInstances());


        instances.put(reach, new AttributeInstance(reach, i -> {
            throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getKey(reach));
        }));

        instances.put(swimSpeed, new AttributeInstance(swimSpeed, i -> {
            throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getKey(swimSpeed));
        }));

        supplierAccessor.setInstances(ImmutableMap.copyOf(instances));
    }
}
