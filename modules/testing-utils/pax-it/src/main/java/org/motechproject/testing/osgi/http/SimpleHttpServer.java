package org.motechproject.testing.osgi.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility Class that implements an extremely simple HTTP server that returns a predictable response at a given URI
 *
 * inspired from from http://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
 */
public final class SimpleHttpServer {

    private static final int MIN_PORT = 8080;
    private static final int MAX_PORT = 9080;

    private int port = MIN_PORT;
    private Set<HttpServer> servers = new HashSet<>();
    private static SimpleHttpServer simpleHttpServer = new SimpleHttpServer();

    private SimpleHttpServer() {  }

    public static SimpleHttpServer getInstance() {
        return simpleHttpServer;
    }

    public class SimpleHttpServerStartException extends RuntimeException {
        SimpleHttpServerStartException(String message) {
            super(message);
        }
    }

    public String start(String resource, int responseCode, String responseBody) {

        HttpServer server = null;
        // Ghetto low tech: loop to find an open port
        do {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                servers.add(server);
            } catch (IOException e) {
                port++;
            }
        } while (null == server && port < MAX_PORT);

        if (port < MAX_PORT) {
            try {
                server.createContext(String.format("/%s", resource), new SimpleHttpHandler(responseCode, responseBody));
                server.setExecutor(null);
                server.start();
                String uri = String.format("http://localhost:%d/%s", port, resource);
                // Increase port number for the next guy...
                port++;
                return uri;
            } catch (Exception e) {
                throw new SimpleHttpServerStartException("Unable to start server: " + e);
            }
        }

        throw new SimpleHttpServerStartException("Unable to find an open port");
    }

    private class SimpleHttpHandler implements HttpHandler {
        private int responseCode;
        private String responseBody;

        public SimpleHttpHandler(int responseCode, String responseBody) {
            this.responseCode = responseCode;
            this.responseBody = responseBody;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            httpExchange.sendResponseHeaders(responseCode, responseBody.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(responseBody.getBytes());
            os.close();
        }
    }
}
