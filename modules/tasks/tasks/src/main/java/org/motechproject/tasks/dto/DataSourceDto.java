package org.motechproject.tasks.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceDto extends TaskConfigStepDto {

    private String providerName;
    private Long providerId;
    private Long objectId;
    private String type;
    private String name;
    private String specifiedName;
    private List<LookupDto> lookup;
    private boolean failIfDataNotFound;

    public DataSourceDto() {
        super(null);
    }

    public DataSourceDto(Integer order, String providerName, Long providerId, Long objectId, String type, String name,
                         String specifiedName, List<LookupDto> lookup, boolean failIfDataNotFound) {
        super(order);
        this.providerName = providerName;
        this.providerId = providerId;
        this.objectId = objectId;
        this.type = type;
        this.name = name;
        this.specifiedName = specifiedName;
        this.lookup = lookup;
        this.failIfDataNotFound = failIfDataNotFound;
    }

    public String getProviderName() {
        return providerName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSpecifiedName() {
        return specifiedName;
    }

    public List<LookupDto> getLookup() {
        return lookup;
    }

    public boolean isFailIfDataNotFound() {
        return failIfDataNotFound;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecifiedName(String specifiedName) {
        this.specifiedName = specifiedName;
    }

    public void setLookup(List<LookupDto> lookup) {
        this.lookup = lookup;
    }

    public void setFailIfDataNotFound(boolean failIfDataNotFound) {
        this.failIfDataNotFound = failIfDataNotFound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, objectId, type, lookup, failIfDataNotFound);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final DataSourceDto other = (DataSourceDto) obj;

        return Objects.equals(this.providerName, providerName)
                && Objects.equals(this.objectId, objectId)
                && Objects.equals(this.type, type)
                && Objects.equals(this.lookup, other.lookup)
                && Objects.equals(this.failIfDataNotFound, other.failIfDataNotFound);
    }
}
