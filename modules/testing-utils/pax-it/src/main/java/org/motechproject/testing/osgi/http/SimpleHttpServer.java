package org.motechproject.testing.osgi.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Utility Class that implements an extremely simple HTTP server that returns a predictable response at a given URI
 *
 * inspired from from http://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
 */
public final class SimpleHttpServer {

    private static final int MIN_PORT = 8080;
    private static final int MAX_PORT = 9080;

    private int port = MIN_PORT;
    private static SimpleHttpServer simpleHttpServer = new SimpleHttpServer();

    private SimpleHttpServer() {  }

    public static SimpleHttpServer getInstance() {
        return simpleHttpServer;
    }

    /**
     * Signals that we were unable to start the Simple HTTP server.
     */
    public class SimpleHttpServerStartException extends RuntimeException {
        SimpleHttpServerStartException(String message) {
            super(message);
        }

        SimpleHttpServerStartException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Starts the HTTP server and uses it to expose the provided resource. The server will start at the first
     * available port between 8080 and 9090.
     * @param resource the path to the resource that will get exposed through the server
     * @param responseCode the response code that will be returned at the path specified by the resource parameter
     * @param responseBody the body that will be served under the path specified by the resource param
     * @return the URL to the resource
     */
    public String start(String resource, int responseCode, String responseBody) {

        HttpServer server = null;
        // Ghetto low tech: loop to find an open port
        do {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
            } catch (IOException e) {
                port++;
            }
        } while (null == server && port < MAX_PORT);

        if (server != null && port < MAX_PORT) {
            try {
                server.createContext(String.format("/%s", resource), new SimpleHttpHandler(responseCode, responseBody));
                server.setExecutor(null);
                server.start();
                String uri = String.format("http://localhost:%d/%s", port, resource);
                // Increase port number for the next guy...
                port++;
                return uri;
            } catch (RuntimeException e) {
                throw new SimpleHttpServerStartException("Unable to start server", e);
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
