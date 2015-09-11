package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

import javax.jdo.annotations.Persistent;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Country extends MdsVersionedEntity {
    @Field
    private String name;

    @Field
    @Cascade(delete = true)
    @Persistent(mappedBy = "country")
    private Set<City> cities;

    @Field
    @Cascade(delete = true)
    private City capital;

    public Country(String name) {
        this.name = name;
        this.cities = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }

    public City getCapital() {
        return capital;
    }

    public void setCapital(City capital) {
        this.capital = capital;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Country country = (Country) o;

        return !(name != null ? !name.equals(country.name) : country.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
