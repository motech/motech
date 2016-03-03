package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;

/**
 * The <code>ModificationDateValueGenerator</code> class is responsible for generating value for
 * {@link org.motechproject.mds.util.Constants.Util#MODIFICATION_DATE_FIELD_NAME} field.
 */
public class ModificationDateValueGenerator extends DateTimeValueGenerator {

    @Override
    protected String getPropertName() {
        return MODIFICATION_DATE_FIELD_NAME;
    }

}
