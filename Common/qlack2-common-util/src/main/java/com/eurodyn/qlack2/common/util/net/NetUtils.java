package com.eurodyn.qlack2.common.util.net;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadLocalRandom;

public class NetUtils {

	/**
	 * Checks if a network port is locally available by trying to open a
	 * listening Socket. Note that this method merely checks the current
	 * availability status and does not reserves the port for the caller.
	 * 
	 * @param port
	 *            The port number to check.
	 * @return True if the port is available, False otherwise.
	 * @throws IOException
	 */
	private static boolean isAvailable(int port) throws IOException {
		Socket s = null;
		try {
			s = new Socket("localhost", port);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * Returns a port in the range of not-known ports (49152-65535) that has
	 * already been checked as being available.
	 * 
	 * @return The number of the available port.
	 * @throws IOException
	 */
	public static int getAvailablePort() throws IOException {
		return getAvailablePort(49152, 65535);
	}

	/**
	 * Returns an available port greater than the port specified. 
	 * @param min
	 * @return The number of the available port.
	 * @throws IOException
	 */
	public static int getAvailablePort(int min) throws IOException {
		return getAvailablePort(min, 65535);
	}

	/**
	 * Returns an available port within the specified range.
	 * @param min The minimum port number.
	 * @param max The maximum port number.
	 * @return The number of the available port.
	 * @throws IOException
	 */
	public static int getAvailablePort(int min, int max) throws IOException {
		int port = ThreadLocalRandom.current().nextInt(min, max + 1);
		if (isAvailable(port)) {
			return port;
		} else {
			boolean avail = false;
			while (!avail) {
				port = ThreadLocalRandom.current().nextInt(min, max + 1);
				avail = isAvailable(port);
			}
			return port;
		}
	}

	public static String getURIProtocol(String uri) throws URISyntaxException {
		return new URI(uri).getScheme();
	}
	public static String getURIHost(String uri) throws URISyntaxException {
		return new URI(uri).getHost();
	}
	public static int getURIPort(String uri) throws URISyntaxException {
		return new URI(uri).getPort();
	}
}
