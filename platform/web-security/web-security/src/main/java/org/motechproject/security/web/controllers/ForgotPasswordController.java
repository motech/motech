package org.motechproject.security.web.controllers;

import org.motechproject.security.ex.UserNotFoundException;
import org.motechproject.security.password.NonAdminUserException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ForgotPasswordController {

    private static final Logger LOG = LoggerFactory.getLogger(ForgotPasswordController.class);

    private static final String ERROR = "error";
    private static final String PAGE_LANG = "pageLang";

    @Autowired
    private PasswordRecoveryService recoveryService;

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public ModelAndView forgotGet(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("forgot");

        mav.addObject(PAGE_LANG, cookieLocaleResolver.resolveLocale(request));

        return mav;
    }

    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public ModelAndView forgotPost(@RequestParam String email, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("forgotProcessed");
        mav.addObject(PAGE_LANG, cookieLocaleResolver.resolveLocale(request));

        try {
            recoveryService.passwordRecoveryRequest(email);
        } catch (UserNotFoundException e) {
            mav.addObject(ERROR, "security.forgot.noSuchUser");
            LOG.debug("Request for a nonexistent email" ,e);
        } catch (Exception e) {
            mav.addObject(ERROR, "security.forgot.errorSending");
            LOG.error("Error processing recovery", e);
        }

        return mav;
    }

    @RequestMapping(value = "/forgotOpenId", method = RequestMethod.GET)
    public ModelAndView forgotOpenIdGet(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("forgotOpenId");

        mav.addObject(PAGE_LANG, cookieLocaleResolver.resolveLocale(request));

        return mav;
    }

    @RequestMapping(value = "/forgotOpenId", method = RequestMethod.POST)
    public ModelAndView forgotOpenIdPost(@RequestParam String email, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("forgotProcessed");
        mav.addObject(PAGE_LANG, cookieLocaleResolver.resolveLocale(request));

        try {
            recoveryService.oneTimeTokenOpenId(email);
        } catch (UserNotFoundException e) {
            mav.addObject(ERROR, "security.forgot.noSuchUser");
            LOG.debug("Request for a nonexistent email" ,e);
        } catch (NonAdminUserException e) {
            mav.addObject(ERROR, "security.forgot.nonAdminUser");
            LOG.debug("Request for a nonexistent email" ,e);
        } catch (Exception e) {
            mav.addObject(ERROR, "security.forgot.errorSending");
            LOG.error("Error processing recovery", e);
        }

        return mav;
    }
}
