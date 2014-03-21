package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.form.UserInfo;
import org.motechproject.server.web.helper.Header;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.util.Locale;

import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.motechproject.commons.date.util.DateUtil.now;

/**
 * Main application controller. Responsible for retrieving information shared across the UI of different modules.
 * The view returned by this controller will embed the UI of the currently requested module.
 */
@Controller
public class DashboardController {
    private StartupManager startupManager;
    private LocaleService localeService;
    private BundleContext bundleContext;

    @RequestMapping({"/index", "/", "/home"})
    public ModelAndView index(final HttpServletRequest request) {
        ModelAndView mav;

        // check if this is the first run
        if (startupManager.isConfigRequired()) {
            mav = new ModelAndView(Constants.REDIRECT_STARTUP);
        } else {
            mav = new ModelAndView("index");
            mav.addObject("isAccessDenied", false);
            mav.addObject("loginPage", false);
            mav.addObject("mainHeader", Header.generateHeader(bundleContext.getBundle()));
            String contextPath = request.getSession().getServletContext().getContextPath();

            if (StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath)) {
                mav.addObject("contextPath", contextPath.substring(1) + "/");
            } else if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
                mav.addObject("contextPath", "");
            }
        }

        return mav;
    }

    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public ModelAndView accessdenied(final HttpServletRequest request) {
        ModelAndView view = index(request);
        view.addObject("isAccessDenied", true);
        view.addObject("loginPage", false);
        return view;
    }

    @RequestMapping(value = "/gettime", method = RequestMethod.POST)
    @ResponseBody
    public String getTime(HttpServletRequest request) {
        Locale locale = localeService.getUserLocale(request);
        DateTimeFormatter format = forPattern("EEE MMM dd, h:mm a, z yyyy").withLocale(locale);
        return now().toString(format);
    }

    @RequestMapping(value = "/getUptime", method = RequestMethod.POST)
    @ResponseBody
    public DateTime getUptime() {
        return now().minus(ManagementFactory.getRuntimeMXBean().getUptime());
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.POST)
    @ResponseBody
    public UserInfo getUser(HttpServletRequest request) {
        String lang = localeService.getUserLocale(request).getLanguage();
        boolean securityLaunch = request.getUserPrincipal() != null;
        String userName = securityLaunch ? request.getUserPrincipal().getName() : "Admin Mode";

        return new UserInfo(userName, securityLaunch, lang);
    }

    @Autowired
    public void setStartupManager(StartupManager startupManager) {
        this.startupManager = startupManager;
    }

    @Autowired
    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
