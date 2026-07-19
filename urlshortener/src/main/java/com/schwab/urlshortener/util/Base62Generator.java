package com.schwab.urlshortener.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Base62Generator {

	private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private Base62Generator() {
	}

	public static String generateShortCode(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));

			long number = 0;
			for (int i = 0; i < 8; i++) {
				number = (number << 8) | (hash[i] & 0xff);
			}
			number = number & Long.MAX_VALUE;
			return encode(number);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String encode(long value) {
		if (value == 0) {
			return "0";
		}

		StringBuilder builder = new StringBuilder();
		while (value > 0) {
			builder.append(BASE62.charAt((int) (value % 62)));
			value /= 62;
		}
		return builder.reverse().toString();
	}

}