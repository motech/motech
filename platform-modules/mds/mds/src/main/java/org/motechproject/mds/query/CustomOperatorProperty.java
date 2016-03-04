package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.util.LookupName;

/**
 * The <code>CustomOperatorProperty</code> class represents a property that will be used in JDO query.
 * This class allows inserting a custom operator, such as {@code >, <=, matches(), etc.}
 *
 * @param <T> type of the passed value
 */
public class CustomOperatorProperty<T> extends Property<T> {

    private final String operator;

    public CustomOperatorProperty(String name, T value, String type, String operator) {
        super(name, value, type);
        this.operator = operator;
    }

    public CustomOperatorProperty(String jdoVariableName, String name, T value, String type, String operator) {
        super(jdoVariableName, name, value, type);
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public CharSequence generateFilter(int idx) {
        if (shouldIgnoreThisProperty()) {
            return null;
        } else if (isForRelation()) {
            return generateFilterForRelation(idx);
        } else {
            if (isOperatorAMethod()) {
                return String.format("%s.%s(param%d)",
                        getName(), getOperator().substring(0, getOperator().length() - 2), idx);
            } else {
                return String.format("%s %s param%d", getName(), getOperator(), idx);
            }
        }
    }

    public CharSequence generateFilterForRelation(int idx) {
        if (isOperatorAMethod()) {
            return String.format("%s.contains(%s) && %s.%s.%s(param%d)", LookupName.getFieldName(getName()), getJdoVariableName(),
                    getJdoVariableName(), LookupName.getRelatedFieldName(getName()), getOperator().substring(0,
                            getOperator().length() - 2), idx);
        } else {
            return String.format("%s.contains(%s) && %s.%s %s param%d", LookupName.getFieldName(getName()), getJdoVariableName(),
                    getJdoVariableName(), LookupName.getRelatedFieldName(getName()), getOperator(), idx);
        }
    }


    public boolean isOperatorAMethod() {
        return StringUtils.endsWith(operator, "()");
    }
}
