/*
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.commcare.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.motechproject.server.ui.UiHttpContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Activator implements BundleActivator {
    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    private static final String CONTEXT_CONFIG_LOCATION = "applicationCommcareAPIBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/commcare/api";
    private static final String RESOURCE_URL_MAPPING = "/commcare";

    private static final String MODULE_NAME = "commcare";

    private ServiceTracker httpServiceTracker;
    private ServiceTracker uiServiceTracker;

    private static BundleContext bundleContext;

    @Override
    public void start(BundleContext context) throws Exception {
        bundleContext = context;

        this.httpServiceTracker = new ServiceTracker(context,
                HttpService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((HttpService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((HttpService) service);
                super.removedService(ref, service);
            }
        };
        this.httpServiceTracker.open();

        this.uiServiceTracker = new ServiceTracker(context,
                UIFrameworkService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((UIFrameworkService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((UIFrameworkService) service);
                super.removedService(ref, service);
            }
        };
        this.uiServiceTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        this.httpServiceTracker.close();
        this.uiServiceTracker.close();
    }

    public static class CommcareApiApplicationContext extends MotechOsgiWebApplicationContext {

        public CommcareApiApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

    }

    private void serviceAdded(HttpService service) {
        try {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
            dispatcherServlet.setContextClass(CommcareApiApplicationContext.class);
            ClassLoader old = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                UiHttpContext httpContext = new UiHttpContext(service.createDefaultHttpContext());

                service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, httpContext);
                service.registerResources(RESOURCE_URL_MAPPING, "/webapp", httpContext);
                logger.debug("Servlet registered");
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void serviceRemoved(HttpService service) {
        service.unregister(SERVLET_URL_MAPPING);
        logger.debug("Servlet unregistered");
    }

    private void serviceAdded(UIFrameworkService service) {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../commcare/");
        regData.addAngularModule("motech-commcare");

        regData.addI18N("messages", "../commcare/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            logger.error("Cant read header.html", e);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }

        service.registerModule(regData);
        logger.debug("Commcare registered in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(MODULE_NAME);
        logger.debug("Commcare unregistered from ui framework");
    }
}
