package org.motechproject.testing.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class HttpServiceCheck {

    private static final int MILLIS_PER_SEC = 1000;

    private HttpServiceCheck() {
    }

    public static void waitForLocalPortToListen(int port, int wait) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        do {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", port));
                break;
            } catch (java.net.ConnectException e) {
                System.err.print(e.getMessage() + "\n");
                Thread.sleep(MILLIS_PER_SEC);
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
            Thread.sleep(1 * MILLIS_PER_SEC);
        } while (wait * MILLIS_PER_SEC > (System.currentTimeMillis() - startTime));
    }
}
