package org.motechproject.mrs.model;

import org.motechproject.mrs.domain.MRSConceptName;

public class MRSConceptNameDto implements MRSConceptName{
    private String name;
    private String locale = "en";
    private String conceptNameType = "FULLY_SPECIFIED";

    public MRSConceptNameDto(String name) {
        this.name = name;
    }

    public MRSConceptNameDto(String name, String locale, String conceptNameType) {
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
}
