package org.motechproject.server.web.controller;

import org.motechproject.server.ui.LocaleSettings;
import org.motechproject.server.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private LocaleSettings localeSettings;



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(final HttpServletRequest request) {
        ModelAndView view = new ModelAndView("login");
        if (!"/".equals(request.getSession().getServletContext().getContextPath())) {
            view.addObject("contextPath", request.getSession().getServletContext().getContextPath().substring(1));
        } else {
            view.addObject("contextPath", "");
        }
        view.addObject("loginForm", new LoginForm());
        view.addObject("pageLang", localeSettings.getUserLocale(request));
        return view;
    }

    @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
    public ModelAndView accessdenied(final HttpServletRequest request) {
        return new ModelAndView("accessdenied");
    }
}
