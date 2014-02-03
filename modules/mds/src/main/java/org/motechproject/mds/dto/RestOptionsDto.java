package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing rest options of given entity.
 */
public class RestOptionsDto {
    private Long id;

    private List<Long> fieldIds = new ArrayList<>();
    private List<Long> lookupIds = new ArrayList<>();

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

    public RestOptionsDto(Long id, boolean create, boolean read, boolean update, boolean delete) {
        this(create, read, update, delete);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addField(Long id) {
        this.fieldIds.add(id);
    }

    public void removeField(Long id) {
        this.fieldIds.remove(id);
    }

    public List<Long> getFieldIds() {
        return fieldIds;
    }

    public void setFieldIds(List<Long> fieldIds) {
        this.fieldIds = null != fieldIds ? fieldIds : new ArrayList<Long>();
    }

    public void addLookup(Integer id) {
        addLookup((long)id);
    }

    public void addLookup(Long id) {
        this.lookupIds.add(id);
    }

    public void removeLookup(Integer id) {
        removeLookup((long)id);
    }

    public void removeLookup(Long id) {
        this.lookupIds.remove(id);
    }

    public List<Long> getLookupIds() {
        return lookupIds;
    }

    public void setLookupIds(List<Long> lookupIds) {
        this.lookupIds = null != lookupIds ? lookupIds : new ArrayList<Long>();
    }

    public boolean isLookupExposedViaRest (Long id) {
        return (lookupIds != null) ? lookupIds.contains(id) : false;
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
