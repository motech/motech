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

public class FileEventLogger extends EventLogger {

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
                    String logString = eventConverter.convertEventToLogString(eventToLog);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (fileToLogTo.canWrite()) {
                writeToFile(informationToLog, fileToLogTo);
            } else {
                // TODO
            }
        }

    }

    private synchronized void writeToFile(String eventToLog, File fileToLogTo) {
        FileWriter fileStream = null;
        try {
            fileStream = new FileWriter(fileToLogTo, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter fileWriter = new BufferedWriter(fileStream);
        try {
            fileWriter.write((eventToLog));
            fileWriter.newLine();
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
