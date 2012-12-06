package org.motechproject.server.web.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.LoginForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LocaleSettings localeSettings;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(final HttpServletRequest request) {
        ModelAndView view = new ModelAndView("login");
        if (StringUtils.isNotBlank(request.getSession().getServletContext().getContextPath())) {
            view.addObject("contextPath", request.getSession().getServletContext().getContextPath().substring(1));
        } else {
            view.addObject("contextPath", "");
        }
        view.addObject("loginMode", getLoginMode());
        view.addObject("openIdProviders", OpenIdProviders.getProviders());
        view.addObject("error", request.getParameter("error"));
        view.addObject("loginForm", new LoginForm());
        view.addObject("pageLang", localeSettings.getUserLocale(request));
        return view;
    }

    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public ModelAndView accessdenied(final HttpServletRequest request) {
        return new ModelAndView("accessdenied");
    }

    private String getLoginMode() {
        File file = new File(String.format("%s/.motech/config/%s", System.getProperty("user.home"), "motech-settings.conf"));
        if (!file.exists()) {
            return null;
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
             LOGGER.info("Can not read file." + e);
        }

        return properties.getProperty("login.mode");
    }


    // TODO: Move this out of here once we move the bundle to platform
    public static class OpenIdProviders {
        private static final Logger LOGGER = LoggerFactory.getLogger(OpenIdProviders.class);

        private String providerName;
        private String providerUrl;

        public OpenIdProviders(String providerName, String providerUrl) {
            this.providerName = providerName;
            this.providerUrl = providerUrl;
        }

        public String getProviderName() {
            return providerName;
        }

        public String getProviderUrl() {
            return providerUrl;
        }

        public static List<OpenIdProviders> getProviders() {
            List<OpenIdProviders> providers = new ArrayList<OpenIdProviders>();
            File file = new File(String.format("%s/.motech/%s", System.getProperty("user.home"), "openIdProviders"));
            if (file.exists()) {
                try (InputStream in = new FileInputStream(file)) {
                    List<String> lines = IOUtils.readLines(new InputStreamReader(in));
                    for (String line : lines) {
                        String words[] = line.split(" ");

                        if (words.length != 2) {
                            LOGGER.warn("Invalid entry in openIdProviders: " + line);
                        }

                        providers.add(new OpenIdProviders(words[0], words[1]));
                    }
                } catch (IOException e) {
                    LOGGER.info("Can not read file." + e);
                }
            }
            return providers;
        }
    }
}
