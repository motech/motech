package org.motechproject.tasks.domain.mds.task;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.DataSourceDto;
import org.motechproject.tasks.dto.LookupDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single data source object used by a task. This class is part of the task itself and does not describe
 * the data source itself. This object translates to retrieving a data source object during task execution.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class DataSource extends TaskConfigStep {
    private static final long serialVersionUID = 6652124746431496660L;

    @Field
    private String providerName;
    private Long providerId;
    private Long objectId;
    private String type;
    private String name;
    private String specifiedName;

    @Field
    @Cascade(delete = true)
    private List<Lookup> lookup;

    private boolean failIfDataNotFound;

    /**
     * Constructor.
     */
    public DataSource() {
        this(null, null, null, null, "id", null, null, false);
    }

    /**
     * Constructor.
     * @param dto DataSource data transfer object
     */
    public DataSource(DataSourceDto dto){
        this(dto.getProviderName(), dto.getProviderId(), dto.getObjectId(), dto.getType(), dto.getName(), dto.getSpecifiedName(),
                Lookup.toLookups(dto.getLookup()), dto.isFailIfDataNotFound(), dto.getOrder());
    }

    /**
     * Constructor.
     *
     * @param providerName  the provider name
     * @param providerId  the provider ID
     * @param objectId  the object ID
     * @param type  the data source type
     * @param name  the data source name
     * @param lookup  the lookup name
     * @param failIfDataNotFound  defines if task should fail if no data was found
     */
    public DataSource(String providerName, Long providerId, Long objectId, String type,
                      String name, String specifiedName, List<Lookup> lookup, boolean failIfDataNotFound) {
        this(providerName, providerId, objectId, type, name, specifiedName, lookup, failIfDataNotFound, null);
    }

    public DataSource(String providerName, Long providerId, Long objectId, String type,
                      String name, String specifiedName, List<Lookup> lookup, boolean failIfDataNotFound, Integer order) {
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

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Lookup> getLookup() {
        return lookup;
    }

    public void setLookup(Object lookup) {
        this.lookup = new ArrayList<>();
        if (lookup instanceof List) {
            for (Object lookupEntity : (List) lookup) {
                LinkedHashMap<String, String> lookupMap = (LinkedHashMap) lookupEntity;
                Lookup l = new Lookup();
                l.setField(lookupMap.get("field"));
                l.setValue(lookupMap.get("value"));
                this.lookup.add(l);
            }
        } else {
            LinkedHashMap<String, String> newLookup = (LinkedHashMap) lookup;
            Lookup l = new Lookup();
            l.setField(newLookup.get("field"));
            l.setValue(newLookup.get("value"));
            this.lookup.add(l);
        }
    }

    public boolean isFailIfDataNotFound() {
        return failIfDataNotFound;
    }

    public void setFailIfDataNotFound(boolean failIfDataNotFound) {
        this.failIfDataNotFound = failIfDataNotFound;
    }

    public DataSourceDto toDto() {
        List<LookupDto> lookupDtos = new ArrayList<>();

        for (Lookup lookupFromList : getLookup()) {
            lookupDtos.add(lookupFromList.toDto());
        }

        return new DataSourceDto(getOrder(), providerName, providerId, objectId, type, name, specifiedName, lookupDtos, failIfDataNotFound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, objectId, type, lookup, failIfDataNotFound);
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

        final DataSource other = (DataSource) obj;

        return objectEquals(other.providerName, other.objectId, other.type)
                && Objects.equals(this.lookup, other.lookup)
                && Objects.equals(this.failIfDataNotFound, other.failIfDataNotFound);
    }

    public boolean objectEquals(String providerName, Long objectId, String type) {
        return Objects.equals(this.providerName, providerName)
                && Objects.equals(this.objectId, objectId)
                && Objects.equals(this.type, type);
    }

    @Override
    public String toString() {
        return String.format(
                "DataSource{providerName='%s', providerId='%s', objectId=%d, type='%s', name='%s', specifiedName=%s, lookup=%s, failIfDataNotFound=%s} %s",
                providerName, providerId, objectId, type, name, specifiedName, lookup, failIfDataNotFound, super.toString()
        );
    }
}
