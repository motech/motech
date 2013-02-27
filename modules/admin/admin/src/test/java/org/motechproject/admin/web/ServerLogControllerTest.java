package org.motechproject.admin.web;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.admin.domain.LogMapping;
import org.motechproject.admin.service.ServerLogService;
import org.motechproject.admin.settings.Loggers;
import org.motechproject.admin.web.controller.ServerLogController;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.admin.service.ServerLogService.ROOT_LOGGER_NAME;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LogManager.class)
public class ServerLogControllerTest {
    @Mock
    private ServerLogService logService;

    private ServerLogController logController;

    private MockMvc controller;

    private File logFile;

    @Before
    public void setUp() throws URISyntaxException {
        initMocks(this);

        logController = new ServerLogController(logService);
        controller = MockMvcBuilders.standaloneSetup(logController).build();

        URL logUrl = getClass().getClassLoader().getResource("logs/catalina.out");
        logFile = new File(logUrl.toURI());

        System.setProperty("catalina.base", logFile.getParentFile().getParent());
    }

    @Test
    public void testRaw() throws Exception {
        controller.perform(
                get("/log/raw").contentType(MediaType.TEXT_PLAIN)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        ).andExpect(
                content().string(FileUtils.readFileToString(logFile))
        );
    }


    @Test
    public void testGetLog() throws Exception {
        controller.perform(
                get("/log").contentType(MediaType.TEXT_PLAIN)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        ).andExpect(
                content().string(FileUtils.readFileToString(logFile))
        );
    }

    @Test
    public void testMissingLog() throws Exception {
        System.setProperty("catalina.base", "nonsense");

        controller.perform(
                get("/log").contentType(MediaType.TEXT_PLAIN)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        ).andExpect(
                content().string("tomcat.error.logFileNotFound")
        );
    }

    @Test
    public void testMissingRaw() throws Exception {
        System.setProperty("catalina.base", "nonsense");

        controller.perform(
                get("/log/raw").contentType(MediaType.TEXT_PLAIN)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        );
    }

    @Test
    public void testGetLogLevels() {
        String adminPackage = "org.motechproject.admin";

        List<LogMapping> logMappings = new ArrayList<>();
        logMappings.add(new LogMapping(adminPackage, "INFO"));

        when(logService.getRootLogLevel()).thenReturn(new LogMapping(ROOT_LOGGER_NAME, "ERROR"));
        when(logService.getLogLevels()).thenReturn(logMappings);

        Loggers loggers = logController.getLogLevels();

        verify(logService).getLogLevels();
        verify(logService).getRootLogLevel();

        assertNotNull(loggers.getRoot());
        assertNotNull(loggers.getLoggers());
        assertNull(loggers.getTrash());

        assertEquals(1, loggers.getLoggers().size());

        assertEquals("ERROR", loggers.getRoot().getLogLevel());
        assertEquals("INFO", loggers.getLoggers().get(0).getLogLevel().toUpperCase());
    }

    @Test
    public void testChangeLogLevelsNullMap() {
        logController.changeLogLevels(null);
        logController.changeLogLevels(new Loggers());

        verify(logService, never()).changeRootLogLevel(anyString());
        verify(logService, never()).changeLogLevel(anyString(), anyString());
        verify(logService, never()).removeLogger(anyString());
    }
}
