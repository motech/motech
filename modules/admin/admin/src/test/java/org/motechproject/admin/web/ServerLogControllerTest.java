package org.motechproject.admin.web;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.admin.web.controller.ServerLogController;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class ServerLogControllerTest {

    private MockMvc controller;

    private File logFile;

    @Before
    public void setUp() throws URISyntaxException {
        controller = MockMvcBuilders.standaloneSetup(new ServerLogController()).build();

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
}
