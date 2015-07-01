package org.motechproject.mds.test.domain.relationshipswithhistory;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Persistent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(recordHistory = true)
public class Language {
    @Field
    private String name;

    @Field
    @Persistent(mappedBy = "languages")
    private Set<State> states;

    @Field
    @Persistent(mappedBy = "language")
    private Set<District> districts;

    public Language() {
        this.states = new HashSet<>();
        this.districts = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    public Set<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Language language = (Language) o;
        return Objects.equals(name, language.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
