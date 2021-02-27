package com.ptsmods.morecommands.miscellaneous;

import com.google.common.collect.ImmutableMap;
import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public class ClientOptions {

    public static final Map<Field, Object> defaultValues;

    static {
        ImmutableMap.Builder<Field, Object> builder = ImmutableMap.builder();
        for (Class<?> clazz : ClientOptions.class.getClasses())
            for (Field f : clazz.getFields())
                builder.put(f, ReflectionHelper.getFieldValue(f, null));
        defaultValues = builder.build();
    }

    @Comment({"Discord Rich Presence support.", "To let everyone on Discord know", "that you've been playing Minecraft for three days straight."})
    public static class RichPresence {
        public static final int ordinal = 0;
        @ChangeCallback("updateRPC")
        @Comment({"Toggle Discord Rich Presence", "May not work on Mac."})
        public static boolean enableRPC = true;
        @ChangeCallback("updateRPC")
        @Comment({"Whether I may advertise this mod on the RPC.", "It would be kindly appreciated if you left this enabled. :)"})
        public static boolean advertiseMC = true;
        @ChangeCallback("updateRPC")
        @Comment({"Whether to show either the ip of the server you're on", "or the name of the world you're in in the details section of the RPC."})
        public static boolean showDetails = true;
        @ChangeCallback("updateTag")
        @Comment({"Whether your Discord tag should be sent to the server", "for others to see.", "Only works if the mod is installed on the server too."})
        public static boolean shareTag = true;
        @ChangeCallback("updateTag")
        @Comment({"Whether players need your permission to view your tag."})
        public static boolean askPermission = true;

        private static void updateRPC() {
            MoreCommandsClient.updatePresence();
        }

        private static void updateTag() {
            MoreCommandsClient.updateTag();
        }
    }

    @Comment("These options change or add things regarding rendering.")
    public static class Rendering {
        public static final int ordinal = 1;
        @Comment({"See player nametags through blocks", "when they're sneaking."})
        public static boolean seeTagSneaking = true;
        @Comment({"Render your own tag in third person mode", "and the inventory screen."})
        public static boolean renderOwnTag = true;
        @Comment({"Show exact latency (ping) of players when holding tab.", "Kind of buggy, so beware."})
        public static boolean showExactLatency = true;
        @Comment({"Adds glint (enchanted) effect to", "MoreCommands powertools."})
        public static boolean powertoolsGlint = true;
    }

    @Comment({"Some tweaks to change your game.", "These are harmless, but can be very useful."})
    public static class Tweaks {
        public static final int ordinal = 2;
        @Comment({"Draw general information in the top-left corner.", "Can be toggled by pressing the O key on your keyboard."})
        public static boolean enableInfoHud = false;
        @Comment({"When using (right-clicking) a door that has", "a corresponding door next to it, use both of them.", "Works on servers without the mod too."})
        public static boolean openDoubleDoors = true;
        @Comment({"Right-click on inner-corner or straight stairs to sit on them.", "Only works if the mod is also installed on the server."})
        public static boolean sitOnStairs = true;
        @Comment({"Prevent your elytra from opening when you press space.", "For if you want to wear it, but not use it.", "(In e.g. pvp areas of survival servers or during parkour)"})
        public static boolean disableElytra = false;
        @Comment({"Allows you to make the lines on signs longer.", "Lines are still cut when rendering (so no giant lines),", "but it's very useful for putting colours on signs."})
        public static boolean noSignLimit = true;
        @Comment({"Whether flying should be locked.", "When true, you will always be flying.", "It is recommended to bind this option to a key", "if you find yourself often needing it."})
        public static boolean lockFlying = false;
        @Comment({"Whether you can target fluids with your cursor.", "Useful for placing blocks on water."})
        public static boolean targetFluids = false;
        @Comment("Whether blocks should push you out when you're inside them.")
        public static boolean doBlockPush = true;
        @Comment({"Whether you should immediately come to a halt", "when you release all movement keys while moving."})
        public static boolean immediateMoveStop = false;
        @Comment({"Whether hidden options should be shown.", "You're highly discouraged to enable this."})
        public static boolean hiddenOptions = false;
        @Comment({"Whether splash texts should be rainbow coloured.", "\u00A7cRequires restart"})
        public static boolean rainbowSplash = false;
        @Comment({"Whether to make the title screen always say", "'Minceraft' instead of 'Minecraft'.", "As you may or may not know, by default it has a 0.1% chance.", "\u00A7cRequires restart"})
        public static boolean alwaysMinceraft = false;
        @Comment({"Whether you want to be able to open screens like", "chat or inventory while teleporting in a nether portal."})
        public static boolean screensInPortal = true;
        @Comment({"Whether the 'Colours' button should be displayed", "on most GUIs that support colours in text input.", "(Signs, books, anvils and chat for now)"})
        public static boolean textColourPicker = true;
        @Comment({"Whether the 'Colours' button should always be expanded", "when first opening a GUI that has it."})
        public static boolean colourPickerOpen = false;
    }

    @Comment({"Some less harmless tweaks.", "All of them are set to mimic the default behaviour of Minecraft.", "Meaning that their default values don't change anything.", "", "\u00A7cTo prevent you getting an unfair advantage,", "\u00A7cthese options only affect singleplayer worlds."})
    @IsHidden("hiddenOptions")
    public static class Cheats {
        public static final int ordinal = 3;
        @Comment({"Lets you jump as soon as you hit the ground while sprinting.", "Gives you a slight speed boost when holding space too."})
        public static boolean sprintAutoJump = false;
        @Comment({"Prevents you from taking damage from cacti by making their", "collision box a little bigger than usual."})
        public static boolean avoidCactusDmg = false;
        @Comment({"Allows you to collide with (almost) every block in the game.", "Allows you to i.e. walk on cobweb, bushes, grass, etc...", "But not fluids, that's just too much."})
        public static boolean collideAll = false;
    }

    @Comment({"Chat related tweaks.", "Most of these are enabled by default."})
    public static class Chat {
        public static final int ordinal = 4;
        @Comment({"Copy a chatmessage when you left-click on it.", "Holding control while doing so will also copy formattings."})
        public static boolean chatMsgCopy = true;
        @Comment("Remove a chatmessage when you right-click on it.")
        public static boolean chatMsgRemove = true;
        @Comment("Do not show messages that have no content in chat.")
        public static boolean ignoreEmptyMsgs = true;
        @Comment("Do not delete chatmessages when there are more than 100.")
        public static boolean infiniteChat = true;
        @Comment("Prepend messages in chat with the time they were received.")
        public static boolean showMsgTime = false;
        @Comment({"Use a 12 hour clock instead of a 24 hour one.", "See 'Show Msg Time'."})
        public static boolean use12HourClock = false;
        @Comment({"Whether to also add seconds or not.", "See 'Show Msg Time'."})
        public static boolean showSeconds = false;
    }

    @Comment({"Don't look in here.", "Stay away.", "", "Keep \u00A7c\u00A7lOUT\u00A7r!! >:c"})
    @IsHidden("hiddenOptions")
    public static class EasterEggs {
        public static final int ordinal = 5;
        @Comment({"Everything is \u00A7urainbows\u00A7r.", "\u00A7lEverything", "\u00A74Not suitable for people prone to epileptic seizures!"})
        public static boolean rainbows = false;
    }

    private static final Properties props = new Properties();
    private static final File f = new File("config/MoreCommands/clientoptions.txt");

    public static void update() {
        for (Class<?> c : ClientOptions.class.getClasses())
            for (Field f : c.getFields())
                props.setProperty(f.getName(), String.valueOf(ReflectionHelper.<Object, Object>getFieldValue(f, null)));
    }

    public static boolean write() {
        update();
        return write0();
    }

    private static boolean write0() {
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) return false;
            } catch (IOException e) {
                MoreCommands.log.catching(e);
                return false;
            }
        }
        try (PrintWriter writer = new PrintWriter(f, "UTF-8")) {
            props.store(writer, "MoreCommand's client-only options.\nThese should not be changed manually, but rather be set via the in-game menu. (Esc -> options -> MoreCommands");
            writer.flush();
            return true;
        } catch (Exception e) {
            MoreCommands.log.error("An error occurred while saving the clientoptions.", e);
            return false;
        }
    }

    public static void read() {
        if (f.exists())
            try (FileReader reader = new FileReader(f)) {
                props.load(reader);
                for (Class<?> c : ClientOptions.class.getClasses())
                    if (!c.isInterface())
                        for (Field f : c.getFields()) {
                            try {
                                if (props.containsKey(f.getName())) {
                                    String s = props.getProperty(f.getName());
                                    Class<?> type = f.getType();
                                    f.set(null, type == boolean.class ? Boolean.valueOf(s) : type == int.class ? Integer.parseInt(s) : type == String.class ? s : null);
                                }
                            } catch (IllegalAccessException ignored) {
                            } catch (Exception e) {
                                MoreCommands.log.catching(e);
                            }
                        }
            } catch (IOException e) {
                MoreCommands.log.catching(e);
            }
        else write();
    }

    public static Class<?> getType(String option) {
        update();
        if (props.containsKey(option))
            for (Class<?> c : ClientOptions.class.getClasses())
                if (!c.isInterface())
                    for (Field f : c.getFields())
                        if (f.getName().equals(option))
                            return f.getType();
        return null;
    }

    public static List<String> getOptions() {
        update();
        List<String> options = new ArrayList<>();
        props.keySet().forEach(o -> options.add(o.toString()));
        return options;
    }

    public static String getOption(String name) {
        update();
        return props.getProperty(name);
    }

    public static void setOption(String name, Object value) {
        update();
        props.setProperty(name, String.valueOf(value));
        write0();
        read();
    }

    public static List<Field> getFields() {
        return new ArrayList<>(defaultValues.keySet());
    }

    public static void reset() {
        for (Field f : getFields())
            if (!Modifier.isFinal(f.getModifiers())) ReflectionHelper.setFieldValue(f, null, defaultValues.get(f));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Cramp {
        int min();
        int max();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Comment {
        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ChangeCallback {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface IsHidden {
        /**
         * @return The name of the boolean option used to determine whether this option or class should be shown.
         */
        String value();
    }

}
