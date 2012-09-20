package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.web.controller.LocaleController;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocaleControllerTest {
    @Mock
    CookieLocaleResolver cookieLocaleResolver;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    @InjectMocks
    LocaleController controller = new LocaleController();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetUserLang() throws Exception {
        Locale locale = Locale.ENGLISH;
        when(cookieLocaleResolver.resolveLocale(httpServletRequest)).thenReturn(locale);

        String userLang = controller.getUserLang(httpServletRequest);

        verify(cookieLocaleResolver).resolveLocale(any(HttpServletRequest.class));
        assertEquals(locale.toString(), userLang);
    }

    @Test
    public void testSetUserLang() throws Exception {
        String language = "th";
        String country = "TH";
        String variant = "TH";
        Locale locale = new Locale(language, country, variant);

        controller.setUserLang(httpServletRequest, httpServletResponse, language, country, variant);

        verify(cookieLocaleResolver).setLocale(httpServletRequest, httpServletResponse, locale);
    }
}
