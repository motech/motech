package org.motechproject.server.impl;

import org.apache.log4j.xml.DOMConfigurator;
import org.motechproject.server.api.BundleLoader;
import org.motechproject.server.api.BundleLoadingException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

/**
 * Custom log4j configuration loader
 * <p/>
 * Merge bundle specific configurations to the existing if a bundle contains
 * log4j.xml
 *
 * @author Ricky Wang
 */
public class Log4JBundleLoader implements BundleLoader {
    private static final String EVENT_RELAY_CLASS_NAME = "org.motechproject.event.listener.EventRelay";
    private static final String MOTECH_EVENT_CLASS_NAME = "org.motechproject.event.MotechEvent";
    private static final String LOG_RECONFIGURATION_EVENT_KEY = "org.motechproject.admin.log.reconfiguration";

    private static Logger logger = LoggerFactory.getLogger(Log4JBundleLoader.class);

    // default log4j configuration file
    private String log4jConf = "log4j.xml";

    @Override
    public void loadBundle(Bundle bundle) throws BundleLoadingException {
        URL log4jUrl = bundle.getResource(log4jConf);
        try {
            if (log4jUrl != null) {
                URLConnection conn = log4jUrl.openConnection();
                InputStream log4jStream = conn.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                db.setEntityResolver(new EntityResolver() {

                    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                        return new InputSource(new StringReader(""));
                    }
                });

                Document log4jDoc = db.parse(log4jStream);
                DOMConfigurator.configure(log4jDoc.getDocumentElement());

                logReconfiguration(bundle.getBundleContext());

                log4jStream.close();
                logger.debug("Added log4j configuration for [" + bundle.getLocation() + "]");
            }
        } catch (Exception e) {
            throw new BundleLoadingException(e);
        }
    }

    private void logReconfiguration(BundleContext context) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        ServiceReference serviceReference = context.getServiceReference(EVENT_RELAY_CLASS_NAME);

        if (serviceReference != null) {
            Object service = context.getService(serviceReference);
            Class<?> serviceClass = service.getClass();
            Class<?> motechEventClass = serviceClass.getClassLoader().loadClass(MOTECH_EVENT_CLASS_NAME);

            Method sendEventMessage = serviceClass.getMethod("sendEventMessage", motechEventClass);
            Object obj = motechEventClass.getDeclaredConstructor(String.class).newInstance(LOG_RECONFIGURATION_EVENT_KEY);

            sendEventMessage.invoke(service, obj);
        }
    }

    public void setLog4jConf(String log4jConf) {
        this.log4jConf = log4jConf;
    }

}
