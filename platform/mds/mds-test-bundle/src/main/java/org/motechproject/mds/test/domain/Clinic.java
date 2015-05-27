package org.motechproject.mds.test.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(tableName = "clinics", recordHistory = true)
public class Clinic {

    @Field
    private Set<Patient> patients;

    @Field
    private String name;

    public Clinic(String name) {
        this.name = name;
        this.patients = new HashSet<>();
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Clinic clinic = (Clinic) o;
        return Objects.equals(name, clinic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
