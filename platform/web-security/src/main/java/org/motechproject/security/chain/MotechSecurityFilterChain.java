package org.motechproject.security.chain;

import org.motechproject.security.domain.MotechURLSecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.RequestMatcher;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements Spring's {@link org.springframework.security.web.SecurityFilterChain} and
 * adds logging which helps to identify which security rules has matched the request. Each filter chain
 * represents one security rule. This filter chain is based on
 * {@link org.springframework.security.web.DefaultSecurityFilterChain}
 */
public class MotechSecurityFilterChain implements SecurityFilterChain {

    private static final Logger LOG = LoggerFactory.getLogger(MotechSecurityFilterChain.class);

    private final MotechURLSecurityRule securityRule;
    private final RequestMatcher requestMatcher;
    private final List<Filter> filters;

    public MotechSecurityFilterChain(MotechURLSecurityRule securityRule, RequestMatcher requestMatcher, Filter... filters) {
        this(securityRule, requestMatcher, Arrays.asList(filters));
    }

    public MotechSecurityFilterChain(MotechURLSecurityRule securityRule, RequestMatcher requestMatcher, List<Filter> filters) {
        this.requestMatcher = requestMatcher;
        this.filters = new ArrayList<>(filters);
        this.securityRule = securityRule;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        boolean matches = requestMatcher.matches(request);
        if (matches && LOG.isDebugEnabled()) {
            LOG.debug("Security Rule " + securityRule + " matches request for " + request.getPathInfo());
        }
        return matches;
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    @Override
    public String toString() {
        return "[ " + requestMatcher + ", " + filters + "]";
    }
}
