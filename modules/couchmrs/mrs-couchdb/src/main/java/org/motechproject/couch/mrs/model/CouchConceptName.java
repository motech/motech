package org.motechproject.couch.mrs.model;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.MRSConceptName;

import java.util.Objects;

@TypeDiscriminator("doc.type === 'ConceptName'")
public class CouchConceptName extends MotechBaseDataObject implements MRSConceptName {
    private String name;
    private String locale = "en";
    private String conceptNameType = "FULLY_SPECIFIED";

    private final String type = "ConceptName";

    public CouchConceptName() {
        this.setType(type);
    }

    public CouchConceptName(String name) {
        this.setType(type);
        this.name = name;;
    }

    public CouchConceptName(String name, String locale, String conceptNameType) {
        this.setType(type);
        this.name = name;
        this.locale = locale;
        this.conceptNameType = conceptNameType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getConceptNameType() {
        return conceptNameType;
    }

    @Override
    public void setConceptNameType(String conceptNameType) {
        this.conceptNameType = conceptNameType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CouchConceptName)) {
            return false;
        }

        CouchConceptName conceptName = (CouchConceptName) o;

        return Objects.equals(name, conceptName.name) && Objects.equals(locale, conceptName.locale) &&
                Objects.equals(conceptNameType, conceptName.conceptNameType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, locale, conceptNameType);
    }
}
