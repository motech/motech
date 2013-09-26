package org.motechproject.server.web.controller;

import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.startup.StartupManager;
import org.motechproject.server.web.form.BootstrapConfigForm;
import org.motechproject.server.web.validator.BootstrapConfigFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * controller for capturing bootstrap configuration from UI
 */
@Controller
public class BootstrapController {

    public static final String REDIRECT_HOME = "redirect:home";

    private StartupManager startupManager = StartupManager.getInstance();

    @Autowired
    private ConfigurationService configurationService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new BootstrapConfigFormValidator());
    }

    @RequestMapping(value = "/bootstrap", method = RequestMethod.GET)
    public ModelAndView bootstrapForm() {
        if (!startupManager.isBootstrapConfigRequired()) {
            return new ModelAndView(REDIRECT_HOME);
        }

        ModelAndView bootstrapView = new ModelAndView("bootstrapconfig");
        bootstrapView.addObject("bootstrapConfig", new BootstrapConfigForm());
        return bootstrapView;
    }

    @RequestMapping(value = "/bootstrap", method = RequestMethod.POST)
    public ModelAndView submitForm(@ModelAttribute("bootstrapConfig") @Valid BootstrapConfigForm form, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView bootstrapView = new ModelAndView("bootstrapconfig");
            bootstrapView.addObject("errors", getErrors(result));
            return bootstrapView;
        }

        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig(form.getDbUrl(), form.getDbUsername(), form.getDbPassword()), form.getTenantId(), ConfigSource.valueOf(form.getConfigSource()));
        configurationService.save(bootstrapConfig);

        startupManager.startup();

        return new ModelAndView(REDIRECT_HOME);
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
