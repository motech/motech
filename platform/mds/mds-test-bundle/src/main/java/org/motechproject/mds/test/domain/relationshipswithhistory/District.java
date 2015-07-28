package org.motechproject.mds.test.domain.relationshipswithhistory;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.Objects;

@Entity(recordHistory = true)
public class District {
    @Field
    private String name;

    @Field
    private Long serialNumber;

    @Field
    private State state;

    @Field
    private Language language;

    public District() {
    }

    public District(String name , Long serialNumber) {
        this.name = name;
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        District district = (District) o;
        return Objects.equals(name, district.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
