package org.motechproject.mds.query;

import java.util.Arrays;
import java.util.Collection;

/**
 * The <code>Property</code> class represents a property that will be used in JDO query. Classes that
 * extend this class should define how that property should be used in WHERE section in JDO query.
 *
 * @param <T> type of the passed value
 */
public abstract class Property<T> {
    private String name;
    private T value;
    private String type;
    private boolean forRelation = false;
    private String jdoVariableName;

    protected Property(String name, T value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    protected Property(String jdoVariableName, String name, T value, String type) {
        this.jdoVariableName = jdoVariableName;
        this.forRelation = true;
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getJdoVariableName() {
        return jdoVariableName;
    }

    public boolean isForRelation() {
        return forRelation;
    }

    public CharSequence asFilter(int idx) {
        return shouldIgnoreThisProperty() ? null : generateFilter(idx);
    }

    protected abstract CharSequence generateFilter(int idx);

    public CharSequence asDeclareParameter(int idx) {
        return shouldIgnoreThisProperty() ? null : generateDeclareParameter(idx);
    }

    protected CharSequence generateDeclareParameter(int idx) {
        return String.format("%s param%d", getType(), idx);
    }

    public Collection unwrap() {
        return shouldIgnoreThisProperty() ? null : Arrays.asList(getValue());
    }

    protected boolean shouldIgnoreThisProperty() {
        return false;
    }
}
