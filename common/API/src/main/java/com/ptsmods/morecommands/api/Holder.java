package com.ptsmods.morecommands.api;

import com.ptsmods.morecommands.api.util.compat.Compat;
import com.ptsmods.morecommands.api.util.compat.client.ClientCompat;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Holder {
    // Trying not to load the Command class, which in turn loads the MoreCommands class, when registering this attribute
    // in MixinPlayerEntity which is done too early.
    public static final Attribute REACH_ATTRIBUTE = new RangedAttribute("attribute.morecommands.reach", 4.5d, 1d, 160d).setSyncable(true);
    @Getter(AccessLevel.PACKAGE)
    private static IMoreCommands moreCommands;
    @Getter(AccessLevel.PACKAGE)
    private static IMoreGameRules moreGameRules;
    @Getter(onMethod_ = @Deprecated) // Not API, use Compat#get() instead.
    private static Compat compat;
    @Getter(onMethod_ = @Deprecated) // Not API, use ClientCompat#get() instead.
    private static ClientCompat clientCompat;
    @Getter(value = AccessLevel.PACKAGE, onMethod_ = @Deprecated)
    private static MixinAccessWidener mixinAccessWidener;
    @Getter(value = AccessLevel.PACKAGE, onMethod_ = @Deprecated) // Not API, use IRainbow#get() instead.
    private static IRainbow rainbow;
    @Getter(value = AccessLevel.PACKAGE, onMethod_ = @Deprecated) // Not API, use IDataTrackerHelper#get() instead.
    private static IDataTrackerHelper dataTrackerHelper;

    @Getter(value = AccessLevel.PACKAGE, onMethod_ = @Deprecated) // Not API, use IDeathTracker#get() instead.
    private static IDeathTracker deathTracker;

    public static void setMoreCommands(IMoreCommands moreCommands) {
        if (Holder.moreCommands != null) throw new IllegalStateException("MoreCommands instance already set.");
        Holder.moreCommands = moreCommands;
    }

    public static void setMoreGameRules(IMoreGameRules moreGameRules) {
        if (Holder.moreGameRules != null) throw new IllegalStateException("MoreGameRules instance already set.");
        Holder.moreGameRules = moreGameRules;
    }

    public static void setCompat(Compat compat) {
        if (Holder.compat != null) throw new IllegalStateException("Compat instance already set.");
        Holder.compat = compat;
    }

    public static void setClientCompat(ClientCompat clientCompat) {
        if (Holder.clientCompat != null) throw new IllegalStateException("ClientCompat instance already set.");
        Holder.clientCompat = clientCompat;
    }

    public static void setMixinAccessWidener(MixinAccessWidener mixinAccessWidener) {
        if (Holder.mixinAccessWidener != null) throw new IllegalStateException("MixinAccessWidener instance already set.");
        Holder.mixinAccessWidener = mixinAccessWidener;
    }

    public static void setRainbow(IRainbow rainbow) {
        if (Holder.rainbow != null) throw new IllegalStateException("Rainbow instance already set.");
        Holder.rainbow = rainbow;
    }

    public static void setDataTrackerHelper(IDataTrackerHelper dataTrackerHelper) {
        if (Holder.dataTrackerHelper != null) throw new IllegalStateException("DataTrackerHelper instance already set.");
        Holder.dataTrackerHelper = dataTrackerHelper;
    }

    public static void setDeathTracker(IDeathTracker deathTracker) {
        if (Holder.deathTracker != null) throw new IllegalStateException("DeathTracker instance already set.");
        Holder.deathTracker = deathTracker;
    }

    public static boolean shouldApplyMixin(Version version, String mixinClassName) {
        return mixinClassName.contains(".plus.") ? Version.getCurrent().isNewerThanOrEqual(version) :
                mixinClassName.contains(".min.") ? Version.getCurrent().isOlderThanOrEqual(version) :
                        Version.getCurrent().equalsExclRev(version);
    }
}
