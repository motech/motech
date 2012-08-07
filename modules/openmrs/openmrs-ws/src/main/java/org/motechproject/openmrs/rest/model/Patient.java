package org.motechproject.openmrs.rest.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Patient {
    private String uuid;
    private List<Identifier> identifiers = new ArrayList<Identifier>();
    private Person person;

    public static class PatientSerializer implements JsonSerializer<Patient> {
        @Override
        public JsonElement serialize(Patient src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getUuid());
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Identifier getIdentifierByIdentifierType(String uuid) {
        for (Identifier identifier : identifiers) {
            IdentifierType type = identifier.getIdentifierType();
            if (type.getUuid().equals(uuid)) {
                return identifier;
            }
        }
        return null;
    }
}
