package com.ptsmods.morecommands.api.util.extensions;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.URL;

public class URLExtensions {

	@SneakyThrows
	public static URI toURISneaky(URL self) {
		return self.toURI();
	}
}
