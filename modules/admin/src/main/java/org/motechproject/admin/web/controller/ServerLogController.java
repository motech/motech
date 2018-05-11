package org.motechproject.admin.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.admin.exception.LogFileTooLargeException;
import org.motechproject.admin.security.SecurityConstants;
import org.motechproject.osgi.web.domain.LogMapping;
import org.motechproject.osgi.web.service.ServerLogService;
import org.motechproject.osgi.web.settings.Loggers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

/**
 * Controller responsible for the logs tab in the Admin UI.
 * Allows retrieving the system logs and changing the log4j log levels at runtime.
 */
@Controller
@Api(value="ServerLogController", description = "Controller responsible for the logs tab in the Admin UI.\n" +
        "Allows retrieving the system logs and changing the log4j log levels at runtime.")
public class ServerLogController {

    @Autowired
    private ServerLogService logService;

    private static final int TWENTY_FIVE_MB = (int) FileUtils.ONE_MB * 25;

    /**
     * Prints the server log. The log is retrieved from the catalina.out file from Tomcat.
     * This always retrieves the last megabyte of the log file. This is the method used for fetching the logs
     * when the user browses to them.
     * @param response the response to which the log will be printed
     * @throws IOException signals an issue with either reading the log file or writing the output
     */
    @PreAuthorize(SecurityConstants.MANAGE_LOGS)
    @RequestMapping(value = "/log", method = RequestMethod.GET)
    @ApiOperation(value="Prints the server log. The log is retrieved from the catalina.out file from Tomcat.\n" +
            "This always retrieves the last megabyte of the log file. This is the method used for fetching the logs\n" +
            "when the user browses to them.")
    public void getServerLog(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        File logFile = getLogFile();

        if (!logFile.exists()) {
            writer.write("server.tomcat.error.logFileNotFound");
        } else {
            long readSize = FileUtils.ONE_KB * 200;
            long fileSize = logFile.length();

            try (InputStream in = new FileInputStream(logFile)) {

                if (fileSize > readSize) {
                    in.skip(fileSize - readSize);

                    while (in.available() > 0) {
                        if (in.read() == '\n') {
                            break;
                        }
                    }
                }

                IOUtils.copy(in, writer);
            }
        }
    }

    /**
     * Prints the server log. The log is retrieved from the catalina.out file from Tomcat.
     * This reads and returns the entire log file, so the response can get big. On the UI this is only
     * activated using the RAW log button. If the file is larger than 2gb then an exception is thrown.
     * @param response the response to which the log will be printed
     * @throws IOException signals an issue with either reading the log file or writing the output
     * @throws org.motechproject.admin.exception.LogFileTooLargeException if the file is too large to be returned(over 2gb)
     */
    @PreAuthorize(SecurityConstants.MANAGE_LOGS)
    @RequestMapping(value = "/log/raw", method = RequestMethod.GET)
    @ApiOperation(value="Prints the server log. The log is retrieved from the catalina.out file from Tomcat.\n" +
            "This reads and returns the entire log file, so the response can get big. On the UI this is only\n" +
            "activated using the RAW log button. If the file is larger than 2gb then an exception is thrown.")
    public void getEntireServerLog(HttpServletResponse response) throws IOException, LogFileTooLargeException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        File logFile = getLogFile();

        if (!logFile.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            long length = logFile.length();
            // TODO: What to do when the logfile is more than 2GB?
            if (length > Integer.MAX_VALUE) {
                throw new LogFileTooLargeException("The log file is too large to be exported. Size: " +
                        FileUtils.byteCountToDisplaySize(length));
            }
            response.setContentLength((int) length);

            // If the file is too big browsers hang trying to render the large plaintext file. In this case force download.
            if (length > TWENTY_FIVE_MB) {
                response.setHeader("Content-Disposition", "attachment; filename=" + logFile.getName());
            }

            try (InputStream in = new FileInputStream(logFile)) {
                IOUtils.copy(in, writer);
            }
        }
    }

    /**
     * Returns the log4j log levels for the platform.
     * @return the levels in a data transfer object
     * @see org.motechproject.osgi.web.settings.Loggers
     */
    @PreAuthorize(SecurityConstants.MANAGE_LOGS)
    @RequestMapping(value = "/log/level", method = RequestMethod.GET)
    @ApiOperation(value="Returns the log4j log levels for the platform")
    @ResponseBody
    public Loggers getLogLevels() {
        return new Loggers(logService.getLogLevels(), logService.getRootLogLevel());
    }

    /**
     * Changes the log levels in the platform.
     * @param config the log level data transfer object describing the changes made to log levels
     * @see org.motechproject.osgi.web.settings.Loggers
     */
    @PreAuthorize(SecurityConstants.MANAGE_LOGS)
    @RequestMapping(value = "/log/level", method = RequestMethod.POST)
    @ApiOperation(value="Changes the log levels in the platform")
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
