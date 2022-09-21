package com.ptsmods.morecommands.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.With;
import net.minecraft.SharedConstants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class Version implements Comparable<Version> {
    private static final Version current;

    public static final Version V1_16 = new Version(16);
    public static final Version V1_17 = new Version(17);
    public static final Version V1_18 = new Version(18);
    public static final Version V1_18_0 = new Version(18, 0);
    public static final Version V1_18_2 = new Version(18, 2);
    public static final Version V1_19 = new Version(19);
    public static final Version V1_19_0 = new Version(19, 0);
    public static final Version V1_19_1 = new Version(19, 1);
    public static final Version V1_19_2 = new Version(19, 2);

    static {
        current = parse(new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(
                SharedConstants.class.getClassLoader().getResourceAsStream("version.json"))), JsonObject.class)
                .get("release_target").getAsString());
    }

    @With
    public final int major, minor;
    @With
    public final @Nullable Integer revision;

    public Version(int minor) {
        this(1, minor, null);
    }

    public Version(int minor, int revision) {
        this(1, minor, revision);
    }

    public Version(int major, int minor, @Nullable Integer revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public static Version getCurrent() {
        return current;
    }

    public static Version parse(String version) {
        String[] parts = version.split("\\.");

        if (parts.length < 2 || !Arrays.stream(parts).allMatch(StringUtils::isNumeric))
            throw new IllegalArgumentException("The given version was invalid");

        return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts.length > 2 ? Integer.parseInt(parts[2]) : null);
    }

    public boolean isNewerThan(Version version) {
        return  major > version.major ||
                major == version.major && minor >  version.minor ||
                major == version.major && minor == version.minor &&
                        (revision != null && version.revision == null && revision != 0 ||
                                revision != null && version.revision != null && revision > version.revision);
    }

    public boolean isNewerThanOrEqual(Version version) {
        return equals(version) || isNewerThan(version);
    }

    public boolean isOlderThanOrEqual(Version version) {
        return equals(version) || isOlderThan(version);
    }

    public boolean isOlderThan(Version version) {
        return  major <  version.major ||
                major == version.major && minor <  version.minor ||
                major == version.major && minor == version.minor &&
                        (revision == null && version.revision != null && version.revision != 0 ||
                                revision != null && version.revision != null && revision < version.revision);
    }

    @Override
    public String toString() {
        return revision == null ? major + "." + minor : String.format("%d.%d.%d", major, minor, revision);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        return compareToAll((Version) o).allMatch(i -> i == 0);
    }

    public boolean equalsExclRev(Version v) {
        return v == this || major == v.major && minor == v.minor;
    }

    public boolean isCurrent() {
        return equalsExclRev(getCurrent());
    }

    public boolean isCurrentExact() {
        return equals(getCurrent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, revision);
    }

    @Override
    public int compareTo(@NotNull Version version) {
        return isNewerThan(version) ? 1 : isOlderThan(version) ? -1 : 0;
    }

    public IntStream compareToAll(@NotNull Version version) {
        return compareToAll(version, true);
    }

    public IntStream compareToAll(@NotNull Version version, boolean includeRev) {
        IntStream.Builder builder = IntStream.builder()
                .add(Integer.compare(version.major, major))
                .add(Integer.compare(version.minor, minor));
        if (includeRev && (revision != null || version.revision != null))
            builder.add(Integer.compare(version.revision == null ? 0 : version.revision, revision == null ? 0 : revision));

        return builder.build();
    }
}
