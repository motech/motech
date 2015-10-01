package org.motechproject.server.web.controller;

import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.ex.PasswordValidatorException;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.server.web.dto.ChangePasswordViewData;
import org.motechproject.server.web.dto.ResetViewData;
import org.motechproject.server.web.form.ChangePasswordForm;
import org.motechproject.server.web.form.ResetForm;
import org.motechproject.server.web.helper.Header;
import org.motechproject.server.web.validator.ChangePasswordFormValidator;
import org.motechproject.server.web.validator.ResetFormValidator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for resetting and changing user password.
 */
@Controller
public class ResetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResetController.class);

    @Autowired
    private PasswordRecoveryService recoveryService;

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @Autowired
    private ResetFormValidator resetFormValidator;

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private MotechUserService motechUserService;

    @RequestMapping(value = "/changepassword", method = RequestMethod.GET)
    public ModelAndView changePasswordView(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("changePassword");
        mav.addObject("mainHeader", Header.generateHeader(bundleContext.getBundle()));

        return mav;
    }

    @RequestMapping(value = "/changepassword", method = RequestMethod.POST)
    @ResponseBody
    public ChangePasswordViewData changePassword(@RequestBody ChangePasswordForm form) {
        ChangePasswordViewData viewData = new ChangePasswordViewData(form);
        ChangePasswordFormValidator validator = new ChangePasswordFormValidator();
        List<String> errors = validator.validate(form);

        if (!errors.isEmpty()) {
            viewData.setErrors(errors);
        } else {
            try {
                MotechUserProfile profile = motechUserService.changeExpiredPassword(form.getUsername(), form.getOldPassword(), form.getPassword());
                if (profile != null) {
                    viewData.setChangeSucceded(true);
                } else {
                    viewData.getErrors().add("server.reset.wrongPassword");
                }
            } catch (PasswordValidatorException e) {
                viewData.getErrors().add(e.getMessage());
            } catch (LockedException e) {
                viewData.setUserBlocked(true);
            }
        }

        viewData.getChangePasswordForm().resetPasswordsAndUserName();
        return viewData;
    }

    @RequestMapping(value = "/forgotreset", method = RequestMethod.GET)
    public ModelAndView resetView(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("reset");
        mav.addObject("mainHeader", Header.generateHeader(bundleContext.getBundle()));
        return mav;
    }

    @RequestMapping(value = "/forgotresetviewdata", method = RequestMethod.GET)
    @ResponseBody
    public ResetViewData getResetViewData(final HttpServletRequest request) {
        ResetViewData viewData = new ResetViewData();
        ResetForm form = new ResetForm();

        String token = request.getParameter("token");

        if (recoveryService.validateToken(token)) {
            form.setToken(token);
            viewData.setInvalidToken(false);
        } else {
            viewData.setInvalidToken(true);
        }

        viewData.setResetForm(form);
        viewData.setResetSucceed(false);
        viewData.setPageLang(cookieLocaleResolver.resolveLocale(request));

        return viewData;
    }

    @RequestMapping(value = "/forgotreset", method = RequestMethod.POST)
    @ResponseBody
    public ResetViewData reset(@RequestBody ResetForm form, final HttpServletRequest request) {
        ResetViewData viewData = new ResetViewData();
        viewData.setResetForm(form);
        viewData.setPageLang(cookieLocaleResolver.resolveLocale(request));
        viewData.setInvalidToken(false);

        List<String> errors = resetFormValidator.validate(form);

        if (!errors.isEmpty()) {
            viewData.setResetSucceed(false);
            viewData.setErrors(errors);

            return viewData;
        } else {
            try {
                recoveryService.resetPassword(form.getToken(), form.getPassword(), form.getPasswordConfirmation());
            } catch (InvalidTokenException e) {
                LOGGER.debug("Reset with invalid token attempted", e);
                errors.add("server.reset.invalidToken");
                viewData.setInvalidToken(true);
            } catch (Exception e) {
                LOGGER.error("Error while reseting passsword", e);
                errors.add("server.reset.error");
            }

            viewData.setResetSucceed(true);
            viewData.setErrors(errors);
        }

        return viewData;
    }
}
