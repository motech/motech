package org.motechproject.mds.query;

/**
 * A convenience extension of the {@link CustomOperatorProperty}.
 * The custom operator is "equalsIgnoreCase()", the underlying type is String.
 */
public class EqualsCaseInsensitiveProperty extends CustomOperatorProperty<String> {

    public EqualsCaseInsensitiveProperty(String name, String value, String type, String operator) {
        super(name, QueryUtil.asEqualsPattern(value), type, operator);
    }

}
