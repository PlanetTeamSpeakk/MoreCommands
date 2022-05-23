package com.ptsmods.morecommands.api.util.extensions;

import java.util.stream.Stream;

public class StringExtensions {

    public static Stream<Character> charStream(String self) {
        return self.chars().mapToObj(i -> (char) i);
    }
}
