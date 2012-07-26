package org.motechproject.eventlogging.loggers.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.motechproject.eventlogging.converter.impl.DefaultFileToLogConverter;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.loggers.EventLogger;
import org.motechproject.scheduler.domain.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEventLogger extends EventLogger {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<File> loggingFiles;

    private DefaultFileToLogConverter eventConverter;

    public FileEventLogger(DefaultFileToLogConverter eventConverter) {
        this.eventConverter = eventConverter;
    }

    public FileEventLogger(List<LoggableEvent> loggableEvents,
            List<File> loggingFiles, DefaultFileToLogConverter eventConverter) {
        this.loggableEvents = loggableEvents;
        if (loggingFiles == null) {
            this.loggingFiles = new ArrayList<File>();
        } else {
            this.loggingFiles = loggingFiles;
        }
        this.eventConverter = eventConverter;
    }

    public void log(MotechEvent eventToLog) {
        for (LoggableEvent loggableEvent : loggableEvents) {
            if (loggableEvent.isLoggableEvent(eventToLog)) {
                if (eventConverter != null) {
                    String logString = eventConverter
                            .convertEventToLogString(eventToLog);
                    log(logString);
                } else {
                    return;
                }
            }
        }
    }

    protected void log(String informationToLog) {
        for (File fileToLogTo : loggingFiles) {
            try {
                fileToLogTo.createNewFile();
            } catch (IOException e) {
                logger.warn("Unable to create file: "
                        + fileToLogTo.getAbsolutePath());
            }
            if (fileToLogTo.canWrite()) {
                writeToFile(informationToLog, fileToLogTo);
            } else {
                logger.warn("Unable to write to: "
                        + fileToLogTo.getAbsolutePath());
            }
        }

    }

    private synchronized void writeToFile(String eventToLog, File fileToLogTo) {
        FileWriter fileStream = null;
        try {
            fileStream = new FileWriter(fileToLogTo, true);
        } catch (IOException e) {
            logger.warn("Unable to open: " + fileToLogTo.getAbsolutePath());
        }
        BufferedWriter fileWriter = new BufferedWriter(fileStream);
        try {
            fileWriter.write((eventToLog));
            fileWriter.newLine();
            fileWriter.flush();
        } catch (IOException e) {
            logger.warn("Unable to write to: " + fileToLogTo.getAbsolutePath());
        }
        try {
            fileStream.close();
        } catch (IOException e) {
            logger.warn("Unable to close: " + fileToLogTo.getAbsolutePath());
        }

    }

}
