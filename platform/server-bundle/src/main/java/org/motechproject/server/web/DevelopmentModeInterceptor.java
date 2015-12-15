package org.motechproject.server.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class sets and updates the path used for static resources in JSP views
 */
public class DevelopmentModeInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView){
        if(modelAndView != null) {
            String devString = "DEVELOPMENT";
            Boolean developmentMode = devString.equals(System.getenv("ENVIRONMENT"));
            modelAndView.addObject("developmentMode", developmentMode);
        }
    }
}
