package org.motechproject.mds.jdo;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.motechproject.mds.util.SecurityUtil.getUsername;

public abstract class UsernameValueGeneratorTest extends AbstractObjectValueGeneratorTest<String> {

    @Override
    protected String getExpectedValue(boolean isNull) {
        return isNull ? defaultIfBlank(getUsername(), "") : "duke";
    }

}
