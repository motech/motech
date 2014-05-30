package org.motechproject.osgi.web.service;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.osgi.web.domain.LogMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testWebUtilApplication.xml"})
public class ServerLogServiceIT {
    private final static String ROOT_LOGGER_NAME = "root";

    @Autowired
    private CoreConfigurationService coreConfigurationService;

    @Autowired
    private ServerLogService logService;

    @Before
    public void setUp() {
        cleanLoggerSettings();
    }

    @After
    public void tearDown() {
        cleanLoggerSettings();
    }

    private void cleanLoggerSettings() {
        for (LogMapping logMapping : logService.getAllLogMappings()) {
            logService.removeLogger(logMapping.getLogName());
        }
    }

    @Test
    public void shouldGetRootLogLevel() {
        LogMapping expected = new LogMapping(ROOT_LOGGER_NAME, Level.ERROR.toString());

        logService.changeRootLogLevel("error");

        LogMapping actual = logService.getRootLogLevel();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetLogLevels() {
        String adminPackage = "org.motechproject.admin";
        LogMapping expected = new LogMapping(adminPackage, Level.INFO.toString());

        logService.changeLogLevel(expected.getLogName(), expected.getLogLevel());

        List<LogMapping> logLevels = logService.getLogLevels();

        assertEquals(1, logLevels.size());

        LogMapping mapping = logLevels.get(0);

        assertEquals(adminPackage, mapping.getLogName());
        assertEquals("INFO", mapping.getLogLevel());
    }

    @Test
    public void shouldChangeRootLogLevel() {
        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.ERROR);

        logService.changeRootLogLevel(Level.DEBUG.toString());
        LogMapping logMapping = logService.getRootLogLevel();

        assertEquals(ROOT_LOGGER_NAME, logMapping.getLogName());
        assertEquals(rootLogger.getLevel().toString(), logMapping.getLogLevel());
    }

    @Test
    public void shouldChangeLogLevels() throws Exception {
        String adminPackage = "org.motechproject.admin";

        Logger adminLogger = LogManager.getLogger(adminPackage);
        adminLogger.setLevel(Level.INFO);

        logService.changeLogLevel(adminPackage, Level.TRACE.toString());
        List<LogMapping> logMappings = logService.getLogLevels();

        assertTrue(adminLogger.isTraceEnabled());
        assertTrue(logMappings.contains(new LogMapping(adminPackage, "TRACE")));
    }

    @Test
    public void shouldRemoveLogLevels() throws Exception {
        String adminPackage = "org.motechproject.admin";

        Logger adminLogger = LogManager.getLogger(adminPackage);
        adminLogger.setLevel(Level.DEBUG);

        assertTrue(adminLogger.isDebugEnabled());

        logService.removeLogger(adminPackage);

        List<LogMapping> logMappings = logService.getLogLevels();
        assertTrue(!logMappings.contains(new LogMapping(adminPackage, "DEBUG")));
    }

}
