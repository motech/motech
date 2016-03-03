package org.motechproject.mds.jdo;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * The <code>UsernameValueGenerator</code> class modifies properties with
 * {@link java.lang.String} type. The given value is returned without any change if it is not blank.
 * Otherise the class tries to get current logged user name. If the user exists and name is not
 * blank then this name is returned otherwise the empty string is returned.
 */
public abstract class UsernameValueGenerator extends AbstractObjectValueGenerator<String> {

    @Override
    protected String modify(String value) {
        return defaultIfBlank(value, defaultIfBlank(getUsername(), ""));
    }

}
