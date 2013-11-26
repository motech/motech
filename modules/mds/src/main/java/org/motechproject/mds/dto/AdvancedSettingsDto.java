package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>AdvancedSettingsDto</code> contains information about advanced settings of an entity.
 */
public class AdvancedSettingsDto {
    private String objectId;
    private TrackingDto tracking = new TrackingDto();
    private List<LookupDto> indexes = new ArrayList<>();
    private RestOptions restOptions = new RestOptions();

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public TrackingDto getTracking() {
        return tracking;
    }

    public void setTracking(TrackingDto tracking) {
        this.tracking = null != tracking ? tracking : new TrackingDto();
    }

    public List<LookupDto> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<LookupDto> indexes) {
        this.indexes = null != indexes ? indexes : new ArrayList<LookupDto>();
    }

    public RestOptions getRestOptions() {
        return restOptions;
    }

    public void setRestOptions(RestOptions restOptions) {
        this.restOptions = restOptions;
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
