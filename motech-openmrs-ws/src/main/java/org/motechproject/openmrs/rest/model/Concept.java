package org.motechproject.openmrs.rest.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Concept {
    private String uuid;
    private String display;
    private ConceptName name;
    private List<ConceptName> names = new ArrayList<ConceptName>();
    private DataType datatype;
    private ConceptClass conceptClass;

    public static class ConceptSerializer implements JsonSerializer<Concept> {
        @Override
        public JsonElement serialize(Concept src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getDisplay());
        }
    }

    public static class ConceptName {
        private String name;
        private String locale = "en";
        private String conceptNameType = "FULLY_SPECIFIED";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getConceptNameType() {
            return conceptNameType;
        }

        public void setConceptNameType(String conceptNameType) {
            this.conceptNameType = conceptNameType;
        }
    }

    public static class DataType {
        private String display;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }

    public static class ConceptClass {
        private String display;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public ConceptName getName() {
        return name;
    }

    public void setName(ConceptName name) {
        this.name = name;
    }

    public List<ConceptName> getNames() {
        return names;
    }

    public void setNames(List<ConceptName> names) {
        this.names = names;
    }

    public DataType getDatatype() {
        return datatype;
    }

    public void setDatatype(DataType datatype) {
        this.datatype = datatype;
    }

    public ConceptClass getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(ConceptClass conceptClass) {
        this.conceptClass = conceptClass;
    }
}
