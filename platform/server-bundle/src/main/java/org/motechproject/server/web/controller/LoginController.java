package org.motechproject.server.web.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.motechproject.config.SettingsFacade;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.osgi.web.service.LocaleService;
import org.motechproject.server.web.dto.LoginViewData;
import org.motechproject.server.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(final HttpServletResponse response) {
        if (startupManager.isBootstrapConfigRequired()) {
            return new ModelAndView(Constants.REDIRECT_BOOTSTRAP);
        }

        if (startupManager.isConfigRequired()) {
            return new ModelAndView(Constants.REDIRECT_STARTUP);
        }

        ModelAndView view = new ModelAndView("loginPage");
        response.addHeader("login-required", "true");

        return view;
    }

    @RequestMapping(value = "/loginviewdata", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the login view data")
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
