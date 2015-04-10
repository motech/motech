package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TrackingDto;

import java.util.Collection;
import java.util.Map;

/**
 * Represents the result of processing entity class, by the {@link org.motechproject.mds.annotations.internal.EntityProcessor}.
 * The fields contain entity details, based on the annotations discovered in the entity class.
 */
public class EntityProcessorOutput {

    private EntityDto entityProcessingResult;
    private RestOptionsDto restOptionsProcessingResult;
    private TrackingDto crudProcessingResult;
    private Collection<FieldDto> fieldProcessingResult;
    private Collection<String> uiFilterableProcessingResult;
    private Map<String, Long> uiDisplayableProcessingResult;
    private RestOptionsDto restIgnoreProcessingResult;
    private Map<String, Boolean> nonEditableProcessingResult;


    public EntityDto getEntityProcessingResult() {
        return entityProcessingResult;
    }

    public void setEntityProcessingResult(EntityDto entityProcessingResult) {
        this.entityProcessingResult = entityProcessingResult;
    }

    public RestOptionsDto getRestOptionsProcessingResult() {
        return restOptionsProcessingResult;
    }

    public void setRestOptionsProcessingResult(RestOptionsDto restOptionsProcessingResult) {
        this.restOptionsProcessingResult = restOptionsProcessingResult;
    }

    public TrackingDto getCrudProcessingResult() {
        return crudProcessingResult;
    }

    public void setCrudProcessingResult(TrackingDto crudProcessingResult) {
        this.crudProcessingResult = crudProcessingResult;
    }

    public Collection<FieldDto> getFieldProcessingResult() {
        return fieldProcessingResult;
    }

    public void setFieldProcessingResult(Collection<FieldDto> fieldProcessingResult) {
        this.fieldProcessingResult = fieldProcessingResult;
    }

    public Map<String, Long> getUiDisplayableProcessingResult() {
        return uiDisplayableProcessingResult;
    }

    public void setUiDisplayableProcessingResult(Map<String, Long> uiDisplayableProcessingResult) {
        this.uiDisplayableProcessingResult = uiDisplayableProcessingResult;
    }

    public RestOptionsDto getRestIgnoreProcessingResult() {
        return restIgnoreProcessingResult;
    }

    public void setRestIgnoreProcessingResult(RestOptionsDto restIgnoreProcessingResult) {
        this.restIgnoreProcessingResult = restIgnoreProcessingResult;
    }

    public Collection<String> getUiFilterableProcessingResult() {
        return uiFilterableProcessingResult;
    }

    public void setUiFilterableProcessingResult(Collection<String> uiFilterableProcessingResult) {
        this.uiFilterableProcessingResult = uiFilterableProcessingResult;
    }

    public Map<String, Boolean> getNonEditableProcessingResult() {
        return nonEditableProcessingResult;
    }

    public void setNonEditableProcessingResult(Map<String, Boolean> nonEditableProcessingResult) {
        this.nonEditableProcessingResult = nonEditableProcessingResult;
    }
}
