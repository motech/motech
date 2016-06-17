package org.motechproject.mds.test.domain.historytest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Entity(recordHistory = true)
public class Company {

    @Field
    private Long id;

    @Field
    @Unique
    private String name;

    @Field
    private DateTime modificationDate;

    @Field
    @Persistent(mappedBy = "companies")
    private Set<Consultant> consultants = new HashSet<>();

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Company)) {
            return false;
        } else {
            Company that = (Company) o;
            return Objects.equals(id, that.id) && StringUtils.equals(name, that.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
