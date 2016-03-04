package org.motechproject.mds.flyway;

import com.googlecode.flyway.core.Flyway;
import org.motechproject.mds.config.MdsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

/**
 * Initializes Flyway migrations for the Schema database on MDS startup.
 * It configures Flyway using user configuration if it is present.
 */
public class FlywayInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayInitializer.class);

    @Autowired
    private MdsConfig mdsConfig;

    @Autowired
    private Flyway flyway;

    public void migrate() {
        LOGGER.debug("Starting Flyway initializer");

        Properties flywaySchemaProps = mdsConfig.getFlywaySchemaProperties();

        LOGGER.debug("Configuring flyway with properties: {}", flywaySchemaProps);
        flyway.configure(flywaySchemaProps);

        LOGGER.info("Starting flyway migrations");
        flyway.migrate();
    }
}
