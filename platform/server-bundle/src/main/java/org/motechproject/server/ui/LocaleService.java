package org.motechproject.server.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

/**
 * A service responsible for localization. Allows retrieval/settings of user language as well as retrieving
 * localized messages for a user request. Can also be used to retrieve a list of usable languages.
 */
public interface LocaleService {
    Locale getUserLocale(final HttpServletRequest request);

    void setUserLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale);

    NavigableMap<String, String> getAvailableLanguages();

    Map<String, String> getMessages(HttpServletRequest request);
}
