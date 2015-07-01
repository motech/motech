package org.motechproject.mds.test.domain.manytomany;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Persistent;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(tableName = "patient", recordHistory = true)
public class Patient {

    @Field
    @Persistent(table = "patients_clinics", mappedBy = "patients")
    @Join(column = "patient_id")
    @Element(column = "clinic_id")
    private Set<Clinic> clinics;

    private String name;

    public Patient(String name) {
        this.name = name;
        this.clinics = new HashSet<>();
    }

    public Set<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
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
        Patient patient = (Patient) o;
        return Objects.equals(name, patient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
