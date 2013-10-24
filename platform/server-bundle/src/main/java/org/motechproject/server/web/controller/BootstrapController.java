package org.motechproject.server.web.controller;

import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.server.web.controller.Constants.REDIRECT_HOME;

/**
 * controller for capturing bootstrap configuration from UI
 */
@Controller
public class BootstrapController {
    public static final String BOOTSTRAP_CONFIG_VIEW = "bootstrapconfig";
    private static final String DB_URL_SUGGESTION = "http://localhost:5984/";
    private static final String TENANT_ID_DEFAULT = "DEFAULT";
    private static final String ERRORS = "errors";
    private static final String WARNINGS = "warnings";
    private static final String SUCCESS = "success";
    private static final int CONNECTION_TIMEOUT = 4000; //ms

    @Autowired
    private StartupManager startupManager;

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

        ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
        bootstrapView.addObject("bootstrapConfig", new BootstrapConfigForm());
        bootstrapView.addObject("username", System.getProperty("user.name"));
        bootstrapView.addObject("dbUrlSuggestion", DB_URL_SUGGESTION);
        bootstrapView.addObject("tenantIdDefault", TENANT_ID_DEFAULT);
        return bootstrapView;
    }

    @RequestMapping(value = "/bootstrap", method = RequestMethod.POST)
    public ModelAndView submitForm(@ModelAttribute("bootstrapConfig") @Valid BootstrapConfigForm form, BindingResult result) {
        if (result.hasErrors()) {
            ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject("errors", getErrors(result));
            bootstrapView.addObject("username", System.getProperty("user.name"));
            bootstrapView.addObject("dbUrlSuggestion", DB_URL_SUGGESTION);
            bootstrapView.addObject("tenantIdDefault", TENANT_ID_DEFAULT);
            return bootstrapView;
        }

        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig(form.getDbUrl(), form.getDbUsername(), form.getDbPassword()), form.getTenantId(), ConfigSource.valueOf(form.getConfigSource()));
        try {
            configurationService.save(bootstrapConfig);
        } catch (Exception e) {
            ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject("errors", Arrays.asList("server.error.bootstrap.save"));
            bootstrapView.addObject("username", System.getProperty("user.name"));
            bootstrapView.addObject("dbUrlSuggestion", DB_URL_SUGGESTION);
            bootstrapView.addObject("tenantIdDefault", TENANT_ID_DEFAULT);
            return bootstrapView;
        }

        startupManager.startup();

        return new ModelAndView(REDIRECT_HOME);
    }

    @RequestMapping(value = "/bootstrap/verify", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ? extends Object> verifyConnection(@ModelAttribute("bootstrapConfig") @Valid BootstrapConfigForm form, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put(WARNINGS, Arrays.asList("server.bootstrap.verify.error"));
            response.put(ERRORS, getErrors(result));
        } else {
            try {
                HttpClient httpClient = new StdHttpClient.Builder()
                        .url(form.getDbUrl())
                        .username(form.getDbUsername())
                        .password(form.getDbPassword())
                        .caching(false)
                        .connectionTimeout(CONNECTION_TIMEOUT)
                        .build();

                CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);
                couchDbInstance.getAllDatabases();

                response.put(SUCCESS, true);

            } catch (MalformedURLException e) {
                response.put(ERRORS, Arrays.asList("server.error.invalid.dbUrl"));
                response.put(ERRORS, Arrays.asList("server.bootstrap.verify.error"));
                response.put(SUCCESS, false);
            } catch (DbAccessException e) {
                response.put(WARNINGS, Arrays.asList("server.bootstrap.verify.warning"));
                response.put(SUCCESS, false);
            }
        }

        return response;
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
