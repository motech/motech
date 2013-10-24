package org.motechproject.osgi.web;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.motechproject.osgi.web.domain.LogMapping;
import org.motechproject.osgi.web.repository.AllLogMappings;
import org.motechproject.osgi.web.service.ServerLogService;
import org.motechproject.server.api.BundleLoadingException;
import org.osgi.framework.Bundle;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import static org.apache.log4j.LogManager.getLogger;


/**
 * This <code>Log4JBundleLoader</code> class is responsible for loading logger's configuration from database or file(log4j.xml).
 */

public class Log4JBundleLoader {
    // default log4j configuration file
    private String log4jConf = "log4j.xml";
    private String loggerTag = "logger";
    private String nameAttr = "name";
    private Properties loggerProperties;
    private List<LogMapping> loggers;
    private static Logger logger = getLogger(Log4JBundleLoader.class);

    @Autowired
    private ServerLogService logService;

    @Autowired
    private AllLogMappings allLogMappings;


    @PostConstruct
    public void loadLoggerDbConfiguration() {
        try {
            loggers = allLogMappings.getAll();
            loggerProperties = createLoggerProperties(loggers);
        } catch (Exception e) {
            logger.error("Failed loading loggers configuration from database");
        }
    }

    public void loadBundle(Bundle bundle) throws BundleLoadingException, IOException {
        URL log4jUrl = bundle.getResource(log4jConf);
        if (log4jUrl != null) {
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
                logger.debug("Added log4j configuration for [" + bundle.getLocation() + "]");
            } catch (Exception e) {
                throw new BundleLoadingException(e);
            } finally {
                log4jStream.close();
            }
        }
    }

    public void setLog4jConf(String log4jConf) {
        this.log4jConf = log4jConf;
    }

    public boolean checkLogXmlConfiguration(Document log4jDoc) {
        log4jDoc.getDocumentElement().normalize();
        NodeList nList = log4jDoc.getElementsByTagName(loggerTag);
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element loggerNode = (Element) nNode;
                if (checkListContainLogger(loggers, loggerNode.getAttribute(nameAttr))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkListContainLogger(List<LogMapping> loggers, String log) {
        for (int i = 0; i < loggers.size(); i++) {
            if (loggers.get(i).getLogName().equals(log)) {
                return true;
            }
        }
        return false;
    }

    public Properties createLoggerProperties(List<LogMapping> log) {
        loggerProperties = new Properties();
        for (int i = 0; i < log.size(); i++) {
            if (("root").equals(log.get(i).getLogName())) {
                loggerProperties.put("log4j.root", log.get(i).getLogLevel() + "," + log.get(i).getLogName());
                loggerProperties.put("log4j.appender", "org.apache.log4j.ConsoleAppender");
                loggerProperties.put("log4j.appender.root.layout", "org.apache.log4j.PatternLayout");
                loggerProperties.put("log4j.appender.root.layout.ConversionPattern", "%d %-5p [%c] %m%n");
            } else {
                loggerProperties.put("log4j.logger." + log.get(i).getLogName(), log.get(i).getLogLevel());
            }
        }
        return loggerProperties;
    }
}
