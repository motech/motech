package org.motechproject.admin.web.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@Controller
public class ServerLogController {

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

    private File getLogFile() {
       return new File(String.format("%s/logs/catalina.out", System.getProperty("catalina.base")));
    }
}
