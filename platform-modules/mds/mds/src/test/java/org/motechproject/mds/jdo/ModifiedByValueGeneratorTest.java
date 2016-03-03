package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;

public class ModifiedByValueGeneratorTest extends UsernameValueGeneratorTest {
    private static final ModifiedByValueGenerator GENERATOR = new ModifiedByValueGenerator();

    @Override
    protected AbstractObjectValueGenerator<String> getGenerator() {
        return GENERATOR;
    }

    @Override
    protected String getPropertyName() {
        return MODIFIED_BY_FIELD_NAME;
    }
}
