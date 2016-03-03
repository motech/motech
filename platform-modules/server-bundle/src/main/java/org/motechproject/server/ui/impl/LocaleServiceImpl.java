package org.motechproject.server.ui.impl;

import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.i18n.I18nRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Implementation of the <code>LocaleService</code> interface. Uses the user service for operations
 * related to user language setting/retrieval. It also falls back to a cookie locale resolver for not
 * not logged in users(also users without a language set. It retrieves messages by loading them from registered
 * modules.
 */
@Service("localeService")
public class LocaleServiceImpl implements LocaleService {


    @Autowired
    private MotechUserService userService;

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @Autowired
    private I18nRepository i18nRepository;

    @Override
    public Locale getUserLocale(final HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return cookieLocaleResolver.resolveLocale(request);
        }

        Locale locale = userService.getLocale(request.getUserPrincipal().getName());

        return (locale == null) ? cookieLocaleResolver.resolveLocale(request) : locale;
    }

    @Override
    public void setUserLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
        userService.setLocale(locale);
        setSessionLocale(request, response, locale);
    }

    @Override
    public void setSessionLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {
        cookieLocaleResolver.setLocale(request, response, locale);
    }

    @Override
    public NavigableMap<String, String> getSupportedLanguages() {
        return i18nRepository.getLanguages();
    }

    @Override
    public Map<String, String> getMessages(HttpServletRequest request) {
        return i18nRepository.getMessages(getUserLocale(request));
    }
}
