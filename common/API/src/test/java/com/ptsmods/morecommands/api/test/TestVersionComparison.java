package com.ptsmods.morecommands.api.test;

import com.ptsmods.morecommands.api.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestVersionComparison {

    @Test
    void testRevisionComparison() {
        Version v19 = Version.V1_19;
        Version v190 = Version.V1_19_0;
        Version v192 = Version.V1_19_2;
        Version v18 = Version.V1_18;
        Version v182 = Version.V1_18_2;

        assertEquals(-1, v19.compareTo(v192));
        assertEquals(1, v192.compareTo(v19));
        assertEquals(0, v19.compareTo(v190));
        assertEquals(1, v19.compareTo(v18));
        assertEquals(-1, v182.compareTo(v19));
    }
}
