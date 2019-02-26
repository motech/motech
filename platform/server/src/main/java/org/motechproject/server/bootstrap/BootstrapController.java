package org.motechproject.server.bootstrap;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.server.impl.OsgiListener;
import org.motechproject.server.osgi.status.PlatformStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * controller for capturing bootstrap configuration from UI
 */
@Controller
@RequestMapping("bootstrap")
public class BootstrapController {

    public static final String BOOTSTRAP_CONFIG_VIEW = "bootstrapconfig";
    private static final String MYSQL_URL_SUGGESTION = "jdbc:mysql://localhost:3306/";
    private static final String POSTGRES_URL_SUGGESTION = "jdbc:postgresql://localhost:5432/";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    private static final String FELIX_PATH_DEFAULT = new File(System.getProperty("user.home"), ".motech" + File.separator + "felix-cache").getAbsolutePath();
    private static final String MOTECH_PATH_DEFAULT = new File(System.getProperty("user.home"), ".motech").getAbsolutePath();
    private static final String ERRORS = "errors";
    private static final String WARNINGS = "warnings";
    private static final String SQL_CONFIG_ERROR = "sqlConfigError";
    private static final String AMQ_CONFIG_ERROR = "amqConfigError";
    private static final String SUCCESS = "success";
    private static final String BOOTSTRAP_CONFIG = "bootstrapConfig";
    private static final String QUEUE_URL_SUGGESTION = "tcp://localhost:61616";

