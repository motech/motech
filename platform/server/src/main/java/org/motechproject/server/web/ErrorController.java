package org.motechproject.server.web;

import org.motechproject.server.event.BundleErrorEventListener;
import org.motechproject.server.impl.OsgiListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the error pages.
 */
@Controller
@RequestMapping("error")
public class ErrorController {

    private static final String ERROR_CODE = "errorCode";
    private static final String SHORT_DESC = "shortDesc";
    private static final String LONG_DESC = "longDesc";
    private static final String ERROR = "error";
    private static final String NO_BOOTSTRAP = "noBootstrap";

    private static final String ERROR_VIEW = "error";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @RequestMapping("/401")
    public ModelAndView error401(HttpServletRequest request) {
        String shortDesc = getMessage("server.error.unauthorized", request);
        String longDesc = getMessage("server.error.unauthorizedDesc", request);

        return errorMav(401, shortDesc, longDesc);
    }

    @RequestMapping("/403")
    public ModelAndView error403(HttpServletRequest request) {
        String shortDesc = getMessage("server.error.accessDenied", request);
        String longDesc = getMessage("server.error.accessDeniedDesc", request);

        return errorMav(403, shortDesc, longDesc);
    }

    @RequestMapping("/404")
    public ModelAndView error404(HttpServletRequest request) {
        String uri = (String) request.getAttribute("javax.servlet.error.request_uri");

        String shortDesc = getMessage("server.error.notFound", request);
        String longDesc = getMessage("server.error.notFoundDesc", uri, request);

        return errorMav(404, shortDesc, longDesc);
    }

    @RequestMapping("/500")
    public ModelAndView error500(HttpServletRequest request) {
        String shortDesc = getMessage("server.error.internal", request);
        String longDesc = getMessage("server.error.internalDesc", request);

        return errorMav(500, shortDesc, longDesc);
    }

    @RequestMapping("/503")
    public ModelAndView error503(HttpServletRequest request) {
        String shortDesc = getMessage("server.error.serviceUnavailable", request);
        String longDesc = getMessage("server.error.serviceUnavailableDesc", request);

        return errorMav(503, shortDesc, longDesc);
    }

    @RequestMapping("/startup")
    public ModelAndView errorStartup(HttpServletRequest request) {
        String shortDesc = getMessage("server.error.startup", request);
        String longDesc = getMessage("server.error.startupDesc", request);

        ModelAndView mav = errorMav(500, shortDesc, longDesc);

        mav.getModel().put(ERROR, BundleErrorEventListener.getBundleError());

        return mav;
    }

    private ModelAndView errorMav(int code, String shortDesc, String longDesc) {
        ModelAndView mav = new ModelAndView(ERROR_VIEW);

        mav.getModel().put(ERROR_CODE, code);
        mav.getModel().put(SHORT_DESC, shortDesc);
        mav.getModel().put(LONG_DESC, longDesc);

        mav.getModel().put(NO_BOOTSTRAP, !OsgiListener.isBootstrapPresent());

        return mav;
    }

    private String getMessage(String key, HttpServletRequest request) {
        return messageSource.getMessage(key, null, localeResolver.resolveLocale(request));
    }

    private String getMessage(String key, String arg, HttpServletRequest request) {
        return messageSource.getMessage(key, new Object[] { arg }, localeResolver.resolveLocale(request));
    }
}
