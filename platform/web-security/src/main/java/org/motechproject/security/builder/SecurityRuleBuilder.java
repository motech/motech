package org.motechproject.security.builder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.security.authentication.MotechAccessVoter;
import org.motechproject.security.authentication.MotechLogoutSuccessHandler;
import org.motechproject.security.authentication.MotechRestBasicAuthenticationEntryPoint;
import org.motechproject.security.constants.SecurityConfigConstants;
import org.motechproject.security.domain.MotechURLSecurityRule;
import org.motechproject.security.ex.SecurityConfigException;
import org.motechproject.server.config.SettingsFacade;
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
import org.springframework.security.openid.OpenIDAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
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
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.AnyRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * The security rule builder is responsible for building a
 * SecurityFilterChain, which consists of a matcher pattern
 * and a list of Spring security filters. The filters are
 * created and configured base upon the security rule's
 * settings.
 *
 */
@Component
public class SecurityRuleBuilder {

    public static final String NO_PATTERN_EXCEPTION_MESSAGE = "Pattern must be defined in security config";
    public static final String NO_PROTOCOL_EXCEPTION_MESSAGE = "Protocol must be defined in security config";
    public static final String NO_SUPPORTED_SCHEMES_EXCEPTION_MESSAGE = "Security rule must specify supported schemes";
    public static final String NO_METHODS_REQUIRED_EXCEPTION_MESSAGE = "Security rule must specify required methods";

    @Autowired
    private ChannelDecisionManager channelDecisionManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

    @Autowired
    private OpenIDAuthenticationFilter openIDAuthenticationFilter;

    @Autowired
    private MotechLogoutSuccessHandler motechLogoutHandler;

    @Autowired
    @Qualifier("basicAuthenticationEntryPoint")
    private AuthenticationEntryPoint basicAuthenticationEntryPoint;

    @Autowired
    @Qualifier("loginFormAuthentication")
    private AuthenticationEntryPoint loginAuthenticationEntryPoint;

    public synchronized SecurityFilterChain buildSecurityChain(MotechURLSecurityRule securityRule, String method) {
        List<Filter> filters = new ArrayList<>();
        RequestMatcher matcher;

        validateRule(securityRule);

        String pattern = securityRule.getPattern();

        if (pattern.equals(SecurityConfigConstants.ANY_PATTERN) || pattern.equals("/**") || pattern.equals("**")) {
            matcher = new AnyRequestMatcher();
        } else {
            if (SecurityConfigConstants.ANY_PATTERN.equals(method)) {
                matcher = new AntPathRequestMatcher(pattern);
            } else {
                matcher = new AntPathRequestMatcher(pattern, method);
            }
        }

        if (!noSecurity(securityRule)) {
            filters = addFilters(securityRule);
        }

        return new DefaultSecurityFilterChain(matcher, filters);
    }

    private void validateRule(MotechURLSecurityRule securityRule) {

        if (StringUtils.isEmpty(securityRule.getPattern())) {
            throw new SecurityConfigException(NO_PATTERN_EXCEPTION_MESSAGE);
        } else if (StringUtils.isEmpty(securityRule.getProtocol()) ) {
            throw new SecurityConfigException(NO_PROTOCOL_EXCEPTION_MESSAGE);
        } else if (CollectionUtils.isEmpty(securityRule.getSupportedSchemes())) {
            throw new SecurityConfigException(NO_SUPPORTED_SCHEMES_EXCEPTION_MESSAGE);
        } else if (CollectionUtils.isEmpty(securityRule.getMethodsRequired())) {
            throw new SecurityConfigException(NO_METHODS_REQUIRED_EXCEPTION_MESSAGE);
        }
    }

    private static boolean noSecurity(MotechURLSecurityRule securityRule) {

        if (!securityRule.getProtocol().equals(SecurityConfigConstants.HTTP)) {
            return false;
        }
        if (!securityRule.getSupportedSchemes().contains(SecurityConfigConstants.NO_SECURITY)) {
            return false;
        }
        if (!CollectionUtils.isEmpty(securityRule.getPermissionAccess())) {
            return false;
        }
        if (!CollectionUtils.isEmpty(securityRule.getUserAccess())) {
            return false;
        }

        return true;
    }

