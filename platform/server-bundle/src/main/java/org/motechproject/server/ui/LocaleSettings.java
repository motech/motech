package org.motechproject.server.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.NavigableMap;

public interface LocaleSettings {
    Locale getUserLocale(final HttpServletRequest request);
    void setUserLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale);
    NavigableMap<String, String> getAvailableLanguages();
}