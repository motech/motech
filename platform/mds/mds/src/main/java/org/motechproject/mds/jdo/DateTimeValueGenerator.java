package org.motechproject.mds.jdo;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;

/**
 * The <code>DateTimeValueGenerator</code> class modifies properties with
 * {@link org.joda.time.DateTime} type. If the given value is null then the current time is
 * returned; otherwise the given value is returned.
 */
public abstract class DateTimeValueGenerator extends AbstractObjectValueGenerator<DateTime> {

    @Override
    protected DateTime modify(DateTime value) {
        return null == value ? DateUtil.now() : value;
    }

}
