package org.motechproject.osgi.web.bundle;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.motechproject.osgi.web.domain.LogMapping;
import org.motechproject.osgi.web.exception.BundleConfigurationLoadingException;
import org.motechproject.osgi.web.service.ServerLogService;
import org.osgi.framework.Bundle;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;


/**
 * This <code>Log4JBundleLoader</code> class is responsible for loading
 * configuration of loggers from properties located in bundle classpaths (log4j.xml).
 */

public class Log4JBundleLoader {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Log4JBundleLoader.class);

    private static final String LOGGER_TAG = "logger";
    private static final String NAME_ATTR = "name";

    // default log4j configuration file
    private String log4JConf = "log4j.xml";
    private Properties loggerProperties;
    private List<LogMapping> loggers;

    @Autowired
    private ServerLogService logService;

    @PostConstruct
    public void loadLoggerDbConfiguration() {
        try {
            loggers = logService.getAllLogMappings();
            loggerProperties = createLoggerProperties(loggers);
        } catch (Exception e) {
            LOGGER.error("Failed loading loggers configuration from database");
        }
    }

    public void loadBundle(Bundle bundle) throws BundleConfigurationLoadingException, IOException {
        String symbolicName = bundle.getSymbolicName();

        LOGGER.debug("Looking for log4j config in {}", symbolicName);

        URL log4jUrl = bundle.getResource(log4JConf);
        if (log4jUrl != null) {
            LOGGER.debug("Log4j config found in {}, loading", symbolicName);

            InputStream log4jStream = null;
            try {
                URLConnection conn = log4jUrl.openConnection();
                log4jStream = conn.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                db.setEntityResolver(new EntityResolver() {

                    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                        return new InputSource(new StringReader(""));
                    }
                });

                Document log4jDoc = db.parse(log4jStream);
                if (loggers != null && checkLogXmlConfiguration(log4jDoc)) {
                    PropertyConfigurator.configure(loggerProperties);
                } else {
                    DOMConfigurator.configure(log4jDoc.getDocumentElement());
                }
                logService.reconfigure();
                LOGGER.debug("Added log4j configuration for [" + bundle.getLocation() + "]");
            } catch (ParserConfigurationException | SAXException e) {
                throw new BundleConfigurationLoadingException("Error while loading log4j configuration from " + bundle, e);
            } finally {
                IOUtils.closeQuietly(log4jStream);
            }
        }
    }

    public void setLog4jConf(String log4jConf) {
        this.log4JConf = log4jConf;
    }

    public boolean checkLogXmlConfiguration(Document log4jDoc) {
        log4jDoc.getDocumentElement().normalize();
        NodeList nList = log4jDoc.getElementsByTagName(LOGGER_TAG);
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element loggerNode = (Element) nNode;
                if (checkListContainLogger(loggers, loggerNode.getAttribute(NAME_ATTR))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkListContainLogger(List<LogMapping> loggers, String log) {
        for (LogMapping logger : loggers) {
            if (logger.getLogName().equals(log)) {
                return true;
            }
        }
        return false;
    }

    public Properties createLoggerProperties(List<LogMapping> log) {
        loggerProperties = new Properties();
        for (LogMapping aLog : log) {
            if (("root").equals(aLog.getLogName())) {
                loggerProperties.put("log4j.root", aLog.getLogLevel() + "," + aLog.getLogName());
                loggerProperties.put("log4j.appender", "org.apache.log4j.ConsoleAppender");
                loggerProperties.put("log4j.appender.root.layout", "org.apache.log4j.PatternLayout");
                loggerProperties.put("log4j.appender.root.layout.ConversionPattern", "%d %-5p [%c] %m%n");
            } else {
                loggerProperties.put("log4j.logger." + aLog.getLogName(), aLog.getLogLevel());
            }
        }
        return loggerProperties;
    }
}
