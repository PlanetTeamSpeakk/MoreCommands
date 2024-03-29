package com.ptsmods.morecommands.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ptsmods.morecommands.api.util.extensions.URLExtensions;
import lombok.Getter;
import lombok.With;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

@ExtensionMethod(URLExtensions.class)
public class Version implements Comparable<Version> {
    @Getter
    private static final Version current;

    public static final Version V1_17 = new Version(17);
    public static final Version V1_18 = new Version(18);
    public static final Version V1_18_0 = new Version(18, 0);
    public static final Version V1_18_2 = new Version(18, 2);
    public static final Version V1_19 = new Version(19);
    public static final Version V1_19_0 = new Version(19, 0);
    public static final Version V1_19_1 = new Version(19, 1);
    public static final Version V1_19_2 = new Version(19, 2);
    public static final Version V1_19_3 = new Version(19, 3);
    public static final Version V1_19_4 = new Version(19, 4);

    static {
        Version current0;
        try {
            Path minecraftJar = MoreCommandsArch.getMinecraftJar();
            Stream<Path> jarStream = Files.list(minecraftJar.getParent());
            // With Forge, at least on MultiMC, the jar is split into two files,
            // one containing assets, data and the version file, the other containing code.
            // The one containing code is the one returned by #getMinecraftJar(), but we need the other one.
            ZipFile jar = jarStream.filter(p -> p.getFileName().toString().endsWith(".jar"))
                    .map(p -> {
                        try {
                            return new ZipFile(p.toFile());
                        } catch (IOException e) {
                            IMoreCommands.LOG.warn("Could not read jar file " + p);
                            IMoreCommands.LOG.catching(e);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(zip -> {
                        boolean hasVersion = zip.getEntry("version.json") != null;
                        if (!hasVersion) try {
                            zip.close();
                        } catch (IOException e) {
                            IMoreCommands.LOG.warn("Could not close zip file.");
                            IMoreCommands.LOG.catching(e);
                        }

                        return hasVersion;
                    })
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Could not find jar containing version.json, MoreCommands cannot be loaded."));
            jarStream.close();

            JsonObject versionObj = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(
                    jar.getInputStream(jar.getEntry("version.json")))), JsonObject.class);

            if (versionObj.has("release_target"))
                current0 = parse(versionObj.get("release_target").getAsString());
            else try {
                current0 = parse(versionObj.get("name").getAsString());
            } catch (IllegalArgumentException e) {
                // Name is probably of a snapshot. Since we don't support snapshots anymore in version 4
                // (because Forge doesn't support em), we can simply ignore it.
                throw new IllegalStateException("This Minecraft version is not supported.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (AssertionError e) {
            // Only happens when running tests.
            current0 = V1_19_2;
        }

        current = current0;
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

    @Override
    public int hashCode() {
        return (major * 31 + minor) * 31 + (revision == null ? 0 : revision);
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
