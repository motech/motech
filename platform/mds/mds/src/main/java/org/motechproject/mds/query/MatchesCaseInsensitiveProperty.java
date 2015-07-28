package org.motechproject.mds.query;

import org.motechproject.mds.util.Constants;

/**
 * A convenience extension of the {@link CustomOperatorProperty}.
 * The custom operator is "matches()" with an added case insensitivity flag added, the underlying type is String.
 * The value passed will be wrapped inside (?i).*.* for matching purposes.
 */
public class MatchesCaseInsensitiveProperty extends CustomOperatorProperty<String> {

    private static final String CASE_INSENSITIVE_FLAG = "(?i)";

    public MatchesCaseInsensitiveProperty(String name, String value) {
        super(name, CASE_INSENSITIVE_FLAG + QueryUtil.asMatchesPattern(value), String.class.getName(),
                Constants.Operators.MATCHES);
    }

    @Override
    protected boolean shouldIgnoreThisProperty() {
        // null cannot be used with the matches operator
        return getValue() == null;
    }
}
