package com.ptsmods.morecommands.miscellaneous;

import com.ptsmods.morecommands.MoreCommands;
import com.ptsmods.morecommands.MoreCommandsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public class ClientOptions {

    public static class Rendering {
        @Comment({"See player nametags through blocks", "when they're sneaking."})
        public static boolean seeTagSneaking = true;
        @Comment({"Render your own tag in third person mode", "and the inventory screen."})
        public static boolean renderOwnTag = true;
        @Comment({"Show exact latency (ping) of players when holding tab.", "Kind of buggy, so beware."})
        public static boolean showExactLatency = true;
        @Comment({"Adds glint (enchanted) effect to", "MoreCommands powertools."})
        public static boolean powertoolsGlint = true;
    }

    public static class Tweaks {
        @Comment({"Draw general information in the top-left corner.", "Can be toggled by pressing the O key on your keyboard."})
        public static boolean enableInfoHud = false;
        @Comment({"When using (right-clicking) a door that has", "a corresponding door next to it, use both of them.", "Works on servers without the mod too."})
        public static boolean openDoubleDoors = true;
        @Comment({"Right-click on inner-corner or straight stairs to sit on them.", "Only works if the mod is also installed on the server."})
        public static boolean sitOnStairs = true;
        @Comment({"Prevent your elytra from opening when you press space.", "For if you want to wear it, but not use it.", "(In e.g. pvp areas of survival servers or during parkour)"})
        public static boolean disableElytra = false;
    }

    public static class Chat {
        @Comment("Copies a chatmessage when you left-click on it.")
        public static boolean chatMsgCopy = true;
        @Comment("Removes a chatmessage when you right-click on it.")
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

    public static class RichPresence {
        @ChangeCallback("update")
        @Comment({"Toggle Discord Rich Presence", "May not work on Mac."})
        public static boolean enableRPC = true;
        @ChangeCallback("update")
        @Comment({"Whether I may advertise this mod on the RPC.", "It would be kindly appreciated if you left this enabled. :)"})
        public static boolean advertiseMC = true;
        @ChangeCallback("update")
        @Comment({"Whether to show either the ip of the server", "you're on or the name of the world you're in", "in the details section of the RPC."})
        public static boolean showDetails = true;

        private static void update() {
            MoreCommandsClient.updatePresence();
        }
    }

    private static final Properties props = new Properties();
    private static final File f = new File("config/MoreCommands/clientoptions.txt");

    public static void update() {
        for (Class<?> c : ClientOptions.class.getClasses())
            for (Field f : c.getFields()) {
                try {
                    props.setProperty(f.getName(), String.valueOf(f.get(null)));
                } catch (IllegalAccessException ignored) {}
            }
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
                e.printStackTrace();
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
        props.setProperty(name, String.valueOf(value));
        write0();
        read();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Cramp {
        int min();
        int max();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Comment {
        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ChangeCallback {
        String value();
    }

}
