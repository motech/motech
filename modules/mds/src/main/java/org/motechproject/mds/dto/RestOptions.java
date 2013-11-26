package org.motechproject.mds.dto;

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

    public List<String> getFieldIds() {
        return fieldIds;
    }

    public void setFieldIds(List<String> fieldIds) {
        this.fieldIds = fieldIds;
    }

    public List<String> getLookupIds() {
        return lookupIds;
    }

    public void setLookupIds(List<String> lookupIds) {
        this.lookupIds = lookupIds;
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
}
