package org.motechproject.server.web.controller;

import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

@Controller
public class LocaleController {

    @Autowired
    private LocaleSettings localeSettings;

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @RequestMapping(value = "/lang", method = RequestMethod.GET)
    @ResponseBody
    public String getUserLang(HttpServletRequest request) {
        return localeSettings.getUserLocale(request).getLanguage();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/lang", method = RequestMethod.POST)
    public void setUserLang(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(required = true) String language,
                            @RequestParam(required = false, defaultValue = "") String country,
                            @RequestParam(required = false, defaultValue = "") String variant) {
        localeSettings.setUserLocale(request, response, new Locale(language, country, variant));
    }

    @RequestMapping(value = "/lang/list", method = RequestMethod.GET)
    @ResponseBody
    public NavigableMap<String, String> getAvailableLanguages() {
        return localeSettings.getAvailableLanguages();
    }

    @RequestMapping(value = "/lang/locate", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> getLangLocalisation() {
        Map<String, List<String>> i18n = new HashMap<>();

        for (String s : Arrays.asList("dashboard", "startup")) {
            i18n.put(s, new ArrayList<String>());
            i18n.get(s).add("resources/messages/");
        }

        Map<String, Collection<ModuleRegistrationData>> modules = uiFrameworkService.getRegisteredModules();

        for (Map.Entry<String, Collection<ModuleRegistrationData>> entry : modules.entrySet()) {
            for (ModuleRegistrationData module : entry.getValue()) {
                for (Map.Entry<String, String> bundle : module.getI18n().entrySet()) {
                    if (!i18n.containsKey(bundle.getKey())) {
                        i18n.put(bundle.getKey(), new ArrayList<String>());
                    }

                    i18n.get(bundle.getKey()).add(bundle.getValue());
                }
            }
        }

        return i18n;
    }
}
