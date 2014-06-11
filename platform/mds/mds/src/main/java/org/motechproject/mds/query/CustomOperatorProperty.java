package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;

/**
 * The <code>CustomOperatorProperty</code> class represents a property that will be used in JDO query.
 * This class allows inserting a custom operator, such as {@code >, <=, matches(), etc.}
 *
 * @param <T> type of the passed value
 */
public class CustomOperatorProperty<T> extends Property<T> {

    private final String operator;

    public CustomOperatorProperty(String name, T value, String operator) {
        super(name, value);
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public CharSequence generateFilter(int idx) {
        if (shouldIgnoreThisProperty()) {
            return null;
        } else {
            if (isOperatorAMethod()) {
                return String.format("%s.%s(param%d)",
                        getName(), getOperator().substring(0, getOperator().length() - 2), idx);
            } else {
                return String.format("%s %s param%d", getName(), getOperator(), idx);
            }
        }
    }

    public boolean isOperatorAMethod() {
        return StringUtils.endsWith(operator, "()");
    }
}
