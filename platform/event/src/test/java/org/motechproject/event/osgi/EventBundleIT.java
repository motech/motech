package org.motechproject.event.osgi;


import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.context.config.ContextNamespaceHandler;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class EventBundleIT extends BaseOsgiIT {
    @Override
    protected String getPlatformName() {
        return Platforms.EQUINOX;
    }

    public void testEventBundle() throws Exception {
        final String subject = "OSGi IT - 001";

        ServiceReference registryReference = bundleContext.getServiceReference(EventListenerRegistry.class.getName());
        assertNotNull(registryReference);
        EventListenerRegistry registry = (EventListenerRegistry)bundleContext.getService(registryReference);
        assertNotNull(registry);
        registry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                System.out.print("Event received. " + event);
            }

            @Override
            public String getIdentifier() {
                return subject;
            }
        }, subject);

        ServiceReference relayReference = bundleContext.getServiceReference(EventRelay.class.getName());
        assertNotNull(relayReference);
        EventRelay eventRelay = (EventRelay)bundleContext.getService(relayReference);
        assertNotNull(eventRelay);
        eventRelay.sendEventMessage(new MotechEvent(subject));


    }

    @Override
    protected Manifest getManifest() {
        StringBuilder builder = new StringBuilder();
        builder
                //.append("junit.framework").append(",")
                //.append("org.osgi.framework").append(",")
                //.append("org.apache.commons.logging").append(",")
                //.append("org.springframework.util").append(",")
                //.append("org.springframework.osgi.service").append(",")
                //.append("org.springframework.osgi.util").append(",")
                //.append("org.springframework.osgi.test").append(",")
                //.append("org.springframework.context").append(",")
                .append("org.motechproject.event").append(",")
                .append("org.motechproject.event.listener");


        Manifest mf = super.getManifest();
        String imports = (String)mf.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
        mf.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, builder.append(",").append(imports).toString());
        return mf;
    }
}