/**
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
package org.motechproject.server.demo;

import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.event.annotations.EventAnnotationBeanPostProcessor;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activator implements BundleActivator, ServiceListener {
	private static Logger logger = LoggerFactory.getLogger(Activator.class);
	private static final String CONTEXT_CONFIG_LOCATION = "classpath:applicationDemo.xml";
	private static final String SERVLET_URL_MAPPING = "/demo";
	private ServiceTracker tracker;
    private ServiceReference httpService;

    private static Activator instance;
    private BundleContext context = null;
    private final List<ServiceReference> ivrServiceList = new ArrayList<ServiceReference>();
    private final Map<ServiceReference, Object> refToObjMap = new HashMap<ServiceReference, Object>();

	@Override
	public void start(BundleContext context) throws Exception {
		this.tracker = new ServiceTracker(context,
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
		this.tracker.open();
        httpService = context.getServiceReference(HttpService.class.getName());
        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceAdded(service);
        }

        instance = this;
        this.context = context;

        synchronized (ivrServiceList) {
            context.addServiceListener(this, "(objectClass=" + IVRService.class.getName() + ")");

            ServiceReference[] refs = context.getServiceReferences(IVRService.class.getName(), "(objectClass=" + IVRService.class.getName() + ")");

            if (refs != null) {
                for (ServiceReference ref : refs) {
                    Object service = context.getService(ref);

                    if ((service != null) && (refToObjMap.get(ref) == null)) {
                        ivrServiceList.add(ref);
                        refToObjMap.put(ref, service);
                    }
                }
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        this.tracker.close();
        if (httpService != null) {
            HttpService service = (HttpService) context.getService(httpService);
            serviceRemoved(service);
        }
    }

	private void serviceAdded(HttpService service) {
		try {
			DispatcherServlet dispatcherServlet = new DispatcherServlet();
			dispatcherServlet.setContextConfigLocation(CONTEXT_CONFIG_LOCATION);
			ClassLoader old = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
				service.registerServlet(SERVLET_URL_MAPPING, dispatcherServlet, null, null);
				logger.debug("Servlet registered");
			} finally {
				Thread.currentThread().setContextClassLoader(old);
			}
			
			// register all annotated handlers
			EventAnnotationBeanPostProcessor.registerHandlers(BeanFactoryUtils.beansOfTypeIncludingAncestors(dispatcherServlet.getWebApplicationContext(), Object.class));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

	}

	private void serviceRemoved(HttpService service) {
		service.unregister(SERVLET_URL_MAPPING);
		logger.debug("Servlet unregistered");
	}

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        synchronized (ivrServiceList) {
            if (serviceEvent.getType() == ServiceEvent.REGISTERED) {
                Object service = context.getService(serviceEvent.getServiceReference());

                if ((service != null) && (refToObjMap.get(serviceEvent.getServiceReference()) == null)) {
                    ivrServiceList.add(serviceEvent.getServiceReference());
                    refToObjMap.put(serviceEvent.getServiceReference(), service);
                }
                else if (service != null) {
                    context.ungetService(serviceEvent.getServiceReference());
                }
            }
            else if (serviceEvent.getType() == ServiceEvent.UNREGISTERING) {
                if (refToObjMap.get(serviceEvent.getServiceReference()) != null) {
                    context.ungetService(serviceEvent.getServiceReference());
                    ivrServiceList.remove(serviceEvent.getServiceReference());
                    refToObjMap.remove(serviceEvent.getServiceReference());
                }
            }
        }
    }

    public static Activator getInstance() {
        return instance;
    }

    public IVRService getIvrService() {
        if (ivrServiceList.size() > 0) {
            return (IVRService) refToObjMap.get(ivrServiceList.get(0));
        }

        return null;
    }
}
