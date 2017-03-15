package com.eurodyn.qlack2.common.util.util;

/**
 * A generic implementation to hold a variable within successive calls 
 * in the same thread. Note that this helper should only be used when you are 
 * sure your calls are executed in the same thread context.
 *
 */
public class TokenHolder {
	private static final ThreadLocal<String> token = new ThreadLocal<>();

	public static String getToken() {
		return token.get();
	}

	public static void setToken(String newToken) {
		token.set(newToken);
	}

	public static void removeToken() {
		token.remove();
	}
}
