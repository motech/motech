package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;

/**
 * The <code>OwnerValueGenerator</code> class is responsible for generating value for
 * {@link org.motechproject.mds.util.Constants.Util#OWNER_FIELD_NAME} field.
 */
public class OwnerValueGenerator extends UsernameValueGenerator {

    @Override
    protected String getPropertName() {
        return OWNER_FIELD_NAME;
    }

}
