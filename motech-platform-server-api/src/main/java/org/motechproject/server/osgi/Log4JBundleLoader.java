package org.motechproject.server.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.xml.DOMConfigurator;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Custom log4j configuration loader
 * <p/>
 * Merge bundle specific configurations to the existing if a bundle contains
 * log4j.xml
 *
 * @author Ricky Wang
 */
public class Log4JBundleLoader implements BundleLoader {

    private static Logger logger = LoggerFactory.getLogger(Log4JBundleLoader.class);

    // default log4j configuration file
    private String log4jConf = "log4j.xml";

    @Override
    public void loadBundle(Bundle bundle) throws Exception {
        URL log4jUrl = bundle.getResource(log4jConf);
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

            log4jStream.close();
            logger.debug("Added log4j configuration for [" + bundle.getLocation() + "]");
        }
    }

    public void setLog4jConf(String log4jConf) {
        this.log4jConf = log4jConf;
    }

}