    private List<Filter> addFilters(MotechURLSecurityRule securityRule) {
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
        LogoutFilter logoutFilter = new LogoutFilter("/module/server/login", motechLogoutHandler, springLogoutHandler );
        logoutFilter.setFilterProcessesUrl("/module/server/j_spring_security_logout");
        filters.add(logoutFilter);
    }

    private void addSecurityContextHolderAwareRequestFilter(List<Filter> filters) {
        SecurityContextHolderAwareRequestFilter securityFilter = new SecurityContextHolderAwareRequestFilter();
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


    private void buildRequestMap(Map<RequestMatcher, Collection<ConfigAttribute>> requestMap, Collection<ConfigAttribute> configAtts, MotechURLSecurityRule securityRule) {
        String pattern = securityRule.getPattern();

        for (String method : securityRule.getMethodsRequired()) {
            RequestMatcher matcher;

            if (securityRule.getMethodsRequired().contains(SecurityConfigConstants.ANY_METHOD) && (pattern.equals(SecurityConfigConstants.ANY_PATTERN) || pattern.equals("/**"))) {
                matcher = new AnyRequestMatcher();
                requestMap.put(matcher, configAtts);
                return;
            } else if (securityRule.getMethodsRequired().contains(SecurityConfigConstants.ANY_METHOD)){
                matcher = new AntPathRequestMatcher(pattern, null);
                requestMap.put(matcher, configAtts);
                return;
            } else {
                matcher = new AntPathRequestMatcher(pattern, method);
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
        List<String> supportedSchemes = securityRule.getSupportedSchemes();

        if (securityRule.isRest()) {
            if (supportedSchemes.contains(SecurityConfigConstants.BASIC)) {
                MotechRestBasicAuthenticationEntryPoint restAuthPoint = new MotechRestBasicAuthenticationEntryPoint(settingsFacade);
                BasicAuthenticationFilter basicAuthFilter = new BasicAuthenticationFilter(authenticationManager, restAuthPoint);
                filters.add(basicAuthFilter);
            }
        } else {
            if (supportedSchemes.contains(SecurityConfigConstants.USERNAME_PASSWORD)) {
                filters.add(usernamePasswordAuthenticationFilter);
            }
            if (supportedSchemes.contains(SecurityConfigConstants.OPEN_ID)) {
                filters.add(openIDAuthenticationFilter);
            }
        }
    }

    private void addSecurityContextPersistenceFilter(List<Filter> filters, SecurityContextRepository contextRepository) {
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(contextRepository);
        filters.add(securityContextFilter);
    }

    private void addSecureChannel(List<Filter> filters, String protocol) {
        ChannelProcessingFilter channelProcessingFilter = new ChannelProcessingFilter();
        channelProcessingFilter.setChannelDecisionManager(channelDecisionManager);
        LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<>();
        RequestMatcher anyRequest = new AnyRequestMatcher();
        Collection<ConfigAttribute> configAtts = new ArrayList<>();

        if (SecurityConfigConstants.HTTP.equals(protocol)) {
            configAtts.add(new SecurityConfig("ANY_CHANNEL"));
            requestMap.put(anyRequest, configAtts);
            FilterInvocationSecurityMetadataSource securityMetadataSource = new DefaultFilterInvocationSecurityMetadataSource(requestMap);
            channelProcessingFilter.setSecurityMetadataSource(securityMetadataSource);
        } else if (SecurityConfigConstants.HTTPS.equals(protocol)){
            configAtts.add(new SecurityConfig("REQUIRES_SECURE_CHANNEL"));
            requestMap.put(anyRequest, configAtts);
            FilterInvocationSecurityMetadataSource securityMetadataSource = new DefaultFilterInvocationSecurityMetadataSource(requestMap);
            channelProcessingFilter.setSecurityMetadataSource(securityMetadataSource);
        }

        filters.add(channelProcessingFilter);
    }
}