    private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapController.class);

    public static final String REDIRECT_HOME = "redirect:../../";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MessageBrokerPingService messageBrokerPingService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new BootstrapConfigFormValidator());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView bootstrapForm() {

        if (OsgiListener.isBootstrapPresent()) {
            if (!OsgiListener.isServerBundleActive() && !OsgiListener.inFatalError()) {
                ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
                bootstrapView.getModelMap().put("redirect", true);
                return bootstrapView;
            } else {
                return new ModelAndView(REDIRECT_HOME);
            }
        }

        ModelAndView bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
        bootstrapView.addObject(BOOTSTRAP_CONFIG, new BootstrapConfigForm());
        addCommonBootstrapViewObjects(bootstrapView);
        return bootstrapView;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView submitForm(@ModelAttribute(BOOTSTRAP_CONFIG) @Valid BootstrapConfigForm form, BindingResult result,
                                   HttpServletRequest request) {
        if (OsgiListener.isBootstrapPresent()) {
            return new ModelAndView(REDIRECT_HOME);
        }

        ModelAndView bootstrapView;
        if (result.hasErrors()) {
            bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject("errors", getErrors(result));
            addCommonBootstrapViewObjects(bootstrapView);
            return bootstrapView;
        }

        String queueUrl = form.getQueueUrl();
        boolean isAmqReachable = messageBrokerPingService.pingBroker(queueUrl);

        if (!isAmqReachable) {
            bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject(ERRORS, singletonList(getMessage("server.bootstrap.verify.amq.warning", new Object[]{queueUrl}, request)));
            addCommonBootstrapViewObjects(bootstrapView);
            return bootstrapView;
        }

        boolean isDBReachable = testSqlConnection(new SQLDBConfig(form.getSqlUrl(), form.getSqlDriver(), form.getSqlUsername(), form.getSqlPassword()), request);

        if (!isDBReachable) {
            bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject(ERRORS, singletonList(getMessage("server.bootstrap.verifySql.error", new Object[]{queueUrl}, request)));
            addCommonBootstrapViewObjects(bootstrapView);
            return bootstrapView;
        }

        BootstrapConfig bootstrapConfig;
        if (form.getOsgiFrameworkStorage() != null) {
            bootstrapConfig = new BootstrapConfig(new SQLDBConfig(form.getSqlUrl(), form.getSqlDriver(), form.getSqlUsername(), form.getSqlPassword()),
                    ConfigSource.valueOf(form.getConfigSource()), form.getOsgiFrameworkStorage(), form.getMotechDir(), form.getQueueUrl());
        } else {
            bootstrapConfig = new BootstrapConfig(new SQLDBConfig(form.getSqlUrl(), form.getSqlDriver(), form.getSqlUsername(), form.getSqlPassword()),
                    ConfigSource.valueOf(form.getConfigSource()), null, form.getMotechDir(), form.getQueueUrl());
        }

        try {
            OsgiListener.saveBootstrapConfig(bootstrapConfig);
        } catch (RuntimeException e) {
            LOGGER.error("Error while saving bootstrap configuration", e);

            bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
            bootstrapView.addObject("errors", singletonList(getMessage("server.error.bootstrap.save", request)));
            addCommonBootstrapViewObjects(bootstrapView);
            return bootstrapView;
        }

        bootstrapView = new ModelAndView(BOOTSTRAP_CONFIG_VIEW);
        bootstrapView.getModelMap().put("redirect", true);
        return bootstrapView;
    }

    @RequestMapping(value = "/verifySql", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ?> verifySqlConnection(@ModelAttribute(BOOTSTRAP_CONFIG) @Valid BootstrapConfigForm form,
                                              BindingResult result, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put(WARNINGS, singletonList(getMessage("server.bootstrap.verifySql.error", request)));
            response.put(ERRORS, getErrors(result));
            response.put(SQL_CONFIG_ERROR, true);
        } else {
            response = verifySqlConnection(new SQLDBConfig(form.getSqlUrl(), form.getSqlDriver(), form.getSqlUsername(), form.getSqlPassword()), request);
        }
        return response;
    }

    @RequestMapping(value = "/verifyAmq", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ?> verifyAmqConnection(@ModelAttribute(BOOTSTRAP_CONFIG) @Valid BootstrapConfigForm form,
                                              BindingResult result, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        if (result.hasErrors()) {
            response.put(WARNINGS, singletonList(getMessage("server.bootstrap.verify.amq.error", request)));
            response.put(SUCCESS, false);
            response.put(AMQ_CONFIG_ERROR, true);
        } else {
            String queueUrl = form.getQueueUrl();
            boolean reachable = messageBrokerPingService.pingBroker(queueUrl);
            if (reachable) {
                response.put(SUCCESS, true);
            } else {
                response.put(ERRORS, singletonList(getMessage("server.bootstrap.verify.amq.warning", new Object[]{queueUrl}, request)));
                response.put(SUCCESS, false);
                response.put(AMQ_CONFIG_ERROR, true);
            }
        }
        return response;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public PlatformStatus status() {
        return OsgiListener.getOsgiService().getCurrentPlatformStatus();
    }

    private List<String> getErrors(final BindingResult result) {
        List<ObjectError> allErrors = result.getAllErrors();
        List<String> errors = new ArrayList<>(allErrors.size());

        for (ObjectError error : allErrors) {
            errors.add(error.getCode());
        }

        return errors;
    }

    private void addCommonBootstrapViewObjects(ModelAndView bootstrapView) {
        bootstrapView.addObject("username", System.getProperty("user.name"));
        bootstrapView.addObject("mysqlUrlSuggestion", MYSQL_URL_SUGGESTION);
        bootstrapView.addObject("postgresUrlSuggestion", POSTGRES_URL_SUGGESTION);
        bootstrapView.addObject("mysqlDriverSuggestion", MYSQL_DRIVER);
        bootstrapView.addObject("postgresDriverSuggestion", POSTGRES_DRIVER);
        bootstrapView.addObject("felixPath", FELIX_PATH_DEFAULT);
        bootstrapView.addObject("queueUrlSuggestion", QUEUE_URL_SUGGESTION);
        bootstrapView.addObject("motechPath", MOTECH_PATH_DEFAULT);
    }

    private String getMessage(String key, HttpServletRequest request) {
        return messageSource.getMessage(key, null, localeResolver.resolveLocale(request));
    }

    private String getMessage(String key, Object[] params, HttpServletRequest request) {
        return messageSource.getMessage(key, params, localeResolver.resolveLocale(request));
    }


    private Map<String, Object> verifySqlConnection(SQLDBConfig config, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        boolean reachable;
        Connection sqlConnection = null;
        try {
            sqlConnection = ConnectionUtil.getConnection(config);
            reachable = sqlConnection.prepareCall("SELECT 0;").execute();
            response.put(SUCCESS, reachable);
            if (!reachable) {
                response.put(SQL_CONFIG_ERROR, true);
            }
        } catch (SQLException e) {
            response.put(WARNINGS, singletonList(getMessage("server.bootstrap.verify.warning", request)));
            response.put(SUCCESS, false);
            response.put(SQL_CONFIG_ERROR, true);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            response.put(ERRORS, singletonList(getMessage("server.error.invalid.sqlDriver", request)));
            response.put(WARNINGS, singletonList(getMessage("server.bootstrap.verifySql.error", request)));
            response.put(SUCCESS, false);
            response.put(SQL_CONFIG_ERROR, true);
        } finally {
            if (sqlConnection != null) {
                try {
                    sqlConnection.close();
                } catch (SQLException e) {
                    LOGGER.error("Error while closing SQL connection", e);
                }
            }
        }

        return response;
    }

    private boolean testSqlConnection(SQLDBConfig config, HttpServletRequest request) {
        Map<String, Object> response = verifySqlConnection(config, request);
        boolean isDBReachable = false;
        if (response.get(SUCCESS) instanceof Boolean) {
            isDBReachable = (Boolean) response.get(SUCCESS);
        }

        return isDBReachable;
    }
}
