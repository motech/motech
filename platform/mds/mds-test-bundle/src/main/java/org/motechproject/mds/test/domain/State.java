package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Persistent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(recordHistory = true)
public class State {
    @Field
    private String name;

    @Field
    @Persistent(mappedBy = "state")
    private Set<District> districts;

    @Field
    private District defaultDistrict;

    @Field
    private Set<Language> languages;

    public State() {
        this.districts = new HashSet<>();
        this.languages = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    public District getDefaultDistrict() {
        return defaultDistrict;
    }

    public void setDefaultDistrict(District defaultDistrict) {
        this.defaultDistrict = defaultDistrict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        State state = (State) o;
        return Objects.equals(name, state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
