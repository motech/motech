package org.motechproject.server.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.server.web.dto.LocaleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * The <code>LocaleController</code> class is responsible for handling requests connected with internationalization
 */

@Controller
@Api(value = "LocaleController", description = "The <code>LocaleController</code> class is responsible for handling requests " +
        "connected with internationalization")
public class LocaleController {

    @Autowired
    private LocaleService localeService;

    @RequestMapping(value = "/lang", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Returns the current language")
    @ResponseBody
    public String getUserLang(HttpServletRequest request) {
        return localeService.getUserLocale(request).getLanguage();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/userlang", method = RequestMethod.POST)
    @ApiOperation(value = "Sets the current language with the given locale for user")
    // The inconsistency in mapping address for this endpoint is caused by the need to bypass the security rule.
    public void setUserLang(HttpServletRequest request, HttpServletResponse response,
                            @RequestBody LocaleDto localeDto) {
        localeService.setUserLocale(request, response, localeDto.toLocale());
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/lang/session", method = RequestMethod.POST)
    @ApiOperation(value = "Sets the current language with the given locale for session")
    public void setSessionLang(HttpServletRequest request, HttpServletResponse response,
                               @RequestBody LocaleDto localeDto) {
        localeService.setSessionLocale(request, response, localeDto.toLocale());
    }

    @RequestMapping(value = "/lang/list", method = RequestMethod.GET)
    @ApiOperation(value = "Returns all the supported languages")
    @ResponseBody
    public NavigableMap<String, String> getSupportedLanguages() {
        return localeService.getSupportedLanguages();
    }

    @RequestMapping(value = "/lang/available", method = RequestMethod.GET)
    @ApiOperation(value = "Returns all the supported locales")
    @ResponseBody
    public Map<String, String> getAvailableLocales(HttpServletRequest request) {
        Locale userLocale = localeService.getUserLocale(request);
        Map<String, String> map = new TreeMap<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            String name = locale.getDisplayName(userLocale);
            String code = locale.toString();

            if (!map.containsKey(name)) {
                map.put(name, code);
            }
        }

        return map;
    }

    @RequestMapping(value = "/lang/locate")
    @ResponseBody
    public Map<String, String> getLangLocalisation(HttpServletRequest request) {
        return localeService.getMessages(request);
    }
}
