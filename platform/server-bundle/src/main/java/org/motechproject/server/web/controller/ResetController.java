package org.motechproject.server.web.controller;

import org.motechproject.security.ex.InvalidTokenException;
import org.motechproject.security.service.PasswordRecoveryService;
import org.motechproject.server.web.dto.ResetViewData;
import org.motechproject.server.web.form.ResetForm;
import org.motechproject.server.web.validator.ResetFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @Qualifier("mainHeaderStr")
    private String mainHeader;

    @RequestMapping(value = "/forgotreset", method = RequestMethod.GET)
    public ModelAndView resetView(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("reset");
        mav.addObject("mainHeader", mainHeader);

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
