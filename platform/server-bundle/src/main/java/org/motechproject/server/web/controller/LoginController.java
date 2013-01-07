package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.startup.MotechPlatformState;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private LocaleSettings localeSettings;

    @Autowired
    private PlatformSettingsService settingsService;

    @Autowired
    private StartupManager startupManager;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(final HttpServletRequest request) {
        if (startupManager.getPlatformState() == MotechPlatformState.NEED_CONFIG) {
            return new ModelAndView("redirect:startup.do");
        }

        ModelAndView view = new ModelAndView("login");

        if (StringUtils.isNotBlank(request.getSession().getServletContext().getContextPath()) && !"/".equals(request.getSession().getServletContext().getContextPath())) {
            view.addObject("contextPath", request.getSession().getServletContext().getContextPath().substring(1) + "/");
        } else if (StringUtils.isBlank(request.getSession().getServletContext().getContextPath()) || "/".equals(request.getSession().getServletContext().getContextPath())) {
            view.addObject("contextPath", "");
        }

        view.addObject("loginMode", settingsService.getPlatformSettings().getLoginMode());
        view.addObject("openIdProviderName", settingsService.getPlatformSettings().getProviderName());
        view.addObject("openIdProviderUrl", settingsService.getPlatformSettings().getProviderUrl());
        view.addObject("error", request.getParameter("error"));
        view.addObject("loginForm", new LoginForm());
        view.addObject("pageLang", localeSettings.getUserLocale(request));

        return view;
    }

    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public ModelAndView accessdenied(final HttpServletRequest request) {
        return new ModelAndView("accessdenied");
    }

}
