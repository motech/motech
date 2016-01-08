package org.motechproject.mds.jdo;

import org.joda.time.DateTime;

import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;

public class ModificationDateValueGeneratorTest extends DateTimeValueGeneratorTest {
    private static final ModificationDateValueGenerator GENERATOR = new ModificationDateValueGenerator();

    @Override
    protected AbstractObjectValueGenerator<DateTime> getGenerator() {
        return GENERATOR;
    }

    @Override
    protected String getPropertyName() {
        return MODIFICATION_DATE_FIELD_NAME;
    }
}
