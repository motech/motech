package org.motechproject.security.filter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

public class ResourcesFilter implements Filter {
    private FilterConfig filterConfig;

    public void reInit() throws ServletException {
        init(filterConfig);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if (path.startsWith("/static") || path.startsWith("/WEB-INF")) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
