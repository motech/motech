package org.motechproject.server.ui;

import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class UiHttpContext implements HttpContext {

    private HttpContext context;

    public UiHttpContext(HttpContext context) {
        this.context = context;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return context.handleSecurity(request, response);
    }

    @Override
    public URL getResource(String name) {
        return context.getResource(resolveName(name));
    }

    @Override
    public String getMimeType(String name) {
        return context.getMimeType(resolveName(name));
    }

    private String resolveName(String name) {
        String resolvedName = name;
        if ("webapp/".equals(name)) {
            resolvedName = "webapp/index.html";
        }
        return resolvedName;
    }
}
