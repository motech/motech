package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;

public class OwnerValueGeneratorTest extends UsernameValueGeneratorTest {
    private static final OwnerValueGenerator GENERATOR = new OwnerValueGenerator();

    @Override
    protected AbstractObjectValueGenerator<String> getGenerator() {
        return GENERATOR;
    }

    @Override
    protected String getPropertyName() {
        return OWNER_FIELD_NAME;
    }
}
