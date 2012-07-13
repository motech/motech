package org.motechproject.openmrs.rest.model;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Encounter {

    private String uuid;
    private Location location;
    private EncounterType encounterType;
    private Date encounterDatetime;
    private Patient patient;
    private Person provider;
    private List<Observation> obs;

    public static class EncounterType {
        private String display;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }

    public static class EncounterTypeSerializer implements JsonSerializer<EncounterType> {

        @Override
        public JsonElement serialize(EncounterType src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getDisplay());
        }

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EncounterType getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(EncounterType encounterType) {
        this.encounterType = encounterType;
    }

    public Date getEncounterDatetime() {
        return encounterDatetime;
    }

    public void setEncounterDatetime(Date encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Person getProvider() {
        return provider;
    }

    public void setProvider(Person provider) {
        this.provider = provider;
    }

    public List<Observation> getObs() {
        return obs;
    }

    public void setObs(List<Observation> obs) {
        this.obs = obs;
    }
}
