package org.motechproject.server.service.impl;

import org.motechproject.server.service.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The <code>LoggerServiceImpl</code> class provides API for logging messages to server logs
 */
@Service("loggerService")
public class LoggerServiceImpl implements LoggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerServiceImpl.class);

    @Override
    public void logMessage(String level, String message) {
        switch (level) {
            case "logger.log.level.trace":
                LOGGER.trace(message);
                break;
            case "logger.log.level.debug":
                LOGGER.debug(message);
                break;
            case "logger.log.level.info":
                LOGGER.info(message);
                break;
            case "logger.log.level.warn":
                LOGGER.warn(message);
                break;
            default:
                LOGGER.error(message);
                break;
        }
    }
}
