package org.motechproject.server.bootstrap;

import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This class converts {@link org.springframework.web.servlet.FlashMap} objects in the
 * session coming from an OSGi ClassLoader to FlashMaps that will be usable on the Tomcat
 * webapp side. This should prevent errors with casting a FlashMap to a FlashMap when getting
 * redirected to error pages.
 */
public class FlashMapInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object flashMapObj = request.getAttribute(FlashMapManager.OUTPUT_FLASH_MAP_ATTRIBUTE);

        if (flashMapObj != null && !(flashMapObj instanceof FlashMap)) {
            // we need to create a FlashMap using the webapp classLoader
            FlashMap flashMapCopy = new FlashMap();
            flashMapCopy.putAll((Map<? extends String, ?>) flashMapObj);
            // then put it in the request
            request.setAttribute(FlashMapManager.OUTPUT_FLASH_MAP_ATTRIBUTE, flashMapCopy);
        }

        return true;
    }
}
