package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.CREATOR_FIELD_NAME;

/**
 * The <code>CreatorValueGenerator</code> class is responsible for generating value for
 * {@link org.motechproject.mds.util.Constants.Util#CREATOR_FIELD_NAME} field.
 */
public class CreatorValueGenerator extends UsernameValueGenerator {

    @Override
    protected String getPropertName() {
        return CREATOR_FIELD_NAME;
    }

}
