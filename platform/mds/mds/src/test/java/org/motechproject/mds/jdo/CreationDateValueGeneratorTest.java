package org.motechproject.mds.jdo;

import org.joda.time.DateTime;

import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;

public class CreationDateValueGeneratorTest extends DateTimeValueGeneratorTest {
    private static final CreationDateValueGenerator GENERATOR = new CreationDateValueGenerator();

    @Override
    protected AbstractObjectValueGenerator<DateTime> getGenerator() {
        return GENERATOR;
    }

    @Override
    protected String getPropertyName() {
        return CREATION_DATE_FIELD_NAME;
    }
}
