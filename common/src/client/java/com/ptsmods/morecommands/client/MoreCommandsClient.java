package com.ptsmods.morecommands.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.api.Holder;
import com.ptsmods.morecommands.api.IMoreCommandsClient;
import com.ptsmods.morecommands.api.MoreCommandsArch;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.addons.AbstractButtonAddon;
import com.ptsmods.morecommands.api.addons.AbstractWidgetAddon;
import com.ptsmods.morecommands.api.addons.ScreenAddon;
import com.ptsmods.morecommands.api.callbacks.ChatMessageSendEvent;
import com.ptsmods.morecommands.api.callbacks.ClientCommandRegistrationEvent;
import com.ptsmods.morecommands.api.callbacks.ClientEntityEvent;
import com.ptsmods.morecommands.api.callbacks.PostInitEvent;
import com.ptsmods.morecommands.api.clientoptions.ClientOption;
import com.ptsmods.morecommands.api.miscellaneous.FormattingColour;
import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import com.ptsmods.morecommands.api.util.text.LiteralTextBuilder;
import com.ptsmods.morecommands.client.commands.SearchItemCommand;
import com.ptsmods.morecommands.client.gui.WorldInitCommandsScreen;
import com.ptsmods.morecommands.client.gui.infohud.InfoHud;
import com.ptsmods.morecommands.client.miscellaneous.ClientCommand;
import com.ptsmods.morecommands.client.miscellaneous.VexParticle;
import com.ptsmods.morecommands.client.mixin.accessor.MixinAbstractWidgetAccessor;
import com.ptsmods.morecommands.client.mixin.accessor.MixinParticleEngineAccessor;
import com.ptsmods.morecommands.client.util.DeathTracker;
import com.ptsmods.morecommands.client.util.PTClientImpl;
import com.ptsmods.morecommands.clientoption.ClientOptions;
import com.ptsmods.morecommands.compat.client.*;
import com.ptsmods.morecommands.miscellaneous.Chair;
import com.ptsmods.morecommands.miscellaneous.Command;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
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
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Unique;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class MoreCommandsClient implements IMoreCommandsClient {
    public static final Logger LOG = LogManager.getLogger();
    public static final KeyMapping TOGGLE_INFO_HUD_BINDING = new KeyMapping("key.morecommands.toggleInfoHud", GLFW.GLFW_KEY_O, ClientCommand.DF + "MoreCommands");
    public static boolean scheduleWorldInitCommands = false;
    private static double speed = 0d;
    private static double avgSpeed = 0d;
    private static final DoubleList lastSpeeds = new DoubleArrayList();
    private static AbstractTickableSoundInstance easterEggSound = null;
    public static final CommandDispatcher<ClientSuggestionProvider> clientCommandDispatcher = new CommandDispatcher<>();
    private static final Map<String, Integer> keys = new LinkedHashMap<>();
    private static final Map<Integer, String> keysReverse = new LinkedHashMap<>();
    private static final List<String> disabledCommands = new ArrayList<>();
    private static final List<String> worldInitCommands = new ArrayList<>();
    private static final File wicFile = MoreCommandsArch.getConfigDirectory().resolve("worldInitCommands.json").toFile();
    private static final Map<String, String> nameMCFriends = new HashMap<>();
    private static final HttpClient sslLenientHttpClient;
    private static final Map<ClientCommand, Collection<CommandNode<ClientSuggestionProvider>>> nodes = new LinkedHashMap<>();
    private static final ResourceLocation unknownContentsTexture = new ResourceLocation("morecommands", "textures/unknown_contents.png");

    static {
        for (Field f : GLFW.class.getFields())
            if (f.getName().startsWith("GLFW_KEY_") || f.getName().startsWith("GLFW_MOUSE_BUTTON_")) {
                int keyCode;
                try {
                    keyCode = f.getInt(null) + (f.getName().contains("MOUSE") ? GLFW.GLFW_KEY_LAST+1 : 0);
                } catch (IllegalAccessException e) {
                    LOG.catching(e);
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

    public static void init() {
        Holder.setMoreCommandsClient(new MoreCommandsClient());
        Holder.setClientCompat(determineCurrentCompat());

        ClientCompat.get().registerUnobtainableItemsTab();

        ClientOptions.init();
        MoreCommands.setFormattings(ClientOptions.Tweaks.defColour.getValue().asFormatting(), ClientOptions.Tweaks.secColour.getValue().asFormatting());

        List<ParticleRenderType> list = new ArrayList<>(MixinParticleEngineAccessor.getRenderOrder());
        list.add(VexParticle.prt);
        MixinParticleEngineAccessor.setRenderOrder(list);

        List<ItemLike> waterItems = Lists.newArrayList(Blocks.WATER, Blocks.BUBBLE_COLUMN);
        Compat.get().<Block>getBuiltInRegistry("block").getOptional(new ResourceLocation("water_cauldron")).ifPresent(waterItems::add);
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> 0x3e76e4, waterItems.toArray(new ItemLike[0]));

        Holder.setDeathTracker(DeathTracker.INSTANCE);

        AtomicInteger wicWarmup = new AtomicInteger();
        PlayerEvent.PLAYER_JOIN.register(player -> {
            updateTag();
            if (scheduleWorldInitCommands) wicWarmup.set(10);
            scheduleWorldInitCommands = false;
        });

        TickEvent.SERVER_POST.register(server -> {
            CommandSourceStack source = server.createCommandSourceStack();
            if (wicWarmup.get() > 0 && wicWarmup.decrementAndGet() == 0) getWorldInitCommands()
                    .forEach(cmd -> Compat.get().performCommand(server.getCommands(), source, cmd));
        });

        PostInitEvent.EVENT.register(() -> {
            if (Platform.isForge()) return; // This doesn't work on Forge cuz of their ridiculous registry handling.

            for (Block block : Compat.get().<Block>getBuiltInRegistry("block")) {
                ResourceLocation id = Compat.get().<Block>getBuiltInRegistry("block").getKey(block);
                if (id == null) continue;

                MappedRegistry<Item> itemRegistry = Compat.get().getBuiltInRegistry("item");
                if (!Compat.get().registryContainsId(itemRegistry, id))
                    Compat.get().register(itemRegistry, new ResourceLocation(id.getNamespace(), "mcsynthetic_" + id.getPath()),
                            new BlockItem(block, new Item.Properties()));
            }

            ClientCompat.get().fillUnobtainableItemsTab();
        });

        if (!Platform.isForge()) KeyMappingRegistry.register(TOGGLE_INFO_HUD_BINDING);

        if (!wicFile.exists()) saveWorldInitCommands();
        try {
            List<String> wic = MoreCommands.readJson(wicFile);
            if (wic != null) worldInitCommands.addAll(wic);
        } catch (IOException e) {
            LOG.error("Could not read World Init Commands.", e);
        }

        ClientGuiEvent.RENDER_HUD.register((matrices, tickDelta) -> {
            if (ClientOptions.Tweaks.enableInfoHUD.getValue()) InfoHud.INSTANCE.render(matrices);
        });

        Set<Entity> coolKids = new HashSet<>();
        ClientTickEvent.CLIENT_LEVEL_PRE.register(world -> {
            if (TOGGLE_INFO_HUD_BINDING.consumeClick()) {
                ClientOptions.Tweaks.enableInfoHUD.setValue(!ClientOptions.Tweaks.enableInfoHUD.getValue());
                ClientOptions.write();
            }

            LocalPlayer p = Minecraft.getInstance().player;
            if (p != null) {
                double x = p.getX() - p.xo;
                double y = p.getY() - p.yo;
                double z = p.getZ() - p.zo;
                speed = Math.sqrt(x * x + y * y + z * z) * 20; // Apparently, Pythagoras' theorem does have some use. Who would've thunk?

                lastSpeeds.add(speed);
                if (lastSpeeds.size() > 20) lastSpeeds.removeDouble(0);

                avgSpeed = Compat.get().doubleStream(lastSpeeds).average().orElse(0);
            }

            for (Entity entity : coolKids)
                if (entity.level == world)
                    for (int i = 0; i < 2; i++)
                        Minecraft.getInstance().particleEngine.add(new VexParticle(entity));
        });

        ClientEntityEvent.ENTITY_LOAD.register((world, entity) -> {
            if (entity instanceof Player && isCool(entity)) coolKids.add(entity);
        });

        ClientEntityEvent.ENTITY_UNLOAD.register((world, entity) -> coolKids.remove(entity));

        Map<ClientCommand, Collection<CommandNode<ClientSuggestionProvider>>> nodes = new LinkedHashMap<>();
        BiConsumer<ClientCommand, CommandDispatcher<ClientSuggestionProvider>> registerer = (cmd, dispatcher) -> {
            try {
                CommandDispatcher<ClientSuggestionProvider> tempDispatcher = new CommandDispatcher<>();
                cmd.cRegister(tempDispatcher);

                for (CommandNode<ClientSuggestionProvider> child : tempDispatcher.getRoot().getChildren()) dispatcher.getRoot().addChild(child);
                nodes.put(cmd, tempDispatcher.getRoot().getChildren());
            } catch (Exception e) {
                LOG.error("Could not register command " + cmd.getClass().getName() + ".", e);
            }
        };

        List<? extends ClientCommand> clientCommands = MoreCommands.getCommandClasses(EnvType.CLIENT, ClientCommand.class).stream()
                .map(MoreCommands::getInstance)
                .filter(Objects::nonNull)
                .toList();
        ClientCommandRegistrationEvent.EVENT.register(dispatcher -> {
            clientCommands
                    .stream()
                    .filter(cmd -> !cmd.doLateInit())
                    .forEach(cmd -> registerer.accept(cmd, dispatcher));

            MoreCommandsClient.nodes.putAll(nodes);
            nodes.clear();

            clientCommands
                    .stream()
                    .filter(Command::doLateInit)
                    .forEach(cmd -> registerer.accept(cmd, dispatcher));

            MoreCommandsClient.nodes.putAll(nodes);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, new ResourceLocation("morecommands:formatting_update"), (buf, context) -> {
            int id = buf.readByte();
            int index = buf.readByte();
            if (index < 0) return;
            ChatFormatting colour = FormattingColour.values()[index].asFormatting();
            switch (id) {
                case 0 -> {
                    MoreCommands.DF = Command.DF = colour;
                    MoreCommands.DS = Command.DS = MoreCommands.DS.withColor(MoreCommands.DF);
                }
                case 1 -> {
                    MoreCommands.SF = Command.SF = colour;
                    MoreCommands.SS = Command.SS = MoreCommands.SS.withColor(MoreCommands.SF);
                }
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, new ResourceLocation("morecommands:disable_client_options"), (buf, context) -> {
            ClientOption.getUnmappedOptions().values().forEach(option -> option.setDisabled(false));
            int length = buf.readVarInt();
            for (int i = 0; i < length; i++)
                Optional.ofNullable(ClientOption.getKeyMappedOptions().get(buf.readUtf()))
                        .ifPresent(option -> option.setDisabled(true));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, new ResourceLocation("morecommands:disable_client_commands"), (buf, context) -> {
            disabledCommands.clear();
            int length = buf.readVarInt();
            for (int i = 0; i < length; i++)
                disabledCommands.add(buf.readUtf());
        });

        ChatMessageSendEvent.EVENT.register(message -> {
            if (message.startsWith("/easteregg")) {
                if (easterEggSound == null) Minecraft.getInstance().getSoundManager().play(easterEggSound = ClientCompat.get().newEESound());
                else {
                    Minecraft.getInstance().getSoundManager().stop(easterEggSound);
                    easterEggSound = null;
                }
                return null;
            }
            return message;
        });

        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            if (!Screen.hasShiftDown() && ClientOptions.Tweaks.sitOnStairs.getValue() && player.getItemInHand(hand).isEmpty() &&
                    Chair.isValid(player.level.getBlockState(pos)) && NetworkManager.canServerReceive(new ResourceLocation("morecommands:sit_on_stairs"))) {
                NetworkManager.sendToServer(new ResourceLocation("morecommands:sit_on_stairs"), new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                return EventResult.interruptTrue();
            }
            return EventResult.pass();
        });

        MoreCommands.execute(() -> {
            try {
                List<Map<String, String>> friends = new Gson().fromJson(getSSLLenientHTML("https://api.namemc.com/profile/" + Minecraft.getInstance().getUser().getUuid() + "/friends"),
                        new TypeToken<List<Map<String, String>>>() {}.getType());
                // SSL lenient because the certificate of api.namemc.com is not recognised on Java 8 for some reason.
                nameMCFriends.putAll(friends.stream().collect(Collectors.toMap(friend -> friend.get("uuid"), friend -> friend.get("name"))));
            } catch (IOException e) {
                LOG.error("Could not look up NameMC friends.", e);
            }
        });
    }

    // Me
    public static boolean isCool(Entity entity) {
        return entity instanceof Player && ("1aa35f31-0881-4959-bd14-21e8a72ba0c1".equals(entity.getStringUUID()) ||
                Platform.isDevelopmentEnvironment()) && (Minecraft.getInstance().player == null ||
                Compat.get().getUUID(entity).equals(Compat.get().getUUID(Minecraft.getInstance().player)));
    }

    // My best friend :3
    public static boolean isCute(Entity entity) {
        return entity instanceof Player && "b8760dc9-19fd-4d01-a5c7-25268a677deb".equals(entity.getStringUUID());
    }

    @Override
    public CommandDispatcher<ClientSuggestionProvider> getClientCommandDispatcher() {
        return clientCommandDispatcher;
    }

    public static void updateTag() {
        if (NetworkManager.canServerReceive(new ResourceLocation("morecommands:discord_data")) && ClientOptions.Tweaks.discordTag.getValue() != null) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBoolean(ClientOptions.Tweaks.askPermission.getValue());
            buf.writeUtf(ClientOptions.Tweaks.discordTag.getValue());
            NetworkManager.sendToServer(new ResourceLocation("morecommands:discord_data"), buf);
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
    @Override
    public void addColourPicker(Screen screen, int xOffset, int yOffset, boolean doCenter, boolean initOpened, Consumer<String> appender, Consumer<Boolean> stateListener) {
        boolean initOpened0 = initOpened || ClientOptions.Tweaks.colourPickerOpen.getValue();
        final int buttonWidth = 24;
        final int wideButtonWidth = (int) (buttonWidth / 0.75f);
        final int buttonHeight = 20;
        ChatFormatting[] formattings = ChatFormatting.values();
        List<Button> btns = new ArrayList<>();
        for (int i = 0; i < formattings.length; i++) {
            int x = i; // Has to be effectively final cuz lambda
            btns.add(((ScreenAddon) screen).mc$addButton(Util.make(
                    ClientCompat.get().newButton(screen, xOffset + (i < 16 ? (buttonWidth+2) * (i%4) : (wideButtonWidth+3) * (i%3)),
                            yOffset + (buttonHeight+2) * ((i < 16 ? i/4 : 4 + (i-16)/3) + 1), i < 16 ? buttonWidth : wideButtonWidth, buttonHeight,
                            LiteralTextBuilder.builder(formattings[x].toString().replace('\u00A7', '&'))
                                    .withStyle(Style.EMPTY.applyFormat(formattings[x]))
                                    .build(),
                            btn -> appender.accept(formattings[x].toString().replace('\u00A7', '&')),
                            i == 22 ? LiteralTextBuilder.literal(ChatFormatting.RED + "Only works on servers with MoreCommands installed.") : null),
                    btn -> {
                        btn.visible = initOpened0;
                        ((AbstractButtonAddon) btn).setIgnoreKeys(true);
                        ((AbstractWidgetAddon) btn).setFocusable(false);
                    }
            )));
        }

        ((ScreenAddon) screen).mc$addButton(Util.make(ClientCompat.get().newButton(screen,
                xOffset + (buttonWidth+2) * 2 - 26, yOffset + (doCenter && !initOpened0 ? (buttonHeight+2) * 7 / 2 : 0),
                50, 20, LiteralTextBuilder.literal("Colours", Command.DS),
                btn -> {
                    boolean b = !btns.get(0).visible;
                    if (doCenter) ((MixinAbstractWidgetAccessor) btn).setY_(b ? yOffset : yOffset + 22 * 7 / 2);

                    btns.forEach(btn0 -> btn0.visible = b);
                    if (stateListener != null) stateListener.accept(b);
                }, null), btn -> {
                    btn.visible = ClientOptions.Tweaks.textColourPicker.getValue();
                    ((AbstractButtonAddon) btn).setIgnoreKeys(true);
                    ((AbstractWidgetAddon) btn).setFocusable(false);
                }));
    }

    public static void clearDisabledCommands() {
        disabledCommands.clear();
    }

    public boolean isCommandDisabled(String input) {
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
            LOG.error("Could not save World Init Commands.", e);
        }
    }

    public static Map<String, String> getNameMCFriends() {
        return ImmutableMap.copyOf(nameMCFriends);
    }

    public static void updateNameMCFriend(String id, String name) {
        nameMCFriends.put(id, name);
    }

    public static Map<ClientCommand, Collection<CommandNode<ClientSuggestionProvider>>> getNodes() {
        return ImmutableMap.copyOf(nodes);
    }

    @Override
    public List<KeyMapping> getKeyMappings() {
        return ImmutableList.of(TOGGLE_INFO_HUD_BINDING, PTClientImpl.CYCLE_KEY_BINDING.get());
    }

    @Override
    public void setScheduleWorldInitCommands(boolean scheduleWorldInitCommands) {
        MoreCommandsClient.scheduleWorldInitCommands = scheduleWorldInitCommands;
    }

    private static ClientCompat determineCurrentCompat() {
        Version v = Version.getCurrent();
        int minor = v.minor, rev = v.revision == null ? 0 : v.revision;

        ClientCompat compat = switch (minor) {
            case 17, 18 -> new ClientCompat17();
            default -> switch (rev) {
                case 0 -> new ClientCompat190();
                case 1 -> new ClientCompat191();
                case 2 -> new ClientCompat192();
                case 3 -> new ClientCompat193();
                default -> new ClientCompat194();
            };
        };
        LOG.info("Determined client compat: " + compat.getClass().getSimpleName());

        return compat;
    }

    @Override
    public Screen newWorldInitScreen(Screen parent) {
        return new WorldInitCommandsScreen(parent);
    }

    @Override
    public void renderSearchItemResults(PoseStack stack, Camera camera) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buff = tess.getBuilder();

        // Begin our buffer.
        buff.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        BufferBuilder finalBuff = buff;
        SearchItemCommand.RESULTS.forEach((pos, res) -> renderBlockOverlay(stack, camera, finalBuff, pos,
                res.r, res.g, res.b, 0.33f, null));
        tess.end();

        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buff = tess.getBuilder();
        buff.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        BufferBuilder finalBuff1 = buff;
        SearchItemCommand.RESULTS.forEach((pos, res) -> {
            if (res.isUnknown()) renderBlockOverlay(stack, camera, finalBuff1, pos, 0, 0, 0, 1, unknownContentsTexture);
        });
        tess.end();

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    private @Unique void renderBlockOverlay(PoseStack stack, Camera cam, BufferBuilder buff, BlockPos pos, float r, float g, float b, float a,
                                            @Nullable ResourceLocation texture) {
        stack.pushPose();
        Vec3 renderPos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).subtract(cam.getPosition());
        stack.translate(renderPos.x, renderPos.y, renderPos.z);

        PoseStack.Pose pose = stack.last();
        boolean isTex = texture != null;

        if (texture != null) RenderSystem.setShaderTexture(0, texture);

        // Up
        vertex(buff, pose, 0, 1, 0, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 1, 0, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 1, 1, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 0, 1, 1, r, g, b, a, 0, 1, isTex);

        // Down
        vertex(buff, pose, 0, 0, 0, r, g, b, a, 0, 1, isTex);
        vertex(buff, pose, 1, 0, 0, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 1, 0, 1, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 0, 0, 1, r, g, b, a, 0, 0, isTex);

        // North
        vertex(buff, pose, 0, 0, 0, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 0, 1, 0, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 1, 0, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 0, 0, r, g, b, a, 0, 1, isTex);

        // East
        vertex(buff, pose, 1, 0, 0, r, g, b, a, 1, 1, isTex);
        vertex(buff, pose, 1, 1, 0, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 1, 1, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 0, 1, r, g, b, a, 0, 1, isTex);

        // South
        vertex(buff, pose, 0, 0, 1, r, g, b, a, 0, 1, isTex);
        vertex(buff, pose, 0, 1, 1, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 1, 1, 1, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 1, 0, 1, r, g, b, a, 1, 1, isTex);

        // West
        vertex(buff, pose, 0, 0, 0, r, g, b, a, 0, 1, isTex);
        vertex(buff, pose, 0, 1, 0, r, g, b, a, 0, 0, isTex);
        vertex(buff, pose, 0, 1, 1, r, g, b, a, 1, 0, isTex);
        vertex(buff, pose, 0, 0, 1, r, g, b, a, 1, 1, isTex);

        stack.popPose();
    }

    private @Unique void vertex(VertexConsumer buff, PoseStack.Pose pose,
                                float x, float y, float z,
                                float r, float g, float b, float a,
                                float u, float v, boolean isTex) {
        ClientCompat.get().vertex(buff, pose, x, y, z);
        if (isTex) buff.uv(u, v);
        else buff.color(r, g, b, a);
        buff.endVertex();
    }
}
