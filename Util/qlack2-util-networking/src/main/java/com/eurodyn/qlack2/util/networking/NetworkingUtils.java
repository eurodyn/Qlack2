package com.eurodyn.qlack2.util.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

public class NetworkingUtils {

  public static int getEphemeralFreePort() {
    return getFreePort(49152, 65535);
  }

  public static int getFreePort(int from, int to) {
    int port = ThreadLocalRandom.current().nextInt(from, to);
    while (true) {
      if (isPortFree(port)) {
        return port;
      } else {
        port = ThreadLocalRandom.current().nextInt(from, to);
      }
    }
  }

  public static boolean isPortFree(int port) {
    try {
      new ServerSocket(port).close();
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
