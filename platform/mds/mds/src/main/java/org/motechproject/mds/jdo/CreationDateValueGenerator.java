package org.motechproject.mds.jdo;

import static org.motechproject.mds.util.Constants.Util.CREATION_DATE_FIELD_NAME;

/**
 * The <code>CreationDateValueGenerator</code> class is responsible for generating value for
 * {@link org.motechproject.mds.util.Constants.Util#CREATION_DATE_FIELD_NAME} field.
 */
public class CreationDateValueGenerator extends DateTimeValueGenerator {

    @Override
    protected String getPropertName() {
        return CREATION_DATE_FIELD_NAME;
    }

}
