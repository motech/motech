package org.motechproject.admin.web.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.admin.domain.LogMapping;
import org.motechproject.admin.service.ServerLogService;
import org.motechproject.admin.settings.Loggers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class ServerLogController {
    private ServerLogService logService;

    @Autowired
    public ServerLogController(ServerLogService logService) {
        this.logService = logService;
    }

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public void getServerLog(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        File logFile = getLogFile();

        if (!logFile.exists()) {
            writer.write("tomcat.error.logFileNotFound");
        } else {
            long readSize = FileUtils.ONE_MB;
            long fileSize = logFile.length();

            try (InputStream in = new FileInputStream(logFile)) {

                if (fileSize > readSize) {
                    in.skip(fileSize - readSize);

                    while (in.available() > 0 && in.read() != '\n') {
                    }
                }

                IOUtils.copy(in, writer);
            }
        }
    }

    @RequestMapping(value = "/log/raw", method = RequestMethod.GET)
    public void getEntireServerLog(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        File logFile = getLogFile();

        if (!logFile.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            int length = (int) logFile.length();
            response.setContentLength(length);

            // If the file is too big browsers hang trying to render the large plaintext file. In this case force download.
            if (length > FileUtils.ONE_MB * 25) {
                response.setHeader("Content-Disposition", "attachment; filename=" + logFile.getName());
            }

            try (InputStream in = new FileInputStream(logFile)) {
                IOUtils.copy(in, writer);
            }
        }
    }

    @RequestMapping(value = "/log/level", method = RequestMethod.GET)
    @ResponseBody
    public Loggers getLogLevels() {
        return new Loggers(logService.getLogLevels(), logService.getRootLogLevel());
    }

    @RequestMapping(value = "/log/level", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void changeLogLevels(@RequestBody Loggers config) {
        if (config != null) {
            LogMapping root = config.getRoot();
            List<LogMapping> loggers = config.getLoggers();
            List<LogMapping> trash = config.getTrash();

            if (root != null) {
                logService.changeRootLogLevel(root.getLogLevel());
            }

            if (loggers != null) {
                for (LogMapping mapping : loggers) {
                    logService.changeLogLevel(mapping.getLogName(), mapping.getLogLevel());
                }
            }

            if (trash != null) {
                for (LogMapping mapping : trash) {
                    logService.removeLogger(mapping.getLogName());
                }
            }
        }
    }

    private File getLogFile() {
        return new File(String.format("%s/logs/catalina.out", System.getProperty("catalina.base")));
    }
}
