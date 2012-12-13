package org.motechproject.security.web.controllers;

import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.security.web.form.ResetForm;
import org.motechproject.security.web.validator.ResetFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class ResetController {

    private static final Logger LOG = LoggerFactory.getLogger(ResetController.class);

    private static final String ERRORS = "errors";
    private static final String TOKEN = "token";
    private static final String PAGE_LANG = "pageLang";

    @Autowired
    private PasswordRecoveryService recoveryService;

    @Autowired
    private CookieLocaleResolver cookieLocaleResolver;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new ResetFormValidator());
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ModelAndView resetView(@RequestParam String token, HttpServletRequest request) {
        ModelAndView mav;

        if (recoveryService.validateToken(token)) {
            mav = new ModelAndView("reset");
            mav.addObject(TOKEN, token);
        } else {
            mav = new ModelAndView("invalidReset");
        }

        mav.addObject(PAGE_LANG, cookieLocaleResolver.resolveLocale(request));
        return mav;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public ModelAndView reset(@ModelAttribute("startupSettings") @Valid ResetForm form, BindingResult bindingResult,
                              HttpServletRequest request) {
        ModelAndView mav;

        if (bindingResult.hasErrors()) {
            mav = new ModelAndView("reset");
            mav.addObject(ERRORS, getErrors(bindingResult));
            mav.addObject(TOKEN, form.getToken());
        } else {
            mav= new ModelAndView("afterReset");

            try {
                recoveryService.resetPassword(form.getToken(), form.getPassword(), form.getPasswordConfirmation());
            } catch (InvalidTokenException e) {
                LOG.debug("Reset with invalid token attempted", e);
                mav.addObject(ERRORS, Arrays.asList("security.invalidToken"));
            } catch (Exception e) {
                LOG.error("Error while reseting passsword", e);
                mav.addObject(ERRORS, Arrays.asList("security.error.reset"));
            }
        }

        mav.addObject(PAGE_LANG, cookieLocaleResolver.resolveLocale(request));
        return mav;
    }

    private List<String> getErrors(final BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        List<String> errors = new ArrayList<>(allErrors.size());

        for (ObjectError error : allErrors) {
            errors.add(error.getCode());
        }

        return errors;
    }
}
