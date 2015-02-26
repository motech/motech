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

    private List<String> fieldNames = new ArrayList<>();
    private List<String> lookupNames = new ArrayList<>();

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

    public void addField(String name) {
        this.fieldNames.add(name);
    }

    public void removeField(String name) {
        this.fieldNames.remove(name);
    }

    public boolean containsField(String name) {
        return fieldNames.contains(name);
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = null != fieldNames ? fieldNames : new ArrayList<String>();
    }

    public void addLookup(String name) {
        this.lookupNames.add(name);
    }

    public void removeLookup(String name) {
        this.lookupNames.remove(name);
    }

    public boolean containsLookup(String name) {
        return lookupNames.contains(name);
    }

    public List<String> getLookupNames() {
        return lookupNames;
    }

    public void setLookupNames(List<String> lookupNames) {
        this.lookupNames = null != lookupNames ? lookupNames : new ArrayList<String>();
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

    public boolean supportAnyOperation() {
        return create || read || update || delete;
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
