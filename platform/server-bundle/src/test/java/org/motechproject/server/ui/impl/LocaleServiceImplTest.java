package org.motechproject.server.ui.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.i18n.I18nRepository;
import org.motechproject.server.ui.impl.LocaleServiceImpl;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocaleServiceImplTest {

    @Mock
    private MotechUserService userService;

    @Mock
    private Principal principal;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CookieLocaleResolver cookieLocaleResolver;

    @Mock
    private I18nRepository i18nRepository;

    @InjectMocks
    private LocaleService localeService = new LocaleServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldRetrieveMessages() {
        Map<String, String> msgs = new HashMap<>();
        msgs.put("key1", "guten tag");
        msgs.put("key2", "auf wiedersehen");
        when(i18nRepository.getMessages(Locale.GERMAN)).thenReturn(msgs);
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.GERMAN);

        Map<String, String> result = localeService.getMessages(request);

        assertEquals(msgs, result);
        verify(i18nRepository).getMessages(Locale.GERMAN);
    }

    @Test
    public void shouldRetrieveLocales() {
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.CHINA);

        assertEquals(Locale.CHINA, localeService.getUserLocale(request));

        when(userService.getLocale("user")).thenReturn(Locale.GERMANY);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(principal.getName()).thenReturn("user");

        assertEquals(Locale.GERMANY, localeService.getUserLocale(request));

        when(userService.getLocale("user")).thenReturn(null);
        when(cookieLocaleResolver.resolveLocale(request)).thenReturn(Locale.CANADA);

        assertEquals(Locale.CANADA, localeService.getUserLocale(request));
    }
}
