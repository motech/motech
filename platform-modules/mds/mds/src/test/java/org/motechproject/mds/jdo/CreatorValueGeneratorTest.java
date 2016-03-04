package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;

public class CreatorValueGeneratorTest extends UsernameValueGeneratorTest {
    private static final CreatorValueGenerator GENERATOR = new CreatorValueGenerator();

    @Override
    protected AbstractObjectValueGenerator<String> getGenerator() {
        return GENERATOR;
    }

    @Override
    protected String getPropertyName() {
        return CREATOR_FIELD_NAME;
    }
}
