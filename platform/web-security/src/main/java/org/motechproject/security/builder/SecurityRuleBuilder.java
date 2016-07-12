package org.motechproject.security.builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.security.authentication.MotechAccessVoter;
import org.motechproject.security.authentication.MotechLogoutSuccessHandler;
import org.motechproject.security.authentication.MotechRestBasicAuthenticationEntryPoint;
import org.motechproject.security.chain.MotechSecurityFilterChain;
import org.motechproject.security.constants.HTTPMethod;
import org.motechproject.security.constants.Protocol;
import org.motechproject.security.constants.Scheme;
import org.motechproject.security.constants.SecurityConfigConstants;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.config.SettingsFacade;
import org.motechproject.security.exception.SecurityConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.channel.ChannelDecisionManager;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.security.constants.HTTPMethod.ANY;
import static org.motechproject.security.constants.Protocol.HTTP;

/**
 * The security rule builder is responsible for building a
 * SecurityFilterChain, which consists of a matcher pattern
 * and a list of Spring security filters. The filters are
 * created and configured base upon the security rule's
 * settings.
 */
@Component
public class SecurityRuleBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityRuleBuilder.class);

    public static final String NO_PATTERN_EXCEPTION_MESSAGE = "Pattern must be defined in security config";
    public static final String NO_PROTOCOL_EXCEPTION_MESSAGE = "Protocol must be defined in security config";
    public static final String NO_SUPPORTED_SCHEMES_EXCEPTION_MESSAGE = "Security rule must specify supported schemes";
    public static final String NO_METHODS_REQUIRED_EXCEPTION_MESSAGE = "Security rule must specify required methods";

    private ChannelDecisionManager channelDecisionManager;
    private AuthenticationManager authenticationManager;
    private SettingsFacade settingsFacade;
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;
    private OpenIDAuthenticationFilter openIDAuthenticationFilter;
    private MotechLogoutSuccessHandler motechLogoutHandler;
    private AuthenticationEntryPoint basicAuthenticationEntryPoint;
    private AuthenticationEntryPoint loginAuthenticationEntryPoint;

    /**
     * Builds SecurityFilterChain which is capable of being
     * matched against HttpServletRequest in order to decide
     * whether it applies to that request
     *
     * @param securityRule that will be used as pattern
     * @param method to be used in filter
     * @return new filter chain with security rule, matcher and filters
     */
    public synchronized SecurityFilterChain buildSecurityChain(MotechURLSecurityRule securityRule, HTTPMethod method) {
        LOGGER.info("Building security chain for rule: {} and method: {}", securityRule.getPattern(), method);

        List<Filter> filters = new ArrayList<>();
        RequestMatcher matcher;

        validateRule(securityRule);

        String pattern = securityRule.getPattern();

        if (pattern.equals(SecurityConfigConstants.ANY_PATTERN) || "/**".equals(pattern) || "**".equals(pattern)) {
            matcher = AnyRequestMatcher.INSTANCE;
        } else if (ANY == method) {
            matcher = new AntPathRequestMatcher(pattern);
        } else {
            matcher = new AntPathRequestMatcher(pattern, method.name());
        }

        if (!noSecurity(securityRule)) {
            try {
                filters = addFilters(securityRule);
            } catch (ServletException e) {
                LOGGER.error("Cannot create {} in {} security rule.", SecurityContextHolderAwareRequestFilter.class, securityRule.getPattern(), e);
            }
        }

        LOGGER.info("Built security chain for rule: {} and method: {}", securityRule.getPattern(), method);

        return new MotechSecurityFilterChain(securityRule, matcher, filters);
    }

    private void validateRule(MotechURLSecurityRule securityRule) {
        String msg = null;

        if (StringUtils.isEmpty(securityRule.getPattern())) {
            msg = NO_PATTERN_EXCEPTION_MESSAGE;
        } else if (securityRule.getProtocol() == null) {
            msg = NO_PROTOCOL_EXCEPTION_MESSAGE;
        } else if (CollectionUtils.isEmpty(securityRule.getSupportedSchemes())) {
            msg = NO_SUPPORTED_SCHEMES_EXCEPTION_MESSAGE;
        } else if (CollectionUtils.isEmpty(securityRule.getMethodsRequired())) {
            msg = NO_METHODS_REQUIRED_EXCEPTION_MESSAGE;
        }

        if (null != msg) {
            throw new SecurityConfigException(msg);
        }
    }

    private static boolean noSecurity(MotechURLSecurityRule securityRule) {
        boolean result = true;

        if (securityRule.getProtocol() != HTTP) {
            result = false;
        }

        if (!securityRule.getSupportedSchemes().contains(Scheme.NO_SECURITY)) {
            result = false;
        }

        if (!CollectionUtils.isEmpty(securityRule.getPermissionAccess())) {
            result = false;
        }

        if (!CollectionUtils.isEmpty(securityRule.getUserAccess())) {
            result = false;
        }

        return result;
    }

    private List<Filter> addFilters(MotechURLSecurityRule securityRule) throws ServletException {
        List<Filter> filters = new ArrayList<>();

        SecurityContextRepository contextRepository = new HttpSessionSecurityContextRepository();
        RequestCache requestCache = new HttpSessionRequestCache();

        addSecureChannel(filters, securityRule.getProtocol());
        addSecurityContextPersistenceFilter(filters, contextRepository);
        addLogoutFilter(filters, securityRule);
        addAuthenticationFilters(filters, securityRule);
        addRequestCacheFilter(filters, requestCache);
        addSecurityContextHolderAwareRequestFilter(filters);
        addAnonymousAuthenticationFilter(filters);
        addSessionManagementFilter(filters, contextRepository);
        addExceptionTranslationFilter(filters, requestCache, securityRule.isRest());
        addFilterSecurityInterceptor(filters, securityRule);

        return filters;
    }

    private void addSessionManagementFilter(List<Filter> filters, SecurityContextRepository contextRepository) {
        SessionManagementFilter sessionManagementFilter = new SessionManagementFilter(contextRepository);
        filters.add(sessionManagementFilter);
    }

    private void addLogoutFilter(List<Filter> filters, MotechURLSecurityRule securityRule) {
        if (securityRule.isRest()) {
            return;
        }

        LogoutHandler springLogoutHandler = new SecurityContextLogoutHandler();
        LogoutFilter logoutFilter = new LogoutFilter("/server/login", motechLogoutHandler, springLogoutHandler);
        logoutFilter.setFilterProcessesUrl("/server/j_spring_security_logout");
        filters.add(logoutFilter);
    }

    private void addSecurityContextHolderAwareRequestFilter(List<Filter> filters) throws ServletException {
        SecurityContextHolderAwareRequestFilter securityFilter = new SecurityContextHolderAwareRequestFilter();
        securityFilter.setTrustResolver(new AuthenticationTrustResolverImpl());
        securityFilter.afterPropertiesSet();
        filters.add(securityFilter);
    }

    private void addRequestCacheFilter(List<Filter> filters, RequestCache requestCache) {
        RequestCacheAwareFilter cacheFilter = new RequestCacheAwareFilter(requestCache);
        filters.add(cacheFilter);
    }

    private void addFilterSecurityInterceptor(List<Filter> filters, MotechURLSecurityRule securityRule) {
        Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();

        List<AccessDecisionVoter> voters = new ArrayList<>();
        Collection<ConfigAttribute> configAtts = new ArrayList<>();

        if (CollectionUtils.isEmpty(securityRule.getPermissionAccess()) && CollectionUtils.isEmpty(securityRule.getUserAccess())) {
            configAtts.add(new SecurityConfig("IS_AUTHENTICATED_FULLY"));
            AuthenticatedVoter authVoter = new AuthenticatedVoter();
            voters.add(authVoter);
        } else {
            if (!CollectionUtils.isEmpty(securityRule.getPermissionAccess())) {
                for (String permission : securityRule.getPermissionAccess()) {
                    configAtts.add(new SecurityConfig(permission));
                }
            }
            if (!CollectionUtils.isEmpty(securityRule.getUserAccess())) {
                for (String userAccess : securityRule.getUserAccess()) {
                    configAtts.add(new SecurityConfig(SecurityConfigConstants.USER_ACCESS_PREFIX + userAccess));
                }
            }
        }

        buildRequestMap(requestMap, configAtts, securityRule);

        FilterInvocationSecurityMetadataSource metadataSource = new DefaultFilterInvocationSecurityMetadataSource((LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>) requestMap);

        FilterSecurityInterceptor interceptor = new FilterSecurityInterceptor();
        interceptor.setSecurityMetadataSource(metadataSource);

        RoleVoter roleVoter = new RoleVoter();

        roleVoter.setRolePrefix(SecurityConfigConstants.ROLE_ACCESS_PREFIX);
        voters.add(roleVoter);

        voters.add(new MotechAccessVoter());

        AccessDecisionManager decisionManager = new AffirmativeBased(voters);

        interceptor.setAccessDecisionManager(decisionManager);
        interceptor.setAuthenticationManager(authenticationManager);

        filters.add(interceptor);
    }


    private void buildRequestMap(Map<RequestMatcher, Collection<ConfigAttribute>> requestMap,
                                 Collection<ConfigAttribute> configAtts, MotechURLSecurityRule securityRule) {
        String pattern = securityRule.getPattern();

        for (HTTPMethod method : securityRule.getMethodsRequired()) {
            RequestMatcher matcher;

            if (securityRule.getMethodsRequired().contains(ANY) &&
                    (pattern.equals(SecurityConfigConstants.ANY_PATTERN) || "/**".equals(pattern))) {
                matcher = AnyRequestMatcher.INSTANCE;
            } else if (securityRule.getMethodsRequired().contains(ANY)) {
                matcher = new AntPathRequestMatcher(pattern, null);
            } else {
                matcher = new AntPathRequestMatcher(pattern, method.name());
            }

            requestMap.put(matcher, configAtts);
        }
    }

    private void addExceptionTranslationFilter(List<Filter> filters, RequestCache requestCache, boolean isRest) {
        ExceptionTranslationFilter exceptionFilter;

        if (isRest) {
            exceptionFilter = new ExceptionTranslationFilter(basicAuthenticationEntryPoint, requestCache);
        } else {
            exceptionFilter = new ExceptionTranslationFilter(loginAuthenticationEntryPoint, requestCache);
        }

        filters.add(exceptionFilter);
    }

    private void addAnonymousAuthenticationFilter(List<Filter> filters) {
        SecureRandom random = new SecureRandom();
        AnonymousAuthenticationFilter anonFilter = new AnonymousAuthenticationFilter(Long.toString(random.nextLong()));
        filters.add(anonFilter);
    }

    private void addAuthenticationFilters(List<Filter> filters, MotechURLSecurityRule securityRule) {
        List<Scheme> supportedSchemes = securityRule.getSupportedSchemes();

        if (securityRule.isRest()) {
            if (supportedSchemes.contains(Scheme.BASIC)) {
                MotechRestBasicAuthenticationEntryPoint restAuthPoint = new MotechRestBasicAuthenticationEntryPoint(settingsFacade);
                BasicAuthenticationFilter basicAuthFilter = new BasicAuthenticationFilter(authenticationManager, restAuthPoint);
                filters.add(basicAuthFilter);
            }
        } else {
            if (supportedSchemes.contains(Scheme.USERNAME_PASSWORD)) {
                filters.add(usernamePasswordAuthenticationFilter);
            }
            if (supportedSchemes.contains(Scheme.OPEN_ID)) {
                filters.add(openIDAuthenticationFilter);
            }
        }
    }

    private void addSecurityContextPersistenceFilter(List<Filter> filters, SecurityContextRepository contextRepository) {
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(contextRepository);
        filters.add(securityContextFilter);
    }

    private void addSecureChannel(List<Filter> filters, Protocol protocol) {
        ChannelProcessingFilter channelProcessingFilter = new ChannelProcessingFilter();
        channelProcessingFilter.setChannelDecisionManager(channelDecisionManager);

        RequestMatcher anyRequest = AnyRequestMatcher.INSTANCE;

        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();
        Collection<ConfigAttribute> configAtts = new ArrayList<>();

        switch (protocol) {
            case HTTP:
                configAtts.add(new SecurityConfig("ANY_CHANNEL"));
                break;
            case HTTPS:
                configAtts.add(new SecurityConfig("REQUIRES_SECURE_CHANNEL"));
                break;
            default:
        }

        requestMap.put(anyRequest, configAtts);
        FilterInvocationSecurityMetadataSource securityMetadataSource = new DefaultFilterInvocationSecurityMetadataSource(requestMap);
        channelProcessingFilter.setSecurityMetadataSource(securityMetadataSource);

        filters.add(channelProcessingFilter);
    }

    @Autowired
    public void setChannelDecisionManager(ChannelDecisionManager channelDecisionManager) {
        this.channelDecisionManager = channelDecisionManager;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @Autowired
    public void setUsernamePasswordAuthenticationFilter(UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter) {
        this.usernamePasswordAuthenticationFilter = usernamePasswordAuthenticationFilter;
    }

    @Autowired
    public void setOpenIDAuthenticationFilter(OpenIDAuthenticationFilter openIDAuthenticationFilter) {
        this.openIDAuthenticationFilter = openIDAuthenticationFilter;
    }

    @Autowired
    public void setMotechLogoutHandler(MotechLogoutSuccessHandler motechLogoutHandler) {
        this.motechLogoutHandler = motechLogoutHandler;
    }

    @Autowired
    @Qualifier("basicAuthenticationEntryPoint")
    public void setBasicAuthenticationEntryPoint(AuthenticationEntryPoint basicAuthenticationEntryPoint) {
        this.basicAuthenticationEntryPoint = basicAuthenticationEntryPoint;
    }

    @Autowired
    @Qualifier("loginFormAuthentication")
    public void setLoginAuthenticationEntryPoint(AuthenticationEntryPoint loginAuthenticationEntryPoint) {
        this.loginAuthenticationEntryPoint = loginAuthenticationEntryPoint;
    }
}
