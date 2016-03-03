package org.motechproject.mds.lookup;

import org.motechproject.mds.dto.LookupDto;

import java.util.List;

/**
 * Stores information about lookups and classname of the related entity.
 */
public class EntityLookups {

    private String entityClassName;
    private List<LookupDto> lookups;

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public List<LookupDto> getLookups() {
        return lookups;
    }

    public void setLookups(List<LookupDto> lookups) {
        this.lookups = lookups;
    }
}
