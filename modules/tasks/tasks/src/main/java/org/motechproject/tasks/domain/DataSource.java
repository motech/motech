package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSource extends TaskConfigStep {
    private static final long serialVersionUID = 6652124746431496660L;

    public static class Lookup implements Serializable {
        private static final long serialVersionUID = -3560581906854128062L;

        private String field;
        private String value;

        public Lookup() {
            this(null, null);
        }

        public Lookup(String field, String value) {
            this.field = field;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final Lookup other = (Lookup) obj;

            return Objects.equals(this.field, other.field)
                    && Objects.equals(this.value, other.value);
        }
    }

    private String providerName;
    private String providerId;
    private Long objectId;
    private String type;
    private String name;
    private List<Lookup> lookup;
    private boolean failIfDataNotFound;

    public DataSource() {
        this(null, null, null, "id", (List <Lookup>) null, false);
    }

    public DataSource(String providerId, Long objectId, String type, String name, List<Lookup> lookup,
                      boolean failIfDataNotFound) {
        this("", providerId, objectId, type, name, lookup, failIfDataNotFound);
    }

    public DataSource(String providerName, String providerId, Long objectId, String type,
                      String name, List<Lookup> lookup, boolean failIfDataNotFound) {
        this.providerName = providerName;
        this.providerId = providerId;
        this.objectId = objectId;
        this.type = type;
        this.name = name;
        this.lookup = lookup;
        this.failIfDataNotFound = failIfDataNotFound;
    }

    @Deprecated
    public DataSource(String providerId, Long objectId, String type, Lookup lookup,
                      boolean failIfDataNotFound) {
        this.providerId = providerId;
        this.objectId = objectId;
        this.type = type;
        this.name = "id";
        this.lookup = asList(lookup);
        this.failIfDataNotFound = failIfDataNotFound;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
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

        return objectEquals(other.providerId, other.objectId, other.type)
                && Objects.equals(this.providerName, other.providerName)
                && Objects.equals(this.lookup, other.lookup)
                && Objects.equals(this.failIfDataNotFound, other.failIfDataNotFound);
    }

    @JsonIgnore
    public boolean objectEquals(String providerId, Long objectId, String type) {
        return Objects.equals(this.providerId, providerId)
                && Objects.equals(this.objectId, objectId)
                && Objects.equals(this.type, type);
    }

    @Override
    public String toString() {
        return String.format(
                "DataSource{providerName='%s', providerId='%s', objectId=%d, type='%s', name='%s', lookup=%s, failIfDataNotFound=%s} %s",
                providerName, providerId, objectId, type, name, lookup, failIfDataNotFound, super.toString()
        );
    }
}
