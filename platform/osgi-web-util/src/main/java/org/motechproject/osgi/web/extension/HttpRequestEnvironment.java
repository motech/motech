package org.motechproject.osgi.web.extension;


import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to categorize Http Requests
 */
public final class HttpRequestEnvironment {

    private HttpRequestEnvironment() {
    }

    public static boolean isAjax(HttpServletRequest request) {
        String header = request.getHeader("x-requested-with");
        if(header != null && header.equals("XMLHttpRequest")) {
            return true;
        } else {
            return false;
        }
    }
}
