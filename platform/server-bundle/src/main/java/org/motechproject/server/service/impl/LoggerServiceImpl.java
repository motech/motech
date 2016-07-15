package org.motechproject.server.service.impl;

import org.motechproject.server.service.LoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <code>LoggerServiceImpl</code> class provides API for logging messages to server logs
 */
@Service("loggerService")
public class LoggerServiceImpl implements LoggerService{

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerServiceImpl.class);

    @Override
    @Transactional
    public void logMessage(String level, String message) {
        switch (level) {
            case "error":
                LOGGER.error(message);
                break;
            case "info":
                LOGGER.info(message);
                break;
            case "debug":
                LOGGER.debug(message);
                break;
        }
    }
}
