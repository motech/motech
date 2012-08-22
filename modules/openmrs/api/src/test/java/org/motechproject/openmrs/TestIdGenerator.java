package org.motechproject.openmrs;

import org.motechproject.util.DateUtil;

import static org.apache.commons.lang.RandomStringUtils.random;

public class TestIdGenerator {

    public static String newGUID(String seed) {
        return seed  + "_" + DateUtil.now().toString("ddMMyyyy_hhmmss") + random(2, seed);
    }
}
