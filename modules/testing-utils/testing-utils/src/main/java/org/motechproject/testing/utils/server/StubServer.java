package org.motechproject.testing.utils.server;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.motechproject.commons.api.MotechException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StubServer {

    public static final String OK = "<status>success</status>";
    private final Server server;

    private Map<String, RequestInfo> requests = new HashMap<>();

    public StubServer(int port, String contextPath) {
        server = new Server(port);
        Context context = new Context(server, contextPath);
        context.addServlet(new ServletHolder(createServlet()), "/*");
        server.setHandler(context);
    }


    public StubServer start() {
        try {
            server.start();
        } catch (Exception e) {
            throw new MotechException("Stub Sever Could not be started", e);
        }
        return this;
    }


    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            new MotechException("Stub Server could not be stopped", e);
        }
    }


    public boolean waitingForRequests() {
        return requests.isEmpty();
    }


    public RequestInfo detailForRequest(String contextPath) {
        return requests.get(contextPath);
    }

    private DefaultServlet createServlet() {
        return new DefaultServlet() {

            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                requests.put(request.getContextPath(), collectRequestInfo(request));
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(OK);
                Request baseRequest = (Request) request;
                baseRequest.setHandled(true);
            }

            private RequestInfo collectRequestInfo(HttpServletRequest request) {
                Map<String, String> map = new HashMap<>();
                String queryString = request.getQueryString();
                String[] queryParameters = queryString.split("&");
                for (String queryParam : queryParameters) {
                    String[] keyValuePair = queryParam.split("=");
                    map.put(keyValuePair[0], keyValuePair[1]);
                }
                return new RequestInfo(request.getContextPath(), map);
            }
        };
    }
}

