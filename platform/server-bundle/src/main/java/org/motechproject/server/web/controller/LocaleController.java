package org.motechproject.server.web.controller;

import org.motechproject.server.ui.LocaleSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.NavigableMap;

@Controller
public class LocaleController {

    @Autowired
    private LocaleSettings localeSettings;

    @RequestMapping(value = "/lang", method = RequestMethod.GET)
    @ResponseBody
    public String getUserLang(HttpServletRequest request) {
        return localeSettings.getUserLanguage(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/lang", method = RequestMethod.POST)
    public void setUserLang(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(required = true) String language,
                            @RequestParam(required = false, defaultValue = "") String country,
                            @RequestParam(required = false, defaultValue = "") String variant) {
        localeSettings.setUserLanguage(request, response, new Locale(language, country, variant));
    }

    @RequestMapping(value = "/lang/list", method = RequestMethod.GET)
    @ResponseBody
    public NavigableMap<String, String> getAvailableLanguages() {
        return localeSettings.getAvailableLanguages();
    }
}
