package org.motechproject.server.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.security.exception.UserNotFoundException;
import org.motechproject.security.exception.NonAdminUserException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.config.SettingsFacade;
import org.motechproject.config.domain.LoginMode;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.web.dto.ForgotViewData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Forgot Controller for reset password.
 */
@Controller
@Api(value = "ForgotController", description = "Forgot Controller for reset password.")
public class ForgotController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotController.class);

    @Autowired
    private SettingsFacade settingsFacade;

    @Autowired
    private StartupManager startupManager;

    @Autowired
    private PasswordRecoveryService recoveryService;

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public ModelAndView login(final HttpServletRequest request) {
        if (startupManager.isBootstrapConfigRequired()) {
            return new ModelAndView(Constants.REDIRECT_BOOTSTRAP);
        }

        if (startupManager.isConfigRequired()) {
            return new ModelAndView(Constants.REDIRECT_STARTUP);
        }

        ModelAndView view = new ModelAndView("forgot");
        view.addObject("error", request.getParameter("error"));

        return view;
    }

    @RequestMapping(value = "/forgotviewdata", method = RequestMethod.GET)
    @ApiOperation(value = "Retrieves forgot view data")
    @ResponseBody
    public ForgotViewData getForgotViewData(final HttpServletRequest request) {
        ForgotViewData view = new ForgotViewData();

        view.setLoginMode(settingsFacade.getPlatformSettings().getLoginMode());
        view.setEmailGetter(true);
        view.setProcessed(false);
        view.setEmail("");
        view.setPageLang(cookieLocaleResolver.resolveLocale(request));

        return view;
    }

    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    @ApiOperation(value = "Recovers the password for the given email")
    @ResponseBody
    public String forgotPost(@RequestBody String email) {

        LoginMode loginMode = settingsFacade.getPlatformSettings().getLoginMode();

        if (loginMode.isRepository()) {
            try {
                recoveryService.passwordRecoveryRequest(email);
            } catch (UserNotFoundException e) {
                LOGGER.warn("Request for a nonexistent email", e);
                return "security.forgot.noSuchUser";
            } catch (RuntimeException e) {
                LOGGER.error("Error processing recovery", e);
                return "security.forgot.errorSending";
            }
        } else {
            try {
                recoveryService.oneTimeTokenOpenId(email);
            } catch (UserNotFoundException e) {
                LOGGER.warn("Request for a nonexistent email", e);
                return "security.forgot.noSuchUser";
            } catch (NonAdminUserException e) {
                LOGGER.warn("Request for a nonexistent email", e);
                return "security.forgot.nonAdminUser";
            } catch (RuntimeException e) {
                LOGGER.error("Error processing recovery", e);
                return "security.forgot.errorSending";
            }
        }

        return null;
    }
}
