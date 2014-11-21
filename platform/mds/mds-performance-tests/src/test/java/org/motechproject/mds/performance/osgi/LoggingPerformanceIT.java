package org.motechproject.mds.performance.osgi;


import org.motechproject.testing.osgi.BasePaxIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

abstract class LoggingPerformanceIT extends BasePaxIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPerformanceIT.class);

    private static final String RESOURCE_USAGE_LOG_FILE = "target/performanceTestResult.log";


    protected void logToFile(long value) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RESOURCE_USAGE_LOG_FILE, true)))) {
            StringBuilder log = new StringBuilder();
            log.append(this.getClass().getSimpleName()+',');
            log.append(Thread.currentThread().getStackTrace()[2].getMethodName()+',');
            log.append(Long.toString(value));
            out.println(log);
        } catch (IOException e) {
            LOGGER.error("Couldn't save to file " + RESOURCE_USAGE_LOG_FILE + ".");
        }
    }
}