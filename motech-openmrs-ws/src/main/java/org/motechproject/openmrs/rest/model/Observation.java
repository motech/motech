package org.motechproject.openmrs.rest.model;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Observation {
    private String uuid;
    private Concept concept;
    private ObservationValue value;
    private Date obsDatetime;
    private List<Observation> groupsMembers;

    public static class ObservationValue {
        private String display;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }

    public static class ObservationValueSerializer implements JsonSerializer<ObservationValue> {
        @Override
        public JsonElement serialize(ObservationValue src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getDisplay());
        }
    }

    public static class ObservationValueDeserializer implements JsonDeserializer<ObservationValue> {
        @Override
        public ObservationValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            if (json.isJsonPrimitive()) {
                ObservationValue value = new ObservationValue();
                value.setDisplay(json.getAsString());
                return value;
            } else {
                ObservationValue value = new ObservationValue();
                value.setDisplay(json.getAsJsonObject().get("display").getAsString());
                return value;
            }
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public ObservationValue getValue() {
        return value;
    }

    public void setValue(ObservationValue value) {
        this.value = value;
    }

    public Date getObsDatetime() {
        return obsDatetime;
    }

    public void setObsDatetime(Date obsDatetime) {
        this.obsDatetime = obsDatetime;
    }

    public List<Observation> getGroupsMembers() {
        return groupsMembers;
    }

    public void setGroupsMembers(List<Observation> groupsMembers) {
        this.groupsMembers = groupsMembers;
    }

    public boolean hasConceptByName(String conceptName) {
        return concept.getDisplay().equals(conceptName);
    }
}
