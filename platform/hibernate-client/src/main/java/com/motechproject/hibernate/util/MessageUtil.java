package com.motechproject.hibernate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MessageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);

    @PostConstruct
    public void after() {
        LOGGER.error("MessageUtil initialized in hibernate-client");
    }

}
