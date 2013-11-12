package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing rest options of given entity.
 */
public class RestOptions implements Serializable {

    private static final long serialVersionUID = 2788308149813128670L;

    private List<String> fieldIds = new ArrayList<>();
    private List<String> lookupIds = new ArrayList<>();

    private boolean create;
    private boolean read;
    private boolean update;
    private boolean delete;

    public void addField(String value) {
        this.fieldIds.add(value);
    }

    public void removeField(String value) {
        this.fieldIds.remove(value);
    }

    public List<String> getFieldIds() {
        return fieldIds;
    }

    public void setFieldIds(List<String> fieldIds) {
        this.fieldIds = null != fieldIds ? fieldIds : new ArrayList<String>();
    }

    public void addLookup(String value) {
        this.lookupIds.add(value);
    }

    public void removeLookup(String value) {
        this.lookupIds.remove(value);
    }

    public List<String> getLookupIds() {
        return lookupIds;
    }

    public void setLookupIds(List<String> lookupIds) {
        this.lookupIds = null != lookupIds ? lookupIds : new ArrayList<String>();
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
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
