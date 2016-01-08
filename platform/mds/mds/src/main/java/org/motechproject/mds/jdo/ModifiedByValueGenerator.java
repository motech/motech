package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;

/**
 * The <code>ModifiedByValueGenerator</code> class is responsible for generating value for
 * {@link org.motechproject.mds.util.Constants.Util#MODIFIED_BY_FIELD_NAME} field.
 */
public class ModifiedByValueGenerator extends UsernameValueGenerator {

    @Override
    protected String getPropertName() {
        return MODIFIED_BY_FIELD_NAME;
    }

}
