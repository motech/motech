package org.motechproject.mds.query;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.util.LookupName;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The <code>RangeProperty</code> class represents a property that will be used in JDO query
 * and it has to be inside the given range.
 *
 * @param <T> type used in range.
 */
public class RangeProperty<T> extends Property<Range<T>> {

    public RangeProperty(String name, Range<T> value, String type) {
        super(name, value, type);
    }

    public RangeProperty(String jdoVariableName, String name, Range<T> value, String type) {
        super(jdoVariableName, name, value, type);
    }

    @Override
    public CharSequence generateFilter(int idx) {
        if (isForRelation()) {
            return generateFilterForRelation(idx);
        }

        StringBuilder sb = new StringBuilder();

        if (getValue().getMin() != null) {
            // {name}>=param{idx}lb
            sb.append(getName()).append(">=param").append(idx).append("lb");
            if (getValue().getMax() != null) {
                sb.append(" && ");
            }
        }

        if (getValue().getMax() != null) {
            // {name}<=param{idx}ub
            sb.append(getName()).append("<=param").append(idx).append("ub");
        }

        return sb.toString();
    }

    public CharSequence generateFilterForRelation(int idx) {
        StringBuilder sb = new StringBuilder();

        if (getValue().getMin() != null) {
            // {name}>=param{idx}lb
            sb.append(getJdoVariableName());
            sb.append(".");
            sb.append(LookupName.getRelatedFieldName(getName())).append(">=param").append(idx).append("lb");
            if (getValue().getMax() != null) {
                sb.append(" && ");
            }
        }

        if (getValue().getMax() != null) {
            // {name}<=param{idx}ub
            sb.append(getJdoVariableName());
            sb.append(".");
            sb.append(LookupName.getRelatedFieldName(getName())).append("<=param").append(idx).append("ub");
        }

        String params = sb.toString();
        if (StringUtils.isNotBlank(params)) {
            return String.format("%s.contains(%s) && %s", LookupName.getFieldName(getName()), getJdoVariableName(), params);
        }
        return params;
    }

    @Override
    public CharSequence generateDeclareParameter(int idx) {
        StringBuilder sb = new StringBuilder();

        if (getValue().getMin() != null) {
            // {type} param{idx}lb
            sb.append(getType()).append(" param").append(idx).append("lb");
            if (getValue().getMax() != null) {
                sb.append(", ");
            }
        }

        if (getValue().getMax() != null) {
            // {type} param{idx}ub
            sb.append(getType()).append(" param").append(idx).append("ub");
        }

        return sb.toString();
    }

    @Override
    public Collection unwrap() {
        if (shouldIgnoreThisProperty()) {
            return null;
        } else {
            ArrayList list = new ArrayList();
            if (getValue().getMin() != null) {
                list.add(getValue().getMin());
            }
            if (getValue().getMax() != null) {
                list.add(getValue().getMax());
            }
            return list;
        }
    }

    @Override
    protected boolean shouldIgnoreThisProperty() {
        Range range = getValue();
        return range == null || (range.getMin() == null && range.getMax() == null);
    }
}
