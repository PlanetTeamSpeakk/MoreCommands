package com.ptsmods.morecommands.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

public class Version implements Comparable<Version> {
	private static final Version current;

	public static final Version V1_16 = new Version(1, 16);
	public static final Version V1_17 = new Version(1, 17);
	public static final Version V1_18 = new Version(1, 18);
	public static final Version V1_18_2 = new Version(1, 18, 2);
	public static final Version V1_19 = new Version(1, 19);

	static {
		current = parse(new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Version.class.getResourceAsStream("/version.json"))), JsonObject.class).get("release_target").getAsString());
	}

	public final int major, minor, revision;

	public Version(int minor, int revision) {
		this(1, minor, revision);
	}

	public Version(int major, int minor, int revision) {
		this.major = major;
		this.minor = minor;
		this.revision = revision;
	}

	public static Version getCurrent() {
		return current;
	}

	public static Version parse(String version) {
		String[] parts = version.split("\\.");

		if (parts.length < 2 || !Arrays.stream(parts).allMatch(s -> s.chars().allMatch(Character::isDigit)))
			throw new IllegalArgumentException("The given version was invalid");

		return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts.length > 2 ? Integer.parseInt(parts[2]) : 0);
	}

	public boolean isNewerThan(Version version) {
		return major > version.major || major == version.major && (minor > version.minor || minor == version.minor && revision > version.revision);
	}

	public boolean isNewerThanOrEqual(Version version) {
		return equals(version) || isNewerThan(version);
	}

	public boolean isOlderThan(Version version) {
		return major < version.major || major == version.major && (minor < version.minor || minor == version.minor && revision < version.revision);
	}

	@Override
	public String toString() {
		return String.format("%d.%d.%d", major, minor, revision);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Version)) return false;
		Version version = (Version) o;
		return major == version.major && minor == version.minor && revision == version.revision;
	}

	public boolean equalsExclRev(Version v) {
		return v == this || major == v.major && minor == v.minor;
	}

	public boolean isCurr() {
		return equalsExclRev(getCurrent());
	}

	public boolean isCurrExact() {
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
}
