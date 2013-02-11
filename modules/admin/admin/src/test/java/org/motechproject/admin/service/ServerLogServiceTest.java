package org.motechproject.admin.service;

import junit.framework.Assert;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.admin.domain.LogMapping;
import org.motechproject.admin.repository.AllLogMappings;
import org.motechproject.admin.service.impl.ServerLogServiceImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.admin.service.ServerLogService.ROOT_LOGGER_NAME;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LogManager.class)
public class ServerLogServiceTest {

    @Mock
    private AllLogMappings allLogMappings;

    private ServerLogService logService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        logService = new ServerLogServiceImpl(allLogMappings, false);
    }

    @Test
    public void shouldGetRootLogLevel() {
        LogMapping expected = new LogMapping(ROOT_LOGGER_NAME, Level.ERROR.toString());

        when(allLogMappings.byLogName(ROOT_LOGGER_NAME)).thenReturn(expected);

        LogMapping actual = logService.getRootLogLevel();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetLogLevels() {
        String adminPackage = "org.motechproject.admin";
        LogMapping expected = new LogMapping(adminPackage, Level.INFO.toString());

        when(allLogMappings.getAll()).thenReturn(asList(expected));

        List<LogMapping> logLevels = logService.getLogLevels();

        assertEquals(1, logLevels.size());

        LogMapping mapping = logLevels.get(0);

        assertEquals(adminPackage, mapping.getLogName());
        assertEquals("INFO", mapping.getLogLevel().toUpperCase());
    }

    @Test
    public void shouldChangeRootLogLevel() {
        Logger rootLogger = LogManager.getRootLogger();
        rootLogger.setLevel(Level.ERROR);

        PowerMockito.mockStatic(LogManager.class);
        PowerMockito.when(LogManager.getRootLogger()).thenReturn(rootLogger);

        logService.changeRootLogLevel(Level.DEBUG.toString());

        ArgumentCaptor<LogMapping> captor = ArgumentCaptor.forClass(LogMapping.class);

        verify(allLogMappings).addOrUpdate(captor.capture());

        LogMapping logMapping = captor.getValue();

        Assert.assertEquals(ROOT_LOGGER_NAME, logMapping.getLogName());
        Assert.assertEquals(rootLogger.getLevel().toString(), logMapping.getLogLevel());
    }

    @Test
    public void shouldChangeLogLevels() throws Exception {
        String adminPackage = "org.motechproject.admin";

        Logger adminLogger = LogManager.getLogger(adminPackage);
        adminLogger.setLevel(Level.INFO);

        PowerMockito.mockStatic(LogManager.class);
        PowerMockito.when(LogManager.getLogger(adminPackage)).thenReturn(adminLogger);

        logService.changeLogLevel(adminPackage, Level.TRACE.toString());

        ArgumentCaptor<LogMapping> captor = ArgumentCaptor.forClass(LogMapping.class);

        verify(allLogMappings).addOrUpdate(captor.capture());

        LogMapping logMapping = captor.getValue();

        Assert.assertEquals(adminPackage, logMapping.getLogName());
        Assert.assertEquals(adminLogger.getLevel().toString(), logMapping.getLogLevel());
    }

    @Test
    public void shouldRemoveLogLevels() throws Exception {
        String adminPackage = "org.motechproject.admin";

        Logger adminLogger = LogManager.getLogger(adminPackage);
        adminLogger.setLevel(Level.INFO);

        PowerMockito.mockStatic(LogManager.class);
        PowerMockito.when(LogManager.getLogger(adminPackage)).thenReturn(adminLogger);

        logService.removeLogger(adminPackage);

        verify(allLogMappings).removeByLogName(adminPackage);
    }

}
