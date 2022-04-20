package com.ptsmods.morecommands.util;

import com.ptsmods.morecommands.api.IMoreCommands;
import com.ptsmods.morecommands.api.Version;
import com.ptsmods.morecommands.api.compat.Compat;
import com.ptsmods.morecommands.api.compat.client.ClientCompat;
import com.ptsmods.morecommands.compat.*;
import com.ptsmods.morecommands.compat.client.ClientCompat16;
import com.ptsmods.morecommands.compat.client.ClientCompat17;
import com.ptsmods.morecommands.compat.client.ClientCompat19;

public class CompatHolder {
    private static final Compat compat = (Compat) determineCurrentCompat(false);
    private static final ClientCompat clientCompat = (ClientCompat) determineCurrentCompat(true);

    public static Compat getCompat() {
        return compat;
    }

    public static ClientCompat getClientCompat() {
        return clientCompat;
    }

    private static Object determineCurrentCompat(boolean client) {
        Object compat = determineCurrentCompat0(client);
        IMoreCommands.LOG.info("Determined " + (client ? "client " : "") + "compat: " + compat.getClass().getSimpleName());

        return compat;
    }

    private static Object determineCurrentCompat0(boolean client) {
        Version v = Version.getCurrent();
        int minor = v.minor, rev = v.revision;

        if (!client) switch (minor) {
            case 16:
                return new Compat16();
            case 17:
                return new Compat17();
            case 18:
                return rev >= 2 ? new Compat182() : new Compat18();
            case 19:
            default:
                return new Compat19();
        }
        else switch (minor) {
            case 16:
                return new ClientCompat16();
            case 17:
            case 18:
                return new ClientCompat17();
            case 19:
            default:
                return new ClientCompat19();
        }
    }
}
