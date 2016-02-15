package org.motechproject.osgi.web.http;

import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * This is the extension of the Felix {@link org.osgi.service.http.HttpContext} used by MOTECH.
 * It acts as a decorator for the default context provided by Felix, its only function is to resolve
 * resource names, so that calls to root ({@code /webapp}) map to the {@code index.html} file from the root directory.
 */
public class UiHttpContext implements HttpContext {

    private HttpContext context;

    /**
     * Constructs the instance by decorating the provided HTTP context.
     * @param context the context to decorate
     */
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

    protected HttpContext getContext() {
        return context;
    }

    private String resolveName(String name) {
        String resolvedName = name;
        if ("webapp/".equals(name)) {
            resolvedName = "webapp/index.html";
        }
        return resolvedName;
    }
}
