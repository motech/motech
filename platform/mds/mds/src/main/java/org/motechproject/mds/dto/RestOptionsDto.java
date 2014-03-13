package org.motechproject.mds.dto;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.motechproject.mds.util.NumberPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing rest options of given entity.
 */
public class RestOptionsDto {
    private Long id;

    private List<Number> fieldIds = new ArrayList<>();
    private List<Number> lookupIds = new ArrayList<>();

    private boolean create;
    private boolean read;
    private boolean update;
    private boolean delete;

    public RestOptionsDto() {
        this(false, false, false, false);
    }

    public RestOptionsDto(boolean create, boolean read, boolean update, boolean delete) {
        this.create = create;
        this.read = read;
        this.update = update;
        this.delete = delete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addField(Number id) {
        this.fieldIds.add(id);
    }

    public void removeField(Number id) {
        this.fieldIds.remove(id);
    }

    public boolean containsFieldId(Number id) {
        return CollectionUtils.exists(fieldIds, new NumberPredicate(id));
    }

    public List<Number> getFieldIds() {
        return fieldIds;
    }

    public void setFieldIds(List<Number> fieldIds) {
        this.fieldIds = null != fieldIds ? fieldIds : new ArrayList<Number>();
    }

    public void addLookup(Number id) {
        this.lookupIds.add(id);
    }

    public void removeLookup(Number id) {
        this.lookupIds.remove(id);
    }

    public boolean containsLookupId(Number id) {
        return CollectionUtils.exists(lookupIds, new NumberPredicate(id));
    }

    public List<Number> getLookupIds() {
        return lookupIds;
    }

    public void setLookupIds(List<Number> lookupIds) {
        this.lookupIds = null != lookupIds ? lookupIds : new ArrayList<Number>();
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
