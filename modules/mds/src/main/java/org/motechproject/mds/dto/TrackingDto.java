package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * The <code>TrackingDto</code> contains information about which fields and what kind of actions
 * should be logged.
 */
public class TrackingDto {
    private List<Long> fields = new LinkedList<>();
    private List<String> actions = new LinkedList<>();

    public List<Long> getFields() {
        return fields;
    }

    public void addField(Number fieldId) {
        this.fields.add(fieldId.longValue());
    }

    public void removeField(Number fieldId) {
        this.fields.remove(fieldId.longValue());
    }

    public void setFields(List<Long> fields) {
        this.fields = CollectionUtils.isEmpty(fields)
                ? new LinkedList<Long>()
                : fields;
    }

    public void addAction(String action) {
        this.actions.add(action);
    }

    public void removeAction(String action) {
        this.actions.remove(action);
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = CollectionUtils.isEmpty(actions)
                ? new LinkedList<String>()
                : actions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
