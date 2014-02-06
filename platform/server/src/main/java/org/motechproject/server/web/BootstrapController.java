package org.motechproject.server.web;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbInstance;
import org.ektorp.DbAccessException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.server.impl.OsgiListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller for capturing bootstrap configuration from UI
 */
@Controller
public class BootstrapController {

    public static final String BOOTSTRAP_CONFIG_VIEW = "bootstrapconfig";
    private static final String COUCHDB_URL_SUGGESTION = "http://localhost:5984/";
    private static final String SQL_URL_SUGGESTION = "jdbc:mysql://localhost:3306/";
    private static final String TENANT_ID_DEFAULT = "DEFAULT";
    private static final String ERRORS = "errors";
    private static final String WARNINGS = "warnings";
    private static final String SUCCESS = "success";

    private static final int CONNECTION_TIMEOUT = 4000; //ms

    public static final String REDIRECT_HOME = "redirect:..";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new BootstrapConfigFormValidator());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView bootstrapForm() {
        if (OsgiListener.isBootstrapPresent()) {
            return new ModelAndView(REDIRECT_HOME);
        }

        ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
        bootstrapView.addObject("bootstrapConfig", new BootstrapConfigForm());
        bootstrapView.addObject("username", System.getProperty("user.name"));
        bootstrapView.addObject("couchDbUrlSuggestion", COUCHDB_URL_SUGGESTION);
        bootstrapView.addObject("sqlUrlSuggestion", SQL_URL_SUGGESTION);
        bootstrapView.addObject("tenantIdDefault", TENANT_ID_DEFAULT);
        return bootstrapView;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView submitForm(@ModelAttribute("bootstrapConfig") @Valid BootstrapConfigForm form, BindingResult result,
                                   HttpServletRequest request) {
        if (OsgiListener.isBootstrapPresent()) {
            return new ModelAndView(REDIRECT_HOME);
        }

        if (result.hasErrors()) {
            ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject("errors", getErrors(result));
            bootstrapView.addObject("username", System.getProperty("user.name"));
            bootstrapView.addObject("couchDbUrlSuggestion", COUCHDB_URL_SUGGESTION);
            bootstrapView.addObject("sqlUrlSuggestion", SQL_URL_SUGGESTION);
            bootstrapView.addObject("tenantIdDefault", TENANT_ID_DEFAULT);
            return bootstrapView;
        }

        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig(form.getCouchDbUrl(), form.getCouchDbUsername(),
                form.getCouchDbPassword()), new SQLDBConfig(form.getSqlUrl(), form.getSqlUsername(), form.getSqlPassword()),
                form.getTenantId(), ConfigSource.valueOf(form.getConfigSource()));

        try {
            OsgiListener.saveBootstrapConfig(bootstrapConfig);
        } catch (Exception e) {
            ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject("errors", Arrays.asList(getMessage("server.error.bootstrap.save", request)));
            bootstrapView.addObject("username", System.getProperty("user.name"));
            bootstrapView.addObject("couchDbUrlSuggestion", COUCHDB_URL_SUGGESTION);
            bootstrapView.addObject("sqlUrlSuggestion", SQL_URL_SUGGESTION);
            bootstrapView.addObject("tenantIdDefault", TENANT_ID_DEFAULT);
            return bootstrapView;
        }

        ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
        bootstrapView.getModelMap().put("redirect", true);
        return bootstrapView;
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ?> verifyConnection(@ModelAttribute("bootstrapConfig") @Valid BootstrapConfigForm form,
                                                          BindingResult result, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put(WARNINGS, Arrays.asList(getMessage("server.bootstrap.verify.error", request)));
            response.put(ERRORS, getErrors(result));
        } else {
            try {
                StdHttpClient.Builder builder = new StdHttpClient.Builder()
                        .url(form.getCouchDbUrl())
                        .caching(false)
                        .connectionTimeout(CONNECTION_TIMEOUT);

                if (StringUtils.isNotBlank(form.getCouchDbUsername())) {
                    builder.username(form.getCouchDbUsername());
                }
                if (StringUtils.isNotBlank(form.getCouchDbPassword())) {
                    builder.password(form.getCouchDbPassword());
                }

                HttpClient httpClient = builder.build();

                CouchDbInstance couchDbInstance = new StdCouchDbInstance(httpClient);

                // verify connection
                couchDbInstance.getAllDatabases();

                // no exception, success
                response.put(SUCCESS, true);
            } catch (MalformedURLException e) {
                response.put(ERRORS, Arrays.asList(getMessage("server.error.invalid.dbUrl", request)));
                response.put(SUCCESS, false);
            } catch (DbAccessException e) {
                response.put(WARNINGS, Arrays.asList(getMessage("server.bootstrap.verify.warning", request)));
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

    private String getMessage(String key, HttpServletRequest request) {
        return messageSource.getMessage(key, null, localeResolver.resolveLocale(request));
    }
}
