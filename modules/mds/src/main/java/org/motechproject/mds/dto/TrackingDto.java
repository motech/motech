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
    private List<String> fields = new LinkedList<>();
    private List<String> actions = new LinkedList<>();

    public List<String> getFields() {
        return fields;
    }

    public void addField(String fieldId) {
        this.fields.add(fieldId);
    }

    public void removeField(String fieldId) {
        this.fields.remove(fieldId);
    }

    public void setFields(List<String> fields) {
        this.fields = CollectionUtils.isEmpty(fields)
                ? new LinkedList<String>()
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
