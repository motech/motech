package org.motechproject.mds.json.rest;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;

import java.util.List;

/**
 * An entry to be included in the MDS REST documentation.
 */
public class RestEntry {

    private EntityDto entity;
    private List<FieldDto> fields;
    private RestOptionsDto restOptions;
    private List<LookupDto> lookups;

    public RestEntry(EntityDto entity, List<FieldDto> fields, RestOptionsDto restOptions, List<LookupDto> lookups) {
        this.entity = entity;
        this.fields = fields;
        this.restOptions = restOptions;
        this.lookups = lookups;
    }

    public EntityDto getEntity() {
        return entity;
    }

    public void setEntity(EntityDto entity) {
        this.entity = entity;
    }

    public List<FieldDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldDto> fields) {
        this.fields = fields;
    }

    public RestOptionsDto getRestOptions() {
        return restOptions;
    }

    public void setRestOptions(RestOptionsDto restOptions) {
        this.restOptions = restOptions;
    }

    public List<LookupDto> getLookups() {
        return lookups;
    }

    public void setLookups(List<LookupDto> lookups) {
        this.lookups = lookups;
    }

    public String getEntityName() {
        return entity.getName();
    }
}
