package org.motechproject.server.web.controller;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.motechproject.server.startup.MotechPlatformState;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static org.motechproject.server.ui.UIFrameworkService.MODULES_WITHOUT_SUBMENU;
import static org.motechproject.server.ui.UIFrameworkService.MODULES_WITH_SUBMENU;

@Controller
public class DashboardController {

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private UIFrameworkService uiFrameworkService;

    @Autowired
    private LocaleSettings localeSettings;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping({"/index", "/", "/home"})
    public ModelAndView index(@RequestParam(required = false) String moduleName, final HttpServletRequest request) {

        ModelAndView mav;

        // check if this is the first run
        if (startupManager.getPlatformState() == MotechPlatformState.NEED_CONFIG) {
            mav = new ModelAndView("redirect:startup.do");
        } else {
            mav = new ModelAndView("index");

            mav.addObject("uptime", getUptime(request));

            Map<String, Collection<ModuleRegistrationData>> modules = uiFrameworkService.getRegisteredModules();
            mav.addObject(MODULES_WITH_SUBMENU, modules.get(MODULES_WITH_SUBMENU));
            mav.addObject(MODULES_WITHOUT_SUBMENU, modules.get(MODULES_WITHOUT_SUBMENU));

            if (moduleName != null) {
                ModuleRegistrationData currentModule = uiFrameworkService.getModuleData(moduleName);
                if (currentModule != null) {
                    mav.addObject("currentModule", currentModule);
                }
            }

            mav.addObject("pageLang", localeSettings.getUserLocale(request));
        }

        return mav;
    }

    @RequestMapping("/old")
    public ModelAndView oldIndex() {
        return new ModelAndView("oldindex");
    }

    private String getUptime(HttpServletRequest request) {
        RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
        Period uptime = new Duration(mx.getUptime()).toPeriod();
        Locale locale = localeSettings.getUserLocale(request);

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays()
                .appendSuffix(" " + messageSource.getMessage("day", null, locale), " " + messageSource.getMessage("days", null, locale))
                .appendSeparator(" " + messageSource.getMessage("and", null, locale))
                .appendHours()
                .appendSuffix(" " + messageSource.getMessage("hour", null, locale), " " + messageSource.getMessage("hours", null, locale))
                .appendSeparator(" " + messageSource.getMessage("and", null, locale) + " ")
                .appendMinutes()
                .appendSuffix(" " + messageSource.getMessage("minute", null, locale), " " + messageSource.getMessage("minutes", null, locale))
                .toFormatter();

        return formatter.print(uptime.normalizedStandard());
    }
}
