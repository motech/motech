package org.motechproject.testing.uifunctionaltests;

import org.junit.Test;
import org.motech.test.TestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldFT extends TestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldFT.class);

    @Test
    public void testHelloWorld() throws Exception {
        LOGGER.info("Hello World!");
    }

}
