package org.motechproject.mds.query;

import org.motechproject.mds.util.Constants;

/**
 * A convenience extension of the {@link org.motechproject.mds.query.CustomOperatorProperty}.
 * The custom operator is "matches()" and the underlying type is String.
 * The value passed will be wrapped inside .*.* for matching purposes.
 */
public class MatchesProperty extends CustomOperatorProperty<String> {

    public MatchesProperty(String name, String value) {
        super(name, QueryUtil.asMatchesPattern(value), Constants.Operators.MATCHES);
    }
}
