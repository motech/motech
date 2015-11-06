package org.motechproject.mds.test.domain.historytest;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

import javax.jdo.annotations.Persistent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity(recordHistory = true)
public class Company extends MdsEntity {

    @Field
    private String name;

    @Field
    @Persistent(mappedBy = "companies")
    private Set<Consultant> consultants;

    public Company() {
    }

    public Company(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Consultant> getConsultants() {
        return consultants;
    }

    public void setConsultants(Set<Consultant> consultants) {
        this.consultants = consultants;
    }

    public void addConsultant(Consultant consultant) {
        if (getConsultants() == null) {
            setConsultants(new HashSet<Consultant>());
        }
        getConsultants().add(consultant);
    }

    public void removeConsultant(String name) {
        Iterator<Consultant> it = getConsultants().iterator();
        while (it.hasNext()) {
            Consultant consultant = it.next();
            if (name.equals(consultant.getName())) {
                it.remove();
            }
        }
    }
}
