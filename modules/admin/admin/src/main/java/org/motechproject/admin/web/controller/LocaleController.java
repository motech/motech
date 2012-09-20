package org.motechproject.admin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
public class LocaleController {
    @Autowired
    private CookieLocaleResolver localeResolver;

    @RequestMapping(value = "/locale/lang/", method = RequestMethod.GET)
    @ResponseBody
    public String getUserLang(final HttpServletRequest request) {
        return localeResolver.resolveLocale(request).toString();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/locale/lang/", method = RequestMethod.POST)
    public void setUserLang(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(required = true) String language,
                            @RequestParam(required = false, defaultValue = "") String country,
                            @RequestParam(required = false, defaultValue = "") String variant) {
        localeResolver.setLocale(request, response, new Locale(language, country, variant));
    }
}
