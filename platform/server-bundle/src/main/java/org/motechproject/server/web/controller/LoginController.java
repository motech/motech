package org.motechproject.server.web.controller;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.ui.LocaleService;
import org.motechproject.server.web.dto.LoginViewData;
import org.motechproject.server.web.form.LoginForm;
import org.motechproject.server.web.helper.Header;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Login Controller for user authentication.
 */
@Controller
public class LoginController {

    @Autowired
    private LocaleService localeService;

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    private StartupManager startupManager;

    @Autowired
    private BundleContext bundleContext;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(final HttpServletRequest request) {
        if (startupManager.isBootstrapConfigRequired()) {
            return new ModelAndView(Constants.REDIRECT_BOOTSTRAP);
        }

        if (startupManager.isConfigRequired()) {
            return new ModelAndView(Constants.REDIRECT_STARTUP);
        }

        ModelAndView view = new ModelAndView("loginPage");
        view.addObject("mainHeader", Header.generateHeader(bundleContext.getBundle()));

        return view;
    }

    @RequestMapping(value = "/loginviewdata", method = RequestMethod.GET)
    @ResponseBody
    public LoginViewData getLoginViewData(final HttpServletRequest request) {

        LoginViewData view = new LoginViewData();
        view.setLoginMode(settingsFacade.getPlatformSettings().getLoginMode());
        view.setOpenIdProviderName(settingsFacade.getPlatformSettings().getProviderName());
        view.setOpenIdProviderUrl(settingsFacade.getPlatformSettings().getProviderUrl());
        view.setLoginForm(new LoginForm());
        view.setError(request.getParameter("error"));
        view.setPageLang(localeService.getUserLocale(request));

        String contextPath = request.getSession().getServletContext().getContextPath();

        if (StringUtils.isNotBlank(contextPath) && !"/".equals(contextPath)) {
            view.setContextPath(contextPath.substring(1) + "/");
        } else if (StringUtils.isBlank(contextPath) || "/".equals(contextPath)) {
            view.setContextPath("");
        }

        return view;
    }
}
