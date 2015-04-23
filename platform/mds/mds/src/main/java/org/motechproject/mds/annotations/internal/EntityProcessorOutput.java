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
    private RestOptionsDto restProcessingResult;
    private TrackingDto trackingProcessingResult;
    private Collection<FieldDto> fieldProcessingResult;
    private Collection<String> uiFilterableProcessingResult;
    private Map<String, Long> uiDisplayableProcessingResult;
    private Collection<String> nonEditableProcessingResult;


    public EntityDto getEntityProcessingResult() {
        return entityProcessingResult;
    }

    public void setEntityProcessingResult(EntityDto entityProcessingResult) {
        this.entityProcessingResult = entityProcessingResult;
    }

    public RestOptionsDto getRestProcessingResult() {
        return restProcessingResult;
    }

    public void setRestProcessingResult(RestOptionsDto restProcessingResult) {
        this.restProcessingResult = restProcessingResult;
    }

    public TrackingDto getTrackingProcessingResult() {
        return trackingProcessingResult;
    }

    public void setTrackingProcessingResult(TrackingDto trackingProcessingResult) {
        this.trackingProcessingResult = trackingProcessingResult;
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

    public Collection<String> getUiFilterableProcessingResult() {
        return uiFilterableProcessingResult;
    }

    public void setUiFilterableProcessingResult(Collection<String> uiFilterableProcessingResult) {
        this.uiFilterableProcessingResult = uiFilterableProcessingResult;
    }

    public Collection<String> getNonEditableProcessingResult() {
        return nonEditableProcessingResult;
    }

    public void setNonEditableProcessingResult(Collection<String> nonEditableProcessingResult) {
        this.nonEditableProcessingResult = nonEditableProcessingResult;
    }
}
